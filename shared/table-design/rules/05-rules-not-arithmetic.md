### Separate Rules from Arithmetic

Tables specify the interesting decisions — classifications, eligibility rules, tier lookups, state
transitions — not that multiplication works.

**The symptom is an expectation cell you cannot predict in one step.** If reading a {{row}} means
classifying first and then computing, the table has fused two rules and states neither.

Give the classification its own table, whose expectation columns *are* the classification. Give the
arithmetic its own, taking the classification as an input. Each table then states one rule, and every
cell is predictable from its {{row}}.

**Three routes to the seam, in this order:**

1. **Make the existing call report the intermediate value.** Where the fused result is the classified
   value scaled by a later input, set that input to its identity — one, zero, an empty adjustment —
   and the output *is* the classification, through the public call, with nothing added. Ask what the
   last step does to the classified value and which value of its input would leave it unchanged.
2. **Add the narrower function.** A table that can only reach the fused result means the seam is
   missing, not that the table must fuse.
3. **Name the seam you may not add** — below.

**Route 1 is the one most often missed, and it is not a workaround.** It adds no API, so the
objection that an intermediate value is an implementation detail does not reach it: the call is the
published one and the inputs are ordinary values. Concluding from *Design Black-Box Tables* that a
classification cannot be observed is what makes the fused table look inevitable — that rule asks for
observable inputs and outputs, and route 1 uses nothing else.

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
