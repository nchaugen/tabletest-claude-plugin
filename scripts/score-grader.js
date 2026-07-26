#!/usr/bin/env node
// Scores a grading run for ACCURACY against docs/grader-answer-key.json.
//
// Every other grader measurement in this repo measures precision — whether the
// grader agrees with itself across runs. This measures whether it is right.
// Majority voting cannot fix a verdict that is reproducibly wrong, which is why
// accuracy needs its own instrument.
//
// Usage:
//   node scripts/score-grader.js --iteration 40 [--grading-suffix haiku] [--json]
//
// Reads iterations/<skill>/iteration-N/<eval-dir>/grading[-suffix].json.

const fs = require("fs");
const path = require("path");

const REPO_ROOT = path.resolve(__dirname, "..");
const KEY_PATH = path.join(REPO_ROOT, "docs", "grader-answer-key.json");

function parseArgs(argv) {
  const args = { iteration: null, skill: "tabletest", gradingSuffix: null, json: false, force: false };
  for (let i = 2; i < argv.length; i++) {
    switch (argv[i]) {
      case "--iteration": args.iteration = argv[++i]; break;
      case "--skill": args.skill = argv[++i]; break;
      case "--grading-suffix": args.gradingSuffix = argv[++i]; break;
      case "--json": args.json = true; break;
      case "--force": args.force = true; break;
      default:
        console.error(`Unknown argument: ${argv[i]}`);
        process.exit(2);
    }
  }
  if (!args.iteration) {
    console.error("Usage: node scripts/score-grader.js --iteration N [--skill S] [--grading-suffix X] [--json] [--force]");
    process.exit(2);
  }
  return args;
}

/** Maps an answer-key eval number onto the iteration's directory for that eval. */
function evalDirsByNumber(iterationDir) {
  const byNumber = new Map();
  for (const entry of fs.readdirSync(iterationDir)) {
    const match = entry.match(/^eval-(\d+)/);
    if (!match) continue;
    if (!fs.statSync(path.join(iterationDir, entry)).isDirectory()) continue;
    byNumber.set(Number(match[1]), entry);
  }
  return byNumber;
}

function loadGrading(iterationDir, evalDir, gradingSuffix) {
  const file = gradingSuffix ? `grading-${gradingSuffix}.json` : "grading.json";
  const full = path.join(iterationDir, evalDir, file);
  if (!fs.existsSync(full)) return null;
  return JSON.parse(fs.readFileSync(full, "utf-8"));
}

/**
 * The key's verdicts were read off one specific set of stored outputs, so they say nothing about a
 * different iteration's. Scoring the wrong iteration does not fail — it returns a confident
 * percentage assembled from verdicts about outputs nobody graded, which is the worst shape an error
 * can take in this repo. The key states the binding in `scored_against`; this enforces it.
 */
function keyAppliesTo(key, skill, iteration) {
  const boundTo = key.scored_against && key.scored_against.iteration;
  if (!boundTo) return { ok: true };
  const expected = `iterations/${skill}/iteration-${iteration}`;
  return boundTo === expected ? { ok: true } : { ok: false, boundTo, requested: expected };
}

function score(args) {
  const key = JSON.parse(fs.readFileSync(KEY_PATH, "utf-8"));

  const applies = keyAppliesTo(key, args.skill, args.iteration);
  if (!applies.ok && !args.force) {
    console.error(`The answer key was read against ${applies.boundTo}, not ${applies.requested}.`);
    console.error(`Its verdicts describe those stored outputs; a different iteration's outputs are`);
    console.error(`different solutions, so scoring them against this key is meaningless.`);
    console.error(`Re-read the key against the outputs you mean, or pass --force if you know why.`);
    process.exit(2);
  }
  if (!applies.ok && args.force) {
    console.error(`WARNING: --force — key is bound to ${applies.boundTo}; this score is not evidence.\n`);
  }

  const iterationDir = path.join(REPO_ROOT, "iterations", args.skill, `iteration-${args.iteration}`);
  if (!fs.existsSync(iterationDir)) {
    console.error(`No such iteration directory: ${iterationDir}`);
    process.exit(1);
  }
  const dirs = evalDirsByNumber(iterationDir);

  const rows = [];
  for (const entry of key.entries) {
    const evalDir = dirs.get(entry.eval);
    if (!evalDir) {
      rows.push({ ...entry, outcome: "eval-missing", actual: null });
      continue;
    }
    const grading = loadGrading(iterationDir, evalDir, args.gradingSuffix);
    if (!grading) {
      rows.push({ ...entry, outcome: "grading-missing", actual: null });
      continue;
    }
    const result = grading.assertions.find((a) => a.id === entry.assertion);
    if (!result) {
      rows.push({ ...entry, outcome: "not-hosted", actual: null });
      continue;
    }
    const actual = Boolean(result.passed);
    if (entry.expected === null) {
      rows.push({ ...entry, outcome: "unscored", actual, evidence: result.evidence });
      continue;
    }
    rows.push({
      ...entry,
      actual,
      evidence: result.evidence,
      outcome: actual === entry.expected ? "correct" : "wrong",
    });
  }
  return rows;
}

function main() {
  const args = parseArgs(process.argv);
  const rows = score(args);
  
  const scored = rows.filter((r) => r.outcome === "correct" || r.outcome === "wrong");
  const correct = scored.filter((r) => r.outcome === "correct");
  const wrong = scored.filter((r) => r.outcome === "wrong");
  const skipped = rows.filter((r) => !["correct", "wrong"].includes(r.outcome));
  
  if (args.json) {
    console.log(JSON.stringify({ rows, correct: correct.length, scored: scored.length }, null, 2));
    process.exit(0);
  }
  
  const label = args.gradingSuffix ? `grading-${args.gradingSuffix}.json` : "grading.json";
  console.log(`\nGrader accuracy — iteration ${args.iteration}, ${label}`);
  console.log(`Answer key: docs/grader-answer-key.json (${key_count(rows)} entries)\n`);
  
  for (const r of rows) {
    const mark = { correct: "  ok  ", wrong: " WRONG", unscored: "  --  " }[r.outcome] || "  ??  ";
    const want = r.expected === null ? "n/a" : r.expected ? "PASS" : "FAIL";
    const got = r.actual === null ? r.outcome : r.actual ? "PASS" : "FAIL";
    console.log(`${mark}  eval-${String(r.eval).padEnd(2)}  ${r.assertion.padEnd(38)} want ${want.padEnd(4)} got ${got}`);
    if (r.outcome === "wrong") {
      console.log(`        why the key says ${want}: ${r.basis}`);
      if (r.evidence) console.log(`        grader said: ${r.evidence}`);
    }
  }
  
  const rate = scored.length ? ((correct.length / scored.length) * 100).toFixed(0) : "0";
  console.log(`\n${correct.length}/${scored.length} correct (${rate}%)`);
  if (wrong.length) console.log(`wrong: ${wrong.map((r) => `${r.assertion}/${r.eval}`).join(", ")}`);
  if (skipped.length) {
    const byOutcome = {};
    for (const r of skipped) (byOutcome[r.outcome] = byOutcome[r.outcome] || []).push(`${r.assertion}/${r.eval}`);
    for (const [outcome, list] of Object.entries(byOutcome)) console.log(`${outcome}: ${list.join(", ")}`);
  }
  console.log("\nA disagreement is not automatically the grader's fault — re-read the artefact before");
  console.log("changing anything. The key is a reading, not an oracle.\n");
  
  function key_count(rows) { return rows.length; }
}

if (require.main === module) main();

module.exports = { keyAppliesTo };
