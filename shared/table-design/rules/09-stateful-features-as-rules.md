### Frame Stateful Features as Transition Rules

When a feature involves state — queues, workflows, inventories — frame each {{row}} as a state
transition rule: the state before, the action, the state after, and any message or result.

Each {{row}} is independent: given this state, when this action happens, expect this result. No {{row}}
depends on a previous one having run.

**Include the before and after columns** even when the description states the operation procedurally.

A sequential path — step 1, then step 2, then step 3 — creates {{row}} dependencies and is not a table
at all.

{{example}}

**Check:** **Stateful {{rows}} independent**: transition {{rows}} carry their own before-state and after-state; no {{row}} depends on another having run
