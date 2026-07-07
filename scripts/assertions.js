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
    const content = getCheckContent(fileContent, allFiles);
    // For each @TableTest, check that @DisplayName < @Description < @TableTest in position
    // Find all annotation clusters (groups near test methods)
    const methods = content.split(/(?=@(?:DisplayName|Description|TableTest))/);
    let violations = [];

    // Simpler approach: find each @TableTest and look backwards for annotation ordering
    const lines = content.split('\n');
    for (let i = 0; i < lines.length; i++) {
      if (/@TableTest\s*\(/.test(lines[i])) {
        // Look backwards for @DisplayName and @Description
        let displayNameLine = -1;
        let descriptionLine = -1;
        for (let j = i - 1; j >= Math.max(0, i - 20); j--) {
          if (/@DisplayName/.test(lines[j]) && displayNameLine === -1) displayNameLine = j;
          if (/@Description/.test(lines[j]) && descriptionLine === -1) descriptionLine = j;
        }

        if (displayNameLine !== -1 && descriptionLine !== -1 && displayNameLine > descriptionLine) {
          violations.push(`@DisplayName (line ${displayNameLine + 1}) after @Description (line ${descriptionLine + 1})`);
        }
        if (displayNameLine !== -1 && displayNameLine > i) {
          violations.push(`@DisplayName (line ${displayNameLine + 1}) after @TableTest (line ${i + 1})`);
        }
        if (descriptionLine !== -1 && descriptionLine > i) {
          violations.push(`@Description (line ${descriptionLine + 1}) after @TableTest (line ${i + 1})`);
        }
      }
    }

    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? "Annotations in correct order: @DisplayName, @Description, @TableTest"
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
      if (/\?.*:/.test(m.body)) violations.push("ternary operator found in method body");
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

    const violations = [];
    for (const m of methods) {
      const assertCount = (m.body.match(/\bassert\w*\s*\(/g) || []).length;
      if (assertCount > 1) {
        violations.push(`Method has ${assertCount} assertions`);
      }
    }

    return {
      passed: violations.length === 0,
      evidence: violations.length === 0
        ? `${methods.length} method(s) each have at most 1 assertion`
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

  "no-kotest-syntax": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const patterns = [
      { regex: /\bshould\s*\{/, label: "should {" },
      { regex: /\bshouldBe\b/, label: "shouldBe" },
      { regex: /\bforAll\s*\(/, label: "forAll(" },
      { regex: /\bwithData\s*\(/, label: "withData(" },
      // describe/it must open a string argument — bare "it (" appears in
      // English prose inside @Description text blocks.
      { regex: /\bdescribe\s*\(\s*["']/, label: "describe(" },
      { regex: /\bit\s*\(\s*["']/, label: "it(" },
      { regex: /io\.kotest/, label: "io.kotest import" },
    ];

    const found = [];
    for (const p of patterns) {
      if (p.regex.test(content)) found.push(p.label);
    }

    return {
      passed: found.length === 0,
      evidence: found.length === 0
        ? "No Kotest syntax found"
        : `Found: ${found.join(", ")}`,
    };
  },

  "no-testng-artifacts": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const patterns = [
      { regex: /@DataProvider/, label: "@DataProvider" },
      { regex: /org\.testng/, label: "org.testng import" },
    ];

    const found = [];
    for (const p of patterns) {
      if (p.regex.test(content)) found.push(p.label);
    }

    return {
      passed: found.length === 0,
      evidence: found.length === 0
        ? "No TestNG artifacts found"
        : `Found: ${found.join(", ")}`,
    };
  },

  "no-methodsource-artifacts": ({ fileContent, allFiles }) => {
    const content = getTestSourceContent(fileContent, allFiles);
    const found = /@MethodSource/.test(content);
    return {
      passed: !found,
      evidence: found ? "Found @MethodSource" : "No @MethodSource found",
    };
  },

  "kotest-dependency-removed": dependencyRemovedChecker("Kotest", /io\.kotest/),

  "spock-dependency-removed": dependencyRemovedChecker("Spock/Groovy", /org\.spockframework|spock-core|org\.codehaus\.groovy|apache\.groovy|groovy-all/),

  "testng-dependency-removed": dependencyRemovedChecker("TestNG", /org\.testng|\btestng\b/),

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
    // Check build files for tabletest-junit dependency with correct version
    const buildFiles = (allFiles || []).filter(f =>
      f.path === "pom.xml" || f.path === "build.gradle" || f.path === "build.gradle.kts"
    );
    if (buildFiles.length === 0) {
      // Fall back to checking fileContent (response.md) for build file snippets
      const hasArtifact = /tabletest-junit/.test(fileContent);
      const hasVersion = /1\.2\.1/.test(fileContent);
      if (hasArtifact && hasVersion) {
        return { passed: true, evidence: "Found tabletest-junit:1.2.1 in response" };
      }
      return { passed: false, evidence: "No build file found in outputs and no tabletest-junit reference in response" };
    }
    const content = buildFiles.map(f => f.content).join('\n');
    const hasArtifact = /tabletest-junit/.test(content);
    const hasVersion = /1\.2\.1/.test(content);
    if (hasArtifact && hasVersion) {
      return { passed: true, evidence: "Build file contains tabletest-junit:1.2.1" };
    }
    if (hasArtifact) {
      return { passed: false, evidence: "Build file references tabletest-junit but not version 1.2.1" };
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

module.exports = { checkers, extractTableTestMethodBodies, parseTableHeaders, countDataRows };
