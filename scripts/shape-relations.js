#!/usr/bin/env node
/**
 * The load-bearing relations of each eval: the questions its assertions ask, restated as
 * arithmetic over a parsed answer.
 *
 * A relation is transcribed from an assertion's own text, never invented. Several of those texts
 * already specify an enumerate-then-decide algorithm ("list every row carrying a premium as
 * age - claims - premium, then name the triple"); a relation runs that algorithm and returns the
 * enumeration as its evidence, which is the part a grader most often gets wrong.
 *
 * Divergence from the reference answer is not error — a ground truth says which rows are
 * load-bearing and which are illustrative, and several decompositions are equally good. So a
 * relation states a *relation between rows* (a pair sharing an age, a boundary in one column) and
 * never a row set to match. A relation the reference answer fails is transcribed wrongly, and
 * `shape-relations.test.js` pins exactly that for every eval here.
 *
 * A relation carrying a `judgement` note has an exemption this cannot decide. Its verdict is a
 * candidate, not a result: determinism buys the countable half and no more.
 */

const { constantExpectationColumns, findColumn, callArguments, literalArgument, numericValue } = require("./answer-shape.js");

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

/** Every eval this module can read, by eval number. */
const EVALS = {
  18: { call: EVAL_18_CALL, relations: EVAL_18_RELATIONS },
};

/** The relations and call name for an eval number, or null where none are authored. */
function relationsFor(number) {
  return EVALS[number] || null;
}

/** The eval numbers relations exist for, for a CLI to name in its error message. */
function authoredEvals() {
  return Object.keys(EVALS);
}

module.exports = {
  EVAL_18_RELATIONS,
  ageBoundaryPair,
  authoredEvals,
  bandedAgePair,
  claimBoundaryPair,
  claimEffectBands,
  claimsByAge,
  internalColumns,
  perClaimTriple,
  policyColumns,
  premiumCases,
  relationsFor,
  tablesAssertingBoth,
};
