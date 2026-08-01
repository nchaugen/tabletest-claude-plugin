# tabletest-claude-plugin

Claude Code plugin with two Java/Kotlin skills:

- `tabletest` (`/tabletest`) — write and convert JUnit tests to TableTest.
- `spec-by-example` (`/spec-by-example`) — clarify behaviour through example tables.

Canonical skill files: `skills/<skill>/SKILL.md`.

## Commits

One-line conventional commits (`feat:`/`fix:`/`docs:`/`chore:`), no body, no attribution footer.

## Release

1. Bump the version in `.claude-plugin/plugin.json`; add a `CHANGELOG.md` entry.
2. Commit and push to `main`.
3. `git tag vX.Y.Z && git push origin vX.Y.Z`.

The tag triggers `.github/workflows/release.yml`, which creates the GitHub release from
`CHANGELOG.md`. Never `gh release create` manually — always use the tag.

## Evals

Per-skill evals with variant comparison validate the skills. Directory roles:

- `evals/<skill>/eval-X-name/` — definitions: `prompt.md`, `expected_output.md`, `eval.json`
  (id, slug, skill, assertions, optional `timeout_ms`).
- `iterations/<skill>/iteration-N/` — results: `benchmark.json` (pass rates, tokens),
  `eval-review.md` (report + regression detection), and per-eval `outputs/`, `grading.json`,
  `timing.json`. Variant results nest under `<variant-name>/` with their own numbering.
- `skill-variants/<skill>/<name>/` — in-development skill copies, never shipped
  (`SKILL.md` + `references/`); absent when nothing is in development.

Results dirs are trimmed each cycle; older ones (summaries + full artefacts) stay in git
history for comparison, audit, and re-grading. Conversation and run logs are never committed.

**Running:** `scripts/run-evals.js`, `--skill` required.

```bash
node scripts/run-evals.js --skill tabletest --iteration N [--evals 1,2] [--compare-iteration M]
```

**Never pipe a run through `tail`, `head`, or a pager — redirect to a file or let it print.** A pipe
to `tail` buffers until the process exits, so there is no interim progress at all, and then it
discards everything outside its window. That window is sized for the run you expected; the lines you
need are the ones an unexpected failure prints, and those are exactly what gets thrown away. Runs
here take from one minute to ninety, so "wait for it to finish and read the last 60 lines" is the
worst of both.

**Follow a run in `iterations/<skill>/iteration-N/run.log`.** The runner opens it before the first
eval and appends every log line as it happens (`run-evals.js:791`), so progress is observable there
whatever you do with stdout — including for a run started in the background. `tail -20` *that file*
as often as you like; it is a file, not a pipe. Per-eval `grading.json` and `timing.json` land as each
eval completes, so results survive even if the console output is lost entirely.

Two consequences for a long run: start it in the background and poll `run.log`, and never re-run
something because you cannot see its output — check `run.log` and the per-eval artefacts first.

**Who pays.** Generation runs on the **Claude subscription** — the eval agent is spawned without
`ANTHROPIC_API_KEY` so the CLI uses the logged-in account. Grading still posts to the API and needs
the key, so keep it exported. Two consequences:

- **`cost_usd` is notional for generation, billed for grading.** Each `benchmark.json` stamps
  `generation_auth` (`subscription` \| `api` \| `unknown` on a regrade of an older run) — read it
  before quoting a dollar figure. `--api-generation` bills generation to the key instead, for a
  machine with no subscription login.
- **The constraint is now the usage cap, and it fails quietly.** A rate-limited eval scores 0 rather
  than stopping the run, and a 0 swings the next two reports — once down, then back up as phantom
  improvements (same signature as § Timeouts). Keep `--parallel` at or below 4 for a full suite, and
  check every eval's `compiles` before believing a report.

**Run it outside any command sandbox** (in Claude Code, with the sandbox override) — every
invocation, including `--grade-only`. Sandboxed, grading fails with a wall of `fetch failed` that
reads like an API outage, Gradle cannot start so every eval fails `compiles`, and — the damaging
part — the eval agents lose their own Bash tool. Without `ls` they cannot find a provided
`src/main`, so on any eval whose prompt does not name the exact path they conclude the project is
empty and write tests against an invented API. That produces a plausible report in which those
evals answer a different task than the baseline did, and nothing in the output says so. Verify with
`grep -l "Bash is down" iterations/**/narration.md` and by checking each output's package and API
against the eval's `project/src/main`.

