# Analysis to-do — table-driven-testing, iteration 11

**Merged gate document.** The suite's baseline is split across two iterations, so neither
generated report covers it alone. Both are kept beside this file:

- `analysis-todo-vs-it9.md` / `eval-review-vs-it9.md` — eval-31 only (1/5 comparable)
- `analysis-todo-vs-it10.md` / `eval-review.md` — evals 32–35 (4/5 comparable)

All 5 evals are comparable once the two are merged. Every fingerprint matches its baseline.

## Result

**41/44 against a 44/44 baseline (13/13 it-9 + 31/31 it-10). Three verdicts moved, all losses.**

| eval | baseline | it-11 | moved |
|---|---|---|---|
| eval-31-travel-insurance-py | 13/13 (it-9) | **11/13** | `threshold-as-column`, `precedence-visible` |
| eval-32-baggage-fees-py | 8/8 (it-10) | **7/8** | `scenario-names-describe-conditions` |
| eval-33-library-fees-py | 9/9 (it-10) | 9/9 | — |
| eval-34-hotel-cancellation-swift | 8/8 (it-10) | 8/8 | — |
| eval-35-cinema-tickets-routing | 6/6 (it-10) | 6/6 | — |

No errored evals, no truncated evals. Digest `8c48d55601403f10`, commit `0019d10`.

**Read the causes before treating this as a regression: two of the three losses are grader
flips on materially identical artefacts, and only one is a real difference in output.**

## LOST `threshold-as-column` — eval-31-travel-insurance-py

Grader said: _Parametrize columns are only ('trip_days','age','has_medical_clearance','decision')
etc; no explicit threshold value column, thresholds only implied via chosen boundary values like
90/91 or 69/70._

- Output: `eval-31-travel-insurance-py/outputs/tests/test_travel_policy.py`
- Narration: `eval-31-travel-insurance-py/narration.md`
- **Cause (from artefact): grader flip, not a regression.** it-9 passed this slot on the same
  pattern and said so explicitly: _"pytest.param(90, 30, False, Decision.APPROVED, …),
  pytest.param(91, 30, False, Decision.MANUAL_REVIEW, …); also age=69/70 pairs shown explicitly"_.
  Both outputs carry the 90/91 boundary pair in a `trip_days` column and the 69/70 pair in an
  `age` column; neither carries a separate threshold column. The artefacts are materially
  identical on what this assertion tests and the verdicts are opposite.
  The skill's own Check (`shared/table-design/rules/17-thresholds-visible.md`) reads "shows it as
  a column, with boundary rows at and just past it" — which the it-11 output does. The cut pass
  added one paragraph to that rule about a *derived* threshold and left the Check untouched, so
  there is no wording change here that could have caused the loss.

## LOST `scenario-names-describe-conditions` — eval-32-baggage-fees-py

Grader said: _pytest.param(23, 0, id="at the no-fee limit") paraphrases the fee_eur=0 expectation
as 'no-fee'_

- Output: `eval-32-baggage-fees-py/outputs/`
- Narration: `eval-32-baggage-fees-py/narration.md`
- **Cause (from artefact): grader flip on the instrument's worst-known slot.** it-10 passed this
  eval 8/8 with `id="top of no-fee tier"` beside the same `fee_eur=0` cell. "top of no-fee tier"
  paraphrases fee=0 exactly as much as "at the no-fee limit" does; the ids changed wording but not
  kind, and the six ids map one-to-one between the two runs. `docs/assertion-triage.md` records
  this slot as the worst in the suite — 3 flips, the most of any assertion, and "already
  majority-wrong and stays wrong". A single run does not settle it, and this is the slot the
  triage doc names first.

## LOST `precedence-visible` — eval-31-travel-insurance-py

Grader said: _Only shows >90-day override when age/clearance would approve (id='just over the
limit, senior applicant with clearance'); no case shows override when age/clearance would decline
(senior without clearance, trip>90)._

- Output: `eval-31-travel-insurance-py/outputs/tests/test_travel_policy.py`
- Narration: `eval-31-travel-insurance-py/narration.md`
- **Cause (from artefact): real difference in the output. The grader is right.** it-9's
  trip-length table had four rows and included `pytest.param(91, 75, False, MANUAL_REVIEW,
  id="trip one day over the limit, older applicant without clearance")` — the case where manual
  review overrides a profile the age rule would *decline*. it-11's table has three rows and
  carries `(91, 75, True)` instead: a senior *with* clearance, who the age rule would approve
  anyway, so the row demonstrates nothing about precedence over a decline. The row that carried
  the assertion is gone.
- **Not attributable to any single change.** it-9 ran at commit `b2921012`, so this eval's delta
  spans repairs 1/2/4/5/6/7 *and* the cut pass (§ J36). Both runs chose the same three-table
  by-axis design and the same 90/91 and 69/70 boundaries; the difference is one row's choice of
  clearance value. Row-choice variance at this granularity is not established as caused by
  guidance, and one run cannot separate it. If it is worth settling, it needs a single-variable
  re-run, not an attribution from this run.

## Verdict for the Part 1 gate

**The cut pass is not shown to have damaged this suite.** Two of three losses are grader flips
against materially identical artefacts, one of them on the slot the triage doc names as the worst
in the instrument. The third is a real dropped row on an eval whose baseline predates the entire
batch, so it measures seven changes at once and attributes to none.

Nothing here identifies a repair to make. The honest reading of 41/44 is 43/44-with-one-open-row,
and the open row is not diagnosed to a cause.
