# Feature 2: Reis Single Ticket Discount

**Type:** Multi-condition decision
**Source:** [Ruter Reis](https://ruter.no/en/about-our-tickets/reis-discount-on-single-tickets)

## Rules

**Discount tiers** — based on single tickets purchased in the last 30 days (rolling window):

| Tickets (last 30 days) | Discount |
|------------------------|----------|
| 1–4                    | 0%       |
| 5–9                    | 5%       |
| 10–14                  | 10%      |
| 15–19                  | 15%      |
| 20–24                  | 20%      |
| 25–29                  | 25%      |
| 30–34                  | 30%      |
| 35–39                  | 35%      |
| 40+                    | 40%      |

**Zone pricing** (adult, full price): 1 zone = 46 kr, 2 = 75, 3 = 105, 4 = 134, 5 = 162

**Eligibility:**
- Adults and pensioners: full Reis discount ladder (0–40%)
- Children: flat 20% discount (no ladder)
- Youth, students: pay full adult price (no discount, no separate category)

**Price formula:** ticket price = zone base price × (1 − discount%)

**30-day rolling window:** Tickets count toward the accumulated total regardless of zone purchased or traveller category. Only tickets with a purchase timestamp within the last 30 days are counted.

---

## Step 1: Ideal Spec Table

Optimised for readability. A product owner at Ruter should be able to verify the discount programme.

Design choices:
- Plain table format (markdown / `.table` file) — no Java annotations
- Separate tables per concern: discount ladder, eligibility, pricing (with discount column), rolling window
- Address domain question: youth/students are not separate categories — they pay adult price

### Discount ladder

| Scenario | Tickets in last 30 days | Discount? |
|---|---|---|
| New traveller, first trip | 1 | 0% |
| Building up, not yet eligible | 4 | 0% |
| First discount kicks in | 5 | 5% |
| Regular commuter | 15 | 15% |
| Daily commuter | 25 | 25% |
| Heavy commuter | 35 | 35% |
| Maximum discount reached | 40 | 40% |
| Beyond maximum, still capped | 60 | 40% |

### Who qualifies for Reis

| Scenario | Traveller type | Discount? |
|---|---|---|
| Adults get the full ladder | Adult | Tiered 0–40% |
| Pensioners same as adults | Pensioner | Tiered 0–40% |
| Children get a flat rate | Child | Flat 20% |
| Youth pay adult price | Youth | None (adult price) |
| Students pay adult price | Student | None (adult price) |

*Note: Youth and students are not separate fare categories — they purchase adult tickets at full price. The "traveller type" for ticketing purposes is Adult.*

### What the customer pays

| Scenario | Traveller | Zones | Tickets last 30 days | Discount? | Ticket price? |
|---|---|---|---|---|---|
| New adult, one zone | Adult | 1 | 1 | 0% | 46.00 |
| Regular commuter, one zone | Adult | 1 | 15 | 15% | 39.10 |
| Maximum discount, one zone | Adult | 1 | 40 | 40% | 27.60 |
| Regular commuter, two zones | Adult | 2 | 15 | 15% | 63.75 |
| Maximum discount, five zones | Adult | 5 | 40 | 40% | 97.20 |
| Child, one zone | Child | 1 | 1 | 20% | 36.80 |
| Child, many trips, same price | Child | 1 | 40 | 20% | 36.80 |
| Youth, one zone, full price | Youth | 1 | 40 | 0% | 46.00 |

### 30-day rolling ticket count

All ticket purchases count toward the accumulated total, regardless of zone or traveller category.

| Scenario | Ticket purchases in window | Accumulated count? |
|---|---|---|
| Three one-zone trips | [1-zone, 1-zone, 1-zone] | 3 |
| Mixed zones all count | [1-zone, 3-zone, 2-zone, 1-zone] | 4 |
| Old tickets fall off | [5 trips last week, 3 trips 31 days ago] | 5 |
| Daily commuter over 30 days | [2 trips/day for 25 days, first 5 days expired] | 40 |

**Strengths:** Clear separation of concerns. Pricing table includes a "Discount?" column so the reader can trace the calculation. Eligibility clarifies the youth/student domain question. Rolling window captures temporal behaviour.

**Weaknesses:** Representative tiers only. Rolling window scenarios are narrative rather than precise. More broadly, the spec tables illustrate the *types* of rules (ladder, eligibility, pricing, window) without specifying boundary conditions within those rules. A developer would need to clarify more before completing implementation — where exactly are the tier boundaries? What happens at day 30 vs day 31?

---

## Step 2: Ideal Test Table

Optimised for test quality.

Design choices:
- Tier table: value sets per tier, no per-line comments — fold tier name into scenario
- Eligibility: yes/no via `@TypeConverterSources`
- Price calculation: test with zone/tier inputs, not base price + discount (avoid testing multiplication)
- Rolling window: separate aspects — timestamp counting, zone counting, category counting

```java
@TableTest("""
    Scenario  | Tickets in last 30 days  | Discount?
    0% tier   | {1, 2, 3, 4}             | 0
    5% tier   | {5, 6, 7, 8, 9}          | 5
    10% tier  | {10, 11, 12, 13, 14}     | 10
    15% tier  | {15, 16, 17, 18, 19}     | 15
    20% tier  | {20, 21, 22, 23, 24}     | 20
    25% tier  | {25, 26, 27, 28, 29}     | 25
    30% tier  | {30, 31, 32, 33, 34}     | 30
    35% tier  | {35, 36, 37, 38, 39}     | 35
    40% cap   | {40, 45, 50, 100}        | 40
    """)
@DisplayName("Discount tier determination")
@Description("Discount increases by 5 percentage points for every 5 tickets, up to 40%")
void shouldDetermineDiscountTier(int ticketsInLast30Days, int discount) { ... }

@TableTest("""
    Scenario                    | Traveller type | Reis eligible? | Discount applied?
    Adults get the full ladder  | Adult          | yes            | Tiered
    Pensioners same as adults   | Pensioner      | yes            | Tiered
    Children get a flat rate    | Child          | no             | Flat 20%
    Youth pay adult price       | Youth          | no             | None
    Students pay adult price    | Student        | no             | None
    """)
@DisplayName("Who qualifies for Reis")
void shouldDetermineEligibility(TravellerType travellerType,
        boolean reisEligible, DiscountType discountApplied) { ... }

@TableTest("""
    Scenario                       | Traveller | Zones | Tickets last 30 days | Discount? | Ticket price?
    New adult, one zone            | Adult     | 1     | 1                    | 0         | 46.00
    Regular commuter, one zone     | Adult     | 1     | 15                   | 15        | 39.10
    Maximum discount, one zone     | Adult     | 1     | 40                   | 40        | 27.60
    Regular commuter, two zones    | Adult     | 2     | 15                   | 15        | 63.75
    Maximum discount, five zones   | Adult     | 5     | 40                   | 40        | 97.20
    Pensioner, same as adult       | Pensioner | 1     | 15                   | 15        | 39.10
    Child, one zone                | Child     | 1     | 1                    | 20        | 36.80
    Child, many trips, same price  | Child     | 1     | 40                   | 20        | 36.80
    Child, five zones              | Child     | 5     | 1                    | 20        | 129.60
    Youth, full price              | Youth     | 1     | 40                   | 0         | 46.00
    Student, full price            | Student   | 1     | 40                   | 0         | 46.00
    """)
@DisplayName("Ticket price after Reis discount")
@Description("End-to-end: traveller type + zone + ticket history → discount tier → final price")
void shouldCalculateTicketPrice(TravellerType traveller, int zones,
        int ticketsLast30Days, int discount, BigDecimal ticketPrice) {
    // integration test: eligibility → tier lookup → price = zone_price × (1 - discount/100)
}

@TableTest("""
    Scenario                        | Purchases (zone@days ago)                     | Count?
    // Counting by timestamp
    All within window               | [1@2, 1@5, 1@10]                             | 3
    Expired tickets excluded        | [1@35, 1@31, 1@1]                            | 1
    Exactly 30 days ago included    | [1@30, 1@29]                                 | 2
    Exactly 31 days ago excluded    | [1@31, 1@29]                                 | 1
    // All zones count equally
    Mixed zones all count           | [1@1, 3@2, 2@5, 5@10]                        | 4
    Single zone only                | [1@1, 1@2, 1@3]                              | 3
    // All traveller types count
    Mixed categories count          | [adult@1, child@2, pensioner@5]               | 3
    """)
@DisplayName("30-day rolling ticket count")
@Description("All purchases count regardless of zone or traveller category; "
    + "only tickets within the last 30 days are included")
void shouldCountTicketsInWindow(List<Purchase> purchases, int count) {
    // purchases older than 30 days are excluded
}
```

**Strengths:** Value sets test every value in every tier (9 rows → 45 cases). Per-line comments removed — tier name IS the scenario. Rolling window separated into three aspects: timestamp boundary, zone independence, category independence. Price table includes discount column for traceability. Youth/student treated as "no discount" consistently.

**Weaknesses:** Rolling window uses a compact `zone@daysAgo` notation that needs a TypeConverter.

---

## Step 3: Comparison

### Dimension A — Content Convergence

| Aspect | Spec | Test | Agreement? |
|--------|------|------|------------|
| **Tier table** | 8 representative rows | 9 value-set rows (→ 45 cases) | Diverge in depth, converge in structure |
| **Eligibility** | Domain question addressed (youth = adult price) | Same content, same domain insight | **Agree** |
| **Pricing** | Includes discount column for traceability | Same — includes discount column | **Agree** |
| **Rolling window** | Narrative scenarios | Three separated aspects (time, zone, category) | Test is more structured, spec is more narrative |

**Convergence:** ~70%. Both include the discount column in pricing. Both address the youth/student domain question. Main divergence is depth of tier coverage and rolling window structure.

### Dimension B — Table Structure

| Aspect | Spec | Test |
|--------|------|------|
| **Number of tables** | 4 | 4 |
| **Table boundaries** | Same four concerns | Same four concerns |

**Structural alignment:** High.

### Dimension C — Value Representation

| Aspect | Spec | Test | Resolvable? |
|--------|------|------|-------------|
| **Percentages** | "5%" | 5 (numeric) | Numeric is cleaner |
| **Eligibility booleans** | "yes"/"no" | yes/no via `@TypeConverterSources` | Already aligned |
| **Rolling window** | Narrative | `zone@daysAgo` notation | Compact notation with TypeConverter |

---

## Step 4: Merged Table

```java
@DisplayName("Reis Single Ticket Discount")
@Description("Reis rewards frequent single-ticket travellers with progressive "
    + "discounts based on tickets purchased in the last 30 days")
@TypeConverterSources(YesNoConverter.class)
class ReisDiscountTest {

    @TableTest("""
        Scenario  | Tickets in last 30 days | Discount?
        0% tier   | {1, 2, 3, 4}            | 0
        5% tier   | {5, 6, 7, 8, 9}         | 5
        10% tier  | {10, 11, 12, 13, 14}    | 10
        15% tier  | {15, 16, 17, 18, 19}    | 15
        20% tier  | {20, 21, 22, 23, 24}    | 20
        25% tier  | {25, 26, 27, 28, 29}    | 25
        30% tier  | {30, 31, 32, 33, 34}    | 30
        35% tier  | {35, 36, 37, 38, 39}    | 35
        40% cap   | {40, 45, 50, 100}       | 40
        """)
    @DisplayName("Discount tier ladder")
    @Description("Discount increases by 5 percentage points for every 5 tickets, capped at 40%")
    void shouldDetermineDiscountTier(int ticketsInLast30Days, int discount) {
        assertEquals(discount, reisDiscount.tierFor(ticketsInLast30Days));
    }

    @TableTest("""
        Scenario                    | Traveller type | Reis eligible? | Discount applied?
        Adults get the full ladder  | Adult          | yes            | Tiered
        Pensioners same as adults   | Pensioner      | yes            | Tiered
        Children get a flat rate    | Child          | no             | Flat 20%
        Youth pay adult price       | Youth          | no             | None
        Students pay adult price    | Student        | no             | None
        """)
    @DisplayName("Who qualifies for Reis")
    @Description("Youth and students are not separate fare categories — "
        + "they purchase adult tickets at full price")
    void shouldDetermineEligibility(TravellerType travellerType,
            boolean reisEligible, DiscountType discountApplied) {
        // yes/no → boolean via @TypeConverterSources(YesNoConverter.class)
    }

    @TableTest("""
        Scenario                       | Traveller | Zones | Tickets last 30 days | Discount? | Ticket price?
        // Adult pricing across tiers
        New adult, one zone            | Adult     | 1     | 1                    | 0         | 46.00
        Regular commuter, one zone     | Adult     | 1     | 15                   | 15        | 39.10
        Maximum discount, one zone     | Adult     | 1     | 40                   | 40        | 27.60
        Regular commuter, two zones    | Adult     | 2     | 15                   | 15        | 63.75
        Maximum discount, five zones   | Adult     | 5     | 40                   | 40        | 97.20
        // Pensioner pricing matches adult
        Pensioner, same as adult       | Pensioner | 1     | 15                   | 15        | 39.10
        // Child flat discount
        Child, one zone                | Child     | 1     | 1                    | 20        | 36.80
        Child, many trips, same price  | Child     | 1     | 40                   | 20        | 36.80
        Child, five zones              | Child     | 5     | 1                    | 20        | 129.60
        // Youth and students — full price
        Youth, full price              | Youth     | 1     | 40                   | 0         | 46.00
        Student, full price            | Student   | 1     | 40                   | 0         | 46.00
        """)
    @DisplayName("Ticket price after Reis discount")
    @Description("End-to-end: traveller + zone + history → discount → price")
    void shouldCalculateTicketPrice(TravellerType traveller, int zones,
            int ticketsLast30Days, int discount, BigDecimal ticketPrice) {
        // integration: eligibility → tier → price = zone_price × (1 - discount/100)
    }

    @TableTest("""
        Scenario                        | Purchases (zone@days ago)                     | Count?
        // Counting by timestamp
        All within window               | [1@2, 1@5, 1@10]                             | 3
        Expired tickets excluded        | [1@35, 1@31, 1@1]                            | 1
        Exactly 30 days ago included    | [1@30, 1@29]                                 | 2
        Exactly 31 days ago excluded    | [1@31, 1@29]                                 | 1
        // All zones count equally
        Mixed zones all count           | [1@1, 3@2, 2@5, 5@10]                        | 4
        Single zone only                | [1@1, 1@2, 1@3]                              | 3
        // All traveller types count
        Mixed categories count          | [adult@1, child@2, pensioner@5]               | 3
        """)
    @DisplayName("30-day rolling ticket count")
    @Description("All purchases count regardless of zone or traveller category; "
        + "only tickets within the last 30 days are included")
    void shouldCountTicketsInWindow(List<Purchase> purchases, int count) {
        // TypeConverter parses "zone@daysAgo" and "category@daysAgo" notation
    }
}
```

### Tension Catalogue

| # | Tension | Spec pulls toward | Test pulls toward | Resolution |
|---|---------|-------------------|-------------------|------------|
| 1 | **Tier depth** | Representative examples | Every value | Value sets — each row IS one tier, readable AND exhaustive |
| 2 | **Pricing traceability** | Include discount column | Include discount column | **No tension** — both want it. The discount column lets the reader trace the calculation. |
| 3 | **Eligibility booleans** | yes/no | true/false | yes/no with `@TypeConverterSources` |
| 4 | **Youth/student domain** | Clarify they're not separate categories | Test the "no discount" path | Domain note in `@Description`; test rows verify the behaviour |
| 5 | **Rolling window aspects** | Narrative examples | Separated aspects (time, zone, category) | Separated with comment groups — each aspect is a spec story AND a test concern |
| 6 | **Price formula** | Show the full pipeline | Avoid testing multiplication | End-to-end integration test — the pipeline IS the spec. Not testing multiplication separately. |

### Scores

| Dimension | Score (1–5) | Notes |
|-----------|-------------|-------|
| **Spec readability** | **5** | Value-set tier table reads as the authoritative definition. Eligibility clarifies domain questions. Pricing includes discount column for traceability. Rolling window separated into intuitive aspects. |
| **Test quality** | **5** | 45 tier cases from 9 rows. End-to-end pricing verifies the full pipeline. Rolling window tests timestamp boundary, zone independence, and category independence separately. Eligibility is exhaustive. |

### Observations

1. **Value sets eliminate the tier exhaustiveness tension entirely.** Each row IS one tier — the value set shows every value in the range. This is simultaneously the best spec (complete tier definition, one line per tier) and the best test (every value tested). No compromise.

2. **The discount column in the pricing table is a shared insight.** Both spec and test want it — the spec needs it so readers can trace the calculation, the test needs it to verify the tier lookup is correct before the price calculation. When spec and test want the same column, that's a strong convergence signal.

3. **Domain questions belong in the experiment.** "Are youth/students separate categories or just adults at full price?" is exactly the kind of question a spec-by-example process surfaces. The table forces you to be explicit about what each traveller type means. This is H0 working as intended — the table format drives domain clarity.

4. **Rolling window has three independent aspects.** Separating "does the 30-day boundary work?" from "do all zones count?" from "do all categories count?" makes each concern independently verifiable. Comment groups within one table are sufficient — no need for separate tables. This is a pattern: **multiple aspects of one rule ≠ multiple tables**.

5. **Avoid testing the language's arithmetic.** The previous version had a separate "price calculation" table testing `base_price × (1 - discount/100)`. That's testing multiplication. The end-to-end pricing table tests the pipeline (eligibility → tier → zone lookup → price) which IS the business rule. Let the unit test framework verify arithmetic.

6. **"Who qualifies for Reis?" is a summary table, not a specification table.** The eligibility table summarises the implication of the discount rules for different traveller groups — useful for product alignment ("are we happy with this?"). But the actual specification lives in the "30-day rolling ticket count" table, which defines *what tickets are counted*. Distinguishing summary tables (product audience) from specification tables (implementation audience) helps set the right expectations for each.

7. **Spec tables should specify boundaries, not just illustrate rule types.** The discount ladder spec has representative rows (1, 4, 5, 15, 25, 35, 40, 60) but doesn't call out every tier boundary. The test table does this with value sets — but the spec should too, since boundary conditions are where misunderstandings live. The gap between "here are the kinds of rules" and "here are the exact thresholds" is where bugs hide.

8. **Rolling ticket count: see tabletest.org for inspiration.** The approach at https://tabletest.org/learn/realistic-example/#counting-eligible-purchases demonstrates how to structure purchase-counting tests with clear temporal boundaries and explicit inclusion/exclusion criteria — a useful reference for windowing rules like the 30-day rolling count.