**Testing the runner:** `node --test 'scripts/*.test.js'` (~1s, no network, no
dependencies — `node:test` is built in). Covers the machinery a score depends on: grader
response parsing, majority voting, retry/backoff, the abort-on-grading-failure path, the
fingerprint guard, and what a comparison could not cover. Run it after touching `run-evals.js` — a
silent bug there corrupts every measurement downstream, and the failure mode is a plausible-looking
number rather than a crash. The eval definitions in the suite are fabricated, never real ones, so the
file carries no answer keys.

**Linting the skill's own examples:** `node scripts/lint-skill-examples.js` runs the deterministic
eval checkers over every `@TableTest` in `skills/tabletest/`, so an illustration is judged by the
same code that grades an agent's output. It runs inside `node --test` against
`scripts/skill-example-baseline.json`, which holds the violations the 2026-07-30 sweep found. **Fix
an example and remove its baseline entry in the same commit** — the test fails either way round, so
the count can only fall on purpose.

This is the mechanical half of the read-back pass below, and it earns its place twice over: on the
sweep it found seven examples the hand read had missed, and it exposed a defect in the *checker*
(`no-if-switch-in-method` could not see a ternary split across lines, which is how the reference
formats one). Point a checker at prose you control and it audits itself.

Regression detection compares against the previous iteration's `benchmark.json` (same
variant); `--compare-official` also compares a variant against the latest official baseline.

**A failed generation is excluded, not scored zero.** When an eval times out or crashes it produced
no answer, so it is left out of the summary totals, named in `summary.errored_evals`, and excluded
from the comparison the way a changed definition is (`generation-failed`). Its tokens and cost still
count — the attempt was paid for. **Re-run it before reading anything into the gap**; a partial
comparison is honest but it is still partial. Before this, a single transient timeout took a
five-eval run from 71/72 to 54/72 and produced 17 phantom moved verdicts, one per assertion the eval
owns.

**Timeouts:** the runner's default is 600s and **every eval's budget is clamped to a 900s ceiling**
(`GENERATION_TIMEOUT_CEILING_MS`). An eval whose recent runs exceed ~60% of its budget needs an
explicit `timeout_ms`, up to that ceiling — beyond it, a run consistently taking 10+ minutes is
telling you the eval is too big, not that the budget is too small. The clamp lives in the runner
rather than in each `eval.json` on purpose: `eval.json` is hashed into the fingerprint, so editing
`timeout_ms` there re-baselines the eval. `--timeout` overrides both, for slow local models.

The ceiling is set from the stored runs, not from taste: across 79 successful generations the
slowest was 665s and the p95 518s. The seven evals previously budgeted at 1500s were carrying 2.3x
the worst case ever observed, and iteration-50 paid for the slack — eval-30 hit a dropped connection
mid-thinking, retried, and re-thought from zero for another ten minutes before timing out.

Timing out still costs a re-run and the wall-clock rather than a corrupted comparison, per the rule
above. What it still costs is the answer: the eval is excluded, so whatever question you were asking
of it goes unanswered until you re-run.

**Spend runs only on questions reading cannot answer.** A run buys exactly one thing: evidence about
what the guidance *causes an agent to do*. Everything else about a change is free to check — whether
it is self-consistent, whether it contradicts a neighbouring passage, whether its illustrations would
pass the assertion they target, whether it covers the artefacts it was drawn from. **Buying a run to
answer a question a read would have answered is the most common way to overspend here**, and it costs
twice: the money, and an iteration spent attributing a result to guidance that was wrong on its face.
Two questions before every run:

- **Has the read-back pass been done?** (§ Developing a variant, step 2.) Minutes against dollars.
  Correctness defects never need a run — a loop cannot tell you a rule contradicts itself, and will
  happily return a plausible number while it does.
- **Does the loop host the target assertion?** If no eval in it carries the assertion the change
  aims at, the run measures nothing about the change, however good its total looks.

