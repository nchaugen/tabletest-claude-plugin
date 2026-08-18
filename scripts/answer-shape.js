#!/usr/bin/env node
/**
 * The structural shape of an answer: its tables, the role each column plays, and the cases
 * its rows actually run as.
 *
 * The grader reads an answer through one bit per assertion, and a slot that moves for six
 * unrelated reasons cannot say which one moved it — eval-18 moves ±6 slots at one skill state.
 * This module reads the same answer structurally instead: deterministically, offline, at no
 * cost, and over every draw already stored in the tree. It answers *what an answer looks like*
 * and never *whether it is good* — the judgement lives in the per-eval relations that consume
 * the shape (`shape-report.js`), so that widening to a second eval adds relations rather than
 * touching extraction.
 *
 * Extraction shares `assertions.js`'s parsing throughout, because a shape that disagreed with the
 * checkers about where a column ends would make every comparison between them unreadable. It
 * split cells itself for one release, while `splitRowCells` still opened a quoted region at any
 * `'`; that is fixed at the source (`9a15c6a`) and the quarantine is gone.
 */

const {
  parseCollectionElements,
  extractTableTestMethodBodies,
  parseParameterList,
  splitRowCells,
} = require("./assertions.js");

/**
 * Parameter types whose cell braces are the value itself. Everything else with braces is a
 * value set that runs the row once per value, and only the declared type tells them apart.
 */
const SET_TYPE = /^(?:java\.util\.)?(?:Set|HashSet|LinkedHashSet|MutableSet)\b/;

/** A header ending in `?` is an expectation column — TableTest's own convention. */
function isExpectationHeader(header) {
  return /\?$/.test(String(header).trim());
}

/**
 * The text of the annotation `name` closing immediately before `index`, or "".
 *
 * `@DisplayName` and `@Description` are the *published surface* several assertions judge against —
 * whether a comparison criterion is stated, whether an invariance is claimed — so a relation needs
 * them beside the rows rather than only the rows.
 */
function annotationBefore(source, name, index) {
  const region = String(source).slice(0, index);
  const at = region.lastIndexOf(`@${name}`);
  if (at === -1) return "";
  // Only an annotation on this same method counts: anything with an intervening `@TableTest` or a
  // closing brace belongs to an earlier one.
  const between = region.slice(at);
  if (/@TableTest|\}\s*$/.test(between.replace(new RegExp(`^@${name}`), ""))) return "";
  const opened = region.indexOf("(", at);
  if (opened === -1) return "";
  const args = argumentList(region, opened);
  if (!args || args.length === 0) return "";
  return args[0].replace(/^"{3}|"{3}$/g, "").replace(/^"|"$/g, "").trim();
}

/**
 * Every `@TableTest` in the source as `{text, method, params, displayName, description}`, in order.
 *
 * The literal and the signature are found with the same shapes `assertions.js` uses; only the
 * splitting of the literal into cells is this module's own.
 */
