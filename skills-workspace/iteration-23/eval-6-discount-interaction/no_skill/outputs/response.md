This is a business logic decision that needs stakeholder alignment before you write more code — good instinct to pause.

A few concrete options to bring to the team:

1. **Stack both** — apply bulk discount first, then loyalty on the remainder (or vice versa). Simple, but can compound to unexpectedly large discounts.
2. **Higher of the two** — take whichever discount is greater, ignore the other. Predictable, easy to reason about.
3. **Stack with a cap** — both apply, but combined discount never exceeds some ceiling (e.g., 30%).
4. **Additive with a cap** — sum the percentages up to a max (e.g., 15% bulk + 10% loyalty = 25%, capped at 20%).

Questions that will likely drive the decision:
- What's the business goal? Rewarding loyalty, moving volume, or both?
- Are there margin constraints that make a cap necessary?
- Does the order of application matter for accounting/reporting?

Once the team decides, the implementation is straightforward — but the rule needs to be pinned down first so you're not refactoring it later. Would you like help drafting the question for the team, or sketching out how each option would look in code so they can see the implications?