The corollary is not "run less" — it is **run for firing evidence and nothing else**. Salience is
real, it is invisible to reading, and it is worth paying for: guidance can be correct, unambiguous,
consistent with its neighbours, and still be skipped by the agent.

**Cost control (tabletest):** almost all run cost is *generation* — solving the eval, not
grading it. Assertion count is close to free (deterministic assertions are code checkers, LLM
assertions share one batched call per eval), so cost is controlled by choosing which evals to
run, never by trimming assertions. Grader *quality* is worth paying for and grader *thrift* is not.

Both costs are recorded per run, so read them from the artefact rather than from memory:
generation as `cost_usd` per eval, grading as `summary.grading` in the same `benchmark.json`
(with the assumed per-token rates stamped alongside). A benchmark predating that record, or one
rebuilt from older gradings, reports `grading: null` — unknown, not free.

**An eval earns a place in the iteration loop by having at least one assertion that is unique
to it and has failed in a recent baseline.** Everything else is promotion-time regression
evidence. Two consequences worth stating, because both are easy to get wrong:

- An eval at 100% teaches nothing during iteration, however good it is. Keep it for promotion.
- Near-duplicate evals bill separately for the same finding, and **this is what evals 26, 27 and 28
  were cut for on 2026-08-01.** They and eval-25 were one task — the same 17-row shipping table — from
  four source frameworks, differing by 9–10 lines of a 150-line `expected_output.md`, and between them
  they guarded a single generic line of skill text (`SKILL.md:1106`). Their eval-unique assertions
  (`no-*-syntax`, `*-dependency-removed`) are deterministic greps that rarely move. **Eval-25 is the
  survivor**: the only cross-language case (Groovy source → Kotlin output) and one of only two Kotlin
  projects in the suite. Before adding an eval, ask what it tests that an existing one does not — four
  hosts of one task is one host measured four times, not four.

Pick the loop per change, from the previous baseline's failing-assertion set, rather than
reusing a fixed list — the discriminating core drifts as failures are fixed. A well-chosen loop is
roughly a quarter of a full suite's cost and time, with strictly more signal. For the absolute
figures read `summary.total_cost_usd` and `summary.total_duration_ms` off a recent `benchmark.json`
rather than a number written down here.

**Then weigh the loop by price too — evals are not interchangeable units of spend.**
`node scripts/eval-costs.js --skill <skill>` prints mean generation cost, grading cost and wall-clock
per eval across every stored run, cheapest first. **The spread is about sevenfold**, so two loops
with equal signal can differ several-fold in cost, and "five evals" says almost nothing about what a
run will cost. Two consequences:

- **A smoke loop is affordable in a way a full loop is not.** The cheapest hosts of an assertion run
  in about a minute for small change; the dearest take ten minutes and dollars. When a targeted
  assertion has a cheap host, an early probe on it costs a fraction of the loop and answers "does
  this fire at all?" before the expensive evals are committed.
- **Signal still wins.** Price is a tiebreak between evals that carry the target, never a reason to
  drop the eval that carries it. A cheap loop measuring nothing is the most expensive kind.

Costs drift with the suite and the model, so read the script rather than a figure quoted in a plan.

### Developing a variant

`skills/` is always the published version — never iterate on it directly.

1. Copy the **full** published skill dir (`SKILL.md` + `references/`) to
   `skill-variants/<skill>/next/`. The runner swaps the whole skill dir for the variant, so a
   partial copy silently drops references.
