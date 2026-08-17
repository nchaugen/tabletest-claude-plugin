#!/usr/bin/env node
/**
 * Reads every stored draw of one eval structurally and reports what its answers look like,
 * beside what the grader said about them.
 *
 * Why this exists: a score delta is evidence only for a slot with a stable prior record, and
 * eval-18's slots move ±6 between identical skill states. The relations below are the same
 * questions the eval's own assertions ask, restated as arithmetic over the parsed table — so
 * they cost nothing, never flip, and can be computed over draws already paid for. A repair's
 * mechanism becomes a boolean over eleven stored answers instead of one bought observation.
 *
 * The relations are transcribed from the assertion texts, not invented. Several of those texts
 * already specify an enumerate-then-decide algorithm ("list every row carrying a premium as
 * age - claims - premium, then name the triple"); where they do, this runs that algorithm
 * exactly, and prints the enumeration as its evidence.
 *
 * What this cannot decide is marked `judgement` and reported without a verdict. Determinism
 * buys the countable half; it does not buy a reading of whether a description states an
 * invariance.
 *
 *   node scripts/shape-report.js --eval 18 [--skill tabletest] [--json]
 */

const fs = require("fs");
const path = require("path");

const {
  answerShape,
  callArguments,
  constantExpectationColumns,
  findColumn,
  literalArgument,
  numericValue,
} = require("./answer-shape.js");

const repoRoot = path.join(__dirname, "..");

/**
 * How eval-18's answers name the quantities its assertions are about.
 *
 * Draws call the same column `Claim Count`, `Claims` or `Prior Claims`, so a relation addresses
 * a role rather than a header. Every resolution is printed, because a role silently resolving
 * to the wrong column is the one way this instrument can lie.
 */
const EVAL_18_ROLES = {
  age: /^age\b|\bage\b/i,
  claims: /claim.?count|^claims?$|prior.?claims/i,
  premium: /premium/i,
  // Draws name the decision `Status?` and `Outcome?` as readily as `Decision?`; iteration-40
  // mixes premium into two `Status?` tables and reading only `Decision?` calls that separated.
  decision: /decision|status|outcome|verdict/i,
};

/** The internals eval-18's `black-box-columns` names, and nothing beyond them. */
const EVAL_18_INTERNALS = [/risk.?score/i, /has.?active.?policy/i, /calculate.?risk/i];

/** The senior threshold the premium band assertions are stated against. */
const SENIOR_AGE = 65;

/** The method under test, whose held literal arguments are as much a row's data as a column is. */
const EVAL_18_CALL = "evaluateApplication";

/**
 * What a table supplies for each role: a column that varies it, or a literal held in the body.
 *
 * A table can hold age at 30 by writing `evaluateApplication("NEW", 30, claimCount)` and carrying
 * no age column at all, and its rows still state a premium at age 30. Resolving roles from
 * columns alone loses every such row — it read eval-18's iteration-50 as having no 0-against-1
 * claim pair when the answer plainly has one, and the grader was right where this was wrong.
 */
function roleResolver(table, sutParameters) {
  const args = callArguments(table.body, EVAL_18_CALL) || [];
  const held = new Map();
  args.forEach((argument, index) => {
    const name = sutParameters[index];
    const literal = literalArgument(argument);
    if (name && literal !== null) held.set(name, literal);
  });

  return (pattern) => {
    const column = findColumn(table, pattern);
    if (column) return (one) => one[column.header];
    const name = [...held.keys()].find((candidate) => pattern.test(candidate));
    if (name) return () => held.get(name);
    return null;
  };
}

/**
 * Every row in the class carrying a premium derived from the risk score, as
 * `{method, age, claims, premium}`.
 *
 * A premium of 0 is never risk-derived here: the standard formula floors at 100 and the senior
 * one at 200, so a zero is an auto-approval or a rejection. `premium-claim-boundary` excludes
 * exactly those rows, and every other premium assertion depends on the same enumeration.
 */
function premiumCases(shape, sutParameters = []) {
  const found = [];
  for (const table of shape.tables) {
    const premium = table.expectationColumns.find((column) => EVAL_18_ROLES.premium.test(column.header));
    if (!premium) continue;
    const resolve = roleResolver(table, sutParameters);
    const age = resolve(EVAL_18_ROLES.age);
    const claims = resolve(EVAL_18_ROLES.claims);
    if (!age || !claims) continue;
    for (const one of table.cases) {
      const values = {
        method: table.method,
        age: numericValue(age(one)),
        claims: numericValue(claims(one)),
        premium: numericValue(one[premium.header]),
      };
      if (values.age === null || values.claims === null || values.premium === null) continue;
      if (values.premium <= 0) continue;
      found.push(values);
    }
  }
  return found;
}