function tableLiterals(source) {
  const tableRegex = /@TableTest\s*\(\s*(?:value\s*=\s*)?"{3}([\s\S]*?)"{3}\s*\)/g;
  const found = [];
  let match;
  while ((match = tableRegex.exec(String(source))) !== null) {
    const after = String(source).slice(match.index + match[0].length);
    const signature = after.match(/(?:fun|void|[A-Za-z_$][\w<>,\[\].\s]*?)\s+(\w+|`[^`]+`)\s*\(([^)]*)\)/);
    found.push({
      text: match[1],
      method: signature ? signature[1] : null,
      params: signature ? parseParameterList(signature[2]) : [],
      displayName: annotationBefore(source, "DisplayName", match.index),
      description: annotationBefore(source, "Description", match.index),
    });
  }
  return found;
}

/**
 * The header and data rows of one table literal, with edge pipes removed.
 *
 * Whether a row carries leading and trailing pipes is a property of the table's style, so it is
 * decided from the header and applied to every row — deciding per row cannot tell `a | b | `
 * (a blank last cell) from `| a | b |` (edge pipes), and guessing shifts every column off its
 * parameter.
 */
function tableRows(text) {
  const lines = String(text)
    .split("\n")
    .map((line) => line.trim())
    .filter((line) => line.length > 0 && line.includes("|"));
  if (lines.length === 0) return { headers: [], rows: [] };

  let headers = splitRowCells(lines[0]);
  let rows = lines.slice(1).map(splitRowCells);
  const dropLeading = headers.length > 1 && headers[0] === "" && lines[0].trimStart().startsWith("|");
  const dropTrailing =
    headers.length > 1 && headers[headers.length - 1] === "" && lines[0].trimEnd().endsWith("|");
  const trim = (cells) => {
    const out = cells.slice();
    if (dropLeading && out[0] === "") out.shift();
    if (dropTrailing && out[out.length - 1] === "") out.pop();
    return out;
  };
  if (dropLeading || dropTrailing) {
    headers = trim(headers);
    rows = rows.map(trim);
  }
  return { headers, rows };
}

/**
 * The values a cell runs as, in order.
 *
 * `{a, b}` on a `String` column is a value set and runs the row twice; the same text on a
 * `Set<String>` column is one literal set. Braces alone cannot decide it, which is why this
 * takes the parameter the column feeds. A `[a, b]` list is always one value.
 */
function cellValues(cell, param) {
  const text = String(cell).trim();
  if (!text.startsWith("{")) return [text];
  if (param && SET_TYPE.test(param.type)) return [text];
  const elements = parseCollectionElements(text);
  if (elements === null) return [text];
  return elements.map((element) => element.trim());
}

/** Cases beyond this in one row are a runaway cartesian product, not a table worth reading. */
const CASE_CEILING = 512;

/**
 * Every case one row runs as, keyed by header.
 *
 * Value sets multiply: two sets of two values in one row are four cases, which is what the
 * rows *mean* and therefore what a coverage relation has to count. Counting literal rows
 * instead reports `{30, 39} | 0 | 106.0` as one age and misses the whole point of the
 * compact form.
 */
function rowCases(columns, cells) {
  let cases = [{}];
  for (const column of columns) {
    const values = column.isScenario
      ? [String(cells[column.index] ?? "").trim()]
      : cellValues(cells[column.index] ?? "", column.param);
    const next = [];
    for (const partial of cases) {
      for (const value of values) {
        next.push({ ...partial, [column.header]: value });
        if (next.length > CASE_CEILING) return cases;
      }
    }
    cases = next;
  }
  return cases;
}

/**
 * The argument expressions of the call to `callName` inside a method body, or null when the body
 * never makes that call.
 *
 * A quantity a table holds constant is often supplied here rather than in a column —
 * `evaluateApplication("NEW", 30, claimCount)` holds age at 30 for every row. Reading only the
 * columns reports that table as having no age at all, which silently drops every one of its rows
 * from a coverage relation. eval-18's iteration-50 is exactly that answer.
 */
function callArguments(body, callName) {
  const opening = String(body ?? "").indexOf(`${callName}(`);
  if (opening === -1) return null;
  return argumentList(String(body), opening + callName.length);
}

/**
 * The arguments of the call whose parenthesis opens at `openIndex`, or null when it never closes.
 *
 * Splits on the commas at the call's own depth, so a nested call or collection stays one argument.
 */
function argumentList(text, openIndex) {
  let depth = 1;
  let quote = null;
  let current = "";
  const args = [];
  for (let i = openIndex + 1; i < text.length; i++) {
    const character = text[i];
    if (quote) {
      current += character;
      if (character === quote) quote = null;
      continue;
    }
    if (character === '"' || character === "'") {
      quote = character;
      current += character;
      continue;
    }
    if (character === "(" || character === "[" || character === "{") depth++;
    else if (character === ")" || character === "]" || character === "}") {
      depth--;
      if (depth === 0) {
        const last = current.trim();
        if (last !== "" || args.length > 0) args.push(last);
        return args;
      }
    }
    if (character === "," && depth === 1) {
      args.push(current.trim());
      current = "";
      continue;
    }
    current += character;
  }
  return null;
}

/**
 * The arguments of the call that consumes the table's parameters — the act step of the test.
 *
 * A method body holds several calls (an assertion, a helper, a lambda), and the one that matters
 * is whichever passes the table's own columns on. Scoring candidates by how many parameter names
 * they mention finds it without the caller having to know the method's name, which varies per
 * answer whenever the eval ships no implementation to call.
 */
function actCallArguments(body, parameterNames) {
  const names = new Set(parameterNames);
  const callRegex = /([A-Za-z_$][\w.$]*)\s*\(/g;
  let best = null;
  let match;
  while ((match = callRegex.exec(String(body))) !== null) {
    const args = argumentList(String(body), match.index + match[0].length - 1);
    if (!args) continue;
    const mentioned = args.filter((argument) => names.has(argument.trim())).length;
    if (mentioned === 0) continue;
    if (!best || mentioned > best.mentioned) best = { name: match[1], args, mentioned };
  }
  return best;
}

/** The value a literal argument states, or null where the argument names or computes something. */
function literalArgument(argument) {
  const text = String(argument ?? "").trim();
  const quoted = text.match(/^"([^"]*)"$/);
  if (quoted) return quoted[1];
  if (/^-?\d+(?:\.\d+)?[dDfFlL]?$/.test(text)) return text.replace(/[dDfFlL]$/, "");
  if (text === "true" || text === "false" || text === "null") return text;
  return null;
}

/** The columns of one parsed table, each tagged with the role its header and parameter give it. */
function tableColumns(table) {
  const offset = table.hasScenarioColumn ? 1 : 0;
  return table.headers.map((header, index) => ({
    header,
    index,
    param: table.params[index - offset] || null,
    isScenario: table.hasScenarioColumn && index === 0,
    isExpectation: isExpectationHeader(header),
  }));
}

/**
 * The shape of one test source: every `@TableTest`, its columns, its literal rows, and the
 * cases those rows expand to.
 *
 * Rows whose cell count disagrees with the header are counted rather than dropped silently.
 * Answers that do not compile are stored in the tree and must still extract — a relation
 * reporting "no premium rows" is wrong if the truth is "the table did not parse".
 */
function answerShape(source) {
  const bodies = extractTableTestMethodBodies(String(source));
  const tables = tableLiterals(source).map((literal, index) => {
    const { headers, rows } = tableRows(literal.text);
    const table = {
      method: literal.method,
      params: literal.params,
      headers,
      // The scenario column is optional, so column-to-parameter mapping cannot assume it. One
      // more column than the method has parameters means a scenario column, which feeds nothing.
      hasScenarioColumn: headers.length === literal.params.length + 1,
    };
    const columns = tableColumns(table);
    const wellFormed = rows.filter((row) => row.length === headers.length);
    const owner = bodies.find((one) => one.name === table.method) || bodies[index] || null;
    return {
      method: table.method,
      params: table.params,
      headers: table.headers,
      displayName: literal.displayName,
      description: literal.description,
      body: owner ? owner.body : "",
      columns,
      expectationColumns: columns.filter((column) => column.isExpectation),
      rows: wellFormed.map((cells) => ({ cells })),
      malformedRows: rows.length - wellFormed.length,
      cases: wellFormed.flatMap((cells) => rowCases(columns, cells)),
    };
  });
  // The whole source travels with the shape: some assertions judge what a *helper* does — sorting
  // before comparing, normalising a value — and a helper is not inside any table's own body.
  return { tables, source: String(source) };
}

/**
 * The expectation columns holding one value across every row of a table.
 *
 * This is condition (1) of `rule-falsifiable-by-a-row` verbatim, and the condition is decidable
 * from the table alone. Its exemption is not: a table whose claim *is* an invariance correctly
 * holds one value, and whether the title says so is a reading. Report the condition and leave
 * the exemption to a reader.
 */
function constantExpectationColumns(table) {
  return table.expectationColumns
    .filter((column) => table.rows.length > 1)
    .filter((column) => {
      const values = table.rows.map((row) => String(row.cells[column.index] ?? "").trim());
      return new Set(values).size === 1;
    })
    .map((column) => ({ header: column.header, value: String(table.rows[0].cells[column.index]).trim() }));
}

/**
 * The first column whose header or parameter name matches `pattern`, or null.
 *
 * Answers name the same domain quantity `Claim Count`, `Claims` or `Prior Claims` between
 * draws, so a relation cannot address a column by a fixed header. Matching the parameter name
 * too catches the tables that carry no scenario column and abbreviate their headers.
 */
function findColumn(table, pattern) {
  return (
    table.columns.find((column) => !column.isScenario && pattern.test(column.header)) ||
    table.columns.find((column) => !column.isScenario && column.param && pattern.test(column.param.name)) ||
    null
  );
}

/** The number a cell states, or null when it states something else (a blank, a name, a range). */
function numericValue(cell) {
  const text = String(cell ?? "").trim();
  if (text === "") return null;
  if (!/^-?\d+(?:\.\d+)?$/.test(text)) return null;
  return Number(text);
}

module.exports = {
  actCallArguments,
  annotationBefore,
  // Re-exported so the relations layer has one parsing import, not two.
  parseCollectionElements,
  answerShape,
  argumentList,
  tableLiterals,
  tableRows,
  callArguments,
  cellValues,
  literalArgument,
  constantExpectationColumns,
  findColumn,
  isExpectationHeader,
  numericValue,
  rowCases,
  tableColumns,
};
