#!/usr/bin/env node
/**
 * What a run moved against the merged official baseline — the comparison the runner cannot make.
 *
 * An official run compares against iteration N−1 by number (`--compare-iteration` overrides it with
 * one other iteration), so a run whose evals were last seen in four different iterations has no
 * single baseline to name and reports a void or near-void comparison. That has now closed the
 * analysis gate on nothing twice: iteration-50 (9 slots won, 9 lost, reported as 1) and iteration-88
 * (5 of 5 evals excluded). This merges the newest stored result per eval, excluding the run itself,
 * and diffs the slots.
 *
 * It also names the skill state each before-draw was generated at, which no existing output does.
 * A delta is only attributable to the change you made if the before-draw was generated at the state
 * just before it; iteration-88's were four repairs older, and nothing in the report said so.
 *
 * Usage: node scripts/compare-official.js --skill tabletest --iteration 89
 */

const fs = require("fs");
const path = require("path");

const { loadOfficialBenchmark, comparisonAgainst } = require("./run-evals.js");

const repoRoot = path.join(__dirname, "..");

/**
 * The skill state an iteration's answers were generated at, for the "is this delta attributable?"
 * question. Returns nulls rather than throwing for an iteration whose benchmark predates digests.
 */
function skillStateOf(skill, iterationName) {
  const benchPath = path.join(repoRoot, "iterations", skill, iterationName, "benchmark.json");
  if (!fs.existsSync(benchPath)) return { digest: null, commit: null };
  const benchmark = JSON.parse(fs.readFileSync(benchPath, "utf-8"));
  return {
    digest: benchmark.skill_digest ? benchmark.skill_digest.slice(0, 10) : null,
    commit: benchmark.skill_commit ? benchmark.skill_commit.slice(0, 7) : null,
  };
}

/**
 * Every before-draw whose skill state differs from the run's own. These are the evals whose deltas
 * carry more than the change under test — the confound is stated per eval, not as a footnote,
 * because it decides whether a moved slot is evidence for the repair or for something older.
 */
function confoundedEvals(pairs, runDigest) {
  return pairs
    .filter((pair) => pair.baselineDigest && pair.baselineDigest !== runDigest)
    .map((pair) => ({ eval: pair.eval, from: pair.from, digest: pair.baselineDigest }));
}

function baselinePairs(skill, benchmark, baseline) {
  const baselineById = new Map(baseline.evals.map((entry) => [entry.id, entry]));
  return benchmark.evals.map((entry) => {
    const before = baselineById.get(entry.id);
    const from = before && before._fromIteration;
    return {
      eval: entry.id,
      from: from || null,
      baselineDigest: from ? skillStateOf(skill, from).digest : null,
    };
  });
}

/**
 * Every stored verdict for one slot, oldest first, over the draws that used the same eval definition
 * the run did — a moved slot means nothing until you know how often it moves by itself. Draws at
 * other fingerprints are left out because their verdicts answer a different question.
 */
function priorVerdicts(skill, evalId, fingerprint, assertion, excludeIteration) {
  const officialDir = path.join(repoRoot, "iterations", skill);
  if (!fs.existsSync(officialDir)) return [];
  const iterationNumber = (name) => parseInt(name.split("-")[1], 10);
  return fs.readdirSync(officialDir)
    .filter((name) => name.startsWith("iteration-") && name !== `iteration-${excludeIteration}`)
    .filter((name) => fs.existsSync(path.join(officialDir, name, "benchmark.json")))
    .sort((a, b) => iterationNumber(a) - iterationNumber(b))
    .flatMap((name) => {
      const benchmark = JSON.parse(fs.readFileSync(path.join(officialDir, name, "benchmark.json"), "utf-8"));
      const entry = benchmark.evals.find((candidate) => candidate.id === evalId);
      if (!entry || entry.fingerprint !== fingerprint) return [];
      const results = entry.results || {};
      if (!results.failed_assertions || results.assertions_total === 0) return [];
      return [results.failed_assertions.includes(assertion) ? "F" : "P"];
    });
}

/**
 * How a moved slot reads against its own record: one that has gone both ways before moves on its
 * own, and a single draw cannot be credited to a skill change. Both of this batch's headline
 * "confirmations" were slots of exactly this kind.
 */