/**
 * The parameter names of the method under test, in order, read from the eval's own project.
 *
 * Positional literals in the fixture call mean nothing without these names, and hardcoding them
 * would let the relations drift from the eval definition they are supposed to restate.
 */
function sutParameterNames(evalDirectory, callName) {
  const sources = findFiles(path.join(evalDirectory, "project", "src", "main"), /\.(java|kt)$/);
  for (const file of sources) {
    const content = fs.readFileSync(file, "utf8");
    const signature = content.match(new RegExp(`${callName}\\s*\\(([^)]*)\\)\\s*\\{`));
    if (!signature) continue;
    return signature[1]
      .split(",")
      .map((part) => part.trim())
      .filter(Boolean)
      .map((part) => {
        const kotlin = part.match(/^(\w+)\s*:/);
        return kotlin ? kotlin[1] : part.split(/\s+/).pop();
      });
  }
  return [];
}

/** The distinct claim counts present at each age, ascending. */
function claimsByAge(cases) {
  const byAge = new Map();
  for (const one of cases) {
    if (!byAge.has(one.age)) byAge.set(one.age, new Set());
    byAge.get(one.age).add(one.claims);
  }
  return new Map([...byAge].map(([age, claims]) => [age, [...claims].sort((a, b) => a - b)]));
}

/**
 * `premium-charge-is-per-claim`: three consecutive claim counts at one age.
 *
 * Two counts leave a per-claim rate and a one-off penalty indistinguishable, which is the whole
 * subject of the assertion. The three rows may sit in different tables provided they share an age.
 */
function perClaimTriple(cases) {
  for (const [age, claims] of claimsByAge(cases)) {
    for (const first of claims) {
      if (claims.includes(first + 1) && claims.includes(first + 2)) {
        return { age, triple: [first, first + 1, first + 2] };
      }
    }
  }
  return null;
}

/** `premium-claim-boundary`: a 0-against-1-claim pair at one age, both premiums risk-derived. */
function claimBoundaryPair(cases) {
  for (const [age, claims] of claimsByAge(cases)) {
    if (claims.includes(0) && claims.includes(1)) return { age };
  }
  return null;
}

/** `premium-age-boundary`: age 64 against age 65 at one claim count. */
function ageBoundaryPair(cases) {
  const byClaims = new Map();
  for (const one of cases) {
    if (!byClaims.has(one.claims)) byClaims.set(one.claims, new Set());
    byClaims.get(one.claims).add(one.age);
  }
  for (const [claims, ages] of byClaims) {
    if (ages.has(SENIOR_AGE - 1) && ages.has(SENIOR_AGE)) return { claims };
  }
  return null;
}

/**
 * `premium-claim-effect-varies-by-band`: a claim-count pair on each side of the senior threshold.
 *
 * One pair, however well chosen, cannot show that the increment differs — so this needs a
 * pair-hosting age below 65 and another at or above it. The pairs need not use the same counts.
 */
function claimEffectBands(cases) {
  const hostingAges = [...claimsByAge(cases)]
    .filter(([, claims]) => claims.length > 1)
    .map(([age]) => age);
  const below = hostingAges.filter((age) => age < SENIOR_AGE);
  const atOrAbove = hostingAges.filter((age) => age >= SENIOR_AGE);
  if (below.length > 0 && atOrAbove.length > 0) return { below, atOrAbove };
  return null;
}

/**
 * `premium-age-is-banded`: two different ages carrying the same premium at one claim count.
 *
 * A table whose every age carries a different premium states "premium rises with age", which is
 * not the rule. The 64-against-65 pair cannot satisfy this and needs no exclusion — those two
 * ages never share a premium.
 */
function bandedAgePair(cases) {
  for (const one of cases) {
    const twin = cases.find(
      (other) => other.claims === one.claims && other.premium === one.premium && other.age !== one.age,
    );
    if (twin) return { claims: one.claims, ages: [one.age, twin.age].sort((a, b) => a - b), premium: one.premium };
  }
  return null;
}

/** `separates-decision-and-premium`: no single table asserts a decision and a premium together. */
function tablesAssertingBoth(shape) {
  return shape.tables
    .filter(
      (table) =>
        table.expectationColumns.some((column) => EVAL_18_ROLES.decision.test(column.header)) &&
        table.expectationColumns.some((column) => EVAL_18_ROLES.premium.test(column.header)),
    )
    .map((table) => table.method);
}

