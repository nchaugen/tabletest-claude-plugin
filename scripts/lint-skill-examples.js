#!/usr/bin/env node
/**
 * Lints the skill's own worked examples with the checkers the evals grade outputs with.
 *
 * The skill teaches by illustration, so an example that breaks the rule it sits beside
 * teaches the break. Four clusters have now found that defect by hand and the tooling has
 * never found it once: the fingerprint guard covers eval definitions, and nothing reads
 * `skills/` at all. These checks are the mechanical half of the read-back pass in
 * AGENTS.md § Developing a variant — free, offline, and they run on every `node --test`.
 *
 * Scope is the `tabletest` skill: the checks below are about `@TableTest` syntax, and the
 * other two skills illustrate with markdown tables and non-JVM frameworks.
 */

const fs = require("fs");
const path = require("path");

const { checkers, extractTableTestMethodBodies } = require("./assertions.js");

/**
 * Fenced code blocks holding a `@TableTest`, each with the 1-based line its fence opens on.
 * Blocks are kept apart so one unparseable example cannot mask the next.
 */
function extractTableTestExamples(markdown) {
  const lines = markdown.split("\n");
  const examples = [];
  let openedAt = null;
  let body = [];

  for (let i = 0; i < lines.length; i++) {
    const isFence = /^\s*```/.test(lines[i]);
    if (!isFence) {
      if (openedAt !== null) body.push(lines[i]);
      continue;
    }
    if (openedAt === null) {
      openedAt = i + 1;
      body = [];
      continue;
    }
    const code = body.join("\n");
    if (/@TableTest/.test(code)) examples.push({ startLine: openedAt, code });
    openedAt = null;
  }

  return examples;
}

/**
 * Header and data rows of every table in an example. Rows are split on the pipe with no
 * quote handling: a linter over illustrations may misread a quoted pipe, and the cost of
 * that is a missed finding rather than a false one, because a misread row simply fails to
 * match its neighbours.
 */
function parseTables(code) {
  const tableRegex = /@TableTest\s*\(\s*(?:value\s*=\s*)?"""([\s\S]*?)"""\s*\)/g;
  const tables = [];
  let match;

  while ((match = tableRegex.exec(code)) !== null) {
    const rows = match[1]
      .split("\n")
      .map((line) => line.trim())
      .filter((line) => line.length > 0 && line.includes("|") && !line.startsWith("//"))
      .map((line) => line.split("|").map((cell) => cell.trim()));
    if (rows.length > 0) tables.push({ headers: rows[0], dataRows: rows.slice(1) });
  }

  return tables;
}

/**
 * An expectation column holding one value in every row is not being tested — SKILL.md's
 * "One rule per table" check. Needs two rows before it can mean anything.
 */
function constantExpectationColumns({ headers, dataRows }) {
  if (dataRows.length < 2) return [];

  return headers.flatMap((header, column) => {
    if (!header.endsWith("?")) return [];
    const values = dataRows.filter((row) => row.length === headers.length).map((row) => row[column]);
    if (values.length !== dataRows.length) return [];
    return new Set(values).size === 1 ? [`${header} is "${values[0]}" in all ${values.length} rows`] : [];
  });
}

const UNINFORMATIVE_OPENER = /^(test|should|verify)/i;

/**
 * Checks that apply to any table the skill publishes. Each returns evidence strings.
 *
 * `no-if-switch-in-method` and `annotation-order` are the eval checkers themselves, so the
 * illustration is judged by the same code that grades an agent's output. The other two are
 * decidable rules the eval suite states per eval rather than as a shared checker.
 */
const exampleChecks = {
  "no-if-switch-in-method": (code) => {
    // The checker reports "no methods found" as a failure, which is right for an eval
    // output and wrong for a documentation fragment with an elided body.
    if (extractTableTestMethodBodies(code).length === 0) return [];
    const result = checkers["no-if-switch-in-method"]({ fileContent: code, allFiles: [] });
    return result.passed ? [] : [result.evidence];
  },

  "annotation-order": (code) => {
    const result = checkers["annotation-order"]({ fileContent: code, allFiles: [] });
    return result.passed ? [] : [result.evidence];
  },

  // The collection-and-quoting checkers, wired in when eval-20's assertions became deterministic
  // (slice 4 step 6). They are pure table-syntax rules, so a skill illustration is exactly the
  // kind of text they should judge — and the skill teaches this notation, so an example getting
  // it wrong teaches the syntax its own evals penalise. Only the checkers that FAIL on a defect
  // are wired: `empty-list-explicit` asserts a table *contains* an empty list, which is a
  // property of an eval fixture rather than of every illustration, so it is deliberately absent.
  ...Object.fromEntries(
    [
      "list-syntax-correct",
      "set-syntax-correct",
      "special-chars-quoted",
      "pipe-quoted",
      "no-blank-collection-elements",
      "newline-in-cell",
    ].map((id) => [
      id,
      (code) => {
        const result = checkers[id]({ fileContent: code, allFiles: [] });
        return result.passed ? [] : [result.evidence];
      },
    ])
  ),

  "uninformative-method-name": (code) =>
    extractTableTestMethodBodies(code)
      .map(({ name }) => name.replace(/`/g, ""))
      .filter((name) => UNINFORMATIVE_OPENER.test(name))
      .map((name) => `${name}() opens with an opener that carries no information`),

  "constant-expectation-column": (code) => parseTables(code).flatMap(constantExpectationColumns),

  // A bare class name in a Throws?/Exception? column fails at run time with
  // ClassNotFoundException: JUnit converts a String to Class<?> only from a fully-qualified
  // name. The skill said so in one place and broke it in its own worked example, which eval-8
  // then copied (2026-08-02, slice 8 A1).
  "exception-column-fully-qualified": (code) =>
    parseTables(code).flatMap(exceptionColumnBareNames),
};

