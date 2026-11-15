`@TableTest` tables inside `BallotBoxTests` for vote replacement. The stub stays unimplemented, so
the tests compile and fail.

## The result cell

`Replacement` has a case with an associated value. Add `Codable` to it and to `Refusal` in the app
— one synthesised word each, which keeps the stub unimplemented — and an expectation cell reads the
case as Swift spells it: `replaced`, `refused(pollClosed)`. No `init(from:)` in the app, and no
test-side `switch` that maps words to cases.

## The rules and their rows

- **The replacement while the poll is open:** `replaced`, and the voter's vote and the tally change.
- **Three refusals, one row each:** the poll is closed (`pollClosed`), the voter has no vote in the
  box (`noVoteToReplace`), the voter's ballot is sealed (`ballotSealed`).
- **A refusal changes nothing.** Each refused row asserts the voter's vote and the tally after the
  call, read from the box (`vote(of:)`, `tally`), not only the returned refusal.

## The unchanged state

An outcome that is the same in every row is a claim that the varied input does not decide it. The
refusal rows state "nothing changed", so the table that holds them shows that claim against a
contrast. Three forms conform:

1. **A contrast row** (the reference): the accepted replacement is a row of the same table. The
   vote and the tally change in that row, so the after-state columns vary, and the row proves that
   the test can see a write.
2. **One row with a value set of the reasons**, with the state before and after, in a table of its
   own.
3. **A before-state that differs between the refused rows**, so the after-state column varies with
   it.

A table of refusals alone, whose after-state column holds one value in every row, does not conform.
An unchanged state asserted with a constant in the test body does not conform either.

## Fixtures

The situation each row starts in (the poll open or closed, a vote or none, a sealed ballot) is
built from a word in the cell, for example by a test-local enum with `String` raw values. Its branch
is a member of that enum; the test function and its helpers have none. The voters and their votes
are named once, in a doc comment or the enum.

## Equally good

One table or two (the replacement and the refusals), provided the refusals' after-state is shown
in one of the three forms. Names, titles and column order are free. The order of the refusal
checks is not stated, so a row with two reasons at once is not needed.
