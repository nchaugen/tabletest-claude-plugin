### Separate Rules from Arithmetic

Tables specify the interesting decisions — classifications, eligibility rules, tier lookups, state
transitions — not that multiplication works.

**The symptom is an expectation cell you cannot predict in one step.** If reading a {{row}} means
classifying first and then computing, the table has fused two rules and states neither.

Give the classification its own table, whose expectation columns *are* the classification. Give the
arithmetic its own, taking the classification as an input. Each table then states one rule, and every
cell is predictable from its {{row}}.

This usually needs a narrower function to call. A table that can only reach the fused result means
the seam is missing, not that the table must fuse.

**Putting the classification in a column of the fused table satisfies this test without splitting
anything.** With the classified value beside the raw data, every cell is predictable in one step
again — and the rule that produces it has still not been stated anywhere. One-step predictability is
necessary, not sufficient; *Decompose When You See These Signs* asks the second question.

**Where you may not add the seam, name it.** Code you cannot change still has the boundary in its
behaviour, and a table that fuses two rules without saying why reads as a design choice. One sentence
on a published surface fixes that — *"the intermediate score is not observable, so the decision and
the amount are verified together; an accessor for it would allow two tables."* Whether the gap gets
closed in the code or bridged here is then the reader's decision to make, which it cannot be while
the gap is invisible.

{{example}}

**Check:** **Rules separated from arithmetic**: every expectation cell is predictable from its {{row}} in one step; a classification and the calculation that follows it are two tables