2. **Read the draft back before paying for it.** Three checks, all free, all against text you already
   have. They catch a different class of defect from a run, and the class they catch is common:
   every cluster so far has turned out to be a *conflict* in the skill rather than a gap.

   - **Run each targeted assertion's own decidable test against your own illustration.** Start with
     `node scripts/lint-skill-examples.js`, which does this mechanically for branching, annotation
     order, method names and constant expectation columns. Then do by hand only what it cannot: the
     assertion texts in `evals/<skill>/*/eval.json` are written as mechanical tests, so they work as
     a linter on the examples you just wrote. A drafted title example once failed
     `title-states-system-behaviour`'s own strike-out test — the illustration could not have passed
     the assertion it existed to fix.
   - **Grep `SKILL.md` for every term the edit introduces, and read what already says something
     about it.** Guidance that contradicts an adjacent passage loses to the adjacent passage. One
     draft licensed keeping a combining table three lines below *Wiring is not a rule*, which
     rejects it.
   - **Apply the draft rule by hand to the offending values in the stored artefacts.** They are
     already paid for, and they are the exact inputs the guidance has to catch.

   **What this cannot tell you is whether the guidance fires.** A rule can be correct, unambiguous,
   consistent with its neighbours, and still be skipped: cluster 3 taught the "force N" naming shape
   explicitly and two evals wrote it anyway. Reading tests correctness; only generation tests
   salience. Do both, in this order — the free one first.

   **Generality gate — apply before writing, not after.** The loop rewards additions and never
   subtractions: every edit is justified by a slot an assertion can see, and nothing in the process
   ever proposes a deletion. Left alone that produces a skill shaped like a list of past failures.
   Three questions, from
   [skill-examples-avoid-eval-domains](../../products/claude-plugin/decisions/skill-examples-avoid-eval-domains.md):

   - **Can you state the principle with no domain nouns at all?** If not, it is not a principle yet —
     it is a patch for the artefact you just read. Write the general statement first, then illustrate
     once.
   - **Where does the skill already say this?** Search before adding. Every cluster so far has found
     the rule already present, or its opposite present — a conflict to resolve, not a gap to fill.
     Adding beside a contradiction leaves the contradiction winning.
   - **Is this a rule or a mechanic?** "How do I express X in TableTest" is legitimately specific and
     belongs. "Here is the fix for the failure eval N showed" does not; find what it is an instance
     of.

   Two facts worth having in view while deciding. A rule stated in two places is a rule that will
   drift — the same fact updated in one spot and stale in the other has caused four defects here,
   including one that a whole cluster existed to fix. And measure the trade: **skill lines added per
   measured slot won**. Across the 2026-07-27 batch it was ~29 before compression, which is too high.
3. Benchmark against the baseline:
   `node scripts/run-evals.js --skill tabletest --variant next --iteration N --compare-official`.
   The `eval-review.md` "Load-Bearing Assertions" and resource-comparison sections show what
   the change wins and loses — but read them alongside the artefacts (see below), never alone.

**HEAD gotcha:** official-skill runs test the last *commit* (the worktree is created from
HEAD), while variant files, prompts, and scaffolding come from the live working tree. Variant
edits apply immediately; `skills/` changes only after committing.

### Analysing a variant outcome

**Gate: do not start the next iteration until every entry in the run's `analysis-todo.md` has a
cause filled in from an artefact.** Each run writes that file next to `eval-review.md`, listing
every assertion whose verdict moved against the comparison baseline, with the grader's words and
the paths that explain it. It exists because the loss list *reads* like evidence — it arrives
compact and causal-sounding — so opening the artefacts feels like confirming what you already
know. That is the trap, and skipping the step has produced a confident wrong fix list and a wasted
iteration.

**Read the coverage line before the deltas.** The to-do opens with `N of M evals comparable`. An
empty moved list satisfies the gate vacuously if nothing was comparable, so the two cases are now
stated apart: a void comparison gets a `⛔ Nothing was compared` section and the run exits **2**
(the run itself succeeded and its results are saved — exit 2 is never a reason to re-run and pay
again). A partial comparison names each excluded eval and why. Silence about exclusions used to read
as agreement; it no longer is silent, but the number is still yours to read.

**One run does not settle a flip-prone slot.** Measured on the current instrument 2026-07-26:
**17 of 365 slots flip** across three identical re-grades, and `minimal-rows-per-concern` (×4) plus
`rule-statable-from-table` (×3) are seven of them. `docs/assertion-triage.md` holds the per-slot list
and the diagnosis — read it rather than guessing which of your moved verdicts are real.

When a cluster's moved verdicts land on any of those slots, confirm with a **re-grade of the same
stored outputs** (`--grade-only --grading-suffix`) before attributing — generation is the expensive
half and you already have it. **Do not reach for `--grade-runs 3`:** majority-of-3 was measured
against the answer key and scored 61/68, exactly matching a single pass, because the split slots lean
wrong rather than wobbling symmetrically. Voting costs 3× and buys nothing here.

