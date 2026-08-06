const test = require("node:test");
const assert = require("node:assert");
const { parseNarration, decisionEvents, decisionOrder, rowDecisionShortfall, pipeTables } = require("./narration-corpus.js");

const NARRATION = `# Narration — 9

Preamble prose that is not a segment.

_[~120 thinking tokens (4 deltas)]_

> (thinking) I'm grouping the roles so one row covers all three actions. Then I'm settling on scenario names that describe the conditions.

Said aloud between blocks.

**Write /tmp/work/src/test/java/Example.java (20 lines)**

_[~1,200 thinking tokens (12 deltas)]_

> (thinking) Actually I want separate tables rather than one table.
`;

test("parses thinking, spoken text and tool actions in order", () => {
  const segments = parseNarration(NARRATION);
  assert.deepStrictEqual(
    segments.map((segment) => segment.channel),
    ["said", "thinking", "said", "action", "thinking"],
  );
  assert.strictEqual(segments[1].tokens, 120);
  assert.strictEqual(segments[4].tokens, 1200);
  assert.strictEqual(segments[3].tool, "Write");
});

test("strips the thinking marker from the block body", () => {
  const [, thinking] = parseNarration(NARRATION);
  assert.ok(thinking.text.startsWith("I'm grouping the roles"));
});

test("orders decision events by sentence position across the whole narration", () => {
  const events = decisionEvents(parseNarration(NARRATION));
  const kinds = events.map((event) => event.kind);
  assert.ok(kinds.includes("rows-decided"));
  assert.ok(kinds.includes("naming-decided"));
  assert.ok(
    events.find((event) => event.kind === "rows-decided").position <
      events.find((event) => event.kind === "naming-decided").position,
  );
});

test("a sentence that reverses is counted as a reversal, not as the structure it reaches", () => {
  const events = decisionEvents(parseNarration(NARRATION));
  assert.strictEqual(events.at(-1).kind, "reversal");
});

test("classifies decision order only when both decisions are reached", () => {
  assert.strictEqual(decisionOrder(decisionEvents(parseNarration(NARRATION))), "rows-first");

  const namingOnly = `_[~10 thinking tokens (1 deltas)]_\n\n> (thinking) I'm settling on scenario names first.\n`;
  assert.strictEqual(decisionOrder(decisionEvents(parseNarration(namingOnly))), "unclassified");
});

test("naming before row structure classifies as naming-first", () => {
  const narration = `_[~10 thinking tokens (1 deltas)]_

> (thinking) Now I'm naming the scenarios by their conditions. For overnight there are three conditions. I will use a value set for the ones that match.
`;
  assert.strictEqual(decisionOrder(decisionEvents(parseNarration(narration))), "naming-first");
});

test("deciding table count is not deciding row structure", () => {
  const narration = `_[~10 thinking tokens (1 deltas)]_

> (thinking) I should split these into separate tables rather than forcing them into one. Now I'm naming the scenarios by their conditions.
`;
  const events = decisionEvents(parseNarration(narration));
  assert.deepStrictEqual(events.map((event) => event.kind), ["tables-decided", "naming-decided"]);
  assert.strictEqual(decisionOrder(events), "unclassified");
});

test("shortfall counts the tables a narration produced without deciding their rows", () => {
  const narration = `_[~10 thinking tokens (1 deltas)]_

> (thinking) For the first table I can use value sets to show the cost never varies. The second table is destination-gated with a flat cost when available.
`;
  const events = decisionEvents(parseNarration(narration));
  assert.strictEqual(rowDecisionShortfall(events, 3), 2);
  assert.strictEqual(rowDecisionShortfall(events, 1), 0);
  assert.strictEqual(rowDecisionShortfall(events, null), null);
});

test("reads a @TableTest block as one table of data rows, header excluded", () => {
  const source = `class Example {
    @TableTest("""
        Scenario     | Role  | Allowed?
        Admin role   | ADMIN | true
        Guest role   | GUEST | false
        """)
    void decides(Role role, boolean allowed) {}
}`;
  const [table] = pipeTables(source);
  assert.strictEqual(pipeTables(source).length, 1);
  assert.strictEqual(table.length, 2);
});

test("drops the markdown separator row so it is not counted as data", () => {
  const markdown = `| Scenario | Cost? |
|----------|-------|
| Cheap    | £1    |
| Dear     | £9    |`;
  const [table] = pipeTables(markdown);
  assert.strictEqual(table.length, 2);
});

test("separates two tables divided by prose", () => {
  const markdown = `| A | B |
| 1 | 2 |

Some prose between them.

| C | D |
| 3 | 4 |
| 5 | 6 |`;
  assert.deepStrictEqual(pipeTables(markdown).map((table) => table.length), [1, 2]);
});

test("ignores a single piped line that is not a table", () => {
  assert.deepStrictEqual(pipeTables("run a | b in the shell\n"), []);
});

test("no rule signature reuses a decision-lexicon phrase", () => {
  const { RULE_SIGNATURES, DECISION_LEXICON } = require("./narration-corpus.js");
  const decisionPatterns = DECISION_LEXICON.flatMap((entry) =>
    entry.patterns.map((pattern) => pattern.source),
  );
  for (const signature of RULE_SIGNATURES) {
    for (const pattern of signature.patterns) {
      assert.ok(
        !decisionPatterns.includes(pattern.source),
        `${signature.rule} would be on-topic by construction via ${pattern}`,
      );
    }
  }
});

test("an echo far from any decision is recorded as unplaced, not on-topic", () => {
  const { ruleEchoes } = require("./narration-corpus.js");
  const narration = `_[~10 thinking tokens (1 deltas)]_

> (thinking) The cost holds regardless of destination. A. B. C. D. E. I will use a value set here.
`;
  const segments = parseNarration(narration);
  const [echo] = ruleEchoes(decisionEvents(segments), segments);
  assert.strictEqual(echo.rule, "08-value-sets");
  assert.strictEqual(echo.nearestDecision, null);
  assert.strictEqual(echo.onTopic, false);
});
