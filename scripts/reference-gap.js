#!/usr/bin/env node
/**
 * What the reference answer does that the stored draws do not — ranked by how much one draw would
 * tell you.
 *
 * `shape-report.js` answers "does this instrument agree with the grader?", one eval at a time. This
 * answers the question that actually generates work: **across every eval, which behaviours does the
 * hand-authored reference show that agents reliably fail to produce?** Every relation is already
 * computed over every stored draw, so the comparison costs nothing and spans several skill states.
 *
 * **Ranking is by base-rate extremity, not by size of gap, and that is the whole point.** A slot at
 * 5 of 11 is a coin flip: a repair that makes it pass has told you nothing, because a re-run at the
 * *same* skill state would have moved it anyway. eval-15 moves about six slots between two runs of
 * one skill state, and its four widest "gaps" are exactly the four slots that gotcha names as coin
 * flips — 2/10, 3/10, 3/10, 4/10 (`gotchas/eval-15-run-variance-is-six-slots.md`). A slot at 0 of 11
 * has never passed at any stored skill state, so a single draw that passes it is evidence. Sorting
 * by gap width puts the unreadable slots on top; sorting by extremity puts the readable ones there.
 *
 * **This is generation variance, and no checker reduces it.** Converting a slot to a deterministic
 * checker removes the grader's judgement — worth doing, and `docs/assertion-triage.md` prefers it —
 * but the answer still differs run to run, so the base rate is unchanged. Slot selection is the only
 * lever on this floor.
 *
 *   node scripts/reference-gap.js [--skill tabletest] [--band target] [--json]
 */

const path = require("path");

const { evalDir, evaluateDraw, storedDraws, sutParameterNames } = require("./shape-report.js");
const { authoredEvals, relationsFor } = require("./shape-relations.js");

/**
 * Where a base rate stops being readable from one draw.
 *
 * Taken from the one per-eval variance measurement there is: eval-15's problem slots sit at 2/10 to
 * 5/10 and move between identical runs, while eval-18's premium slots at 0/11 have never passed in
 * any stored run and are informative on a single draw. The boundary is set just above eval-18's
 * worst target (2 of 11, 0.18) and below eval-15's best coin flip (2 of 10, 0.20) — the two
 * populations very nearly touch, so the band is a guide and the count beside it is the real evidence.
 */
const TARGET_CEILING = 0.19;
const SATURATED_FLOOR = 0.8;

/** How readable one draw is: how far the base rate sits from a coin flip. */
function extremity(rate) {
  return Math.abs(rate - 0.5);
}

/**
 * Band order for reporting, which is not the same as extremity order.
 *
 * A saturated slot is as far from a coin flip as a target one and is the opposite of a finding —
 * every draw already does it. Ranking on extremity alone interleaves the two and buries eval-18's
 * 1-of-11 slots under forty rows of 11/11.
 */
const BAND_ORDER = { target: 0, "coin-flip": 1, saturated: 2 };

/** What a slot is worth spending a draw on. */
function band(rate) {
  if (rate <= TARGET_CEILING) return "target";
  if (rate >= SATURATED_FLOOR) return "saturated";
  return "coin-flip";
}

/**
 * Every relation of one eval, as the reference's verdict against the draws' base rate.
 *
 * Advisory relations are dropped: their verdict is a candidate rather than a result, so a base rate
 * over them would count guesses. A relation the reference *fails* is a transcription bug, not a gap
 * — it is reported as such rather than ranked, because the fix is to the relation.
 */
function gapsForEval(number, skill) {
  const authored = relationsFor(number);
  const dir = evalDir(skill, number);
  if (!authored || !dir) return { slug: null, gaps: [], broken: [] };

  const slug = path.basename(dir);
  const draws = storedDraws(skill, slug);
  const reference = draws.find((draw) => draw.isReference);
  if (!reference) return { slug, gaps: [], broken: [] };

  const context = { sutParameters: sutParameterNames(dir, authored.call) };
  const referenceRows = evaluateDraw(reference, authored.relations, context);
  const drawRows = draws.filter((draw) => !draw.isReference).map((draw) => evaluateDraw(draw, authored.relations, context));

  const gaps = [];
  const broken = [];
  for (const row of referenceRows) {
    if (row.advisory) continue;
    if (!row.holds) {
      broken.push({ eval: number, slug, id: row.id, evidence: row.evidence });
      continue;
    }
    const peers = drawRows.map((rows) => rows.find((one) => one.id === row.id)).filter((one) => one && !one.advisory);
    if (peers.length === 0) continue;
    const held = peers.filter((one) => one.holds).length;
    const rate = held / peers.length;
    gaps.push({ eval: number, slug, id: row.id, held, of: peers.length, rate, band: band(rate), extremity: extremity(rate) });
  }
  return { slug, gaps, broken };
}