**Judge a cluster by whether its targeted slots moved, never by the net.** The whole-suite score
spread is 318/323/322 across identical re-grades — the size of a typical cluster's entire effect. Two
consequences: a net figure is not evidence, and **the stored baseline is the low draw of its own
distribution** (318 against a mean of 321), so every comparison against it flatters the variant by
roughly 3 slots.

**The report tells you which assertions moved. It does not tell you why, and its grader
justifications are not evidence of cause.** Before attributing a delta to a specific edit, read
the artefacts under `iterations/<skill>/<variant>/iteration-N/<eval>/`:

- `outputs/` — the generated test code. This is the primary evidence: what the guidance actually
  produced. Read it for every assertion you intend to explain — not one eval and then a
  generalisation to the rest.
- `narration.md` — the agent's own account: its visible narration plus the order in which it wrote
  files. Committed, so it survives trimming. This is where you see whether the guidance *fired*
  and in what words ("a combining table for the one genuine precedence question"), and the write
  order shows drafts the final output no longer contains.
- `conversation.jsonl` — the raw transcript `narration.md` is distilled from. Gitignored and
  trimmed each cycle. **Thinking arrives as a summary, and its cost is recorded either way.**
  `thinking.display` defaults to `"omitted"` on Claude 5-family models, which is why blocks used to
  arrive with a signature and an empty `thinking` field. It is a request parameter, so no CLI flag,
  settings key or env var reaches it — but the **control protocol** does: the runner passes
  `--input-format stream-json` and sends `set_max_thinking_tokens` with
  `thinking_display: "summarized"` before the prompt (`THINKING_DISPLAY_REQUEST` in `run-evals.js`).
  This changes visibility only — the model thinks and bills the same either way, so runs before and
  after it stay comparable. The raw chain of thought is still never returned under any setting.
  Independently, the CLI emits a `system/thinking_tokens` event per delta, so **`narration.md` carries
  a per-block thinking-token line and any `api_retry`** even on a run that produced nothing. That is
  what makes a spent-everything-produced-nothing run readable: iteration-50's eval-30 shows a block
  reaching ~31,800 tokens, a dropped connection, then a second block re-thinking from zero until the
  timeout — five lines, no transcript parsing.

**Two verdicts that look like findings and are not.** A grader can fail an assertion whose own
wording the output satisfies — check the assertion text against the artefact before believing a
loss (`rule-statable-from-table` once failed an output for showing a threshold as a column, which
is what the skill mandates and what its sibling assertion rewards). And a holistic assertion
flipping on two structurally identical outputs is noise, not signal: compare the *structure* of
the two runs' outputs before attributing a flip to wording.

**Check every grader justification against the artefact.** A justification names the right
assertion and can still name the wrong cause — a `description-*` failure whose real trigger is a
scenario name, say. Graders also misfire outright, failing an assertion whose own wording the
output satisfies. An analysis built on justifications alone will produce a confident, wrong fix
list.

**Compare against the right baseline.** `--compare-official` merges the newest official result per
eval, so it is correct **provided each mid-batch promotion wrote its loop result into the official
tree** (step 4 of Promoting a variant). Skip that step and the official benchmark is stale for every
eval an earlier promotion already improved, and those wins reappear as if the current variant had
earned them. `node scripts/check-baseline.js --skill <skill>` tells you which benchmark will be used
and whether its definitions are current.

### Promoting a variant

**A full run per promotion is not required — a full run per *release* is.** The two questions a
run answers have very different prices: "did the change do what I aimed it at?" needs only the
discriminating loop (~$4), while "did it break something else?" needs all 17 evals ($15). Only
the second is expensive, and it does not have to be asked once per promotion.

