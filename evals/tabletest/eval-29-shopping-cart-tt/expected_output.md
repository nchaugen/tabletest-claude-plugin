Five @TableTest methods, one per cart operation: add item, remove item, apply coupon, calculate
total, checkout. Cart contents, catalogue, and inventory are `Map<String, X>` and use standard
TableTest map syntax (`[Widget: 2]`, `[:]` for empty) with a @TypeConverter turning each map into
its domain object — no custom string parsing. Coupon data lives in a single column with a
@TypeConverter (readable shorthand *or* map syntax), never spread across sparse type/value/target
columns. The coupon table uses before/after columns to show the state change. Business-language
columns throughout. Underspecified rules are surfaced as explicit rows a reviewer can challenge,
not resolved silently in the method body.

## The concerns

`CartService` bundles five operations that share a `Cart` but otherwise decide different things.
The substance of this eval is giving each its own `@TableTest` with only the rows that concern
needs — not one monolithic table cross-multiplying items × coupons × stock.

| Concern | Governs | Irrelevant to it |
|---|---|---|
| **Add item** | cart contents + message after adding, incl. unknown-product and bad-quantity rejection | coupons, stock |
| **Remove item** | cart contents + message after removing, incl. not-in-cart rejection | coupons, stock, prices |
| **Apply coupon** | which coupon (if any) becomes active, and the message — validity and replacement only | prices, the discount's *size* |
| **Calculate total** | the money: `sum(qty × price) − discount`, floored at zero, per coupon type | messages, stock |
| **Checkout** | pass/fail against inventory + what the failure names | prices, coupons |

Coupon *validity/replacement* and coupon *price effect* are deliberately two tables: the apply-coupon
table never computes a discount, and the total table never exercises expired/unknown codes.

## Modelling decisions this eval fixes

- **Coupon in a single column with a @TypeConverter.** Type, amount, and target are one value
  (`20% off cart`, `$5 off`, `$5 off Widget`, or the map form `[type: PERCENT, value: 20]`), not
  three sparse columns. Readable shorthand and map syntax are both acceptable; the raw-enum form
  (`PERCENT 20`) is fine too — this column is the sanctioned exception to "no custom string
  parsing", because a coupon does not map naturally onto a `Map<String, X>`.
- **Cart, catalogue, inventory use standard map syntax.** These *do* map onto `Map<String, X>`, so
  `[Widget: 2, Gadget: 1]` and `[:]` for empty, converted by a @TypeConverter — never a bespoke
  bracket grammar the built-in parser already covers.
- **Before/after columns for coupon state.** `Active coupon before` / `Active coupon after?` make the
  replacement rule visible: a valid code replaces, an invalid one leaves the prior coupon untouched.
- **A result object with several outputs is asserted field-by-field, uniformly — and that is one
  assertion pattern, not a violation.** `CartResult` carries `success`, `message`, and the resulting
  `cart`, and it has *no value equality*, so a row cannot assert one `CartResult` equals another. The
  correct shape is the same three `assertEquals` on every row. Because the body never branches on the
  row to choose what it checks, it is a single uniform pattern. (This is what the reworked
  `single-assertion-in-method` assertion now rewards — see Judging.)
- **Checkout's message is only partly specified**, so it is asserted by *containment*, not equality:
  a list column (`Message mentions?` → `List<String>`) names the fragments the message must contain
  (the short products; `empty` for the empty-cart case), and the body loops the same `assertTrue(…
  contains …)` over the list. The exact wording is latitude; naming what is short is the rule.

## Reference decomposition

Values are verified. Catalogue prices are shown in a column wherever they affect the outcome.

**Add item** — result carries `cart after`, `success`, `message`; assert all three per row.

| Scenario | Catalogue | Cart before | Product | Quantity | Cart after? | Success? | Message? |
|---|---|---|---|---|---|---|---|
| Add to empty cart | [Widget: 9.99] | [:] | Widget | 2 | [Widget: 2] | true | Added 2x Widget |
| Add different item alongside | [Widget: 9.99, Gadget: 4.50] | [Widget: 1] | Gadget | 3 | [Widget: 1, Gadget: 3] | true | Added 3x Gadget |
| Add more of an existing item | [Widget: 9.99] | [Widget: 1] | Widget | 2 | [Widget: 3] | true | Added 2x Widget |
| Product not in catalogue | [Widget: 9.99] | [:] | Gadget | 1 | [:] | false | *unknown product* |
| Zero quantity rejected | [Widget: 9.99] | [:] | Widget | 0 | [:] | false | *quantity must be positive* |
| Negative quantity rejected | [Widget: 9.99] | [Widget: 1] | Widget | -2 | [Widget: 1] | false | *quantity must be positive* |

**Remove item** — same three-field result.

| Scenario | Cart before | Product | Cart after? | Success? | Message? |
|---|---|---|---|---|---|
| Remove the only item | [Widget: 2] | Widget | [:] | true | Removed Widget |
| Remove one, others remain | [Widget: 2, Gadget: 1] | Gadget | [Widget: 2] | true | Removed Gadget |
| Remove item not in cart | [Widget: 2] | Gadget | [Widget: 2] | false | *not in the cart* |
| Remove from empty cart | [:] | Widget | [:] | false | *not in the cart* |

**Apply coupon** — before/after; `Coupon in store` encodes what the store holds for the code (a
definition, `expired`, or blank = unrecognised).

| Scenario | Active coupon before | Coupon code | Coupon in store | Active coupon after? | Success? |
|---|---|---|---|---|---|
| Apply valid coupon, none active | | SAVE10 | 10% off cart | SAVE10 | true |
| Valid coupon replaces active | SAVE10 | FIVEOFF | $5 off | FIVEOFF | true |
| Expired code does not replace | SAVE10 | OLD | expired | SAVE10 | false |
| Unknown code does not replace | SAVE10 | FAKE | | SAVE10 | false |
| Expired code, none active | | OLD | expired | | false |
| Unknown code, none active | | FAKE | | | false |

