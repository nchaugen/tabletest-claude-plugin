# Eval Framework Upgrade: Project Scaffolding + Deterministic Assertions

## Context

Iteration 4 of the tabletest minimal skill variant revealed systemic eval framework issues:
- **Agent wandering**: Eval agents timeout searching for non-existent project files (eval-14, eval-23, eval-26). The agents invoke the skill, then spend 10 minutes doing `Glob *.java` → "No files found" in an empty worktree.
- **Grading timeouts**: The grading step spawns full `claude` processes with tool access. Three grading calls in the same batch timed out at 600s (eval-18, eval-20, eval-22), likely from haiku using tools or process contention.
- **Non-reproducible assertions**: ~50% of assertions (structural checks like annotation order, `@TableTest` presence) are graded by LLM when they could be checked deterministically.

This design addresses all three by: (1) giving evals real project scaffolding, (2) replacing CLI-based grading with direct API calls, and (3) adding deterministic assertion tooling.

## Phased Rollout

- **Phase 0**: Fix grading — direct Anthropic API, no CLI spawn
- **Phase 1**: Pilot 3 evals (1, 14, 25) with scaffolding + deterministic assertions + build verification
- **Phase 2**: Roll out to remaining 16 evals
- **Phase 3**: Require tests-pass for all source-code evals

## Phase 0: Direct API Grading

### Problem

`gradeOne()` in `run-evals.js` calls `runClaude()` which spawns a `claude` CLI process with all tools available. This is overkill for grading — the model just needs to return JSON. The CLI spawn adds:
- Process startup + plugin loading overhead
- Tool access (haiku can use Bash, Read, Glob instead of just answering)
- Session initialization
- Risk of tool loops causing 600s timeouts

### Solution

Replace `runClaude()` in the grading path with a direct `fetch()` to `https://api.anthropic.com/v1/messages`:

```js
async function gradeViaApi(systemPrompt, userPrompt, model) {
  const resp = await fetch("https://api.anthropic.com/v1/messages", {
    method: "POST",
    headers: {
      "x-api-key": process.env.ANTHROPIC_API_KEY,
      "anthropic-version": "2023-06-01",
      "content-type": "application/json",
    },
    body: JSON.stringify({
      model: resolveModel(model),
      max_tokens: 4096,
      system: systemPrompt,
      messages: [{ role: "user", content: userPrompt }],
    }),
  });
  const data = await resp.json();
  return data.content[0].text;
}
```

Model resolution: `"haiku"` → `"claude-haiku-4-5-20251001"`, `"sonnet"` → `"claude-sonnet-4-6"`, `"opus"` → `"claude-opus-4-6"`. Pass through any model string that already contains a dash (already a full model ID).

The grading system prompt, JSON format, and parsing logic stay the same. Only the transport changes.

### Files to modify

- `scripts/run-evals.js`: Replace `runClaude()` call in `gradeOne()` with `gradeViaApi()`. Add `resolveModel()` helper.

## Phase 1: Pilot Scaffolding + Deterministic Assertions

### Pilot evals

| Eval | Type | Language | Build tool | Has source code |
|------|------|----------|------------|-----------------|
| eval-1 (convert repetitive tests) | Simple conversion | Java | Maven | Yes — `DiscountService.java` |
| eval-14 (weekly pay) | Spec-based, no source | Java | Gradle | No |
| eval-25 (convert from Spock) | Framework conversion | Kotlin | Gradle (KTS) | Yes — `ShippingCostCalculator.java` + domain classes |

### Project scaffolding structure

Each eval gains a `project/` directory:

```
evals/tabletest/eval-1-convert-repetitive-tests/
  prompt.md             ← unchanged
  eval.json             ← gains assertion type field
  expected_output.md
  project/              ← NEW
    pom.xml
    src/main/java/com/example/DiscountService.java
    src/test/java/.gitkeep
```

```
evals/tabletest/eval-14-weekly-pay/
  project/
    build.gradle
    src/main/java/.gitkeep
    src/test/java/.gitkeep
```

```
evals/tabletest/eval-25-convert-from-spock/
  project/
    build.gradle.kts
    src/main/java/com/example/ShippingCostCalculator.java
    src/main/java/com/example/ShippingZone.java
    src/main/java/com/example/PackageOptions.java
    src/main/java/com/example/Carrier.java
    src/test/kotlin/.gitkeep
```

**Prompts are NOT updated** — the agent discovers the project structure via its tools. This tests whether the skill + realistic project is enough to guide the agent without explicit instructions.

### Worktree setup changes

In `run-evals.js`, the worktree setup (`setupWorktree`) gains a step: if the current eval has a `project/` directory, copy its contents to the worktree root before running the agent. The eval prompt files stay in `evals/` as before.

After the agent finishes, find generated test files in the worktree (`src/test/**/*.java`, `src/test/**/*.kt`) and copy them to the eval's `outputs/` directory alongside `response.md`.

### Build verification

After output collection, detect the build tool and run:

| Build tool | Compile check | Test check |
|------------|--------------|------------|
| Maven (`pom.xml`) | `mvn compile test-compile -q` | `mvn test -q` |
| Gradle (`build.gradle` or `build.gradle.kts`) | `gradle compileTestJava -q` or `gradle compileTestKotlin -q` | `gradle test -q` |

Results stored in `outputs/build-result.json`:
```json
{
  "compiles": true,
  "tests_pass": true,
  "compile_output": "",
  "test_output": ""
}
```

For spec-only evals (eval-14), `tests_pass` is `null` (not checked).

### Assertion type field

`eval.json` assertions gain a `"type"` field:

```json
{
  "id": "has-tabletest-annotation",
  "text": "Output contains a @TableTest annotation",
  "type": "deterministic"
}
```

Types:
- `"deterministic"` — checked by `scripts/assertions.js` against generated source files
- `"build"` — checked by build verification (compiles, tests-pass)
- `"llm"` — graded by Anthropic API (default, for backwards compatibility)

All existing assertions in the 3 pilot evals get explicit types. Remaining evals keep `"llm"` as default until phase 2.

### Deterministic assertion library

New file: `scripts/assertions.js`

Exports a map of assertion ID → checker function. Each checker takes `{fileContent, allFiles}` and returns `{passed: boolean, evidence: string}`.

#### Assertions that become deterministic

| Assertion ID | Frequency | Check method |
|---|---|---|
| `has-tabletest-annotation` | 16 evals | Regex: `/@TableTest/` |
| `annotation-order` | 16 evals | Regex position: `@DisplayName` < `@Description` < `@TableTest` |
| `no-if-switch-in-method` | 15 evals | Extract method bodies via brace-counting, check for `if (` / `switch (` |
| `scenario-column-present` | 13 evals | Parse `@TableTest` table header, check first column doesn't end with `?` |
| `has-question-mark-column` | 12 evals | Parse table headers, check any ends with `?` |
| `description-uses-textblock` | 12 evals | Regex: `@Description` uses `"""` not `+` concatenation |
| `uses-value-sets` | 5 evals | Regex: `\{[^}]+,\s*[^}]+\}` in table data |
| `single-assertion-in-method` | 3 evals | Count `assert` calls in method body |
| `has-three-data-rows` | 1 eval | Count pipe-delimited data rows in table |
| `fewer-than-nine-rows` | 1 eval | Count data rows |
| `no-groovy-syntax` | 1 eval | Regex: no `def `, `where:`, `expect:` |
| `no-kotest-syntax` | 1 eval | Regex: no kotest patterns |
| `no-testng-artifacts` | 1 eval | Regex: no `@DataProvider`, `org.testng` |
| `no-methodsource-artifacts` | 1 eval | Regex: no `@MethodSource` |

#### Method body extraction (brace-counting)

To check `no-if-switch-in-method` and `single-assertion-in-method`, we extract `@TableTest`-annotated method bodies:

1. Find each `@TableTest` annotation
2. Scan forward to the method signature (next `void` or return type + method name + `(`)
3. Find the opening `{` after the parameters
4. Count braces to find the matching `}`
5. The content between is the method body

This is simple brace-counting, not a full parser. It handles 95%+ of cases. Edge case: braces inside string literals within method bodies — but TableTest tables are in annotations (above the method), not in the body, so this is safe.

#### Assertions that remain LLM-graded

| Assertion ID | Frequency | Why LLM needed |
|---|---|---|
| `has-descriptive-title` | 16 evals | Subjective — is the name "descriptive"? |
| `concerns-decomposed` | 13 evals | Subjective — are concerns properly separated? |
| `scenario-names-describe-conditions` | 12 evals | Subjective — conditions vs outcomes |
| `business-language-columns` | 11 evals | Subjective — domain vs implementation terms |
| `minimal-rows-per-concern` | 9 evals | Subjective — are there unnecessary rows? |
| `description-if-present-adds-information` | 5 evals | Subjective — does description add value? |
| All eval-specific domain assertions | 1-2 evals each | Domain-specific judgment |

### Pipeline flow

```
generateOne(eval):
  1. Setup worktree with project scaffolding (if project/ exists)
  2. Run claude agent (existing flow)
  3. Save response.md (existing flow)
  4. Collect generated test files from worktree → outputs/
  5. Run build verification → build-result.json

gradeOne(eval):
  1. Run deterministic assertions against generated files
  2. Run build assertions from build-result.json
  3. Send only LLM-typed assertions to gradeViaApi()
  4. Merge all assertion results into grading.json
```

### New files

| File | Purpose |
|------|---------|
| `scripts/assertions.js` | Deterministic assertion checker library |
| `evals/tabletest/eval-1-*/project/` | Maven project scaffolding + DiscountService.java |
| `evals/tabletest/eval-14-*/project/` | Gradle project scaffolding (no source) |
| `evals/tabletest/eval-25-*/project/` | Kotlin Gradle project + shipping domain classes |

### Modified files

| File | Changes |
|------|---------|
| `scripts/run-evals.js` | Add `gradeViaApi()`, modify `setupWorktree()` for project scaffolding, modify `generateOne()` for output collection + build verification, modify `gradeOne()` for deterministic + LLM split |
| `evals/tabletest/eval-1-*/eval.json` | Add `type` field to all assertions |
| `evals/tabletest/eval-14-*/eval.json` | Add `type` field, add `compiles` assertion |
| `evals/tabletest/eval-25-*/eval.json` | Add `type` field, add `compiles` + `tests-pass` assertions |

## Verification

### Phase 0 verification
- Run `--grade-only` on iteration 4 outputs with the new API grading
- Confirm evals 18, 20, 22 grade successfully (no timeout)
- Compare grading results with existing grading.json to ensure consistency

### Phase 1 verification
- Run eval-1 with scaffolding: agent should find DiscountService.java, write test, code compiles, tests pass
- Run eval-14 with scaffolding: agent should see the project structure, write test in correct location, code compiles
- Run eval-25 with scaffolding: agent should find source classes, write Kotlin test, code compiles, tests pass
- Deterministic assertions match LLM grading on the same outputs (cross-validate)
- No eval agent timeouts from wandering