1. Replace `skills/<skill>/` with the variant; reconcile `references/` (drop any the new
   SKILL.md subsumes); delete `skill-variants/<skill>/next/`.

   **Replacement is only safe for a single variant against an unchanged `skills/`.** It is a
   wholesale directory copy, so it silently reverts anything `skills/` gained since the variant was
   branched — a second parallel variant's promotion, or a correction landed mid-batch. Whenever
   `skills/` has moved, **promote by applying the variant's own diff instead**:

   ```
   git show <branch-point>:skills/<skill>/SKILL.md > /tmp/anc.md
   diff -u /tmp/anc.md skill-variants/<skill>/<name>/SKILL.md > /tmp/v.patch
   patch --dry-run -p0 skills/<skill>/SKILL.md < /tmp/v.patch   # check before applying
   ```

   Do the same for each `references/` file the variant touched — compare against the ancestor to find
   them, rather than copying the directory. Then grep the result for a marker from *both* sides: one
   phrase the variant added and one the correction added. A clean `patch` is not proof the merge is
   right, only that the hunks fitted.

   **A promotion done this way leaves `skill_digest` mismatched, and that is expected.** The stored
   partial's digest is the variant's, while the promoted skill is variant + whatever else `skills/`
   carries. Note the divergence in the ledger row rather than trying to make the numbers agree — the
   results were genuinely produced by the variant alone.
2. Add a user-facing `CHANGELOG.md` entry under `## [Unreleased]` (no variant/development
   terminology). **Do not bump `.claude-plugin/plugin.json`** — see below.
3. Commit (`feat:`). The variant's own loop result — graded in the standard regime, against the
   same loop run on the previous skill version — is the promotion evidence. **No full run yet.**
4. **Copy the promoted loop result into the official tree as a partial iteration** —
   `iterations/<skill>/next/iteration-N/` becomes `iterations/<skill>/iteration-M/`, next number up.
   `loadOfficialBenchmark` merges the newest result *per eval* across official iterations, so
   `--compare-official` then returns the promoted numbers for the evals in the loop and the old
   baseline for everything else. Without this step the baseline is a per-eval fact recorded only in a
   plan's prose, and the next cluster compares against results the promotion already superseded.
   The copy is honest: the variant's `skill_digest` is the digest of the skill you just promoted —
   check it matches if you want to be sure. Note `skill_commit` names the commit HEAD was at during
   generation, which is the commit *before* the promotion.
5. Repeat 1–4 for further variants, accumulating entries under `## [Unreleased]`. Nothing is
   released mid-batch.
6. **Compress before the closing run, not after.** A batch's promotions accrete: each was written
   against one artefact, and several will turn out to restate a rule already in the file or to
   duplicate its illustration. Re-read everything the batch added and merge, demote or delete —
   then let the closing run measure the version you actually intend to ship. Doing it afterwards
   means the run measured something else. Expect to find, as the 2026-07-27 batch did: the same rule
   stated in four prose passages and two Quality Checks; a design rule stated twice because a syntax
   subsection sat between the halves; one illustration repeated three times for four facts; and a
   checklist line still carrying wording the prose had already replaced. Verify by grepping for a
   distinctive phrase from every claim the batch measured, before and after.
7. **Close the batch with one official full iteration.** Its regression report is the evidence
   for every promotion in the batch, and its `benchmark.json` becomes the new baseline. The next run
   reads the baseline from disk.

   **Keep iteration directories by default. Trimming is a size decision, not a hygiene one, and
   nothing here is currently near a size that justifies it** (2026-07-31: all of `iterations/` is
   37 MB against a 54 MB `.git`; a full iteration is 1–6 MB). The repo's distil-then-delete
   lifecycle governs *documents* — plans, working notes, things that were scaffolding for a decision.
   **Stored outputs and gradings are measurement data, and this repo's method is re-reading them:**
   artefact-first analysis compares a run against outputs from three or four earlier iterations, and
   validating an assertion edit means re-grading old outputs whose correct verdict you already know.
   Every iteration kept is another free test case for the next assertion repair.

   **Four things a trim would destroy, so check all four before deleting anything:**

   - **`conversation.jsonl` and `*.log` are gitignored** (`.gitignore`), so "git history retains
     them" is false for exactly the files that hold the raw transcript and the per-block thinking
     tokens. Deleting an iteration directory destroys those permanently. Only tracked files —
     `benchmark.json`, `eval-review.md`, `outputs/`, `grading*.json`, `timing.json`, `narration.md` —
     are recoverable, and only by path from a commit.
   - **A closing run supersedes only the evals it actually ran.** `iteration-50` covered 14 of 17, so
     it supersedes nothing for evals 27, 29 and 30 — their newest stored results are still in
     `iteration-45`. The mechanical test: an iteration holding the newest result for any eval is one
     `loadOfficialBenchmark` still selects, and deleting it silently changes what `--compare-official`
     resolves to.
   - **The answer key is bound to one set of outputs** by `scored_against.iteration`
     (`score-grader.js:67`) — currently `iteration-40`. Sweep those outputs and the key can score
     nothing.
   - **`docs/grader-tuning.md` § The sweep** — outputs whose fingerprints still match current
     definitions are re-gradable and must survive, and a variance probe must be distilled before it
     is swept.

   When size does eventually justify a trim, drop **variance probes and superseded regrades** first
   (many benchmarks over one set of outputs), never the outputs themselves.
