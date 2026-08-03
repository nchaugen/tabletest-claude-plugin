### Keep the Tables of One Concern Consistent

Tables that sit together are read together. Within one {{table_noun}} set, the same concept takes the
same column name, the same kind of value takes the same notation, and the same failure takes the same
spelling. `Response Time?` in one table and `Timing?` in the next reads as two different things; `<50`
beside a bare `50` leaves the reader deciding whether the second is a maximum or an exact value.

**Decide the shared notation before writing the first table, not while writing the third.** The choice
is cheapest at the start and gets more expensive with every table that fixes it differently.

Four things to hold steady across the set:

- **Column names** — one name per concept, everywhere it appears.
- **Value notation** — one way of writing a bound, a duration, an absent value.
- **Failure vocabulary** — one spelling of an error, not `ERROR` here and `FAIL` there.
- **The helpers behind them** — one parser or fixture shared, rather than a copy per table.

{{example}}

**Check:** **Consistent across tables**: within one {{table_noun}} set, one concept has one column name, one kind of value has one notation, one failure has one spelling, and the {{rows}} are served by shared helpers rather than per-table copies