The two `does not replace` rows are the point of the before/after columns: `after = before` proves the
active coupon survived an invalid attempt.

**Calculate total** — `sum(qty × price) − discount`, floored at zero. One assertion (`compareTo` on
`BigDecimal`, so `16.00` and `16.0` compare equal).

| Scenario | Catalogue | Cart | Coupon | Total? |
|---|---|---|---|---|
| No coupon, single line | [Widget: 10.00] | [Widget: 3] | | 30.00 |
| No coupon, multiple lines | [Widget: 10.00, Gadget: 5.00] | [Widget: 2, Gadget: 4] | | 40.00 |
| Percentage off whole cart | [Widget: 10.00] | [Widget: 2] | 20% off cart | 16.00 |
| Fixed amount below subtotal | [Widget: 10.00] | [Widget: 2] | $5 off | 15.00 |
| Fixed amount equals subtotal | [Widget: 10.00] | [Widget: 2] | $20 off | 0.00 |
| Fixed amount exceeds subtotal (floored) | [Widget: 10.00] | [Widget: 2] | $25 off | 0.00 |
| Product coupon, target present | [Widget: 10.00, Gadget: 5.00] | [Widget: 2, Gadget: 1] | $5 off Widget | 20.00 |
| Product coupon, target absent | [Widget: 10.00, Gadget: 5.00] | [Gadget: 1] | $5 off Widget | 5.00 |
| Empty cart, no coupon | [Widget: 10.00] | [:] | | 0.00 |
| Empty cart, any coupon | [Widget: 10.00] | [:] | {20% off cart, $5 off} | 0.00 |

Arithmetic: 20% of 20 = 4 → 16; fixed 25 on 20 → −5 floored to 0; product coupon subtracts a flat $5
only when Widget is in the cart (25 − 5 = 20), otherwise no effect (5). The last row's value set fires
the same expectation for two coupon types, proving an empty cart floors to zero regardless of coupon.

**Checkout** — `success` plus a containment check on the message.

| Scenario | Cart | Stock | Success? | Message mentions? |
|---|---|---|---|---|
| Empty cart cannot check out | [:] | [:] | false | [empty] |
| Sufficient stock | [Widget: 2] | [Widget: 5] | true | [] |
| Stock exactly meets demand | [Widget: 5] | [Widget: 5] | true | [] |
| Short by one unit | [Widget: 6] | [Widget: 5] | false | [Widget] |
| One of several items short | [Widget: 2, Gadget: 5] | [Widget: 5, Gadget: 2] | false | [Gadget] |
| Several items short | [Widget: 6, Gadget: 5] | [Widget: 5, Gadget: 2] | false | [Widget, Gadget] |
| Product missing from inventory | [Widget: 1] | [:] | false | [Widget] |

The `several items short` row is why `mentions` is a list, not a single string: both shortfalls must be
named. Success rows assert no fragments (or a single `placed`/`order` token) — the exact wording is
not fixed by the prompt.

## Underspecified → make it an explicit row

The prompt is a first-cut spec and the solver cannot ask questions; each gap below is expected to
become a row, not a silent decision.

- **Adding a product already in the cart** — accumulate or replace? The `Add more of an existing item`
  row commits to accumulation (`1 + 2 = 3`) and lets a reviewer challenge it.
- **Zero / negative quantity** — the prompt says quantity but not its domain; two rejection rows make
  the chosen semantics explicit.
- **Product coupon when its target is absent** — the `target absent` row commits to "no effect".
- **Fixed / percentage discount exceeding the subtotal** — the floor-at-zero rule is proved by the
  `exceeds subtotal` and empty-cart rows, not just stated.
- **Invalid-code replacement** — that an expired/unknown code leaves the active coupon untouched is a
  rule the prompt implies; the before/after rows make it falsifiable.

## Out of scope, but foreseeable

Omitting any of these is not penalised; contradicting the prompt to invent one is.

- **Partial-quantity removal.** The prompt removes an *item*; modelling "remove 1 of 3" is a defensible
  extension but neither expected nor rewarded.
- **Exact checkout / coupon message strings.** Unspecified; asserting containment (or a boolean) is
  correct, asserting a specific full sentence over-fits.
- **Coupon stacking.** Only one coupon is active at a time — the feature is replacement, not
  accumulation; a table that stacks two discounts has invented a rule.
- **`Stock` vs `Inventory` as the column name.** The prompt uses both words ("stock levels", "insufficient
  inventory"); either header is traceable. Latitude, not a defect.

## Judging

Score the decomposition first: the five operations in separate `@TableTest` methods, each carrying only
the rows its concern needs, with coupon validity kept apart from coupon price effect. Then the
representation the assertions encode — single coupon column via converter, standard map syntax for
cart/catalogue/inventory, before/after coupon columns, business-language headers, and the underspecified
rules surfaced as challengeable rows rather than buried in the body.

Two things that are **not** faults. A method that asserts several fields of a result object uniformly
across every row (because the result has no value equality) is a single uniform assertion pattern — the
correct shape, not a violation of `single-assertion-in-method`. And a `Message mentions?` list asserted
by containment is a uniform pattern whose criterion is stated (the column name and the @Description say
"mentions"/"must contain"); it satisfies `rule-statable-from-table`.

A genuine, minor legibility nit worth noting but not weighting heavily: a `Product Id` header leaks the
`Id` code-ism where the cell values are product *names* (`Widget`), so `Product` reads cleaner — the kind
of small polish `business-language-columns` exists to catch.
