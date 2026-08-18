#!/usr/bin/env node
/**
 * Grading an assertion with the relation that was written to measure it.
 *
 * A relation restates one assertion's question as arithmetic over the parsed answer
 * (`shape-relations.js`). Where a relation has agreed with the grader on every stored draw, the
 * LLM judgement on that slot is buying variance and cost and nothing else, so the slot moves here:
 * the relation becomes the verdict, and `eval.json` marks the assertion `"type": "deterministic"`.
 *
 * **The relation grades, rather than a transcription of it.** Hand-copying the rule into a checker
 * would leave two implementations of one judgement free to drift, and the agreement figure that
 * justified the conversion would then describe code that is no longer the code doing the grading.
 *
 * This module sits above `answer-shape.js` and `shape-relations.js`, which is why it is not inside
 * `assertions.js`: the shape parser already depends on the checkers' own parsing, and putting the
 * bridge there would close the cycle.
 *
 * **Two refusals, both loud.** A relation carrying a `judgement` note has an exemption it cannot
 * decide — its verdict is advisory and must never grade. And an assertion with no relation for that
 * eval fails rather than passing vacuously, because a slot silently graded by nothing is worse than
 * one graded badly ([[deterministic-checkers-rot-silently]]).
 */

const { answerShape } = require("./answer-shape.js");
const { relationsFor } = require("./shape-relations.js");
const { evalDir, sutParameterNames } = require("./shape-report.js");

/**
 * The verdict of `assertionId`'s relation for `evalNumber` over one answer's source.
 *
 * Returns the checker protocol — `{ passed, evidence }` — so a caller cannot tell a relation-backed
 * slot from a hand-written one.
 *
 * **The context is not optional.** Several relations take `(shape, context)` and read the system
 * under test's own parameter names from it; evaluating them without it silently changes the verdict,
 * which is how eval-18's `premium-claim-boundary` was graded FAIL against a stored PASS on
 * 2026-08-18. It is built here exactly as `shape-report.js` builds it, so the slot that grades and
 * the figure that justified converting it are the same computation.
 */
function relationChecker(assertionId, evalNumber, source, skill = "tabletest") {
  const authored = relationsFor(evalNumber);
  const relation = authored && authored.relations.find((one) => one.id === assertionId);

  if (!relation) {
    return {
      passed: false,
      evidence:
        `No relation for "${assertionId}" on eval ${evalNumber}. Mark an assertion deterministic ` +
        "only once a relation restates it and agrees with the grader over the stored draws.",
    };
  }

  if (relation.judgement) {
    return {
      passed: false,
      evidence:
        `"${assertionId}" carries an exemption its relation cannot decide (${relation.judgement}), ` +
        "so its verdict is advisory and must not grade. Keep this assertion on the LLM grader.",
    };
  }

  const directory = evalDir(skill, evalNumber);
  const context = { sutParameters: directory ? sutParameterNames(directory, authored.call) : [] };
  const verdict = relation.evaluate(answerShape(source), context);
  return { passed: verdict.holds, evidence: verdict.evidence };
}

/** Whether `assertionId` can be graded by a relation on this eval, used to pick a grading path. */
function hasRelationChecker(assertionId, evalNumber) {
  const authored = relationsFor(evalNumber);
  const relation = authored && authored.relations.find((one) => one.id === assertionId);
  return Boolean(relation && !relation.judgement);
}

module.exports = { relationChecker, hasRelationChecker };