8. Tag and release (see Release) — that is where the single version bump for the whole batch
   happens, and where `## [Unreleased]` becomes `## [X.Y.Z] - <date>`.

**One version bump per release, not per promotion.** A version is a publication fact: it names
something a user can install. Numbering intermediate promoted-but-unreleased states burns
versions on things that were never published, and buys nothing — `run-evals.js` never reads
`plugin.json`, so the version is invisible to every comparison. What identifies a skill state to
the instrument is the git commit (official runs build their worktree from HEAD) and the
`skill_commit`/`skill_digest` provenance stamped into each `benchmark.json`.

**When the batch run shows a regression, bisect — don't re-run the suite.** Re-run *only the
regressed evals* against each promotion commit in the batch. That is per-eval money (~$1 each),
so attributing a two-eval regression across four promotions costs a few dollars, not $60. The
saving over a full run per promotion grows with batch size and is only spent when a regression
actually appears.

**Batching does not cost you isolation when the variants are eval-disjoint.** Check which evals
carry each variant's target assertions before sequencing; when two variants' target sets do not
overlap, both can be developed and validated in parallel, each on its own loop, and the eval
partition supplies the isolation that serialised full runs were meant to buy. Two caveats:
disjoint *targets* are not disjoint *effects* — every variant edits the same SKILL.md and added
length perturbs everything, which is exactly what the batch-closing run is for — and two variants
editing SKILL.md must be reconciled before that run. Targets overlap more often than the failing
sets suggest: two variants editing the same *section* collide even when their assertions differ.

**Optional per-promotion regression tier.** If a batch is long enough that deferring all
regression signal feels risky, add the evals sitting at zero failures in the current baseline
(read them off its `benchmark.json`). They teach nothing during iteration, which is exactly why
they are good sentinels: they have nowhere to go but down. Loop + sentinels is roughly half a
full run, with per-promotion attribution preserved.

**Three things that do not save money.** Trimming assertions (grading is a rounding error
against generation). A cheaper generation model (the model must match or every delta is
meaningless). Reusing stored outputs across promotions — `--grade-only` is valid across
*grading* changes only; any skill edit invalidates every stored output. If wall-clock rather
than cost is the constraint, generation parallelism is the lever, and it does not reduce spend.

### Evolving the eval suite

The suite is the instrument, the skill the subject — a score delta means something only when
exactly one of them changed between the compared iterations.

- **An assertion id shared across suites has one text, and `eval.json` is not where you edit it.**
  Its source is `shared/assertions/<id>.md`; `node scripts/build-skills.js` renders it through each
  suite's vocabulary (`shared/assertions/vocabulary.json`) and writes it into every `eval.json` that
  carries the id. Edit the source, run the build, commit both. Editing the `eval.json` copy is
  reverted by the next build, and `node --test` fails on the divergence meanwhile. The vocabulary is
  what makes one text legitimate in three places — `@TableTest method` / `markdown table` /
  `parametrized test` — so if a rule cannot be stated without a framework noun the vocabulary lacks,
  it is not shared guidance; leave it in the eval.
- **Sequence, don't interleave.** Freeze the suite within a comparison cycle. Land suite
  changes as their own commits (`feat(evals):`, `fix(evals):`), re-baseline the published
  skill, then resume skill iteration.
