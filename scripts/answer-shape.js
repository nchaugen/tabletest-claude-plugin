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
 * Extraction is deliberately built on `assertions.js`'s parser, not a second one. A shape that
 * disagreed with the checkers about where a column ends would make every comparison between
 * them unreadable.
 */

const { tableTestTables, parseCollectionElements, extractTableTestMethodBodies } = require("./assertions.js");

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
  const start = opening + callName.length + 1;

  let depth = 1;
  let quote = null;
  let current = "";
  const args = [];
  for (let i = start; i < body.length; i++) {
    const character = body[i];
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
  const tables = tableTestTables(String(source)).map((table, index) => {
    const columns = tableColumns(table);
    const wellFormed = table.rows.filter((row) => row.length === table.headers.length);
    const owner = bodies.find((one) => one.name === table.method) || bodies[index] || null;
    return {
      method: table.method,
      params: table.params,
      headers: table.headers,
      body: owner ? owner.body : "",
      columns,
      expectationColumns: columns.filter((column) => column.isExpectation),
      rows: wellFormed.map((cells) => ({ cells })),
      malformedRows: table.rows.length - wellFormed.length,
      cases: wellFormed.flatMap((cells) => rowCases(columns, cells)),
    };
  });
  return { tables };
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
  answerShape,
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
