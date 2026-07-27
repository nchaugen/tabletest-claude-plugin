#!/usr/bin/env node
/**
 * What does each eval cost to run?
 *
 * Loop selection is documented by signal — which evals carry a failing target assertion — and never
 * by price, which leaves the impression that evals cost about the same. They do not: the spread is
 * roughly sevenfold, so two loops with equal signal can differ by an order of magnitude in spend. A
 * smoke loop of the two cheapest hosts is often minutes and small change against a full loop's
 * dollars, and that trade is invisible unless the prices are in front of you.
 *
 * Costs are averaged over every stored run that measured the eval, official and variant alike, so
 * the figures track the suite as it drifts rather than a number written into a document.
 *
 * Usage: node scripts/eval-costs.js --skill tabletest
 * Exit codes: 0 printed · 1 bad usage or nothing stored.
 */

const fs = require("fs");
const path = require("path");

/**
 * Mean generation cost, grading cost and wall-clock per eval, cheapest first.
 *
 * Grading is averaged only over the runs that recorded it: a benchmark predating that record, or one
 * rebuilt from older gradings, reports `grading: null` — unknown, not free — and an eval no run has
 * priced keeps `null` here rather than being reported as costless.
 */
function costsByEval(benchmarks) {
  const observations = new Map();
  for (const benchmark of benchmarks) {
    for (const entry of benchmark.evals ?? []) {
      if (!observations.has(entry.id)) observations.set(entry.id, []);
      observations.get(entry.id).push(entry.results);
    }
  }

  return [...observations]
    .map(([id, results]) => {
      const priced = results.filter((result) => result.grading_usage);
      const generation = mean(results.map((result) => result.cost_usd));
      const grading = priced.length ? mean(priced.map((result) => result.grading_usage.cost_usd)) : null;
      return {
        id,
        runs: results.length,
        generation,
        grading,
        total: generation + (grading ?? 0),
        seconds: Math.round(mean(results.map((result) => result.duration_ms)) / 1000),
      };
    })
    .sort((a, b) => a.total - b.total);
}

function mean(values) {
  return values.reduce((total, value) => total + value, 0) / values.length;
}

/** Every `benchmark.json` under an iterations tree, official iterations and variant runs alike. */
function storedBenchmarks(iterationsDir) {
  if (!fs.existsSync(iterationsDir)) return [];
  const found = [];
  const walk = (dir) => {
    for (const item of fs.readdirSync(dir, { withFileTypes: true })) {
      const full = path.join(dir, item.name);
      if (item.isDirectory()) walk(full);
      else if (item.name === "benchmark.json") found.push(JSON.parse(fs.readFileSync(full, "utf-8")));
    }
  };
  walk(iterationsDir);
  return found;
}

function parseArgs(argv) {
  const args = { skill: null };
  for (let i = 0; i < argv.length; i++) {
    if (argv[i] === "--skill") args.skill = argv[++i];
  }
  return args;
}

function money(amount) {
  return amount === null ? "     ?" : `$${amount.toFixed(2)}`.padStart(6);
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  if (!args.skill) {
    console.error("Usage: node scripts/eval-costs.js --skill SKILL");
    process.exit(1);
  }

  const iterationsDir = path.join(process.cwd(), "iterations", args.skill);
  const costs = costsByEval(storedBenchmarks(iterationsDir));
  if (!costs.length) {
    console.error(`No stored benchmarks under ${iterationsDir}.`);
    process.exit(1);
  }

  const width = Math.max(...costs.map((entry) => entry.id.length));
  console.log(`${"eval".padEnd(width)}    gen  grade  total   time  runs`);
  for (const entry of costs) {
    console.log(
      `${entry.id.padEnd(width)} ${money(entry.generation)} ${money(entry.grading)} ${money(entry.total)} ` +
        `${(entry.seconds + "s").padStart(6)} ${String(entry.runs).padStart(5)}`,
    );
  }

  const suite = costs.reduce((total, entry) => total + entry.total, 0);
  const cheapest = costs[0];
  const dearest = costs[costs.length - 1];
  console.log(
    `\nWhole suite $${suite.toFixed(2)}. Dearest eval (${dearest.id}) is ` +
      `${(dearest.total / cheapest.total).toFixed(1)}x the cheapest (${cheapest.id}).`,
  );
  console.log("Grading is a rounding error against generation — choose which evals to run, never how many assertions.");
}

if (require.main === module) main();

module.exports = { costsByEval, storedBenchmarks };