/** `black-box-columns`: a column naming an internal the assertion lists. */
function internalColumns(shape) {
  const named = [];
  for (const table of shape.tables) {
    for (const column of table.columns) {
      if (column.isScenario) continue;
      if (EVAL_18_INTERNALS.some((internal) => internal.test(column.header))) {
        named.push({ method: table.method, header: column.header });
      }
    }
  }
  return named;
}

/**
 * Columns declaring a policy constant the API never receives.
 *
 * Reported without a verdict on purpose. Repair 11 teaches this column and `black-box-columns`
 * has failed three draws for carrying one, so its presence is the conflict itself rather than a
 * defect — the count is what makes the conflict countable across draws.
 */
function policyColumns(shape) {
  const named = [];
  for (const table of shape.tables) {
    for (const column of table.columns) {
      if (column.isScenario) continue;
      if (/\(policy\)|threshold|cutoff|limit/i.test(column.header)) {
        named.push({ method: table.method, header: column.header });
      }
    }
  }
  return named;
}

/**
 * Every eval-18 relation, each naming the assertion it restates.
 *
 * `holds` is the mechanical verdict; `evidence` is the enumeration the assertion asks a grader
 * to produce before deciding. A relation whose `judgement` is set carries an exemption this
 * cannot decide, and its verdict is a candidate rather than a result.
 */
const EVAL_18_RELATIONS = [
  {
    id: "premium-charge-is-per-claim",
    label: "three consecutive claims at one age",
    evaluate: (shape, context) => {
      const cases = premiumCases(shape, context.sutParameters);
      const found = perClaimTriple(cases);
      return {
        holds: found !== null,
        evidence: found
          ? `age ${found.age}: claims ${found.triple.join(", ")}`
          : `no triple; claims by age ${describeClaimsByAge(cases)}`,
      };
    },
  },
  {
    id: "premium-claim-boundary",
    label: "0-against-1 claim pair at one age",
    evaluate: (shape, context) => {
      const found = claimBoundaryPair(premiumCases(shape, context.sutParameters));
      return { holds: found !== null, evidence: found ? `age ${found.age}` : "no 0/1 pair at any one age" };
    },
  },
  {
    id: "premium-age-boundary",
    label: "age 64 against 65 at one claim count",
    evaluate: (shape, context) => {
      const found = ageBoundaryPair(premiumCases(shape, context.sutParameters));
      return { holds: found !== null, evidence: found ? `at ${found.claims} claim(s)` : "no 64/65 pair" };
    },
  },
  {
    id: "premium-claim-effect-varies-by-band",
    label: "a claim pair on each side of 65",
    evaluate: (shape, context) => {
      const found = claimEffectBands(premiumCases(shape, context.sutParameters));
      return {
        holds: found !== null,
        evidence: found
          ? `pairs at ages ${found.below.join(",")} and ${found.atOrAbove.join(",")}`
          : "pairs on one side of the threshold only",
      };
    },
  },
  {
    id: "premium-age-is-banded",
    label: "two ages sharing a premium",
    evaluate: (shape, context) => {
      const found = bandedAgePair(premiumCases(shape, context.sutParameters));
      return {
        holds: found !== null,
        evidence: found
          ? `ages ${found.ages.join(" and ")} both ${found.premium} at ${found.claims} claim(s)`
          : "every age carries its own premium",
      };
    },
  },
  {
    id: "rule-falsifiable-by-a-row",
    label: "no constant expectation column",
    judgement: "the stated-invariance exemption is a reading of the title and description",
    evaluate: (shape) => {
      const constant = shape.tables.flatMap((table) =>
        constantExpectationColumns(table).map((column) => `${table.method}: ${column.header}=${column.value}`),
      );
      return {
        holds: constant.length === 0,
        evidence: constant.length === 0 ? "every expectation column varies" : constant.join("; "),
      };
    },
  },
  {
    id: "separates-decision-and-premium",
    label: "no table asserts decision and premium",
    evaluate: (shape) => {
      const both = tablesAssertingBoth(shape);
      return {
        holds: both.length === 0,
        evidence: both.length === 0 ? "no table carries both" : `both in ${both.join(", ")}`,
      };
    },
  },
  {
    id: "black-box-columns",
    label: "no column names a listed internal",
    evaluate: (shape) => {
      const internals = internalColumns(shape);
      const policy = policyColumns(shape);
      const note = policy.length > 0 ? ` (policy columns: ${policy.map((one) => one.header).join(", ")})` : "";
      return {
        holds: internals.length === 0,
        evidence:
          (internals.length === 0
            ? "no risk-score, active-policy or formula column"
            : internals.map((one) => `${one.method}: ${one.header}`).join("; ")) + note,
      };
    },
  },
];

