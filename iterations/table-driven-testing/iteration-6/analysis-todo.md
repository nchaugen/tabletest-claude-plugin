# Analysis to-do — table-driven-testing variant=next, iteration 2

Compared against **official (iterations 4, 3 merged)**, grading claude-sonnet-5/default.

**2 of 2 evals comparable.**

No assertion verdicts moved among the evals that could be compared, so there is nothing
to attribute.

> **Gate closed 2026-08-01 — targeted confirmation of the rule 14 fix, and the pre-registered
> prediction held.** Evals 32 and 34 are the only two hosting `exception-case-separated`; both went
> 7/8 -> **8/8**, so both slots flipped back to PASS. "0 moved" here is the *good* result: it means
> the variant now matches the official baseline on these two, i.e. the regression `iteration-1`
> showed is gone.
>
> **What this does not establish.** Merging with `iteration-1` gives 43/44, but those runs used
> different skill states (`skill_digest` 8a5d087421c43cab against 57343014205c2535), so 43/44 is
> indicative rather than a measurement. The argument that the merge is sound here is that evals 31,
> 33 and 35 contain no rejection handling at all, so rule 14's wording has no surface to change their
> outputs — an argument, not a measured fact. A full-suite variant run at the current state is what
> would settle it.
