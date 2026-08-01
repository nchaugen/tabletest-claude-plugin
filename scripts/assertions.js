/**
 * Deterministic assertion checkers for eval grading.
 *
 * Each checker: ({fileContent, allFiles}) => {passed: boolean, evidence: string}
 *   - fileContent: the primary generated test file content (or response.md fallback)
 *   - allFiles: array of {path, content} for all generated files
 */

// --- Helpers ---

/**
 * Extract method bodies for @TableTest-annotated methods using brace-counting.
 * Returns array of {methodSignature, name, body} objects.
 */
function extractTableTestMethodBodies(content) {
  const results = [];
  const tableTestRegex = /@TableTest/g;
  let match;

  while ((match = tableTestRegex.exec(content)) !== null) {
    // Scan forward from @TableTest to find the method signature
    const after = content.slice(match.index);

    // Find the opening brace of the method body (after parameter list)
    // Java: returnType methodName(params) {
    // Kotlin: fun methodName(params) {  or  fun `name with spaces`(params): ReturnType {
    const methodMatch = after.match(/(?:fun|void|boolean|int|long|double|float|String|[A-Z]\w*(?:<[^>]*>)?)\s+(\w+|`[^`]+`)\s*\([^)]*\)\s*(?::\s*\S+\s*)?(?:throws\s+[^{]*)?\{/);
    if (!methodMatch) continue;

    const braceStart = match.index + after.indexOf(methodMatch[0]) + methodMatch[0].length - 1;
    let depth = 1;
    let i = braceStart + 1;

    while (i < content.length && depth > 0) {
      if (content[i] === '{') depth++;
      else if (content[i] === '}') depth--;
      i++;
    }

    const body = content.slice(braceStart + 1, i - 1);
    results.push({ methodSignature: methodMatch[0], name: methodMatch[1], body });
  }

  return results;
}

/**
 * Parse column names from @TableTest annotation table headers.
 * Looks for the first pipe-delimited line after @TableTest.
 */
function parseTableHeaders(content) {
  const tableTestRegex = /@TableTest\s*\(\s*(?:value\s*=\s*)?["\"]{3}([\s\S]*?)["\"]{3}\s*\)/g;
  const allHeaders = [];
  let match;

  while ((match = tableTestRegex.exec(content)) !== null) {
    const tableContent = match[1];
    const lines = tableContent.split('\n').map(l => l.trim()).filter(l => l.length > 0);

    for (const line of lines) {
      if (line.includes('|')) {
        const cols = line.split('|').map(c => c.trim()).filter(c => c.length > 0);
        allHeaders.push(cols);
        break; // first pipe-delimited line is the header
      }
    }
  }

  return allHeaders;
}

/**
 * Count pipe-delimited data rows (non-header, non-empty) in @TableTest tables.
 */
function countDataRows(content) {
  const tableTestRegex = /@TableTest\s*\(\s*(?:value\s*=\s*)?["\"]{3}([\s\S]*?)["\"]{3}\s*\)/g;
  let totalRows = 0;
  let match;

  while ((match = tableTestRegex.exec(content)) !== null) {
    const tableContent = match[1];
    const lines = tableContent.split('\n').map(l => l.trim()).filter(l => l.length > 0 && l.includes('|'));
    // First line is header, rest are data rows
    if (lines.length > 1) {
      totalRows += lines.length - 1;
    }
  }

  return totalRows;
}

/**
 * Split a table row into the cells its pipes actually separate.
 *
 * `split("|")` shreds exactly the values these checkers exist to judge: the pipe in
 * `"tech:milestone|v2"` is data, not a separator. Depth tracks `[]`/`{}`, quotes suppress
 * everything inside them, and **blank cells are preserved** — dropping them, as the older
 * `filter(c => c.length > 0)` idiom does, silently changes a row's shape and hides the
 * blank-cell defects outright.
 */
function splitRowCells(line) {
  const cells = [];
  let current = "";
  let depth = 0;
  let quote = null;

  for (const ch of line) {
    if (quote) {
      if (ch === quote) quote = null;
      current += ch;
    } else if (ch === '"' || ch === "'") {
      quote = ch;
      current += ch;
    } else if (ch === "[" || ch === "{") {
      depth++;
      current += ch;
    } else if (ch === "]" || ch === "}") {
      depth--;
      current += ch;
    } else if (ch === "|" && depth === 0) {
      cells.push(current.trim());
      current = "";
    } else {
      current += ch;
    }
  }
  cells.push(current.trim());
  return cells;
}

/**
 * The elements of a collection cell, or null when the cell is not one.
 *
 * Splits on the commas at the collection's own depth, so nested collections and quoted commas
 * stay whole. Blank elements are preserved deliberately: `[a, , c]` must surface as a blank
 * element, since that is the defect `no-blank-collection-elements` exists to catch.
 */
function parseCollectionElements(cell) {
  const text = String(cell).trim();
  const open = text[0];
  if (open !== "[" && open !== "{") return null;
  const close = open === "[" ? "]" : "}";
  if (text[text.length - 1] !== close) return null;

  const inner = text.slice(1, -1);
  if (inner.trim() === "") return [];

  const elements = [];
  let current = "";
  let depth = 0;
  let quote = null;

  for (const ch of inner) {
    if (quote) {
      if (ch === quote) quote = null;
      current += ch;
    } else if (ch === '"' || ch === "'") {
      quote = ch;
      current += ch;
    } else if (ch === "[" || ch === "{") {
      depth++;
      current += ch;
    } else if (ch === "]" || ch === "}") {
      depth--;
      current += ch;
    } else if (ch === "," && depth === 0) {
      elements.push(current.trim());
      current = "";
    } else {
      current += ch;
    }
  }
  elements.push(current.trim());
  return elements;
}

/** Is the whole element wrapped in one pair of quotes? */
function isQuoted(element) {
  const t = String(element).trim();
  return t.length >= 2 && ((t[0] === '"' && t[t.length - 1] === '"') || (t[0] === "'" && t[t.length - 1] === "'"));
}

/** Does the element start a nested collection, so brackets in it are structure, not stray syntax? */
function isNestedCollection(element) {
  const t = String(element).trim();
  return t[0] === "[" || t[0] === "{";
}

/**
 * Split a parameter list on the commas between parameters, not the ones inside generics.
 * Handles both `List<String> tags` (Java) and `tags: List<String>` (Kotlin).
 */
function parseParameterList(text) {
  if (!text || !text.trim()) return [];
  const parts = [];
  let current = "";
  let depth = 0;
  for (const ch of text) {
    if (ch === "<") depth++;
    else if (ch === ">") depth--;
    if (ch === "," && depth === 0) {
      parts.push(current.trim());
      current = "";
      continue;
    }
    current += ch;
  }
  parts.push(current.trim());

  return parts.filter(Boolean).map((part) => {
    const kotlin = part.match(/^(\w+)\s*:\s*(.+)$/);
    if (kotlin) return { name: kotlin[1], type: kotlin[2].trim() };
    const split = part.lastIndexOf(" ");
    if (split === -1) return { name: part, type: part };
    return { name: part.slice(split + 1).trim(), type: part.slice(0, split).trim() };
  });
}

const LIST_TYPE = /^(?:java\.util\.)?(?:List|Collection|Iterable|ArrayList|MutableList)\b/;
const SET_TYPE = /^(?:java\.util\.)?(?:Set|HashSet|LinkedHashSet|MutableSet)\b/;

/**
 * Every `@TableTest` in the file, paired with the parameter list of the method it annotates.
 *
 * **Column i+1 feeds parameter i** — the scenario column is a display name, not a parameter — and
 * that mapping is what lets a checker ask "is this column declared `List`?" rather than guess from
 * the cell. Guessing is not good enough here: `{tech, business, urgent}` on a `String` column is a
 * value set that runs the row three times, not a `Set` literal, and only the declared type tells
 * them apart.
 */
function tableTestTables(content) {
  const tableRegex = /@TableTest\s*\(\s*(?:value\s*=\s*)?"{3}([\s\S]*?)"{3}\s*\)/g;
  const tables = [];
  let match;

  while ((match = tableRegex.exec(content)) !== null) {
    const lines = match[1]
      .split("\n")
      .map((l) => l.trim())
      .filter((l) => l.length > 0 && l.includes("|"));
    if (lines.length === 0) continue;

    const after = content.slice(match.index + match[0].length);
    const signature = after.match(/(?:fun|void|[A-Za-z_$][\w<>,\[\].\s]*?)\s+(\w+|`[^`]+`)\s*\(([^)]*)\)/);

    // Leading/trailing pipes are optional, and whether one is present is a property of the
    // table's style, not of a row. Decide it from the header and apply it to every row —
    // deciding per row cannot tell `a | b | ` (a blank last cell) from `| a | b |` (edge pipes),
    // and guessing wrong shifts every column off its parameter.
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

    // **The scenario column is optional**, so column-to-parameter mapping cannot assume it.
    // A table with one more column than the method has parameters has a scenario column, which
    // is a display name and feeds nothing; a table whose counts match has none, and column 0
    // feeds parameter 0. Assuming it is always present shifts every column one parameter left
    // and makes an expectation column look like the collection column beside it.
    const params = signature ? parseParameterList(signature[2]) : [];
    tables.push({
      method: signature ? signature[1] : null,
      params,
      hasScenarioColumn: headers.length === params.length + 1,
      headers,
      rows,
    });
  }
  return tables;
}