const THROWS_COLUMN = /^(throws|exception)\?$/i;

/**
 * Values in a Throws?/Exception? column that name a class without qualifying it.
 *
 * A blank cell is the "nothing thrown" row and is fine. Anything already carrying a dot is
 * qualified. What is left — `IllegalArgumentException` — is the defect.
 */
function exceptionColumnBareNames({ headers, dataRows }) {
  return headers.flatMap((header, column) => {
    if (!THROWS_COLUMN.test(header)) return [];
    return dataRows
      .filter((row) => row.length === headers.length)
      .map((row) => row[column])
      .filter((value) => value && !value.includes(".") && /^[A-Z]\w*$/.test(value))
      .map((value) => `${header} holds bare \`${value}\` — Class<?> conversion needs the fully-qualified name`);
  });
}

function findViolations(example) {
  return Object.entries(exampleChecks).flatMap(([check, run]) =>
    run(example.code).map((evidence) => ({ check, evidence, line: example.startLine }))
  );
}

/**
 * Business domains the eval suite owns, which a skill illustration must never borrow.
 *
 * Source of truth is `products/claude-plugin/decisions/skill-examples-avoid-eval-domains.md`
 * in the private docs repo. Reserved *for* skill illustrations, and therefore off-limits to new
 * evals: blood-donation deferral, flight-crew duty limits, greenhouse climate control,
 * medication dosing, waste-sorting classification.
 *
 * Why this is worth a checker: a skill that teaches "make the threshold a column" using loan
 * approval, graded by an eval that asks for loan approval, scores well without anything portable
 * being learned — and nothing fails, so the number just quietly stops meaning what it reports.
 * The rule survived a year on prose alone and was broken within a day of being re-read
 * (2026-08-02, slice 8 cluster 1: tag filtering, ticket types and a coupon message all landed in
 * one commit).
 */
const EVAL_DOMAIN_TERMS = [
  "loyalty", "coupon", "baggage", "cinema", "hotel cancellation", "travel insurance",
  "loan approval", "weekly pay", "overtime", "shopping cart", "order splitting", "shipping cost",
  "subscription billing", "event registration", "tag filter", "money parser",
  // Paraphrases that slipped past the nouns above. "groups items into shipments" reached a shared
  // rule on 2026-08-02 without matching "order splitting" — the checker catches vocabulary, not
  // meaning, so add the words a paraphrase actually uses.
  "shipment", "warehouse", "insurance", "ticket",
  // Stock states are eval-30's vocabulary and matched none of the above: an illustration reading
  // `{IN_STOCK, BACKORDERED}` passed the checker on 2026-08-04 while naming the exact enum the eval
  // grades.
  "in_stock", "backordered", "pre_ordered", "pre-ordered",
];

/**
 * Eval-domain vocabulary appearing anywhere in a skill file, examples or prose.
 *
 * Deliberately not limited to fenced examples: a domain noun in a sentence anchors just as well
 * as one in a table.
 */
function evalDomainTerms(markdown) {
  const lower = markdown.toLowerCase();
  return EVAL_DOMAIN_TERMS.filter((term) => lower.includes(term)).map(
    (term) => `"${term}" is a domain the eval suite owns — illustrate with a reserved domain instead`
  );
}

