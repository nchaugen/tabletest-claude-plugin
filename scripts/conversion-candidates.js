#!/usr/bin/env node
/**
 * Which of an eval's slots can move off the LLM grader, and which cannot yet.
 *
 * A slot converts when its relation has agreed with the grader on every stored draw *and* the
 * grader has had it both ways. A relation that has only ever said PASS has never been shown to
 * catch anything, so its agreement is a statement about the draws rather than about the relation —
 * that is the rule this encodes, and it is the one a hand-read of the report keeps forgetting.
 *
 * Reads only what is already on disk: no API key, no generation, no cost. Run it before buying a
 * regrade — [[diff-checkers-over-the-corpus-before-regrading]].
 *
 *   node scripts/conversion-candidates.js --eval 14 [--skill tabletest]
 */

const path = require("path");
const { checkers } = require("./assertions.js");
const { relationsFor } = require("./shape-relations.js");
const { evalDir, evaluateDraw, selfGradedAssertions, storedDraws, sutParameterNames } = require("./shape-report.js");

/** Fewer than this many stored comparisons says more about the corpus than about the relation. */
const READABLE_MINIMUM = 2;

/**
 * One slot's case for conversion, from its rows across every stored draw.
 *
 * `rows` are `evaluateDraw`'s output for this relation, one per draw; `graded: null` means the draw
 * carries no verdict for it and is not a comparison at all.
 */
function candidacy(id, rows, { judgement = null, converted = false } = {}) {
  const compared = rows.filter((row) => row.graded !== null);
  const agreeing = compared.filter((row) => row.agrees).length;
  const passes = compared.filter((row) => row.graded === true).length;
  const fails = compared.length - passes;
  const base = { id, comparisons: compared.length, agreeing, passes, fails };

  if (converted) {
    return { ...base, verdict: "converted", why: "already graded by this relation" };
  }
  if (judgement) {
    return { ...base, verdict: "advisory", why: `carries an exemption it cannot decide: ${judgement}` };
  }
  if (compared.length < READABLE_MINIMUM) {
    return { ...base, verdict: "too-few", why: `${compared.length} stored comparison(s) is too few to read` };
  }
  if (agreeing < compared.length) {
    return { ...base, verdict: "disagrees", why: `disagrees on ${compared.length - agreeing} of ${compared.length} draws` };
  }
  if (passes === 0 || fails === 0) {
    return {
      ...base,
      verdict: "one-directional",
      why: `the grader has only ever said ${passes === 0 ? "FAIL" : "PASS"} here, so agreement shows nothing was caught`,
    };
  }
  return { ...base, verdict: "convert", why: `agrees on all ${compared.length}, with ${passes} PASS and ${fails} FAIL` };
}

const ORDER = ["convert", "disagrees", "one-directional", "too-few", "advisory", "converted"];

/** Convertible slots first; the rest grouped by why they were withheld. */
function rank(candidates) {
  return [...candidates].sort((a, b) => ORDER.indexOf(a.verdict) - ORDER.indexOf(b.verdict));
}

/**
 * The verdicts of whichever implementation would actually grade this slot.
 *
 * A hand-written checker in `assertions.js` wins over the relation, so measuring the relation would
 * describe code that is not doing the grading. Checkers needing the file list (a build-file
 * dependency, say) cannot be evaluated from a concatenated source and are reported as unevaluable
 * rather than guessed at.
 */
function gradingVerdicts(id, draws, relationRows, evalSlug) {
  const checker = checkers[id];
  if (!checker) return { rows: relationRows, path: "relation" };
  try {
    const rows = draws.map((draw, index) => {
      const { passed } = checker({ fileContent: draw.source, allFiles: [], evalSlug });
      const graded = relationRows[index] ? relationRows[index].graded : null;
      return { id, holds: passed, graded, agrees: graded === null ? null : graded === passed };
    });
    return { rows, path: "checker" };
  } catch {
    return { rows: [], path: "unevaluable" };
  }
}

function parseArgs(argv) {
  const args = { skill: "tabletest", eval: null };
  for (let i = 0; i < argv.length; i++) {
    if (argv[i] === "--skill") args.skill = argv[++i];
    else if (argv[i] === "--eval") args.eval = Number(argv[++i]);
  }
  return args;
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  if (!args.eval) {
    console.error("Usage: node scripts/conversion-candidates.js --eval N [--skill tabletest]");
    process.exit(1);
  }

  const authored = relationsFor(args.eval);
  if (!authored) {
    console.error(`No relations authored for eval ${args.eval}`);
    process.exit(2);
  }
  const dir = evalDir(args.skill, args.eval);
  if (!dir) {
    console.error(`No eval ${args.eval} under evals/${args.skill}`);
    process.exit(2);
  }

  const slug = path.basename(dir);
  const draws = storedDraws(args.skill, slug).filter((draw) => !draw.staleGrading);
  const context = { sutParameters: sutParameterNames(dir, authored.call) };
  const evaluations = draws.map((draw) => evaluateDraw(draw, authored.relations, context));
  const converted = selfGradedAssertions(dir);

  const candidates = authored.relations.map((relation) => {
    const relationRows = evaluations.map((rows) => rows.find((row) => row.id === relation.id)).filter(Boolean);
    const { rows, path: gradedBy } = gradingVerdicts(relation.id, draws, relationRows, slug.replace(/^eval-\d+-/, ""));
    return {
      ...candidacy(relation.id, rows, { judgement: relation.judgement, converted: converted.has(relation.id) }),
      gradedBy,
    };
  });

  const width = Math.max(...candidates.map((one) => one.id.length));
  console.log(`${slug} — ${draws.length} stored draw(s)\n`);
  for (const one of rank(candidates)) {
    const counts = `${one.agreeing}/${one.comparisons} agree, ${one.passes}P ${one.fails}F`;
    const via = one.gradedBy === "checker" ? " [hand-written checker]" : "";
    console.log(`${one.verdict.padEnd(16)}${one.id.padEnd(width + 2)}${counts.padEnd(24)}${one.why}${via}`);
  }
}

if (require.main === module) main();

module.exports = { candidacy, rank, parseArgs, gradingVerdicts, READABLE_MINIMUM };
