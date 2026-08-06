#!/usr/bin/env node
/**
 * The deliberation shape of every archived run, read from `conversation.jsonl` rather than the
 * narration distilled from it.
 *
 * `iteration-72`'s eval-25 spent 886 of its 900 seconds inside **one** thinking block of 34,021
 * characters after nine tool calls, and delivered nothing. That signature — few tool calls, one
 * block carrying nearly all the deliberation — is what an agent negotiating with itself looks like,
 * and it is invisible in a score and invisible in `narration.md`'s prose. This finds every other run
 * with the same shape.
 *
 * **Block structure survives where thinking text does not.** Runs made before the CLI was asked for
 * summaries carry blocks with token counts and no text, so 74 narrations read as empty while their
 * conversations still answer "how was the deliberation distributed".
 *
 * Usage: node scripts/conversation-shape.js [--skill tabletest] [--outliers]
 */

const fs = require("fs");
const path = require("path");

/** Thinking blocks, tool calls and retry cost of one run, in the order the transcript records them. */
function conversationShape(file) {
  const blocks = [];
  const tools = [];
  const retries = [];
  for (const line of fs.readFileSync(file, "utf-8").split("\n")) {
    if (!line.trim()) continue;
    let event;
    try { event = JSON.parse(line); } catch { continue; }
    if (event.subtype === "api_retry") {
      retries.push(Number(event.retry_delay_ms) || 0);
    }
    const content = event.message && event.message.content;
    if (!Array.isArray(content)) continue;
    for (const part of content) {
      if (part.type === "thinking") blocks.push((part.thinking || "").length);
      if (part.type === "tool_use") tools.push(part.name);
    }
  }
  const total = blocks.reduce((sum, size) => sum + size, 0);
  const largest = blocks.length ? Math.max(...blocks) : 0;
  return {
    blocks: blocks.length,
    thinkingChars: total,
    largestBlock: largest,
    // How much of the run's thinking sits in its single biggest block. A run that reasons in steps
    // spreads this; a run that enters one block and does not come out approaches 1.
    concentration: total ? largest / total : 0,
    toolCalls: tools.length,
    retries: retries.length,
    retryDelayMs: retries.reduce((sum, delay) => sum + delay, 0),
  };
}

/**
 * Runs that deliberated far more than the same eval usually needs.
 *
 * **Concentration is not the signal, and measuring it was the first thing tried.** One dominant
 * thinking block is the *normal* shape: the median run concentrates 0.84 of its thinking in one
 * block under `tabletest` and 0.97 under `spec-by-example`, where the task is a single document.
 * Flagging high concentration returns mostly healthy runs.
 *
 * What separates `iteration-72`'s 900s timeout from its healthy neighbours is volume against the
 * eval's own history — 35k characters where eval-25 normally needs 17k. Comparing an eval only to
 * itself is what makes this readable: eval-15 legitimately thinks twice as hard as eval-7 about
 * everything, so a corpus-wide threshold would just rank the evals by difficulty.
 */
function deliberationOutliers(rows, factor = 1.5) {
  const byEval = new Map();
  for (const row of rows) {
    if (!row.thinkingChars) continue;
    const key = `${row.skill}/${row.eval}`;
    if (!byEval.has(key)) byEval.set(key, []);
    byEval.get(key).push(row);
  }
  const flagged = [];
  for (const runs of byEval.values()) {
    if (runs.length < 3) continue;
    const sorted = [...runs].map((r) => r.thinkingChars).sort((a, b) => a - b);
    const median = sorted[Math.floor(sorted.length / 2)];
    for (const run of runs) {
      if (run.thinkingChars >= median * factor) flagged.push({ ...run, median, ratio: run.thinkingChars / median });
    }
  }
  return flagged.sort((a, b) => b.ratio - a.ratio);
}

function scan(root, skillFilter) {
  const rows = [];
  const iterationsRoot = path.join(root, "iterations");
  for (const skill of fs.readdirSync(iterationsRoot).sort()) {
    if (skillFilter && skill !== skillFilter) continue;
    const skillDir = path.join(iterationsRoot, skill);
    if (!fs.statSync(skillDir).isDirectory()) continue;
    for (const iteration of fs.readdirSync(skillDir).sort()) {
      const iterationDir = path.join(skillDir, iteration);
      if (!fs.statSync(iterationDir).isDirectory()) continue;
      for (const evalName of fs.readdirSync(iterationDir).sort()) {
        const file = path.join(iterationDir, evalName, "conversation.jsonl");
        if (!fs.existsSync(file)) continue;
        const timingFile = path.join(iterationDir, evalName, "timing.json");
        const timing = fs.existsSync(timingFile)
          ? JSON.parse(fs.readFileSync(timingFile, "utf-8")) : {};
        rows.push({
          skill,
          iteration: Number(iteration.replace("iteration-", "")),
          eval: evalName,
          durationMs: timing.duration_ms,
          failureKind: timing.failure_kind || "",
          ...conversationShape(file),
        });
      }
    }
  }
  return rows;
}

function main() {
  const args = process.argv.slice(2);
  const skillIndex = args.indexOf("--skill");
  const rows = scan(process.cwd(), skillIndex === -1 ? null : args[skillIndex + 1]);
  const shown = args.includes("--outliers") ? deliberationOutliers(rows) : rows;
  console.log(["skill", "it", "eval", "blocks", "chars", "largest", "conc", "tools", "retries", "retry_ms", "dur_s", "failure"].join("\t"));
  for (const row of shown.sort((a, b) => b.concentration - a.concentration)) {
    console.log([
      row.skill, row.iteration, row.eval, row.blocks, row.thinkingChars, row.largestBlock,
      row.concentration.toFixed(2), row.toolCalls, row.retries, row.retryDelayMs,
      row.durationMs ? Math.round(row.durationMs / 1000) : "", row.failureKind,
    ].join("\t"));
  }
  console.error(`\n${shown.length} of ${rows.length} runs shown`);
}

if (require.main === module) main();

module.exports = { conversationShape, deliberationOutliers, scan };