/**
 * Ordinary English and technical vocabulary a Title-Case scan picks up from any document, plus
 * words the reserved skill domains legitimately use — `travel` and `heavy` for blood-donation
 * deferral and medication dosing, `credit` for flight-crew rest credit, `adult`/`senior`/`premium`
 * for age and class descriptors.
 *
 * This list is generic English, so an eval landing never requires an edit here. That inverts the
 * maintenance burden of `EVAL_DOMAIN_TERMS`, which must gain a term per eval or silently stop
 * covering it: forgetting a word here costs a false positive someone dismisses, not a silent miss.
 */
const GENERIC_IDENTIFIERS = new Set([
  "accepted", "action", "adult", "below", "boolean", "both", "category", "credit", "days",
  "description", "does", "email", "empty", "exactly", "expected", "full", "heavy", "integer",
  "invalid", "iso", "items", "just", "kept", "list", "message", "missing", "mixed", "name",
  "notes", "null", "optional", "premium", "rejected", "result", "scenario", "senior", "several",
  "short", "single", "standard", "state", "string", "success", "table", "test", "this", "throws",
  "total", "travel", "unknown", "valid", "weight", "where", "write",
]);

const IDENTIFIER_PATTERN = /\b[A-Z][a-z]{3,}\b|\b[A-Z]{3,}(?:_[A-Z]+)*\b/g;

/**
 * The parts of a markdown file where a value is *data* rather than prose: fenced code, table
 * rows, and backticked spans.
 *
 * Prose was too noisy to act on — every Title-Case sentence opener matched. A domain value that
 * anchors an illustration to an eval appears as data on both sides, which is a much narrower
 * target and is where both 2026-08-06 breaches sat.
 */
function dataPositions(markdown) {
  const kept = [];
  let inFence = false;

  for (const line of markdown.split("\n")) {
    if (/^\s*```/.test(line)) {
      inFence = !inFence;
      continue;
    }
    if (inFence || line.split("|").length > 2) {
      kept.push(line);
      continue;
    }
    for (const span of line.match(/`[^`]+`/g) || []) kept.push(span);
  }

  return kept.join("\n");
}

/** Title-Case words and ALLCAPS enum-style tokens in data positions, minus generic vocabulary. */
function distinctiveIdentifiers(markdown) {
  return new Set(
    (dataPositions(markdown).match(IDENTIFIER_PATTERN) || [])
      .map((token) => token.toLowerCase())
      .filter((token) => !GENERIC_IDENTIFIERS.has(token))
  );
}

/**
 * Data values an eval owns, appearing verbatim as data in a skill.
 *
 * `EVAL_DOMAIN_TERMS` catches domain *phrases* — "shopping cart", "order splitting" — and by
 * construction cannot catch a product name or an opaque code. Both slipped through on 2026-08-06:
 * `2 x Widget @ £5.00` borrows eval-29's literal catalogue entry (`[Widget: 9.99]`), and
 * `W12/DELIVERY/addr-1` packs three of eval-30's five splitting dimensions — warehouse, fulfilment
 * type, delivery address — into one token. No term list would have held either.
 */
function evalIdentifierTerms(markdown, evalIdentifiers) {
  return [...distinctiveIdentifiers(markdown)]
    .filter((token) => evalIdentifiers.has(token))
    .sort()
    .map((token) => `"${token}" is a value the eval suite uses as data — rename it to a reserved domain`);
}

/** Every distinctive identifier the eval prompts and answer keys use as data. */
function evalSuiteIdentifiers(repoRoot) {
  const evalsRoot = path.join(repoRoot, "evals");
  if (!fs.existsSync(evalsRoot)) return new Set();

  const identifiers = new Set();
  for (const skill of fs.readdirSync(evalsRoot)) {
    const skillDir = path.join(evalsRoot, skill);
    if (!fs.statSync(skillDir).isDirectory()) continue;
    for (const evalName of fs.readdirSync(skillDir)) {
      for (const file of ["prompt.md", "expected_output.md"]) {
        const full = path.join(skillDir, evalName, file);
        if (!fs.existsSync(full) || !fs.statSync(full).isFile()) continue;
        for (const token of distinctiveIdentifiers(fs.readFileSync(full, "utf-8"))) {
          identifiers.add(token);
        }
      }
    }
  }
  return identifiers;
}