function baseRateNote(prior) {
  if (prior.length === 0) return "no prior draw at this fingerprint — a single observation";
  const pattern = prior.join("");
  const moves = pattern.includes("P") && pattern.includes("F");
  return `prior ${pattern}${moves ? " — moves on its own, one draw settles nothing" : ""}`;
}

function scoreOf(entry) {
  const results = entry && entry.results;
  if (!results || results.assertions_passed === undefined) return null;
  return `${results.assertions_passed}/${results.assertions_total}`;
}

function report(skill, iteration) {
  const benchPath = path.join(repoRoot, "iterations", skill, `iteration-${iteration}`, "benchmark.json");
  if (!fs.existsSync(benchPath)) throw new Error(`No benchmark at ${benchPath}`);
  const benchmark = JSON.parse(fs.readFileSync(benchPath, "utf-8"));
  const baseline = loadOfficialBenchmark(repoRoot, skill, { excludeIteration: iteration });
  if (!baseline) throw new Error(`No official baseline for ${skill} outside iteration ${iteration}`);

  const { moved, notComparable, comparableEvals, totalEvals } = comparisonAgainst(benchmark, baseline);
  const pairs = baselinePairs(skill, benchmark, baseline);
  const runDigest = benchmark.skill_digest ? benchmark.skill_digest.slice(0, 10) : null;
  const baselineById = new Map(baseline.evals.map((entry) => [entry.id, entry]));

  const lines = [];
  lines.push(`${skill} iteration ${iteration} vs merged official baseline (newest stored result per eval)`);
  lines.push(`skill under test: ${runDigest} (${(benchmark.skill_commit || "").slice(0, 7)})`);
  lines.push("");
  lines.push(`${comparableEvals} of ${totalEvals} evals comparable.`);
  for (const excluded of notComparable) lines.push(`  excluded: ${excluded.eval} — ${excluded.reason}`);
  lines.push("");

  for (const entry of benchmark.evals) {
    const pair = pairs.find((candidate) => candidate.eval === entry.id);
    const before = baselineById.get(entry.id);
    const movedHere = moved.filter((slot) => slot.eval === entry.id);
    const state = pair.baselineDigest === runDigest ? "same skill state" : `skill ${pair.baselineDigest}`;
    lines.push(`${entry.id}  ${scoreOf(before) || "—"} → ${scoreOf(entry) || "—"}   before: ${pair.from || "none"}, ${state}`);
    const withBaseRate = (slot) => {
      const prior = priorVerdicts(skill, entry.id, entry.fingerprint, slot.assertion, iteration);
      return `${slot.assertion}  (${baseRateNote(prior)})`;
    };
    for (const slot of movedHere.filter((slot) => slot.direction === "won")) lines.push(`   won   ${withBaseRate(slot)}`);
    for (const slot of movedHere.filter((slot) => slot.direction === "lost")) lines.push(`   lost  ${withBaseRate(slot)}`);
    if (movedHere.length === 0 && pair.from) lines.push(`   (no slot moved)`);
  }

  const confounded = confoundedEvals(pairs, runDigest);
  if (confounded.length > 0) {
    lines.push("");
    lines.push("⚠️  Before-draws generated at a different skill state than the one under test.");
    lines.push("    Their deltas carry every change in between, not only the one you are judging:");
    for (const entry of confounded) lines.push(`    ${entry.eval} — ${entry.from}, skill ${entry.digest}`);
  }
  return lines.join("\n");
}

function parseArgs(argv) {
  const args = { skill: null, iteration: null };
  for (let i = 0; i < argv.length; i += 1) {
    if (argv[i] === "--skill") args.skill = argv[i + 1];
    if (argv[i] === "--iteration") args.iteration = argv[i + 1];
  }
  return args;
}

if (require.main === module) {
  const args = parseArgs(process.argv.slice(2));
  if (!args.skill || !args.iteration) {
    console.error("Usage: node scripts/compare-official.js --skill SKILL --iteration N");
    process.exit(1);
  }
  console.log(report(args.skill, args.iteration));
}

module.exports = { confoundedEvals, baselinePairs, scoreOf, parseArgs, priorVerdicts, baseRateNote };
