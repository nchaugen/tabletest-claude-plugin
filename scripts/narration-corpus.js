#!/usr/bin/env node
/**
 * One row per archived narration, so 123 runs can be reasoned over as a table instead of read.
 *
 * The archive is a controlled comparison nobody designed: 23 evals observed under three or more
 * skill states. Reading it linearly is not a sitting, and the questions worth asking — does a
 * structural decision precede scenario naming, does a rule fire only where the agent is standing —
 * are counts over ordered decision events, not impressions. This emits those events with their
 * positions, plus the table shape the run actually produced, and leaves the judging to the reader.
 *
 * Counts are evidence to check, not evidence to trust: domain vocabulary inflates keyword hits, so
 * every row carries the matched sentence and `--sample` prints them for hand-checking.
 *
 * Usage: node scripts/narration-corpus.js [--skill tabletest] [--json|--tsv] [--sample eval-7]
 */

const fs = require("fs");
const path = require("path");

/**
 * What a narration sentence is evidence of. Ordered detection, first match wins per sentence, so a
 * sentence that both rejects an option and decides structure is counted as the decision it reaches.
 *
 * `naming` and `structure` are the pair H1 turns on: whether the agent fixed the rows before or
 * after it named them. The others exist so the ordering can be read in context rather than alone.
 */
