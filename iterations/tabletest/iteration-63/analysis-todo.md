# Analysis to-do — tabletest, iteration 63

Compared against **iteration 55**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `assertion-criteria-declared` — eval-30-order-splitting-tt

Grader said: _assertEquals(Set.copyOf(shipments), groupsOf(actual)); applies unordered set comparison with no description stating order is irrelevant_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A real rule 10 miss, and the difference from iteration-55 is the expectation's type.** iteration-63
  declares `List<Set<String>>` and then compares with `assertEquals(Set.copyOf(shipments),
  groupsOf(actual))` — the order-insensitivity is applied in the test body, where no reader sees it.
  iteration-55 expected a `Map<Availability, Set<String>>`, so unorderedness was visible in the
  notation itself (`[IMMEDIATE: {camera, tripod}]`) and needed no prose. That is exactly rule 10's
  second repair — *"an unordered collection as the expectation says order does not matter in the
  table itself, which beats saying so in prose"* — satisfied by accident in it-55 and missed here.
  **Attribution is weak.** Nothing in the batch tells the agent to prefer a list over a map for this
  expectation; the shape table's *"several objects → a list of maps"* row is about *input* cells.
  One draw, no narration evidence that any batch passage drove the type choice. Record it, do not
  attribute it.

## LOST `no-duplicate-rows-within-a-table` — eval-30-order-splitting-tt

Grader said: _separatesShipmentsByFulfillmentType: PASS; separatesDeliveryShipmentsByAddress: PASS; shipsInStockItems...: FAIL ('In-stock item does not wait for a backordered item' and '...for a pre-ordered item' both re-cover obligation (b) under the stated two-state model); choosesWarehouseCombination...: PASS; keepsCompanionProducts...: PASS._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **J1's bound could not be applied — the collapse it mandates is not expressible here, and that is a
  real finding rather than a salience miss.** The two rows are *In-stock item does not wait for a
  backordered item* and *…for a pre-ordered item*, differing only in `BACKORDERED` vs `PRE_ORDERED`,
  and the agent's own `@Description` says those two are equivalent (*"Backordered and pre-ordered
  items both ship WHEN_AVAILABLE"*). So no rule names them apart and rule 06's narrowed floor bullet
  says collapse them into a value set. **It cannot be written.** The varying attribute sits inside a
  map cell — `[p1: IN_STOCK, p2: BACKORDERED]` — and value-set expansion is strictly per column:
  `ValueSetUtil.isToBeExpanded` tests one argument per column against that column's parameter type
  (`tabletest/tabletest-junit/.../ValueSetUtil.java`), so `{BACKORDERED, PRE_ORDERED}` nested in a map
  value parses as a `Set` value and never expands. iteration-55 avoided the whole problem a third way:
  it used a *third item* so one row carried IN_STOCK, BACKORDERED and PRE_ORDERED together, and it had
  no duplicate pair.
  **This is the principle deliberately not landed after eval-17 for want of a measured slot** (see
  `iterations/spec-by-example/iteration-7/analysis-todo.md`): an attribute buried inside a composite
  value cannot be a value set. It now has one. **Proposed repair, not landed:** *What the Notation
  Cannot Express* gains the nesting limit, and the collapse rules gain the alternative — give the
  varying attribute its own column, or cover the combination in one row with an extra element.

## LOST `scenario-names-describe-conditions` — eval-30-order-splitting-tt

Grader said: _'All items in stock ship together immediately' echoes the expectation cell '[[items: [p1, p2], availability: IMMEDIATE]]'_

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A genuine rule 12 violation, and not new to this batch.** *"All items in stock ship together
  immediately"* states the condition and then restates the expectation cell. iteration-55's
  equivalent {{row}} was named *"All items in stock"* — condition only. So the name got longer and
  picked up the outcome.
  **Note the standing caveat before treating this as a regression:** `docs/assertion-triage.md` lists
  `scenario-names-describe-conditions` as the third-worst flipper (2 flips), and it is one of the
  slots the triage says to confirm with a re-grade of the same stored outputs before attributing.
  That re-grade was not bought — the run was capped at three evals by quota. **Unconfirmed.**
