#!/usr/bin/env node
/**
 * Which assertion slots flip across repeated gradings of the same outputs.
 *
 * A variance probe is only useful once it is reduced to *which* slots move — "18 of 365 flip" tells
 * a later session nothing it can act on, while the slot list tells it whether a moved verdict in its
 * own run is signal or noise. `docs/grader-tuning.md` requires that list before a probe is swept, so
 * this produces it in a form that can be pasted into `docs/assertion-triage.md`.
 *
 * Usage: node scripts/flip-report.js --skill tabletest --iteration 40 --suffixes ,p1,p2
 *        (an empty entry means the plain-named benchmark.json)
 */

const fs = require("fs");
const path = require("path");

/**
 * The failing-assertion set of each run, keyed `evalNumber\tassertionId`, reduced to the slots whose
 * verdict was not unanimous. A slot absent from a run entirely — the eval was not in that run —
 * makes the slot uncomparable rather than flipped, so runs must cover the same evals.
 */
function flipReport(runs) {
  const verdicts = new Map();
  runs.forEach((run, index) => {
    for (const slot of run.failedSlots) {
      if (!verdicts.has(slot)) verdicts.set(slot, new Array(runs.length).fill(false));
      verdicts.get(slot)[index] = true;
    }
  });

  const flipped = [];
  let stableFailures = 0;
  for (const [slot, pattern] of [...verdicts.entries()].sort()) {
    if (pattern.every(Boolean)) {
      stableFailures += 1;
      continue;
    }
    const [evalNumber, assertion] = slot.split("\t");
    flipped.push({ evalNumber, assertion, pattern });
  }
  return { flipped, stableFailures };
}

/** How often each assertion appears in the flip set — the shape that tells you which are intrinsic. */
function flipsByAssertion(flipped) {
  const counts = {};
  for (const flip of flipped) counts[flip.assertion] = (counts[flip.assertion] || 0) + 1;
  return Object.entries(counts).sort((a, b) => b[1] - a[1] || a[0].localeCompare(b[0]));
}

function readRun(skill, iteration, suffix) {
  const file = suffix ? `benchmark-${suffix}.json` : "benchmark.json";
  const benchmarkPath = path.join("iterations", skill, `iteration-${iteration}`, file);
  if (!fs.existsSync(benchmarkPath)) {
    console.error(`No benchmark at ${benchmarkPath}`);
    process.exit(1);
  }
  const benchmark = JSON.parse(fs.readFileSync(benchmarkPath, "utf-8"));
  const failedSlots = [];
  for (const entry of benchmark.evals) {
    const evalNumber = entry.id.match(/eval-(\d+)/)?.[1];
    for (const assertion of entry.results?.failed_assertions || []) {
      failedSlots.push(`${evalNumber}\t${assertion}`);
    }
  }
  return {
    label: suffix || "plain",
    failedSlots,
    benchmark,
    slots: benchmark.summary.assertions_total,
    score: `${benchmark.summary.assertions_passed}/${benchmark.summary.assertions_total}`,
  };
}

function main() {
  const argv = process.argv.slice(2);
  const arg = (name) => {
    const at = argv.indexOf(`--${name}`);
    return at === -1 ? null : argv[at + 1];
  };
  const skill = arg("skill");
  const iteration = arg("iteration");
  const suffixes = (arg("suffixes") ?? "").split(",");
  if (!skill || !iteration || suffixes.length < 2) {
    console.error("Usage: node scripts/flip-report.js --skill SKILL --iteration N --suffixes ,p1,p2");
    console.error("Needs at least two runs; an empty entry means the plain-named benchmark.json.");
    process.exit(1);
  }

  const runs = suffixes.map((suffix) => readRun(skill, iteration, suffix.trim()));

  const instruments = new Set(
    runs.map((run) => run.benchmark.evals.map((e) => `${e.id}:${e.fingerprint}`).sort().join("|"))
  );
  const regimes = new Set(
    runs.map((run) => `${run.benchmark.grading_model}/${run.benchmark.grading_effort || "default"}`)
  );
  console.log(`Runs: ${runs.map((r) => `${r.label} (${r.score})`).join(", ")}`);
  if (instruments.size > 1 || regimes.size > 1) {
    console.log(
      `\n⛔ These runs are not a variance probe — ${instruments.size > 1 ? "their eval definitions differ" : "they were graded under different regimes"}.`
    );
    console.log(`   A flip between them is a change of instrument, not grader noise.`);
    process.exit(2);
  }
  console.log(`Regime: ${[...regimes][0]} · same instrument across all ${runs.length} runs.\n`);

  const { flipped, stableFailures } = flipReport(runs);
  const total = runs[0].slots;
  console.log(`**${flipped.length} of ${total} slots flip; ${stableFailures} fail in all ${runs.length}.**\n`);

  console.log(`| Eval | Assertion | ${runs.map((r) => r.label).join(" ")} |`);
  console.log(`|---|---|---|`);
  for (const flip of flipped) {
    console.log(
      `| ${flip.evalNumber} | \`${flip.assertion}\` | ${flip.pattern.map((f) => (f ? "F" : "p")).join(" ")} |`
    );
  }

  console.log(`\nBy assertion:`);
  for (const [assertion, count] of flipsByAssertion(flipped)) {
    console.log(`  ${String(count).padStart(2)}×  ${assertion}`);
  }
}

if (require.main === module) main();

module.exports = { flipReport, flipsByAssertion };