const DECISION_LEXICON = [
  {
    kind: "reversal",
    patterns: [
      /\b(?:actually|on second thought|wait[,—-]|let me reconsider|i was wrong|rethinking|scratch that|changed my mind|revisit(?:ing)? (?:that|this|the) decision)\b/i,
    ],
  },
  {
    kind: "naming-decided",
    patterns: [
      /\bnaming the (?:scenario|row|case|test)/i,
      /\b(?:scenario|row|case) names?\b/i,
      /\bnames? (?:the|each|every) (?:scenario|row|case)/i,
      /\bname (?:them|it|these) (?:by|after|for)/i,
      /\bdescriptive names?\b/i,
      /\bsettl(?:e|ing) on (?:scenario )?names?\b/i,
      /\bcall(?:ing)? (?:the|each|this) (?:scenario|row|case)\b/i,
      /\bscenario column\b/i,
      /\bdescription column\b/i,
    ],
  },
  {
    kind: "tables-decided",
    patterns: [
      /\bseparate tables?\b/i,
      /\bsingle table\b/i,
      /\bone table\b/i,
      /\b(?:two|three|four|five|six) tables\b/i,
      /\bone table per\b/i,
      /\bsplit(?:ting)? (?:this |these |them |the )?(?:into )?(?:separate )?tables?\b/i,
      /\bdecompos(?:e|ing|ition|ed) into\b/i,
      /\bmerg(?:e|ing) (?:the |them |these )?tables?\b/i,
      /\bhow many tables\b/i,
    ],
  },
  {
    kind: "rows-decided",
    patterns: [
      /\bvalue sets?\b/i,
      /\bcollaps(?:e|ing|ed)\b/i,
      /\bgroup(?:ing|ed)? (?:the |them |these |permissions|by|into|together)/i,
      /\bone row per\b/i,
      /\ba single row\b/i,
      /\brows? (?:that |which )?(?:test|cover|capture)/i,
      /\bcreate rows?\b/i,
      /\bcombin(?:e|ing) (?:the |them |these )?(?:rows?|cases?)/i,
      /\bmerg(?:e|ing) (?:the |them |these )?rows?\b/i,
      /\bhold(?:ing)? (?:the )?(?:other|irrelevant|remaining)\b/i,
      /\bhow many rows\b/i,
      /\bredundant\b/i,
    ],
  },
  {
    kind: "option-rejected",
    patterns: [
      /\brather than\b/i,
      /\binstead of\b/i,
      /\bi could .{0,60}\bbut\b/i,
      /\bdecided against\b/i,
      /\bdoesn't (?:make sense|work|fit)\b/i,
      /\bwould (?:just )?(?:pad|bloat|add nothing)\b/i,
    ],
  },
  {
    kind: "rule-invoked",
    patterns: [
      /\bthe skill(?:'s)?\b/i,
      /\bthe guidance\b/i,
      /\bfollowing (?:the|its) own\b/i,
      /\bper the (?:skill|rule|convention)\b/i,
      /\bthe (?:principle|rule) (?:of|that|says)\b/i,
      /\baligns with the\b/i,
      /\bconvention (?:says|is)\b/i,
    ],
  },
];

/**
 * Which shared table-design rule a sentence is echoing, and which decision that rule owns.
 *
 * H2 asks whether a rule fires only where the agent is standing. That needs both halves: the rule
 * the text quotes, and the decision the text is making when it quotes it. Phrases are chosen to be
 * distinctive to one rule — a phrase the agent would only write having read that rule — because a
 * generic hit ("table", "row") would score every rule on every sentence.
 *
 * No phrase here may also appear in `DECISION_LEXICON`. One that does makes the rule on-topic by
 * construction: "value set" would detect rule 08 *and* the row decision, and H2 would measure its
 * own wiring. Rule 08 is detected by "regardless of" instead, and rule 06 by "obligation".
 */
const RULE_SIGNATURES = [
  { rule: "01-one-rule-one-axis", owns: "tables-decided", patterns: [/\bone rule,? one axis\b/i, /\bsingle axis\b/i, /\bone axis\b/i] },
  { rule: "02-all-outputs-of-a-concern", owns: "tables-decided", patterns: [/\ball outputs of\b/i, /\boutputs of (?:the|a|one) concern\b/i] },
  { rule: "03-decompose-signs", owns: "tables-decided", patterns: [/\btwo concerns\b/i, /\bcannot name (?:it )?without\b/i, /\bderived value\b/i, /\bmissing concern\b/i, /\btwo tables,? not one\b/i] },
  { rule: "04-combining-table", owns: "tables-decided", patterns: [/\bcombining table\b/i, /\bits own rule\b/i] },
  { rule: "05-rules-not-arithmetic", owns: "tables-decided", patterns: [/\bseparate(?:s|d)? (?:the )?rules? from (?:the )?arithmetic\b/i, /\barithmetic of\b/i] },
  { rule: "06-obligations-one-row-each", owns: "rows-decided", patterns: [/\bobligations?\b/i, /\bsmallest set of\b/i, /\bcovering problem\b/i, /\bshare an expectation\b/i] },
  { rule: "07-tiers-and-boundaries", owns: "rows-decided", patterns: [/\bboth sides of\b/i, /\bevery tier\b/i, /\ball (?:four |three |the )?tiers\b/i, /\btier boundar(?:y|ies)\b/i, /\bboundary (?:rows?|cases?|values?)\b/i] },
  { rule: "08-value-sets", owns: "rows-decided", patterns: [/\bregardless of\b/i, /\bdoes(?:n't| not) affect the outcome\b/i, /\bdoes(?:n't| not) read the column\b/i] },
  { rule: "11-titles-form-an-index", owns: "naming-decided", patterns: [/\btitles? .{0,20}index\b/i, /\bmethod name\b/i, /\btable titles?\b/i] },
  { rule: "12-scenario-names-as-conditions", owns: "naming-decided", patterns: [/\bunder what circumstances\b/i, /\bconditions? rather than outcomes?\b/i, /\bcondition,? not outcome\b/i, /\bnames? the outcome\b/i] },
  { rule: "13-name-expectation-columns", owns: "naming-decided", patterns: [/\bexpectation columns?\b/i, /\bquestion mark\b/i] },
  { rule: "17-thresholds-visible", owns: "rows-decided", patterns: [/\bthreshold visible\b/i, /\bmake the threshold\b/i, /\bits own column\b/i] },
  { rule: "20-black-box-tables", owns: "tables-decided", patterns: [/\bblack[- ]box\b/i, /\bpublic (?:API|method|interface)\b/i, /\binternal(?:s)? (?:of|to) the\b/i] },
];

/**
 * Rule echoes paired with the nearest decision, so H2 can ask whether the two agree.
 *
 * `window` is in sentences, not tokens: a rule invoked three sentences from the decision it governs
 * is still governing it, while one invoked twenty sentences away is being recited, not applied.
 */
function ruleEchoes(events, segments, window = 3) {
  const echoes = [];
  let position = 0;
  const decisionAt = new Map();
  for (const event of events) {
    if (event.kind.endsWith("-decided")) decisionAt.set(event.position, event.kind);
  }
  for (const segment of segments) {
    if (segment.channel === "action") {
      position += 1;
      continue;
    }
    for (const sentence of sentences(segment.text)) {
      for (const signature of RULE_SIGNATURES) {
        if (!signature.patterns.some((pattern) => pattern.test(sentence))) continue;
        let nearest = null;
        for (let offset = 0; offset <= window; offset += 1) {
          nearest = decisionAt.get(position - offset) || decisionAt.get(position + offset) || nearest;
          if (nearest) break;
        }
        echoes.push({
          position,
          rule: signature.rule,
          owns: signature.owns,
          nearestDecision: nearest,
          onTopic: nearest === signature.owns,
          sentence,
        });
      }
      position += 1;
    }
  }
  return echoes;
}

/** A narration split into ordered segments, keeping thinking apart from what the agent said aloud. */
function parseNarration(text) {
  const segments = [];
  let current = null;
  for (const line of text.split("\n")) {
    const thinkingTokens = line.match(/^_\[~([\d,]+) thinking tokens/);
    if (thinkingTokens) {
      current = {
        channel: "thinking",
        tokens: Number(thinkingTokens[1].replace(/,/g, "")),
        lines: [],
      };
      segments.push(current);
      continue;
    }
    const action = line.match(/^\*\*(Write|Edit|Bash|Read|Glob|Grep|TodoWrite)\b(.*)\*\*$/);
    if (action) {
      current = null;
      segments.push({ channel: "action", tool: action[1], detail: action[2].trim(), lines: [] });
      continue;
    }
    if (!line.trim()) continue;
    const quoted = line.match(/^>\s?(.*)$/);
    const body = quoted ? quoted[1] : line;
    if (!body.trim()) continue;
    if (current && current.channel === "thinking" && quoted) {
      current.lines.push(body.replace(/^\(thinking\)\s*/, ""));
      continue;
    }
    if (!current || current.channel !== "said") {
      current = { channel: "said", lines: [] };
      segments.push(current);
    }
    current.lines.push(body);
  }
  return segments
    .map((segment) => ({ ...segment, text: (segment.lines || []).join(" ").trim() }))
    .filter((segment) => segment.channel === "action" || segment.text);
}

/** Sentence split that keeps decimals, abbreviations and `@Description` from ending a sentence. */
function sentences(text) {
  return text
    .split(/(?<=[.!?])\s+(?=[A-Z"'`*—-])/)
    .map((sentence) => sentence.trim())
    .filter(Boolean);
}

/**
 * Decision events in the order the narration reaches them, each anchored to the sentence that
 * produced it. Position is the sentence index across the whole narration, which is what an ordering
 * question needs — token offsets would make two narrations of different length uncomparable.
 */
function decisionEvents(segments) {
  const events = [];
  let position = 0;
  for (const segment of segments) {
    if (segment.channel === "action") {
      events.push({ position, kind: `wrote:${segment.tool}`, channel: "action", sentence: segment.detail });
      position += 1;
      continue;
    }
    for (const sentence of sentences(segment.text)) {
      const entry = DECISION_LEXICON.find(({ patterns }) =>
        patterns.some((pattern) => pattern.test(sentence)),
      );
      if (entry) events.push({ position, kind: entry.kind, channel: segment.channel, sentence });
      position += 1;
    }
  }
  return events;
}

/** Where a kind of decision is first reached, or null when the narration never reaches it. */
function firstPosition(events, kind) {
  const found = events.find((event) => event.kind === kind);
  return found ? found.position : null;
}

/**
 * H1's classification. A narration only answers the question when it reaches both decisions; one
 * that never names its scenarios is `unclassified`, not a vote either way.
 *
 * Row structure, not table decomposition, is the decision H1 is about — eval-13 splits into three
 * tables *and* names its scenarios, but the rows of its third table came from the names.
 */
function decisionOrder(events) {
  const rows = firstPosition(events, "rows-decided");
  const naming = firstPosition(events, "naming-decided");
  if (rows === null || naming === null) return "unclassified";
  return rows < naming ? "rows-first" : "naming-first";
}

/**
 * How many of a run's tables the narration actually decided rows for. eval-13 is the case that
 * forces this: it decides rows for its standard and express tables and says nothing about the rows
 * of its overnight table, which then comes out as one row per condition name. A narration-level
 * ordering cannot see that, because the narration reaches both orders inside one run.
 *
 * Negative coverage is not meaningful, so a run that deliberates more than it tabulates reports 0.
 */
function rowDecisionShortfall(events, tableCount) {
  if (tableCount === null) return null;
  const decided = events.filter((event) => event.kind === "rows-decided").length;
  return Math.max(0, tableCount - decided);
}

/** Contiguous pipe-delimited lines, which is what both a markdown table and a `@TableTest` are. */
function pipeTables(text) {
  const tables = [];
  let rows = [];
  const flush = () => {
    const data = rows.filter((row) => !/^[|\s:-]+$/.test(row));
    if (data.length >= 2) tables.push(data.slice(1));
    rows = [];
  };
  for (const line of text.split("\n")) {
    const trimmed = line.trim();
    if (trimmed.includes("|") && !/^\s*(?:\/\/|\*|#)/.test(trimmed)) rows.push(trimmed);
    else flush();
  }
  flush();
  return tables;
}

/** Parametrised-case blocks for the ecosystems that have no pipe syntax at all. */
function parametrisedBlocks(text) {
  const markers = text.match(/@pytest\.mark\.parametrize|\.each\(|arguments:\s*\[|for _, (?:tt|tc) :?=/g);
  return markers ? markers.length : 0;
}

/**
 * The table shape a run produced, read from what it wrote rather than from what it said. Returns a
 * null count with its reason when no parser fired, so a missing measurement never reads as zero.
 */
function outputShape(outputsDir) {
  if (!fs.existsSync(outputsDir)) return { tableCount: null, reason: "no outputs" };
  const files = [];
  const walk = (dir) => {
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) walk(full);
      else files.push(full);
    }
  };
  walk(outputsDir);

  const sources = files.filter((file) => /\.(java|kt|md|py|swift|ts|js|go)$/.test(file));
  const tables = [];
  let parametrised = 0;
  let valueSets = false;
  for (const file of sources) {
    if (/build-result\.json$/.test(file)) continue;
    const text = fs.readFileSync(file, "utf-8");
    const isResponse = /response\.md$/.test(file);
    const found = pipeTables(text);
    // response.md restates the tables it also wrote to source; count it only when it is all there is.
    if (found.length) tables.push({ file, tables: found, isResponse });
    parametrised += parametrisedBlocks(text);
    if (/\{[^{}\n]*,[^{}\n]*\}/.test(text) && found.length) valueSets = true;
  }

  const fromSource = tables.filter((file) => !file.isResponse);
  const chosen = fromSource.length ? fromSource : tables;
  if (!chosen.length && parametrised) {
    return { tableCount: parametrised, rowCounts: null, valueSets, reason: "parametrised blocks" };
  }
  if (!chosen.length) return { tableCount: null, reason: "no tables found" };
  const rowCounts = chosen.flatMap((file) => file.tables.map((table) => table.length));
  return {
    tableCount: rowCounts.length,
    rowCounts,
    totalRows: rowCounts.reduce((sum, count) => sum + count, 0),
    valueSets,
    reason: fromSource.length ? "source tables" : "response.md tables",
  };
}

/** Iteration-level facts every eval in it shares — the digest, not the commit, identifies the state. */
function readBenchmark(iterationDir) {
  const file = path.join(iterationDir, "benchmark.json");
  if (!fs.existsSync(file)) return {};
  const benchmark = JSON.parse(fs.readFileSync(file, "utf-8"));
  const scores = {};
  for (const entry of benchmark.evals || []) {
    scores[entry.id] = {
      passed: entry.results?.assertions_passed,
      total: entry.results?.assertions_total,
    };
  }
  return {
    skillDigest: benchmark.skill_digest,
    skillCommit: benchmark.skill_commit,
    timestamp: benchmark.timestamp,
    scores,
  };
}

function corpusRows(root, skillFilter) {
  const rows = [];
  const iterationsRoot = path.join(root, "iterations");
  for (const skill of fs.readdirSync(iterationsRoot).sort()) {
    if (skillFilter && skill !== skillFilter) continue;
    const skillDir = path.join(iterationsRoot, skill);
    if (!fs.statSync(skillDir).isDirectory()) continue;
    for (const iteration of fs.readdirSync(skillDir).sort()) {
      const iterationDir = path.join(skillDir, iteration);
      if (!fs.statSync(iterationDir).isDirectory()) continue;
      const benchmark = readBenchmark(iterationDir);
      for (const evalName of fs.readdirSync(iterationDir).sort()) {
        const evalDir = path.join(iterationDir, evalName);
        const narrationFile = path.join(evalDir, "narration.md");
        if (!fs.existsSync(narrationFile)) continue;
        const text = fs.readFileSync(narrationFile, "utf-8");
        if (!/^> \(thinking\)/m.test(text)) continue;

        const segments = parseNarration(text);
        const events = decisionEvents(segments);
        const shape = outputShape(path.join(evalDir, "outputs"));
        const score = benchmark.scores?.[evalName] || {};
        rows.push({
          skill,
          iteration: Number(iteration.replace("iteration-", "")),
          eval: evalName,
          evalNumber: Number((evalName.match(/^eval-(\d+)/) || [])[1]),
          skillDigest: benchmark.skillDigest,
          skillCommit: benchmark.skillCommit,
          timestamp: benchmark.timestamp,
          passed: score.passed,
          total: score.total,
          thinkingTokens: segments
            .filter((segment) => segment.channel === "thinking")
            .reduce((sum, segment) => sum + segment.tokens, 0),
          decisionOrder: decisionOrder(events),
          firstRows: firstPosition(events, "rows-decided"),
          firstTables: firstPosition(events, "tables-decided"),
          firstNaming: firstPosition(events, "naming-decided"),
          rowDecisions: events.filter((event) => event.kind === "rows-decided").length,
          rowDecisionShortfall: rowDecisionShortfall(events, shape.tableCount),
          eventCounts: events.reduce((counts, event) => {
            counts[event.kind] = (counts[event.kind] || 0) + 1;
            return counts;
          }, {}),
          ruleEchoes: ruleEchoes(events, segments),
          rulesMentioned: RULE_SIGNATURES
            .filter(({ patterns }) => patterns.some((pattern) => pattern.test(text)))
            .map(({ rule }) => rule),
          tableCount: shape.tableCount,
          rowCounts: shape.rowCounts,
          totalRows: shape.totalRows,
          valueSets: shape.valueSets,
          shapeSource: shape.reason,
          events,
        });
      }
    }
  }
  return rows;
}

function tsv(rows) {
  const header = [
    "skill", "iteration", "eval", "digest", "passed", "total", "thinking_tokens",
    "decision_order", "first_rows", "first_naming", "row_decisions", "shortfall",
    "tables", "rows", "value_sets", "shape_source",
  ];
  const blank = (value) => (value === null || value === undefined ? "" : value);
  const lines = [header.join("\t")];
  for (const row of rows) {
    lines.push([
      row.skill, row.iteration, row.eval, blank(row.skillDigest), blank(row.passed), blank(row.total),
      row.thinkingTokens, row.decisionOrder, blank(row.firstRows), blank(row.firstNaming),
      row.rowDecisions, blank(row.rowDecisionShortfall),
      blank(row.tableCount), blank(row.totalRows), blank(row.valueSets), blank(row.shapeSource),
    ].join("\t"));
  }
  return lines.join("\n");
}

/**
 * How many narrations arrive at each rule at all, by its distinctive vocabulary.
 *
 * **This is the instrument that works** (§ J22) — no pairing, no holdout, no co-location window —
 * and it is the check to run before moving guidance from one rule to another. It lives here rather
 * than in a shell one-liner because a retyped grep is how the first measurement went wrong: an
 * over-narrow pattern for rule 12 (requiring a literal "by" before "conditions") reported 3/123
 * where the real figure is 13/123.
 *
 * **Reach is counted over the whole narration, not over sentences that produced a decision event.**
 * Scoring only decision sentences was the second version of this mistake and it halved rule 08 and
 * rule 06: the question is whether the agent arrives at the rule at all, and it can quote one while
 * explaining, rejecting, or merely noticing something.
 */
function ruleReach(rows) {
  const reach = RULE_SIGNATURES.map(({ rule }) => {
    const hits = rows.filter((row) => row.rulesMentioned.includes(rule));
    return { rule, narrations: hits.length, share: hits.length / rows.length };
  });
  return reach.sort((a, b) => b.narrations - a.narrations);
}

function main() {
  const args = process.argv.slice(2);
  const flag = (name) => {
    const index = args.indexOf(`--${name}`);
    return index === -1 ? null : args[index + 1];
  };
  const rows = corpusRows(process.cwd(), flag("skill"));
  const sample = flag("sample");
  if (sample) {
    for (const row of rows.filter((entry) => entry.eval.includes(sample))) {
      console.log(`\n=== ${row.skill}/${row.iteration} ${row.eval} — ${row.decisionOrder} (${row.skillDigest})`);
      for (const event of row.events) {
        if (event.channel === "action") continue;
        console.log(`  [${String(event.position).padStart(4)}] ${event.kind.padEnd(18)} ${event.sentence.slice(0, 150)}`);
      }
    }
    return;
  }
  if (args.includes("--reach")) {
    for (const entry of ruleReach(rows)) {
      console.log(`${entry.rule.padEnd(34)} ${String(entry.narrations).padStart(3)}/${rows.length}  ${(entry.share * 100).toFixed(0).padStart(3)}%`);
    }
    return;
  }
  if (args.includes("--json")) {
    console.log(JSON.stringify(rows, null, 2));
    return;
  }
  console.log(tsv(rows));
}

if (require.main === module) main();

module.exports = {
  parseNarration, sentences, decisionEvents, decisionOrder, rowDecisionShortfall, ruleEchoes,
  pipeTables, outputShape, corpusRows, ruleReach, RULE_SIGNATURES, DECISION_LEXICON,
};