/** The claim counts present at each age, for a failing relation to show its working. */
function describeClaimsByAge(cases) {
  const entries = [...claimsByAge(cases)].sort((a, b) => a[0] - b[0]);
  if (entries.length === 0) return "(no premium rows found)";
  return entries.map(([age, claims]) => `${age}:[${claims.join(",")}]`).join(" ");
}

const RELATIONS_BY_EVAL = { 18: EVAL_18_RELATIONS };

/** The eval directory for a number, or null. Directories carry a slug the number does not. */
function evalDir(skill, number) {
  const base = path.join(repoRoot, "evals", skill);
  if (!fs.existsSync(base)) return null;
  const match = fs
    .readdirSync(base)
    .find((name) => new RegExp(`^eval-${number}-`).test(name));
  return match ? path.join(base, match) : null;
}

/** Every file under `dir` matching `pattern`, recursively. */
function findFiles(dir, pattern) {
  if (!fs.existsSync(dir)) return [];
  const found = [];
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) found.push(...findFiles(full, pattern));
    else if (pattern.test(entry.name)) found.push(full);
  }
  return found;
}

/**
 * Every stored draw of one eval, oldest first, with the reference answer last.
 *
 * The reference is included deliberately and labelled: a relation the reference fails is a
 * relation transcribed wrongly, which is the cheapest available check on this instrument.
 */
function storedDraws(skill, slug) {
  const base = path.join(repoRoot, "iterations", skill);
  if (!fs.existsSync(base)) return [];
  const draws = [];
  const iterationDirs = fs
    .readdirSync(base)
    .filter((name) => /^iteration-\d+$/.test(name))
    .sort((a, b) => Number(a.split("-")[1]) - Number(b.split("-")[1]));

  for (const name of iterationDirs) {
    const dir = path.join(base, name, slug);
    if (fs.existsSync(dir)) draws.push(readDraw(name, dir, path.join(base, name), false));
  }
  const referenceBase = path.join(base, "reference");
  if (fs.existsSync(referenceBase)) {
    for (const name of fs.readdirSync(referenceBase).filter((entry) => /^iteration-\d+$/.test(entry))) {
      const dir = path.join(referenceBase, name, slug);
      if (fs.existsSync(dir)) draws.push(readDraw(`reference/${name}`, dir, path.join(referenceBase, name), true));
    }
  }
  return draws.filter(Boolean);
}

/** One draw: its answer source, the grader's verdicts, and the skill state it was generated at. */
function readDraw(label, dir, iterationDir, isReference) {
  const sources = findFiles(path.join(dir, "outputs"), /\.(java|kt)$/);
  if (sources.length === 0) return null;
  const source = sources.map((file) => fs.readFileSync(file, "utf8")).join("\n");

  const gradingPath = path.join(dir, "grading.json");
  const graded = new Map();
  if (fs.existsSync(gradingPath)) {
    const grading = JSON.parse(fs.readFileSync(gradingPath, "utf8"));
    for (const assertion of grading.assertions || []) graded.set(assertion.id, assertion.passed);
  }

  let digest = null;
  const benchmarkPath = path.join(iterationDir, "benchmark.json");
  if (fs.existsSync(benchmarkPath)) {
    digest = JSON.parse(fs.readFileSync(benchmarkPath, "utf8")).skill_digest || null;
  }

  return { label, isReference, source, graded, digest };
}

/** Every relation's mechanical verdict for one draw, beside the grader's. */
function evaluateDraw(draw, relations, context = { sutParameters: [] }) {
  const shape = answerShape(draw.source);
  return relations.map((relation) => {
    const { holds, evidence } = relation.evaluate(shape, context);
    const graded = draw.graded.has(relation.id) ? draw.graded.get(relation.id) : null;
    return {
      id: relation.id,
      label: relation.label,
      judgement: relation.judgement || null,
      holds,
      evidence,
      graded,
      agrees: graded === null ? null : graded === holds,
    };
  });
}

const mark = (value) => (value === null ? "·" : value ? "P" : "F");