- **Re-baseline cheaply.** Assertions/checkers only changed → re-grade stored outputs with
  `--grade-only` (no generation cost). Prompts/scaffolding changed → re-run the published skill
  for the affected `--evals`; `--compare-official` merges the newest result per eval.
- **When a grading run dies part-way, grade the failures then `--rebuild`.** A fatal grading
  failure deliberately writes no benchmark, which strands the gradings that succeeded. `--rebuild`
  assembles the benchmark from the `grading{suffix}.json` files already on disk — no API calls, no
  key needed, seconds rather than a full pass. **Do not re-run the full command to recover**: it
  re-grades every eval and throws away the work you already paid for. The error message prints both
  commands in the right order.
- **Cross-suite regression reports are noise** — added/renamed assertions show as spurious
  regressions. Re-baseline instead of interpreting them.
- **The grading regime is part of the instrument.** Standard model is **sonnet**
  (`claude-sonnet-5`). **A comparison is void across a change of grading model, `--grade-runs`,
  `LLM_GRADING_BATCH_SIZE`, or the grader's response schema.** Re-baseline instead. Current
  measurements — instability, accuracy, level, what each batch of assertion edits moved — live in
  `docs/assertion-triage.md`; read that rather than trusting a figure remembered from a past session.
- **Changing the grader itself is a separate workstream with its own procedure:
  `docs/grader-tuning.md`.** Read it before any `--grading-suffix` work. It covers what a suffix
  names, measuring accuracy against the answer key rather than agreement, variance probes, promoting
  a regrade to the plain name, and the sweep that hands a single unambiguous baseline back. Skipping
  it is what left nineteen benchmarks in iteration-40 and voided a skill-iteration run.
- **Check the baseline is live before spending a loop against it.**
  `node scripts/check-baseline.js --skill tabletest` names every eval whose definition has moved since
  the benchmark `--compare-official` would pick. It is a second, and the failure it catches otherwise
  surfaces only after the run is paid for.
- **Every run appends a row to `docs/measurement-ledger.md`.** You do not append; you **annotate** the
  `Note` column when the analysis gate closes, and you **never delete a row**. The ledger outlives the
  iteration directories, so once results are trimmed it is the only record that a measurement
  happened — and the only durable source for what a run costs and takes. Read its header before
  comparing two rows: a score column is not a trend line, because the instrument moves under it.
- **Fingerprint guard (automatic).** Each `benchmark.json` result is stamped with a content
  fingerprint of its definition (`prompt.md`, `eval.json`, `expected_output.md`, `project/`);
  reports compare only matching evals and exclude changed ones as "not comparable". Pre-guard
  benchmarks (no fingerprint) count as comparable.
  **The guard protects comparison, not re-grade validity** — it excludes changed evals when
  comparing two iterations, but a fresh `--grade-only` writes a benchmark stamped with the
  *current* fingerprint, so re-grading a stored output whose prompt has since changed scores a
  stale response as if it were valid, silently. Check what changed before re-grading.
- **Skill provenance (automatic).** Each `benchmark.json` records `skill_commit` (repo HEAD at
  run time) and `skill_digest` (content hash of the skill directory the agent was actually
  handed, after any variant was applied), so a stored result still answers "which skill state
  produced this?" once the iteration dirs around it are trimmed. A `--grade-only` run *inherits*
  these from the benchmark already in the iteration dir rather than restamping — provenance
  belongs to the generation, and today's skill did not write yesterday's answers. Runs predating
  this record `unknown`.

### Contamination protocol

`docs/`, `iterations/`, `skill-variants/`, each `eval.json`/`expected_output.md`, and the
project markdown (`README.md`, `AGENTS.md`, `CHANGELOG.md`) hold answer keys, prior responses,
and eval strategy — an agent exploring during a run could use them and invalidate results.

`run-evals.js` isolates automatically: it runs in a git worktree with those files removed and
`--disallowedTools "Bash(git:*)"` so history can't recover them.

Running manually, replicate it: create a worktree, `rm -rf docs iterations skill-variants`,
`rm -f {README,CLAUDE,AGENTS,CHANGELOG}.md`, remove eval answer keys
(`find evals -name eval.json -o -name expected_output.md | xargs rm -f`), run with
`--disallowedTools "Bash(git:*)"`, then remove the worktree.
