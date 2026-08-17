#!/usr/bin/env node
/**
 * Reads every stored draw of one eval structurally and reports what its answers look like,
 * beside what the grader said about them.
 *
 * Why this exists: a score delta is evidence only for a slot with a stable prior record, and
 * eval-18's slots move ±6 between identical skill states. The relations in `shape-relations.js`
 * cost nothing, never flip, and can be computed over draws already paid for — so a repair's
 * mechanism becomes a boolean over every stored answer instead of one bought observation.
 *
 * This file is the edge: it finds the draws, reads the sources and gradings, and renders the
 * panel. The judgement lives in `shape-relations.js` and the parsing in `answer-shape.js`.
 *
 *   node scripts/shape-report.js --eval 18 [--skill tabletest] [--json]
 */

const fs = require("fs");
const path = require("path");

const { answerShape } = require("./answer-shape.js");
const { authoredEvals, relationsFor } = require("./shape-relations.js");

const repoRoot = path.join(__dirname, "..");

/**
 * The parameter names of the method under test, in order, read from the eval's own project.
 *
 * Positional literals in a fixture call mean nothing without these names, and hardcoding them
 * would let the relations drift from the eval definition they are supposed to restate. An eval
 * whose project ships no implementation — the test-driven ones — simply has none, and a relation
 * needing held values must say so rather than guess.
 */
function sutParameterNames(evalDirectory, callName) {
  if (!callName) return [];
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
      advisory: Boolean(relation.advisory),
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

/**
 * Where the mechanical verdict and the grader disagree — the only rows worth a human read.
 *
 * An advisory relation is excluded: it decides one condition of an assertion that has an exemption
 * this cannot read, so a divergence there says nothing about either side. They are reported
 * separately by `advisoryDivergences` rather than counted.
 */
function disagreements(draws, evaluations) {
  const found = [];
  draws.forEach((draw, index) => {
    for (const row of evaluations[index]) {
      if (row.agrees === false && !row.advisory) found.push({ draw: draw.label, ...row });
    }
  });
  return found;
}

/** Divergences on advisory relations, reported without being counted against either side. */
function advisoryDivergences(draws, evaluations) {
  const found = [];
  draws.forEach((draw, index) => {
    for (const row of evaluations[index]) {
      if (row.agrees === false && row.advisory) found.push({ draw: draw.label, ...row });
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
  const authored = relationsFor(args.eval);
  if (!authored) {
    console.error(`No relations authored for eval ${args.eval}. Available: ${authoredEvals().join(", ")}`);
    process.exit(2);
  }
  const relations = authored.relations;
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
  const context = { sutParameters: sutParameterNames(dir, authored.call) };
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
  const counted = evaluations.flat().filter((row) => row.graded !== null && !row.advisory);
  const agreed = counted.filter((row) => row.agrees).length;
  console.log(
    `\n${agreed} of ${counted.length} graded comparisons agree (${Math.round((100 * agreed) / Math.max(counted.length, 1))}%),` +
      ` ${conflicts.length} disagreement(s):`,
  );
  for (const one of conflicts) {
    console.log(`  ${one.draw} ${one.id}: mechanical ${mark(one.holds)}, grader ${mark(one.graded)} — ${one.evidence}`);
  }

  const advisory = advisoryDivergences(draws, evaluations);
  if (advisory.length > 0) {
    console.log(`\n${advisory.length} divergence(s) on advisory relations, not counted above:`);
    for (const one of advisory) {
      console.log(`  ${one.draw} ${one.id}: mechanical ${mark(one.holds)}, grader ${mark(one.graded)} — ${one.evidence}`);
    }
  }

  const judged = relations.filter((relation) => relation.judgement);
  if (judged.length > 0) {
    console.log(`\nRelations carrying an exemption this cannot decide:`);
    for (const relation of judged) console.log(`  ${relation.id}: ${relation.judgement}`);
  }
}

if (require.main === module) main();

module.exports = {
  advisoryDivergences,
  disagreements,
  evalDir,
  evaluateDraw,
  findFiles,
  parseArgs,
  storedDraws,
  sutParameterNames,
};