/** Every eval's gaps, ranked most readable first, then widest first within equal readability. */
function rankedGaps(skill) {
  const gaps = [];
  const broken = [];
  for (const number of authoredEvals().map(Number).sort((a, b) => a - b)) {
    const one = gapsForEval(number, skill);
    gaps.push(...one.gaps);
    broken.push(...one.broken);
  }
  gaps.sort(
    (a, b) => BAND_ORDER[a.band] - BAND_ORDER[b.band] || b.extremity - a.extremity || b.of - a.of,
  );
  return { gaps, broken };
}

function parseArgs(argv) {
  const args = { skill: "tabletest", band: null, json: false };
  for (let i = 0; i < argv.length; i++) {
    if (argv[i] === "--skill") args.skill = argv[++i];
    else if (argv[i] === "--band") args.band = argv[++i];
    else if (argv[i] === "--json") args.json = true;
  }
  return args;
}

const BAND_NOTE = {
  target: "never or almost never produced — one draw that passes it is evidence",
  "coin-flip": "moves between identical runs; two draws minimum, and prefer the narration to the score",
  saturated: "already produced by nearly every draw; no readable headroom",
};

function main() {
  const args = parseArgs(process.argv.slice(2));
  const { gaps, broken } = rankedGaps(args.skill);
  // Saturated slots are listed only when asked for. They are the largest group and the least
  // actionable — knowing an eval has thirty of them is a fact about the eval, not a row to read.
  const shown = args.band ? gaps.filter((one) => one.band === args.band) : gaps.filter((one) => one.band !== "saturated");

  if (args.json) {
    console.log(JSON.stringify({ skill: args.skill, gaps: shown, broken }, null, 2));
    return;
  }

  console.log(`\n${args.skill}: ${gaps.length} relations the reference satisfies, over ${new Set(gaps.map((one) => one.eval)).size} evals`);
  console.log(`Ranked by how far the draws' base rate sits from a coin flip — most readable first.\n`);
  console.log(`${"eval".padEnd(6)}${"draws".padEnd(9)}${"band".padEnd(11)}relation`);
  console.log("-".repeat(78));
  let lastBand = null;
  for (const one of shown) {
    if (one.band !== lastBand) {
      console.log(`${" ".repeat(26)}— ${one.band}: ${BAND_NOTE[one.band]}`);
      lastBand = one.band;
    }
    console.log(`${String(one.eval).padEnd(6)}${`${one.held}/${one.of}`.padEnd(9)}${one.band.padEnd(11)}${one.id}`);
  }

  const targets = gaps.filter((one) => one.band === "target");
  const saturated = gaps.filter((one) => one.band === "saturated");
  console.log(
    `\n${targets.length} target(s) across ${new Set(targets.map((one) => one.eval)).size} evals — the slots a single draw can read.`,
  );
  if (!args.band && saturated.length > 0) {
    const byEval = {};
    for (const one of saturated) byEval[one.eval] = (byEval[one.eval] || 0) + 1;
    const listed = Object.entries(byEval).sort((a, b) => b[1] - a[1]).map(([e, n]) => `eval-${e} ${n}`);
    console.log(`${saturated.length} saturated, not listed (--band saturated to see them): ${listed.join(", ")}`);
  }
  console.log(`Read the artefact before acting on any of them: node scripts/shape-report.js --eval <n>`);

  if (broken.length > 0) {
    console.log(`\n⚠️  ${broken.length} relation(s) the reference itself fails — a transcription bug, not a gap:`);
    for (const one of broken) console.log(`  eval-${one.eval} ${one.id}: ${one.evidence}`);
  }
}

if (require.main === module) main();

module.exports = { band, extremity, gapsForEval, parseArgs, rankedGaps, SATURATED_FLOOR, TARGET_CEILING };
