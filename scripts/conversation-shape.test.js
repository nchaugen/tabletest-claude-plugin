const test = require("node:test");
const assert = require("node:assert");
const fs = require("fs");
const os = require("os");
const path = require("path");
const { conversationShape, deliberationOutliers } = require("./conversation-shape.js");

function writeConversation(lines) {
  const file = path.join(fs.mkdtempSync(path.join(os.tmpdir(), "conv-")), "conversation.jsonl");
  fs.writeFileSync(file, lines.map((l) => JSON.stringify(l)).join("\n"));
  return file;
}

const assistant = (content) => ({ type: "assistant", message: { role: "assistant", content } });

test("counts thinking blocks, tool calls and total thinking", () => {
  const file = writeConversation([
    assistant([{ type: "thinking", thinking: "a".repeat(100) }]),
    assistant([{ type: "tool_use", name: "Read", input: {} }]),
    assistant([{ type: "thinking", thinking: "b".repeat(900) }]),
    assistant([{ type: "tool_use", name: "Write", input: {} }]),
  ]);
  const shape = conversationShape(file);
  assert.strictEqual(shape.blocks, 2);
  assert.strictEqual(shape.thinkingChars, 1000);
  assert.strictEqual(shape.largestBlock, 900);
  assert.strictEqual(shape.toolCalls, 2);
});

test("concentration is the share of thinking in the single largest block", () => {
  const file = writeConversation([
    assistant([{ type: "thinking", thinking: "a".repeat(200) }]),
    assistant([{ type: "thinking", thinking: "b".repeat(800) }]),
  ]);
  assert.strictEqual(conversationShape(file).concentration, 0.8);
});

test("a conversation with no thinking reports zero rather than dividing by zero", () => {
  const file = writeConversation([assistant([{ type: "tool_use", name: "Read", input: {} }])]);
  const shape = conversationShape(file);
  assert.strictEqual(shape.thinkingChars, 0);
  assert.strictEqual(shape.concentration, 0);
});

test("sums api_retry delays so retry cost can be told from budget overrun", () => {
  const file = writeConversation([
    { type: "system", subtype: "api_retry", retry_delay_ms: 515 },
    { type: "system", subtype: "api_retry", retry_delay_ms: 569 },
    assistant([{ type: "thinking", thinking: "x".repeat(50) }]),
  ]);
  const shape = conversationShape(file);
  assert.strictEqual(shape.retries, 2);
  assert.strictEqual(shape.retryDelayMs, 1084);
});

test("skips malformed lines instead of failing the scan", () => {
  const dir = fs.mkdtempSync(path.join(os.tmpdir(), "conv-"));
  const file = path.join(dir, "conversation.jsonl");
  fs.writeFileSync(file, `not json\n${JSON.stringify(assistant([{ type: "thinking", thinking: "ok" }]))}\n`);
  assert.strictEqual(conversationShape(file).blocks, 1);
});

test("outliers compare an eval to itself, never to other evals", () => {
  const rows = [
    // eval-15 thinks hard about everything; none of its runs is an outlier of its own history.
    { skill: "tt", eval: "eval-15", thinkingChars: 30000 },
    { skill: "tt", eval: "eval-15", thinkingChars: 32000 },
    { skill: "tt", eval: "eval-15", thinkingChars: 31000 },
    // eval-7 is small, so a 3x run is an outlier even though it is far below eval-15.
    { skill: "tt", eval: "eval-7", thinkingChars: 1000 },
    { skill: "tt", eval: "eval-7", thinkingChars: 1100 },
    { skill: "tt", eval: "eval-7", thinkingChars: 3300 },
  ];
  const flagged = deliberationOutliers(rows);
  assert.deepStrictEqual(flagged.map((f) => f.thinkingChars), [3300]);
});

test("an eval with fewer than three runs has no usable median and is not flagged", () => {
  const flagged = deliberationOutliers([
    { skill: "tt", eval: "eval-9", thinkingChars: 1000 },
    { skill: "tt", eval: "eval-9", thinkingChars: 90000 },
  ]);
  assert.deepStrictEqual(flagged, []);
});
