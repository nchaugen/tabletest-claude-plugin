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
 * Returns array of {methodSignature, body} objects.
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
    // Kotlin: fun methodName(params) {  or  fun methodName(params): ReturnType {
    const methodMatch = after.match(/(?:fun|void|boolean|int|long|double|float|String|[A-Z]\w*(?:<[^>]*>)?)\s+(\w+)\s*\([^)]*\)\s*(?::\s*\S+\s*)?(?:throws\s+[^{]*)?\{/);
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
    results.push({ methodSignature: methodMatch[0], body });
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
      return { passed: false, evidence: "No @TableTest method bodies found" };
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
      return { passed: false, evidence: "No @TableTest method bodies found" };
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
    const content = getCheckContent(fileContent, allFiles);
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
    const content = getCheckContent(fileContent, allFiles);
    const patterns = [
      { regex: /\bshould\s*\{/, label: "should {" },
      { regex: /\bforAll\s*\(/, label: "forAll(" },
      { regex: /\bdescribe\s*\(/, label: "describe(" },
      { regex: /\bit\s*\(/, label: "it(" },
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
    const content = getCheckContent(fileContent, allFiles);
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
    const content = getCheckContent(fileContent, allFiles);
    const found = /@MethodSource/.test(content);
    return {
      passed: !found,
      evidence: found ? "Found @MethodSource" : "No @MethodSource found",
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
};

// Aliases for eval-specific prefixed assertion IDs that map to the same checker
const aliases = {
  "1.12-format-annotation-order": "annotation-order",
  "1.13-format-description-textblock": "description-uses-textblock",
  "2.13-format-annotation-order": "annotation-order",
  "2.14-format-description-textblock": "description-uses-textblock",
};

for (const [alias, target] of Object.entries(aliases)) {
  if (checkers[target]) checkers[alias] = checkers[target];
}

module.exports = { checkers, extractTableTestMethodBodies, parseTableHeaders, countDataRows };