/**
 * Walk every data cell of every table as {table, rowIndex, columnIndex, header, cell, param}.
 * `param` is the declared parameter the column feeds, or null for the scenario column.
 */
function eachDataCell(content) {
  const cells = [];
  for (const table of tableTestTables(content)) {
    table.rows.forEach((row, rowIndex) => {
      row.forEach((cell, columnIndex) => {
        cells.push({
          table,
          rowIndex,
          columnIndex,
          header: table.headers[columnIndex] || `column ${columnIndex + 1}`,
          cell,
          param: table.params[columnIndex - (table.hasScenarioColumn ? 1 : 0)] || null,
        });
      });
    });
  }
  return cells;
}

/** Standard shape for a checker that reports the first few violations it found. */
function verdict(violations, cleanEvidence) {
  return {
    passed: violations.length === 0,
    evidence: violations.length === 0 ? cleanEvidence : violations.slice(0, 3).join("; "),
  };
}

/**
 * Get content to check: prefer generated files from allFiles, fall back to fileContent.
 */
function getCheckContent(fileContent, allFiles) {
  if (allFiles && allFiles.length > 0) {
    return allFiles.map(f => f.content).join('\n\n');
  }
  return fileContent;
}

/**
 * Get test source content only (src/**.java|kt) — excludes build files, so
 * code-level checks aren't tripped by build-file references (e.g. a leftover
 * dependency coordinate). Falls back to fileContent when no test sources exist.
 */
