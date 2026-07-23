A single `@TableTest` over `canPerform(Role, Action)` that consolidates the nine role/action
combinations into **five rows** using value sets to group the actions that share an outcome for
a given role. Every role shows both where permission holds and — where applicable — where it
stops. No `if`/`switch` in the method body; the leftmost column names the condition, not the
verdict.

## The reference decomposition

| Scenario | Role | Actions (value set) | Allowed? |
|---|---|---|---|
| Admin performs any action | ADMIN | `{READ, WRITE, DELETE}` | true |
| User reads or writes | USER | `{READ, WRITE}` | true |
| User deletes | USER | `DELETE` | false |
| Guest reads | GUEST | `READ` | true |
| Guest writes or deletes | GUEST | `{WRITE, DELETE}` | false |

Five rows, not nine (`fewer-than-nine-rows`), and no two rows share the same role *and* the same
boolean outcome (`no-duplicate-role-output`) — that pair of constraints is exactly what forces
the value-set consolidation.

## Value sets express "same rule, several actions"

A value set is legitimate **only where every action in it yields the same result for that role**:
`{READ, WRITE}` for USER (both true) is correct; grouping READ with DELETE for USER would be
wrong because their outcomes differ. ADMIN collapses to one all-true row because *every* action
shares the outcome.

## Show where permission stops, not only where it holds

ADMIN has no denial, so it is a single true row. USER and GUEST each have a true group **and** a
false group, and both must appear — a table that lists only what each role *can* do never states
where the rule ends, and a reader cannot tell GUEST from ADMIN. The denied rows are the boundary.

## Fully specified — little to make explicit

Unlike the parser evals, this prompt enumerates every rule and both `Role` and `Action` are
closed enums, so there is no hidden interpretation to surface. Scenario names describe the
condition ("Guest reads", "User deletes"), never the outcome ("Allowed", "Denied").
`@Description` is optional; there is no out-of-band context (where in the request lifecycle the
check runs, etc.) in the prompt, so omitting it is correct rather than a gap. The checker is a
stub — tests are expected not to pass; only `compiles` is asserted.

## Judging

Score the value-set consolidation and the presence of both allowed and denied rows per role
first. Whether ADMIN is one row or the grouping is arranged slightly differently is latitude;
enumerating all nine combinations, or using a `Yes/No` scenario column that echoes the data,
is not.
