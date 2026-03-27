# Eval Automation — Design

**Date:** 2026-03-22
**Status:** Draft

## Context

The tabletest-claude-plugin validates its skills via a manual eval benchmark in `skills-workspace/`. Eval runs involve invoking the skill with a prompt, capturing output, grading assertions, and aggregating results. This process is entirely manual today, which creates two problems:

1. **Contamination risk:** Experiment documents in the same repo contain ideal answer tables for eval features. An agent exploring the codebase during an eval run could discover and copy them.
2. **Protocol drift:** Without automation, steps get skipped or done inconsistently across iterations.

## Goals

1. Automate eval generation (running the skill with eval prompts and capturing output)
2. Automate eval grading (LLM-as-judge against predefined assertions)
3. Enforce contamination isolation by default
4. Support regression detection between skill iterations
5. Produce a readable eval report in markdown

## Non-Goals

- Automating skill improvement (this is a measurement tool, not an optimiser)
- Real-time CI integration (runs are triggered manually)
- HTML report generation (markdown is sufficient)

## Design

### Script

`scripts/run-evals.js` — a Node.js script that orchestrates eval runs.

### Three Phases

#### Phase 1: Setup

1. Create a temporary git worktree from current HEAD
2. Remove `docs/superpowers/experiments/` from the worktree (contamination prevention)
3. Parse `skills-workspace/evals/evals.json`
4. Create the iteration directory structure under `skills-workspace/iteration-N/`

#### Phase 2: Generate Responses

For each eval, invoke `claude` CLI in a clean, isolated environment:

```bash
claude --bare --print \
  --plugin-dir ./ \
  --model sonnet \
  --output-format json \
  -p "<eval prompt>"
```

Key flags:
- `--bare` — no user CLAUDE.md, no hooks, no auto-memory, no plugin sync. Skills still resolve via the plugin.
- `--plugin-dir ./` — loads only this plugin's skills (tabletest + spec-by-example)
- `--output-format json` — returns structured output including token counts
- `--print` — non-interactive, captures output to stdout

The working directory for the claude invocation is the clean worktree (no experiment documents).

**Output:** Save to `iteration-N/eval-X-name/with_skill/outputs/response.md` and `timing.json`.

**Baseline mode (`--baseline` flag):** Also run each eval without the plugin:

```bash
claude --bare --print \
  --model sonnet \
  --output-format json \
  -p "<eval prompt>"
```

Save to `iteration-N/eval-X-name/no_skill/outputs/response.md` and `timing.json`.

The baseline run measures the absolute value of the skill — whether it helps compared to no skill at all. This is useful periodically as models improve, but is not needed for every iteration.

**Parallelism:** Evals are independent. Run up to N in parallel (default 4, configurable via `--parallel`).

#### Phase 3: Grade Responses

For each captured response, run a separate `claude` invocation as a grader:

```bash
claude --bare --print \
  --model sonnet \
  --output-format json \
  --system-prompt "<grading system prompt>" \
  -p "<grading prompt with assertions and response>"
```

**Grading system prompt:**

```
You are an eval grader. You will receive a model response and a list of assertions.
For each assertion, determine whether it passes or fails based on the response content.

Return a JSON object with this exact structure:
{
  "assertions": [
    {
      "id": "<assertion id>",
      "text": "<assertion text>",
      "passed": true/false,
      "evidence": "<direct quote or specific reference from the response supporting your judgement>"
    }
  ]
}

Rules:
- Be strict. An assertion passes only if clearly demonstrated in the response.
- The evidence field must contain a direct quote from the response, not your interpretation.
- If the assertion is about absence (e.g. "does NOT invent..."), evidence should explain what you checked and why it passes/fails.
```

**Grading prompt** (per eval):

```
## Assertions

<assertions from evals.json>

## Expected Output Description

<expected_output from evals.json>

## Model Response

<captured response>
```

**Output:** Save to `grading.json`. Compute `assertions_passed`, `assertions_total`, `pass_rate`.

### Aggregation

After all evals are graded:

1. **`benchmark.json`** — aggregate all results:
   ```json
   {
     "skill_name": "tabletest + spec-by-example",
     "iteration": N,
     "model": "sonnet",
     "evals": [
       {
         "id": "eval-14-weekly-pay",
         "skill": "tabletest + spec-by-example",
         "results": {
           "with_skill": {
             "assertions_passed": 10,
             "assertions_total": 11,
             "pass_rate": 0.909,
             "failed_assertions": ["1.6-readability-empty-cells"],
             "total_tokens": 15000,
             "duration_ms": 30000
           }
         }
       }
     ],
     "summary": {
       "with_skill": {
         "assertions_passed": 40,
         "assertions_total": 44,
         "pass_rate": 0.909
       }
     }
   }
   ```

2. **`eval-review.md`** — human-readable markdown report:
   - Summary: overall pass rate, total tokens, total duration
   - Delta vs previous iteration (if exists): regressions flagged, improvements noted
   - Per-eval breakdown: pass/fail per assertion with evidence
   - Baseline comparison (if `--baseline` was used)

### Regression Detection

The script loads the previous iteration's `benchmark.json` (if it exists) and compares:

- **Per-assertion:** Any assertion that passed before and fails now is a **regression**
- **Per-eval:** Any eval whose pass rate dropped is flagged
- **Overall:** Summary delta (e.g., "+2 assertions, -1 regression")

Regressions are highlighted in the eval-review.md report.

### CLI Interface

```bash
# Run all evals for a new iteration
node scripts/run-evals.js --iteration 4

# Run specific evals only
node scripts/run-evals.js --iteration 4 --evals 14,15,16,17

# Also run without skill for baseline comparison
node scripts/run-evals.js --iteration 4 --baseline

# Use a different model
node scripts/run-evals.js --iteration 4 --model opus

# Control parallelism
node scripts/run-evals.js --iteration 4 --parallel 2

# Re-grade existing outputs without regenerating
node scripts/run-evals.js --iteration 4 --grade-only
```

### Contamination Protocol

**Enforced automatically:** The script always creates a clean worktree and removes experiment documents before running any eval. This is not optional — there is no flag to skip it.

**What is removed:**
- `docs/superpowers/experiments/` — contains ideal answer tables for eval features

**Why a worktree:** The `--bare` flag prevents CLAUDE.md auto-discovery and hooks, but the agent can still use Glob/Grep/Read to explore files in the working directory. A worktree with experiment docs removed is the only reliable way to prevent contamination.

**Documentation:** The contamination protocol is documented in CLAUDE.md under the eval framework section, so that anyone running evals manually (without the script) knows to follow the same steps.

### File Structure After a Run

```
skills-workspace/
  evals/evals.json                          ← source of truth
  iteration-4/
    benchmark.json                          ← aggregated results
    eval-review.md                          ← human-readable report
    eval-14-weekly-pay/
      with_skill/
        outputs/response.md                 ← captured model output
        grading.json                        ← assertion pass/fail with evidence
        timing.json                         ← tokens and duration
      no_skill/                             ← only present with --baseline
        outputs/response.md
        grading.json
        timing.json
    eval-15-reis-discount/
      with_skill/
        ...
```

### Config Naming

The previous `old_skill` name is retired. New convention:
- `with_skill` — run with the current plugin loaded
- `no_skill` — run without any plugin (baseline, optional)

Regression detection compares `iteration-N/with_skill` against `iteration-(N-1)/with_skill`, not against `no_skill`.

## Open Questions

1. **Grading accuracy:** How reliable is LLM-as-judge for these assertions? We should spot-check grading results for the first automated run and compare against manual grading to calibrate.
2. **Determinism:** Model responses vary between runs. Should we support multiple runs per eval (e.g., `--runs 3`) and report variance? Deferred for now — start with single runs.
3. **Eval subsets:** Some evals test `tabletest`, others `spec-by-example`. Should the script support `--skill tabletest` to run only evals for one skill? Low priority but easy to add.
4. **Cost tracking:** With `--output-format json` we get token counts. Should the report include estimated cost? Nice-to-have.