function getTestSourceContent(fileContent, allFiles) {
  const testFiles = (allFiles || []).filter(f => /\.(java|kt)$/.test(f.path) && /(^|\/)src\//.test(f.path));
  if (testFiles.length > 0) {
    return testFiles.map(f => f.content).join('\n\n');
  }
  return fileContent;
}

/**
 * Test source files as {path, content}, so a checker reporting line numbers can
 * name the file the reader will open. Falls back to a single unnamed entry
 * holding fileContent when no test sources were collected.
 */
function getTestSourceFiles(fileContent, allFiles) {
  const testFiles = (allFiles || []).filter(f => /\.(java|kt)$/.test(f.path) && /(^|\/)src\//.test(f.path));
  if (testFiles.length > 0) {
    return testFiles;
  }
  return [{ path: null, content: fileContent }];
}

/**
 * Split source lines into one block per member declaration: the annotations
 * preceding a method plus its signature. A block never spans a method
 * boundary, so an annotation belonging to an earlier method cannot be read as
 * part of a later one. Returns arrays of 0-based line indices.
 */
function splitIntoMemberBlocks(lines) {
  const methodSignature = /(?:fun|void|boolean|int|long|double|float|String|[A-Z]\w*(?:<[^>]*>)?)\s+(?:\w+|`[^`]+`)\s*\([^)]*\)\s*(?::\s*\S+\s*)?(?:throws\s+[^{]*)?\{/;
  const blocks = [];
  let current = [];

  for (let i = 0; i < lines.length; i++) {
    current.push(i);
    const endsBlock = methodSignature.test(lines[i]) || /^\s*\}/.test(lines[i]);
    if (endsBlock) {
      blocks.push(current);
      current = [];
    }
  }
  if (current.length > 0) blocks.push(current);

  return blocks;
}

/**
 * Joined build-file content (pom.xml / build.gradle / build.gradle.kts), or null.
 */
function getBuildFileContent(allFiles) {
  const buildFiles = (allFiles || []).filter(f =>
    f.path === "pom.xml" || f.path === "build.gradle" || f.path === "build.gradle.kts"
  );
  if (buildFiles.length === 0) return null;
  return buildFiles.map(f => f.content).join('\n');
}

/**
 * Checker factory: build files must no longer reference the old test framework.
 */
function dependencyRemovedChecker(label, regex) {
  return ({ allFiles }) => {
    const content = getBuildFileContent(allFiles);
    if (content === null) {
      return { passed: true, evidence: "No build file in outputs — nothing to check" };
    }
    const found = regex.test(content);
    return {
      passed: !found,
      evidence: found
        ? `Build file still references ${label}`
        : `Build file has no ${label} reference`,
    };
  };
}

/**
 * Python test source content only (*.py files) — falls back to fileContent
 * when no Python files were collected.
 */
function getPythonTestContent(fileContent, allFiles) {
  const pyFiles = (allFiles || []).filter(f => f.path.endsWith(".py"));
  if (pyFiles.length > 0) {
    return pyFiles.map(f => f.content).join("\n\n");
  }
  return fileContent;
}

/**
 * Swift test source content only (*.swift files, excluding Package.swift and
 * Sources/) — falls back to fileContent when no Swift test files were collected.
 */
function getSwiftTestContent(fileContent, allFiles) {
  const swiftFiles = (allFiles || []).filter(f =>
    f.path.endsWith(".swift")
    && !/(^|\/)Package\.swift$/.test(f.path)
    && !/(^|\/)Sources\//.test(f.path)
  );
  if (swiftFiles.length > 0) {
    return swiftFiles.map(f => f.content).join("\n\n");
  }
  return fileContent;
}

/**
 * Extract the argument text of each @Test attribute using paren-counting,
 * so `arguments:` detection cannot leak across attribute boundaries.
 * @Test with no parenthesised arguments yields an empty string.
 */
function extractSwiftTestAttributes(content) {
  const results = [];
  const testAttrRegex = /@Test\b/g;
  let match;

  while ((match = testAttrRegex.exec(content)) !== null) {
    const after = content.slice(match.index + match[0].length);
    const parenStart = after.search(/\S/);
    if (parenStart === -1 || after[parenStart] !== "(") {
      results.push("");
      continue;
    }
    let depth = 1;
    let i = parenStart + 1;
    while (i < after.length && depth > 0) {
      if (after[i] === "(") depth++;
      else if (after[i] === ")") depth--;
      i++;
    }
    results.push(after.slice(parenStart + 1, i - 1));
  }

  return results;
}

/**
 * Extract function bodies of @Test-annotated functions using brace-counting.
 * Returns array of {name, body} objects.
 */
function extractSwiftTestFunctionBodies(content) {
  const results = [];
  const testAttrRegex = /@Test\b/g;
  let match;

  while ((match = testAttrRegex.exec(content)) !== null) {
    const after = content.slice(match.index);
    const funcMatch = after.match(/\bfunc\s+(\w+|`[^`]+`)\s*\([^)]*\)[^{]*\{/);
    if (!funcMatch) continue;

    const braceStart = match.index + after.indexOf(funcMatch[0]) + funcMatch[0].length - 1;
    let depth = 1;
    let i = braceStart + 1;
    while (i < content.length && depth > 0) {
      if (content[i] === "{") depth++;
      else if (content[i] === "}") depth--;
      i++;
    }

    results.push({ name: funcMatch[1], body: content.slice(braceStart + 1, i - 1) });
  }

  return results;
}

// --- Checkers ---

const checkers = {
  "has-tabletest-annotation": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    const found = /@TableTest/.test(content);
    return {
      passed: found,
      evidence: found ? "Found @TableTest annotation" : "No @TableTest annotation found",
    };
  },

  "annotation-order": ({ fileContent, allFiles }) => {
    // Within each method's annotation block, require @DisplayName < @Description < @TableTest.
    // Blocks are per method and per file, so an earlier method's @Description is never
    // read as this method's.
    const violations = [];
    // How many @TableTest methods actually carried an annotation this could order. A method with
    // neither @DisplayName nor @Description has nothing to get wrong, so it passes — correctly,
    // but for no reason the assertion is about. Counting them is what stops a later reader taking
    // "annotations are in the right order" for a win when the real change was the annotations
    // disappearing: iteration-49 recorded exactly that on eval-20.
    let orderable = 0;

    for (const file of getTestSourceFiles(fileContent, allFiles)) {
      const lines = file.content.split('\n');
      const where = file.path === null ? "" : `${file.path} `;

      for (const block of splitIntoMemberBlocks(lines)) {
        const lineOf = (regex) => {
          const index = block.find(i => regex.test(lines[i]));
          return index === undefined ? -1 : index;
        };
        const tableTestLine = lineOf(/@TableTest\s*\(/);
        if (tableTestLine === -1) continue;

        const displayNameLine = lineOf(/@DisplayName/);
        const descriptionLine = lineOf(/@Description/);
        if (displayNameLine !== -1 || descriptionLine !== -1) orderable++;

        if (displayNameLine !== -1 && descriptionLine !== -1 && displayNameLine > descriptionLine) {
          violations.push(`${where}@DisplayName (line ${displayNameLine + 1}) after @Description (line ${descriptionLine + 1})`);
        }
        if (displayNameLine > tableTestLine) {
          violations.push(`${where}@DisplayName (line ${displayNameLine + 1}) after @TableTest (line ${tableTestLine + 1})`);
        }
        if (descriptionLine > tableTestLine) {
          violations.push(`${where}@Description (line ${descriptionLine + 1}) after @TableTest (line ${tableTestLine + 1})`);
        }
      }
    }

    if (violations.length === 0 && orderable === 0) {
      return {
        passed: true,
        evidence:
          "VACUOUS: no @TableTest method carries a @DisplayName or @Description, so there was no " +
          "ordering to check. This pass is not evidence that ordering improved — do not read a " +
          "move onto it as a win.",
      };
    }
    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? `Annotations in correct order (@DisplayName, @Description, @TableTest) across ${orderable} annotated method(s)`
        : `Order violations: ${violations.join("; ")}`,
    };
  },

  "no-if-switch-in-method": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    const methods = extractTableTestMethodBodies(content);
    if (methods.length === 0) {
      if (!/@TableTest/.test(content)) {
        return { passed: false, evidence: "No @TableTest methods found" };
      }
      // @TableTest present but bodies unparseable: checker limitation, not a
      // model failure — do not record a spurious fail.
      return { passed: true, evidence: "Could not parse @TableTest method bodies (checker limitation) — review manually" };
    }

    const violations = [];
    for (const m of methods) {
      if (/\bif\s*\(/.test(m.body)) violations.push("if statement found in method body");
      if (/\bswitch\s*\(/.test(m.body)) violations.push("switch statement found in method body");
      // A ternary's `?` and `:` always sit in one statement, so `[^;]` bounds the search
      // without anchoring it to a single line — a ternary broken across lines to fit a
      // margin is the same rule in the body. `(?<!<)` keeps `Class<? extends Throwable>`
      // from reading as one.
      if (/(?<!<)\?[^;]*:/.test(m.body)) violations.push("ternary operator found in method body");
    }

    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? `${methods.length} method(s) checked — no if/switch/ternary`
        : violations.join("; "),
    };
  },

  "scenario-column-present": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    const headerSets = parseTableHeaders(content);
    if (headerSets.length === 0) {
      return { passed: false, evidence: "No table headers found" };
    }

    const failures = [];
    for (const headers of headerSets) {
      const first = headers[0];
      if (first && first.endsWith("?")) {
        failures.push(`First column "${first}" ends with '?' (output column, not scenario)`);
      }
    }

    return {
      passed: failures.length === 0,
      evidence: failures.length === 0
        ? `First column(s): ${headerSets.map(h => `"${h[0]}"`).join(", ")}`
        : failures.join("; "),
    };
  },

  "has-question-mark-column": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    const headerSets = parseTableHeaders(content);
    if (headerSets.length === 0) {
      return { passed: false, evidence: "No table headers found" };
    }

    const qCols = [];
    for (const headers of headerSets) {
      for (const h of headers) {
        if (h.endsWith("?")) qCols.push(h);
      }
    }

    return {
      passed: qCols.length > 0,
      evidence: qCols.length > 0
        ? `Question-mark columns: ${qCols.join(", ")}`
        : "No column names ending with '?'",
    };
  },

  "description-uses-textblock": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    if (!/@Description/.test(content)) {
      return { passed: true, evidence: "No @Description present — passes by default" };
    }

    // Check for string concatenation with + in @Description
    const descRegex = /@Description\s*\(([\s\S]*?)\)\s*(?=@|\bpublic\b|\bprivate\b|\bprotected\b|\bvoid\b)/g;
    let match;
    const violations = [];

    while ((match = descRegex.exec(content)) !== null) {
      const descContent = match[1].trim();
      if (descContent.includes('"""')) continue; // text block — good
      if (/"\s*\+\s*"/.test(descContent)) {
        violations.push("@Description uses string concatenation with +");
      }
    }

    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? "@Description uses text block or is short single-line"
        : violations.join("; "),
    };
  },

  "uses-value-sets": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    // Value set syntax: {val1, val2} in table data
    const found = /\{[^}]+,\s*[^}]+\}/.test(content);
    return {
      passed: found,
      evidence: found ? "Value set syntax found" : "No value set syntax {a, b} found",
    };
  },

  "single-assertion-in-method": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    const methods = extractTableTestMethodBodies(content);
    if (methods.length === 0) {
      if (!/@TableTest/.test(content)) {
        return { passed: false, evidence: "No @TableTest methods found" };
      }
      return { passed: true, evidence: "Could not parse @TableTest method bodies (checker limitation) — review manually" };
    }

    // A @TableTest body executes identically for every row, so several
    // unconditional assertions (one per observable output of a result object
    // that has no value equality) are still a single uniform pattern. What the
    // assertion forbids is *different assertions per scenario* — a body that
    // branches on a row value to choose what or how it asserts. That requires
    // row-dependent control flow: if/switch/ternary (also caught by
    // no-if-switch-in-method) or a try/catch that asserts exceptions on some
    // rows and values on others. Count of assertion statements is irrelevant.
    const violations = [];
    for (const m of methods) {
      const smells = [];
      if (/\bif\s*\(/.test(m.body)) smells.push("if");
      if (/\bswitch\s*\(/.test(m.body)) smells.push("switch");
      if (/\?[^:\n]*:/.test(m.body)) smells.push("ternary");
      if (/\btry\s*\{/.test(m.body) && /\bcatch\s*\(/.test(m.body)) smells.push("try/catch");
      if (smells.length > 0) {
        violations.push(`${m.name} branches the assertion on the row (${smells.join(", ")})`);
      }
    }

    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? `${methods.length} method(s) apply one uniform assertion pattern to all rows`
        : violations.join("; "),
    };
  },

  "has-three-data-rows": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    const totalRows = countDataRows(content);
    return {
      passed: totalRows === 3,
      evidence: `Found ${totalRows} data row(s)`,
    };
  },

  "fewer-than-nine-rows": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    const totalRows = countDataRows(content);
    return {
      passed: totalRows < 9,
      evidence: `Found ${totalRows} data row(s)`,
    };
  },

  "no-groovy-syntax": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const patterns = [
      { regex: /\bdef\s+/, label: "def keyword" },
      { regex: /\bwhere\s*:/, label: "where:" },
      { regex: /\bexpect\s*:/, label: "expect:" },
      { regex: /\bgiven\s*:/, label: "given:" },
      { regex: /#\w+/, label: "GString interpolation" },
      { regex: /\bthrown\s*\(/, label: "thrown()" },
      { regex: /\bold\s*\(/, label: "old()" },
    ];

    const found = [];
    for (const p of patterns) {
      if (p.regex.test(content)) found.push(p.label);
    }

    return {
      passed: found.length === 0,
      evidence: found.length === 0
        ? "No Groovy/Spock syntax found"
        : `Found: ${found.join(", ")}`,
    };
  },





  "spock-dependency-removed": dependencyRemovedChecker("Spock/Groovy", /org\.spockframework|spock-core|org\.codehaus\.groovy|apache\.groovy|groovy-all/),


  "has-descriptive-title": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    if (/@DisplayName/.test(content)) {
      return { passed: true, evidence: "@DisplayName annotation present" };
    }
    const methods = extractTableTestMethodBodies(content);
    if (methods.length === 0) {
      return { passed: false, evidence: "No @TableTest methods found" };
    }
    const offenders = [];
    for (const m of methods) {
      const raw = m.name;
      if (raw.startsWith("`")) {
        // Kotlin backtick name: qualifies if it reads as a multi-word phrase
        if (raw.slice(1, -1).trim().split(/\s+/).length < 2) offenders.push(raw);
        continue;
      }
      if (/^test(\d+|method|case\d*)?$/i.test(raw)) {
        offenders.push(raw);
        continue;
      }
      const words = raw.replace(/([a-z0-9])([A-Z])/g, "$1 $2").split(/[\s_]+/).filter(Boolean);
      if (words.length < 2) offenders.push(raw);
    }
    return {
      passed: offenders.length === 0,
      evidence: offenders.length === 0
        ? `${methods.length} @TableTest method name(s) read as descriptive titles (no @DisplayName needed)`
        : `Non-descriptive method name(s): ${offenders.join(", ")}`,
    };
  },

  "has-tabletest-dependency": ({ fileContent, allFiles }) => {
    // Deliberately version-agnostic: the assertion text requires the artifact, not a
    // particular release. A pinned version here silently fails every correct output
    // from the day the library is bumped, which reads as a skill regression.
    const buildFiles = (allFiles || []).filter(f =>
      f.path === "pom.xml" || f.path === "build.gradle" || f.path === "build.gradle.kts"
    );
    if (buildFiles.length === 0) {
      // Fall back to checking fileContent (response.md) for build file snippets
      if (/tabletest-junit/.test(fileContent)) {
        return { passed: true, evidence: "Found tabletest-junit in response" };
      }
      return { passed: false, evidence: "No build file found in outputs and no tabletest-junit reference in response" };
    }
    const content = buildFiles.map(f => f.content).join('\n');
    if (/tabletest-junit/.test(content)) {
      return { passed: true, evidence: "Build file contains tabletest-junit" };
    }
    return { passed: false, evidence: "Build file does not contain tabletest-junit dependency" };
  },

  "null-as-blank-cell": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    // Check that no table data row contains literal "null" as a cell value
    const tableTestRegex = /@TableTest\s*\(\s*(?:value\s*=\s*)?["\"]{3}([\s\S]*?)["\"]{3}\s*\)/g;
    let match;
    const violations = [];

    while ((match = tableTestRegex.exec(content)) !== null) {
      const tableContent = match[1];
      const lines = tableContent.split('\n').map(l => l.trim()).filter(l => l.length > 0 && l.includes('|'));
      // Skip header (first line), check data rows
      for (let i = 1; i < lines.length; i++) {
        const cells = lines[i].split('|').map(c => c.trim()).filter(c => c.length > 0);
        // Skip first cell (scenario column), check remaining for literal "null"
        for (let j = 1; j < cells.length; j++) {
          if (cells[j].toLowerCase() === 'null') {
            violations.push(`Row ${i}: cell "${cells[j]}" is literal null (should be blank)`);
          }
        }
      }
    }

    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? "No literal null values found in table data cells"
        : violations.slice(0, 3).join("; "),
    };
  },

  "empty-string-uses-quotes": ({ fileContent, allFiles }) => {
    const content = getCheckContent(fileContent, allFiles);
    // Check that at least one table cell contains "" or '' (quoted empty string)
    const found = /\|\s*(['"])\1\s*(?:\||$)/m.test(content);
    return {
      passed: found,
      evidence: found
        ? "Found quoted empty string in table cell"
        : "No quoted empty string ('\"\"' or \"''\") found in table cells",
    };
  },

  "list-syntax-correct": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const listCells = eachDataCell(content).filter((c) => c.param && LIST_TYPE.test(c.param.type));
    const violations = listCells
      .filter((c) => c.cell !== "" && !c.cell.startsWith("["))
      .map((c) => `${c.table.method} row ${c.rowIndex + 1}, column "${c.header}": ${c.cell} is not bracket syntax`);
    if (listCells.length === 0) {
      return { passed: true, evidence: "No List-typed column in any table — nothing to check." };
    }
    return verdict(violations, `All ${listCells.length} List-typed cell(s) use bracket syntax.`);
  },

  "set-syntax-correct": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const setCells = eachDataCell(content).filter((c) => c.param && SET_TYPE.test(c.param.type));
    const violations = setCells
      .filter((c) => c.cell !== "" && !c.cell.startsWith("{"))
      .map((c) => `${c.table.method} row ${c.rowIndex + 1}, column "${c.header}": ${c.cell} is not brace syntax`);
    if (setCells.length === 0) {
      return { passed: true, evidence: "No Set-typed column in any table — nothing to check." };
    }
    return verdict(violations, `All ${setCells.length} Set-typed cell(s) use brace syntax.`);
  },

  "empty-list-explicit": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const listCells = eachDataCell(content).filter((c) => c.param && LIST_TYPE.test(c.param.type));
    if (listCells.length === 0) {
      return { passed: true, evidence: "No List-typed column in any table — nothing to check." };
    }
    const explicit = listCells.find((c) => c.cell === "[]");
    return {
      passed: Boolean(explicit),
      evidence: explicit
        ? `${explicit.table.method} row ${explicit.rowIndex + 1} writes the empty list as [] in column "${explicit.header}".`
        : "No List-typed cell is written as []; an empty list is never distinguished from a blank cell (null).",
    };
  },

  "special-chars-quoted": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const violations = [];
    for (const c of eachDataCell(content)) {
      const elements = parseCollectionElements(c.cell);
      if (!elements) continue;
      // A Map cell is written `[k: v]`, so its colons are structure. Only a List or Set column
      // turns an unquoted colon into a mis-parse — the element becomes a map entry.
      const colonIsData = c.param && (LIST_TYPE.test(c.param.type) || SET_TYPE.test(c.param.type));
      for (const element of elements) {
        if (element === "" || isQuoted(element) || isNestedCollection(element)) continue;
        const offenders = [];
        if (colonIsData && element.includes(":")) offenders.push("colon");
        if (element.includes("|")) offenders.push("pipe");
        if (/[\][{}]/.test(element)) offenders.push("bracket");
        if (offenders.length > 0) {
          violations.push(
            `${c.table.method} row ${c.rowIndex + 1}, column "${c.header}": element ${element} contains an unquoted ${offenders.join("/")}`
          );
        }
      }
    }
    return verdict(violations, "Every collection element containing a colon, pipe or bracket is quoted.");
  },

  "pipe-quoted": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const violations = [];
    let quotedPipes = 0;
    for (const c of eachDataCell(content)) {
      const elements = parseCollectionElements(c.cell) || [c.cell];
      for (const element of elements) {
        if (!element.includes("|")) continue;
        if (isQuoted(element) || isNestedCollection(element)) quotedPipes++;
        else violations.push(`${c.table.method} row ${c.rowIndex + 1}, column "${c.header}": ${element} holds an unquoted pipe`);
      }
    }
    if (violations.length === 0 && quotedPipes === 0) {
      return { passed: true, evidence: "No value in any table contains a pipe — nothing to check." };
    }
    return verdict(violations, `All ${quotedPipes} pipe-containing value(s) are quoted.`);
  },

  "no-blank-collection-elements": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const violations = [];
    for (const c of eachDataCell(content)) {
      const elements = parseCollectionElements(c.cell);
      if (!elements) continue;
      elements.forEach((element, i) => {
        if (element === "") {
          violations.push(
            `${c.table.method} row ${c.rowIndex + 1}, column "${c.header}": ${c.cell} has a blank element at position ${i + 1}`
          );
        }
      });
    }
    return verdict(violations, "No collection value contains a blank element.");
  },

  "newline-in-cell": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    // A literal line break inside a row cannot survive as a row: it splits into two lines and the
    // halves carry the wrong number of cells. Column-count agreement with the header is therefore
    // the decidable form of "the newline did not break the row structure".
    const violations = [];
    for (const table of tableTestTables(content)) {
      table.rows.forEach((row, i) => {
        if (row.length !== table.headers.length) {
          violations.push(
            `${table.method} row ${i + 1}: ${row.length} cells against ${table.headers.length} headers — a row split by a literal line break, or an unquoted pipe`
          );
        }
      });
    }
    return verdict(violations, "Every data row has the same cell count as its header; no row is broken by a literal newline.");
  },

  "output-is-kotlin": ({ allFiles }) => {
    const ktFiles = allFiles.filter(f => f.path.endsWith(".kt") && /src\/test\//.test(f.path));
    const javaFiles = allFiles.filter(f => f.path.endsWith(".java") && /src\/test\//.test(f.path));
    if (ktFiles.length > 0 && javaFiles.length === 0) {
      return { passed: true, evidence: `Found ${ktFiles.length} Kotlin test file(s): ${ktFiles.map(f => f.path).join(", ")}` };
    }
    if (ktFiles.length === 0) {
      return { passed: false, evidence: `No Kotlin test files found. Java test files: ${javaFiles.map(f => f.path).join(", ") || "none"}` };
    }
    return { passed: false, evidence: `Mixed output: Kotlin: ${ktFiles.map(f => f.path).join(", ")}; Java: ${javaFiles.map(f => f.path).join(", ")}` };
  },

  "no-parameterized-test": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const found = /@ParameterizedTest/.test(content);
    return {
      passed: !found,
      evidence: found
        ? "Test source uses @ParameterizedTest instead of @TableTest"
        : "No @ParameterizedTest in test source",
    };
  },

  // --- Python (table-driven-testing skill) ---

  "uses-parametrize": ({ fileContent, allFiles }) => {
    const content = getPythonTestContent(fileContent, allFiles);
    const found = /@pytest\.mark\.parametrize/.test(content);
    return {
      passed: found,
      evidence: found ? "Found @pytest.mark.parametrize" : "No @pytest.mark.parametrize found",
    };
  },

  "parametrize-has-ids": ({ fileContent, allFiles }) => {
    const content = getPythonTestContent(fileContent, allFiles);
    if (!/@pytest\.mark\.parametrize/.test(content)) {
      return { passed: false, evidence: "No @pytest.mark.parametrize found" };
    }
    const found = /\bids\s*=/.test(content) || /pytest\.param\([^)]*\bid\s*=/.test(content);
    return {
      passed: found,
      evidence: found
        ? "Parametrized cases carry ids (ids= or pytest.param id=)"
        : "No ids= argument or pytest.param(id=...) found",
    };
  },

  "no-if-in-python-test": ({ fileContent, allFiles }) => {
    const content = getPythonTestContent(fileContent, allFiles);
    const violations = [];
    let inTest = false;
    let defIndent = 0;
    for (const line of content.split("\n")) {
      const def = line.match(/^(\s*)def\s+(\w+)/);
      if (def) {
        inTest = def[2].startsWith("test_");
        defIndent = def[1].length;
        continue;
      }
      if (!inTest || line.trim() === "") continue;
      const indent = line.match(/^(\s*)/)[1].length;
      if (indent <= defIndent) {
        inTest = false;
        continue;
      }
      if (/^\s*(if|elif)\b/.test(line)) violations.push(line.trim());
    }
    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? "No if/elif statements in test function bodies"
        : `Branching in test body: ${violations.slice(0, 3).join("; ")}`,
    };
  },

  // --- Swift (table-driven-testing skill) ---

  "uses-test-arguments": ({ fileContent, allFiles }) => {
    const content = getSwiftTestContent(fileContent, allFiles);
    const attributes = extractSwiftTestAttributes(content);
    if (attributes.length === 0) {
      return { passed: false, evidence: "No @Test attribute found" };
    }
    const found = attributes.some(attr => /\barguments\s*:/.test(attr));
    return {
      passed: found,
      evidence: found
        ? "Found @Test(arguments:)"
        : `${attributes.length} @Test attribute(s) found, none with arguments:`,
    };
  },

  "no-loop-in-swift-test": ({ fileContent, allFiles }) => {
    const content = getSwiftTestContent(fileContent, allFiles);
    const bodies = extractSwiftTestFunctionBodies(content);
    if (bodies.length === 0) {
      return { passed: false, evidence: "No @Test function found" };
    }
    const violations = bodies.filter(({ body }) =>
      /\bfor\s+[\w`(]/.test(body) || /\bwhile\b/.test(body) || /\.forEach\b/.test(body)
    );
    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? "No loops in @Test function bodies"
        : `Loop over cases in @Test body: ${violations.map(v => v.name).join(", ")}`,
    };
  },

  "no-if-in-swift-test": ({ fileContent, allFiles }) => {
    const content = getSwiftTestContent(fileContent, allFiles);
    const bodies = extractSwiftTestFunctionBodies(content);
    if (bodies.length === 0) {
      return { passed: false, evidence: "No @Test function found" };
    }
    const violations = bodies.filter(({ body }) =>
      /\bif\b/.test(body) || /\bswitch\b/.test(body) || /\bguard\b/.test(body)
    );
    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? "No if/switch/guard statements in @Test function bodies"
        : `Branching in @Test body: ${violations.map(v => v.name).join(", ")}`,
    };
  },
};

// Aliases for eval-specific prefixed assertion IDs that map to the same checker
const aliases = {
  "1.12-format-annotation-order": "annotation-order",
  "1.13-format-description-textblock": "description-uses-textblock",
  "2.13-format-annotation-order": "annotation-order",
  "2.14-format-description-textblock": "description-uses-textblock",
  "result-column-with-question-mark": "has-question-mark-column",
  "1.10-format-displayname": "has-descriptive-title",
  "2.10-format-displayname": "has-descriptive-title",
};

for (const [alias, target] of Object.entries(aliases)) {
  if (checkers[target]) checkers[alias] = checkers[target];
}

module.exports = {
  checkers,
  extractTableTestMethodBodies,
  parseTableHeaders,
  countDataRows,
  splitRowCells,
  parseCollectionElements,
  parseParameterList,
  tableTestTables,
};