function lintMarkdown(file, markdown, evalIdentifiers = new Set()) {
  const domainHits = evalDomainTerms(markdown).map((evidence) => ({
    file, line: 1, check: "eval-domain-in-skill", evidence,
  }));
  const identifierHits = evalIdentifierTerms(markdown, evalIdentifiers).map((evidence) => ({
    file, line: 1, check: "eval-identifier-in-skill", evidence,
  }));
  return domainHits.concat(identifierHits).concat(
    extractTableTestExamples(markdown).flatMap((example) =>
      findViolations(example).map(({ check, evidence, line }) => ({ file, line, check, evidence }))
    )
  );
}

function skillMarkdownFiles(skillDir) {
  const referencesDir = path.join(skillDir, "references");
  const references = fs.existsSync(referencesDir)
    ? fs.readdirSync(referencesDir).filter((f) => f.endsWith(".md")).sort().map((f) => path.join(referencesDir, f))
    : [];
  return [path.join(skillDir, "SKILL.md"), ...references].filter((f) => fs.existsSync(f));
}

function lintSkill(skillDir, repoRoot, evalIdentifiers = new Set()) {
  return skillMarkdownFiles(skillDir).flatMap((file) =>
    lintMarkdown(path.relative(repoRoot, file), fs.readFileSync(file, "utf-8"), evalIdentifiers)
  );
}

/**
 * Deliberately carries no line number. Fixing one example shifts every line below it, and
 * a line-keyed baseline would then report the whole file as both fixed and reintroduced.
 * The evidence string names the method or column, which survives the shift.
 */
function violationKey({ file, check, evidence }) {
  return `${file} :: ${check} :: ${evidence}`;
}

function summarise(violations) {
  return violations.reduce((counts, violation) => {
    const key = violationKey(violation);
    counts[key] = (counts[key] || 0) + 1;
    return counts;
  }, {});
}

/**
 * Compares today's violations against the recorded baseline. New ones fail the build, and
 * so do ones that have been fixed — the baseline can only shrink deliberately, and never
 * drifts silently out of date.
 */
function compareToBaseline(violations, baseline) {
  const found = summarise(violations);
  const keys = new Set([...Object.keys(found), ...Object.keys(baseline)]);

  const introduced = [];
  const fixed = [];
  for (const key of keys) {
    const now = found[key] || 0;
    const before = baseline[key] || 0;
    if (now > before) introduced.push(`${key} (${now}, baseline ${before})`);
    if (now < before) fixed.push(`${key} (${now}, baseline ${before})`);
  }

  return { introduced: introduced.sort(), fixed: fixed.sort() };
}

const BASELINE_FILE = path.join(__dirname, "skill-example-baseline.json");

function readBaseline() {
  return JSON.parse(fs.readFileSync(BASELINE_FILE, "utf-8")).violations;
}

function main() {
  const repoRoot = path.resolve(__dirname, "..");
  // Every skill, not just tabletest: the eval-domain reservation binds all three, and each has
  // its own suite to keep out-of-domain. The example checks are TableTest-specific but harmless
  // elsewhere — extractTableTestExamples only matches fenced code containing @TableTest.
  const skillsRoot = path.join(repoRoot, "skills");
  const evalIdentifiers = evalSuiteIdentifiers(repoRoot);
  const violations = fs.readdirSync(skillsRoot).sort()
    .filter((name) => fs.statSync(path.join(skillsRoot, name)).isDirectory())
    .flatMap((name) => lintSkill(path.join(skillsRoot, name), repoRoot, evalIdentifiers));

  if (process.argv.includes("--write-baseline")) {
    const body = {
      note: "Violations the skill's own examples are still allowed to have. Emptied 2026-07-30 "
        + "when the sweep's 41 were fixed, and it should stay empty: an entry here is a defect "
        + "the skill is publishing, not a rule being waived.",
      violations: summarise(violations),
    };
    fs.writeFileSync(BASELINE_FILE, JSON.stringify(body, null, 2) + "\n");
    console.log(`Wrote ${Object.keys(body.violations).length} baseline entries.`);
    return;
  }

  for (const { file, line, check, evidence } of violations) {
    console.log(`${file}:${line}  ${check}\n    ${evidence}`);
  }
  console.log(`\n${violations.length} violation(s) in the skills' own examples.`);
}

if (require.main === module) main();

module.exports = {
  extractTableTestExamples,
  distinctiveIdentifiers,
  evalIdentifierTerms,
  evalSuiteIdentifiers,
  parseTables,
  findViolations,
  lintMarkdown,
  lintSkill,
  skillMarkdownFiles,
  summarise,
  compareToBaseline,
  violationKey,
  readBaseline,
};
