# Analysis to-do — tabletest, iteration 75

Compared against **iteration 74**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**5 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `concern-not-over-split` — eval-25-convert-from-spock

Grader said: _appliesSurchargesToTheBaseFee is a single @TableTest covering oversize, hazmat, fragile, insurance and their combination in one table_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Repair 7, by a route not predicted — § J33 predicted this would NOT move.** The four thin surcharge tables merged into one `appliesSurchargesToTheBaseFee` (8 tables to 4). **This corrects § J24**, which attributed the over-split to *One Rule, One Axis* licensing it. The tables were split because each field had its own column; give the object one column and the reason to split disappears. **No separate rule-01 repair is needed.**

## WON `dimensions-as-list` — eval-25-convert-from-spock

Grader said: _Dimensions (cm) | [10, 10, 10] ... fun ...(dimensions: List<Int>, ...)_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Repair 7's collection clause**, which was added during the read-back pass precisely because the object wording would not have reached a `List<Int>` parameter. Cells are `[30, 20, 15]` / `[100, 5, 5]`. Had that clause been left out, this slot would not have moved.

## WON `options-as-map` — eval-25-convert-from-spock

Grader said: _Options | [:] ... [handling: hazmat] ... [fragile: true, insuredValue: 200] — map column, [:] used for no-options row_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Repair 7, as registered.** The class now has one `Options` column carrying `[:]`, `[handling: hazmat]`, `[fragile: true]`, `[insuredValue: 200]` and `[fragile: true, insuredValue: 200]` — the exact prescribed form including `[:]` for the empty object. narration confirms the decision was taken from the signature, before columns.

## WON `options-type-converter` — eval-25-convert-from-spock

Grader said: _@TypeConverter fun parsePackageOptions(fields: Map<String, String>): PackageOptions { ... fields["fragile"]?.toBoolean() ?: false ... }_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Repair 7.** Same decision — once the column holds the object there is something for a converter to convert. `parsePackageOptions(fields: Map<String, String>)` is package-level, the Kotlin idiom the skill prefers.

## LOST `scenario-names-describe-conditions` — eval-25-convert-from-spock

Grader said: _'No surcharges apply' beside Fee? = 7.50 (the unmodified base fee) echoes the row's expectation analogous to the disallowed 'No discount applies' example_

- Output: `eval-25-convert-from-spock/outputs/`
- Narration: `eval-25-convert-from-spock/narration.md`
- Raw transcript: `eval-25-convert-from-spock/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Repair 7's cost, and it is repeatable rather than a draw.** The merged table needs a baseline row, and it was named `No surcharges apply` beside an unchanged 7.50 — the grader reads it as echoing the expectation. **Both merged-table runs fail this slot (it-73 *"Neither surcharge applies"*, it-75) and both split-table runs pass it (it-71 *"Not fragile"*, it-74 *"No special handling"*)** — 2 for 2 each way, one cause. The repair is to name the baseline row by its condition, not to un-merge.
