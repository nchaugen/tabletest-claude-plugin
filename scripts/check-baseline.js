#!/usr/bin/env node
/**
 * Does a benchmark still describe the current eval suite?
 *
 * `--compare-official` resolves to whatever is plain-named, and nothing stops that file being from a
 * superseded regime whose every eval the fingerprint guard will exclude. A run then reports "no
 * verdicts moved" over a comparison that measured nothing. Answering "is this baseline live?" costs
 * a second here and is otherwise only discovered after paying for a loop.
 *
 * Usage: node scripts/check-baseline.js --skill tabletest [--benchmark PATH]
 * Exit codes: 0 every eval matches · 2 one or more are stale · 1 bad usage.
 */

const fs = require("fs");
const path = require("path");
const { computeEvalFingerprint, loadOfficialBenchmark } = require("./run-evals.js");

/**
 * The evals a benchmark still carries that have since been removed from the suite.
 *
 * A deleted eval fingerprints as the hash of no files at all, so it reads as "changed" and would
 * otherwise hold the baseline stale for ever — a check that can never pass is a check nobody runs.
 * Retirement is benign in a way staleness is not: a comparison iterates the evals the *run* holds,
 * so a baseline entry with no definition behind it is inert rather than silently excluded.
 * `hasDefinition` is injected to keep the decision a pure function of what exists.
 */
function retiredEvals(stampedEvals, hasDefinition) {
  return stampedEvals.filter((entry) => !hasDefinition(entry.id)).map((entry) => entry.id);
}

/**
 * The evals whose stored fingerprint no longer matches their definition on disk — the ones a
 * comparison against this benchmark would silently exclude. `fingerprintOf` is injected so the
 * decision stays a pure function of what is stamped versus what is current.
 *
 * Evals with no definition are retired, not stale, and are reported separately; without
 * `hasDefinition` every eval counts as defined, which is the pre-retirement behaviour.
 */
function staleEvals(stampedEvals, fingerprintOf, hasDefinition = () => true) {
  return stampedEvals
    .filter((entry) => hasDefinition(entry.id))
    .filter((entry) => entry.fingerprint && entry.fingerprint !== fingerprintOf(entry.id))
    .map((entry) => entry.id);
}

function parseArgs(argv) {
  const args = { skill: null, benchmark: null };
  for (let i = 0; i < argv.length; i++) {
    if (argv[i] === "--skill") args.skill = argv[++i];
    else if (argv[i] === "--benchmark") args.benchmark = argv[++i];
  }
  return args;
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  if (!args.skill) {
    console.error("Usage: node scripts/check-baseline.js --skill SKILL [--benchmark PATH]");
    process.exit(1);
  }

  // Resolve exactly as the runner does. `--compare-official` merges the newest result per eval
  // across official iterations, so checking only the newest directory would pass a suite whose
  // older thirteen evals are stale — the very partial-coverage blindness this script exists to
  // catch. A `--benchmark PATH` overrides, for vetting one candidate before promoting it.
  let benchmark;
  let source;
  if (args.benchmark) {
    if (!fs.existsSync(args.benchmark)) {
      console.error(`No benchmark at ${args.benchmark}.`);
      process.exit(1);
    }
    benchmark = JSON.parse(fs.readFileSync(args.benchmark, "utf-8"));
    source = args.benchmark;
  } else {
    benchmark = loadOfficialBenchmark(process.cwd(), args.skill);
    if (!benchmark) {
      console.error(`No official benchmark found for skill "${args.skill}".`);
      process.exit(1);
    }
    source = benchmark._iterationName || "official";
  }

  const hasDefinition = (id) =>
    fs.existsSync(path.join("evals", args.skill, id, "eval.json"));
  const retired = retiredEvals(benchmark.evals, hasDefinition);
  const stale = staleEvals(
    benchmark.evals,
    (id) => computeEvalFingerprint(path.join("evals", args.skill, id)),
    hasDefinition
  );

  const regime = `${benchmark.grading_model || "unknown"}/${benchmark.grading_effort || "default"}`;
  console.log(`${source} — graded ${regime}, ${benchmark.evals.length} evals`);
  const byIteration = {};
  for (const entry of benchmark.evals) {
    const from = entry._fromIteration || "this benchmark";
    byIteration[from] = (byIteration[from] || 0) + 1;
  }
  if (Object.keys(byIteration).length > 1) {
    console.log(
      `   merged from: ${Object.entries(byIteration).map(([k, n]) => `${k} (${n})`).join(", ")}`
    );
  }

  if (retired.length > 0) {
    console.log(
      `ℹ️  ${retired.length} eval(s) in this benchmark are retired — no longer in the suite, so a ` +
      `comparison ignores them: ${retired.join(", ")}`
    );
  }

  if (stale.length === 0) {
    const live = benchmark.evals.length - retired.length;
    console.log(`✅ All ${live} evals still in the suite match the current definitions; this baseline is live.`);
    return;
  }

  console.log(`⛔ ${stale.length} of ${benchmark.evals.length} evals no longer match their definitions:`);
  for (const id of stale) console.log(`   ${id}`);
  console.log(`A comparison against this benchmark would exclude them and report the rest as clean.`);
  console.log(`Re-baseline, or promote the regrade whose fingerprints do match (docs/grader-tuning.md).`);
  process.exit(2);
}

if (require.main === module) main();

module.exports = { staleEvals, retiredEvals };
