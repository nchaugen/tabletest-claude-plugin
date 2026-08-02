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

function lintMarkdown(file, markdown) {
  return extractTableTestExamples(markdown).flatMap((example) =>
    findViolations(example).map(({ check, evidence, line }) => ({ file, line, check, evidence }))
  );
}

function skillMarkdownFiles(skillDir) {
  const referencesDir = path.join(skillDir, "references");
  const references = fs.existsSync(referencesDir)
    ? fs.readdirSync(referencesDir).filter((f) => f.endsWith(".md")).sort().map((f) => path.join(referencesDir, f))
    : [];
  return [path.join(skillDir, "SKILL.md"), ...references].filter((f) => fs.existsSync(f));
}

function lintSkill(skillDir, repoRoot) {
  return skillMarkdownFiles(skillDir).flatMap((file) =>
    lintMarkdown(path.relative(repoRoot, file), fs.readFileSync(file, "utf-8"))
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
  const skillDir = path.join(repoRoot, "skills", "tabletest");
  const violations = lintSkill(skillDir, repoRoot);

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
  console.log(`\n${violations.length} violation(s) in the tabletest skill's own examples.`);
}

if (require.main === module) main();

module.exports = {
  extractTableTestExamples,
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
