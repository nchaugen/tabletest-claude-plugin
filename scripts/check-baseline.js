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
const { computeEvalFingerprint } = require("./run-evals.js");

/**
 * The evals whose stored fingerprint no longer matches their definition on disk — the ones a
 * comparison against this benchmark would silently exclude. `fingerprintOf` is injected so the
 * decision stays a pure function of what is stamped versus what is current.
 */
function staleEvals(stampedEvals, fingerprintOf) {
  return stampedEvals
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

/** The newest official iteration's plain-named benchmark — what `--compare-official` would reach for. */
function latestOfficialBenchmark(skill) {
  const officialDir = path.join("iterations", skill);
  if (!fs.existsSync(officialDir)) return null;
  const iterations = fs
    .readdirSync(officialDir)
    .filter((entry) => entry.startsWith("iteration-"))
    .sort((a, b) => parseInt(b.split("-")[1], 10) - parseInt(a.split("-")[1], 10));
  for (const iteration of iterations) {
    const candidate = path.join(officialDir, iteration, "benchmark.json");
    if (fs.existsSync(candidate)) return candidate;
  }
  return null;
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  if (!args.skill) {
    console.error("Usage: node scripts/check-baseline.js --skill SKILL [--benchmark PATH]");
    process.exit(1);
  }

  const benchmarkPath = args.benchmark || latestOfficialBenchmark(args.skill);
  if (!benchmarkPath || !fs.existsSync(benchmarkPath)) {
    console.error(`No benchmark found for skill "${args.skill}".`);
    process.exit(1);
  }

  const benchmark = JSON.parse(fs.readFileSync(benchmarkPath, "utf-8"));
  const stale = staleEvals(benchmark.evals, (id) =>
    computeEvalFingerprint(path.join("evals", args.skill, id))
  );

  const regime = `${benchmark.grading_model || "unknown"}/${benchmark.grading_effort || "default"}`;
  console.log(`${benchmarkPath} — graded ${regime}, ${benchmark.evals.length} evals`);

  if (stale.length === 0) {
    console.log(`✅ Every eval matches the current definitions; this baseline is live.`);
    return;
  }

  console.log(`⛔ ${stale.length} of ${benchmark.evals.length} evals no longer match their definitions:`);
  for (const id of stale) console.log(`   ${id}`);
  console.log(`A comparison against this benchmark would exclude them and report the rest as clean.`);
  console.log(`Re-baseline, or promote the regrade whose fingerprints do match (docs/grader-tuning.md).`);
  process.exit(2);
}

if (require.main === module) main();

module.exports = { staleEvals, latestOfficialBenchmark };