/** The panel: one column per draw, one row per relation, mechanical over grader. */
function renderPanel(draws, evaluations, relations) {
  const width = Math.max(...relations.map((relation) => relation.id.length));
  const columns = draws.map((draw) => draw.label.replace(/^iteration-/, "it-").replace("reference/it-", "ref-"));
  const lines = [];
  lines.push(`${"relation".padEnd(width)}  ${columns.map((name) => name.padStart(7)).join("")}`);
  lines.push("-".repeat(width + 2 + columns.length * 7));

  for (const relation of relations) {
    const cells = draws.map((draw, index) => {
      const row = evaluations[index].find((one) => one.id === relation.id);
      return `${mark(row.holds)}/${mark(row.graded)}`.padStart(7);
    });
    lines.push(`${relation.id.padEnd(width)}  ${cells.join("")}`);
  }
  return lines.join("\n");
}

/** Where the mechanical verdict and the grader disagree — the only rows worth a human read. */
function disagreements(draws, evaluations) {
  const found = [];
  draws.forEach((draw, index) => {
    for (const row of evaluations[index]) {
      if (row.agrees === false) found.push({ draw: draw.label, ...row });
    }
  });
  return found;
}

function parseArgs(argv) {
  const args = { skill: "tabletest", eval: null, json: false };
  for (let i = 0; i < argv.length; i++) {
    if (argv[i] === "--skill") args.skill = argv[++i];
    else if (argv[i] === "--eval") args.eval = Number(argv[++i]);
    else if (argv[i] === "--json") args.json = true;
  }
  return args;
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  const relations = RELATIONS_BY_EVAL[args.eval];
  if (!relations) {
    console.error(
      `No relations authored for eval ${args.eval}. Available: ${Object.keys(RELATIONS_BY_EVAL).join(", ")}`,
    );
    process.exit(2);
  }
  const dir = evalDir(args.skill, args.eval);
  if (!dir) {
    console.error(`No eval ${args.eval} under evals/${args.skill}`);
    process.exit(2);
  }

  const slug = path.basename(dir);
  const draws = storedDraws(args.skill, slug);
  if (draws.length === 0) {
    console.error(`No stored draws with an answer for ${slug}`);
    process.exit(2);
  }
  const context = { sutParameters: sutParameterNames(dir, EVAL_18_CALL) };
  const evaluations = draws.map((draw) => evaluateDraw(draw, relations, context));

  if (args.json) {
    console.log(JSON.stringify({ slug, draws: draws.map((d, i) => ({ label: d.label, digest: d.digest, relations: evaluations[i] })) }, null, 2));
    return;
  }

  console.log(`\n${slug} — ${draws.length} stored answers, ${relations.length} relations`);
  console.log(`Cells read mechanical/grader. P pass, F fail, · not graded in that draw.\n`);
  console.log(renderPanel(draws, evaluations, relations));

  console.log(`\nSkill state per draw:`);
  for (const draw of draws) {
    console.log(`  ${draw.label.padEnd(22)} ${draw.digest || "(no benchmark)"}${draw.isReference ? "  [reference answer]" : ""}`);
  }

  console.log(`\nEvidence, per draw:`);
  draws.forEach((draw, index) => {
    console.log(`\n  ${draw.label}${draw.isReference ? "  [reference answer]" : ""}`);
    for (const row of evaluations[index]) {
      const flag = row.agrees === false ? "  <-- disagrees with grader" : "";
      console.log(`    ${mark(row.holds)}/${mark(row.graded)}  ${row.id}: ${row.evidence}${flag}`);
    }
  });

  const conflicts = disagreements(draws, evaluations);
  console.log(`\n${conflicts.length} disagreement(s) between the mechanical read and the grader.`);
  for (const one of conflicts) {
    console.log(`  ${one.draw} ${one.id}: mechanical ${mark(one.holds)}, grader ${mark(one.graded)} — ${one.evidence}`);
  }

  const judged = relations.filter((relation) => relation.judgement);
  if (judged.length > 0) {
    console.log(`\nRelations carrying an exemption this cannot decide:`);
    for (const relation of judged) console.log(`  ${relation.id}: ${relation.judgement}`);
  }
}

if (require.main === module) main();

module.exports = {
  EVAL_18_RELATIONS,
  ageBoundaryPair,
  bandedAgePair,
  claimBoundaryPair,
  claimEffectBands,
  claimsByAge,
  disagreements,
  evalDir,
  evaluateDraw,
  internalColumns,
  parseArgs,
  perClaimTriple,
  policyColumns,
  premiumCases,
  storedDraws,
  tablesAssertingBoth,
};
