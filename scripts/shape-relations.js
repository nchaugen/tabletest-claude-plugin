#!/usr/bin/env node
/**
 * The load-bearing relations of each eval: the questions its assertions ask, restated as
 * arithmetic over a parsed answer.
 *
 * A relation is transcribed from an assertion's own text, never invented. Several of those texts
 * already specify an enumerate-then-decide algorithm ("list every row carrying a premium as
 * age - claims - premium, then name the triple"); a relation runs that algorithm and returns the
 * enumeration as its evidence, which is the part a grader most often gets wrong.
 *
 * Divergence from the reference answer is not error — a ground truth says which rows are
 * load-bearing and which are illustrative, and several decompositions are equally good. So a
 * relation states a *relation between rows* (a pair sharing an age, a boundary in one column) and
 * never a row set to match. A relation the reference answer fails is transcribed wrongly, and
 * `shape-relations.test.js` pins exactly that for every eval here.
 *
 * A relation carrying a `judgement` note has an exemption this cannot decide. Its verdict is a
 * candidate, not a result: determinism buys the countable half and no more.
 */

const {
  actCallArguments,
  callArguments,
  parseCollectionElements,
  constantExpectationColumns,
  findColumn,
  literalArgument,
  numericValue,
  rowCases,
} = require("./answer-shape.js");

/**
 * How eval-18's answers name the quantities its assertions are about.
 *
 * Draws call the same column `Claim Count`, `Claims` or `Prior Claims`, so a relation addresses
 * a role rather than a header. Every resolution is printed, because a role silently resolving
 * to the wrong column is the one way this instrument can lie.
 */
const EVAL_18_ROLES = {
  age: /^age\b|\bage\b/i,
  claims: /claim.?count|^claims?$|prior.?claims/i,
  premium: /premium/i,
  // Draws name the decision `Status?` and `Outcome?` as readily as `Decision?`; iteration-40
  // mixes premium into two `Status?` tables and reading only `Decision?` calls that separated.
  decision: /decision|status|outcome|verdict/i,
};

/** The internals eval-18's `black-box-columns` names, and nothing beyond them. */
const EVAL_18_INTERNALS = [/risk.?score/i, /has.?active.?policy/i, /calculate.?risk/i];

/** The senior threshold the premium band assertions are stated against. */
const SENIOR_AGE = 65;

/** The method under test, whose held literal arguments are as much a row's data as a column is. */
const EVAL_18_CALL = "evaluateApplication";

/**
 * What a table supplies for each role: a column that varies it, or a literal held in the body.
 *
 * A table can hold age at 30 by writing `evaluateApplication("NEW", 30, claimCount)` and carrying
 * no age column at all, and its rows still state a premium at age 30. Resolving roles from
 * columns alone loses every such row — it read eval-18's iteration-50 as having no 0-against-1
 * claim pair when the answer plainly has one, and the grader was right where this was wrong.
 */
function roleResolver(table, sutParameters) {
  const args = callArguments(table.body, EVAL_18_CALL) || [];
  const held = new Map();
  args.forEach((argument, index) => {
    const name = sutParameters[index];
    const literal = literalArgument(argument);
    if (name && literal !== null) held.set(name, literal);
  });

  return (pattern) => {
    const column = findColumn(table, pattern);
    if (column) return (one) => one[column.header];
    const name = [...held.keys()].find((candidate) => pattern.test(candidate));
    if (name) return () => held.get(name);
    return null;
  };
}

/**
 * Every row in the class carrying a premium derived from the risk score, as
 * `{method, age, claims, premium}`.
 *
 * A premium of 0 is never risk-derived here: the standard formula floors at 100 and the senior
 * one at 200, so a zero is an auto-approval or a rejection. `premium-claim-boundary` excludes
 * exactly those rows, and every other premium assertion depends on the same enumeration.
 */
function premiumCases(shape, sutParameters = []) {
  const found = [];
  for (const table of shape.tables) {
    const premium = table.expectationColumns.find((column) => EVAL_18_ROLES.premium.test(column.header));
    if (!premium) continue;
    const resolve = roleResolver(table, sutParameters);
    const age = resolve(EVAL_18_ROLES.age);
    const claims = resolve(EVAL_18_ROLES.claims);
    if (!age || !claims) continue;
    for (const one of table.cases) {
      const values = {
        method: table.method,
        age: numericValue(age(one)),
        claims: numericValue(claims(one)),
        premium: numericValue(one[premium.header]),
      };
      if (values.age === null || values.claims === null || values.premium === null) continue;
      if (values.premium <= 0) continue;
      found.push(values);
    }
  }
  return found;
}

/** The distinct claim counts present at each age, ascending. */
function claimsByAge(cases) {
  const byAge = new Map();
  for (const one of cases) {
    if (!byAge.has(one.age)) byAge.set(one.age, new Set());
    byAge.get(one.age).add(one.claims);
  }
  return new Map([...byAge].map(([age, claims]) => [age, [...claims].sort((a, b) => a - b)]));
}

/**
 * `premium-charge-is-per-claim`: three consecutive claim counts at one age.
 *
 * Two counts leave a per-claim rate and a one-off penalty indistinguishable, which is the whole
 * subject of the assertion. The three rows may sit in different tables provided they share an age.
 */
function perClaimTriple(cases) {
  for (const [age, claims] of claimsByAge(cases)) {
    for (const first of claims) {
      if (claims.includes(first + 1) && claims.includes(first + 2)) {
        return { age, triple: [first, first + 1, first + 2] };
      }
    }
  }
  return null;
}

/** `premium-claim-boundary`: a 0-against-1-claim pair at one age, both premiums risk-derived. */
function claimBoundaryPair(cases) {
  for (const [age, claims] of claimsByAge(cases)) {
    if (claims.includes(0) && claims.includes(1)) return { age };
  }
  return null;
}

/** `premium-age-boundary`: age 64 against age 65 at one claim count. */
function ageBoundaryPair(cases) {
  const byClaims = new Map();
  for (const one of cases) {
    if (!byClaims.has(one.claims)) byClaims.set(one.claims, new Set());
    byClaims.get(one.claims).add(one.age);
  }
  for (const [claims, ages] of byClaims) {
    if (ages.has(SENIOR_AGE - 1) && ages.has(SENIOR_AGE)) return { claims };
  }
  return null;
}

/**
 * `premium-claim-effect-varies-by-band`: a claim-count pair on each side of the senior threshold.
 *
 * One pair, however well chosen, cannot show that the increment differs — so this needs a
 * pair-hosting age below 65 and another at or above it. The pairs need not use the same counts.
 */
function claimEffectBands(cases) {
  const hostingAges = [...claimsByAge(cases)]
    .filter(([, claims]) => claims.length > 1)
    .map(([age]) => age);
  const below = hostingAges.filter((age) => age < SENIOR_AGE);
  const atOrAbove = hostingAges.filter((age) => age >= SENIOR_AGE);
  if (below.length > 0 && atOrAbove.length > 0) return { below, atOrAbove };
  return null;
}

/**
 * `premium-age-is-banded`: two different ages carrying the same premium at one claim count.
 *
 * A table whose every age carries a different premium states "premium rises with age", which is
 * not the rule. The 64-against-65 pair cannot satisfy this and needs no exclusion — those two
 * ages never share a premium.
 */
function bandedAgePair(cases) {
  for (const one of cases) {
    const twin = cases.find(
      (other) => other.claims === one.claims && other.premium === one.premium && other.age !== one.age,
    );
    if (twin) return { claims: one.claims, ages: [one.age, twin.age].sort((a, b) => a - b), premium: one.premium };
  }
  return null;
}

/** `separates-decision-and-premium`: no single table asserts a decision and a premium together. */
function tablesAssertingBoth(shape) {
  return shape.tables
    .filter(
      (table) =>
        table.expectationColumns.some((column) => EVAL_18_ROLES.decision.test(column.header)) &&
        table.expectationColumns.some((column) => EVAL_18_ROLES.premium.test(column.header)),
    )
    .map((table) => table.method);
}

/** `black-box-columns`: a column naming an internal the assertion lists. */
function internalColumns(shape) {
  const named = [];
  for (const table of shape.tables) {
    for (const column of table.columns) {
      if (column.isScenario) continue;
      if (EVAL_18_INTERNALS.some((internal) => internal.test(column.header))) {
        named.push({ method: table.method, header: column.header });
      }
    }
  }
  return named;
}

/**
 * Columns declaring a policy constant the API never receives.
 *
 * Reported without a verdict on purpose. Repair 11 teaches this column and `black-box-columns`
 * has failed three draws for carrying one, so its presence is the conflict itself rather than a
 * defect — the count is what makes the conflict countable across draws.
 */
function policyColumns(shape) {
  const named = [];
  for (const table of shape.tables) {
    for (const column of table.columns) {
      if (column.isScenario) continue;
      if (/\(policy\)|threshold|cutoff|limit/i.test(column.header)) {
        named.push({ method: table.method, header: column.header });
      }
    }
  }
  return named;
}

/**
 * Every eval-18 relation, each naming the assertion it restates.
 *
 * `holds` is the mechanical verdict; `evidence` is the enumeration the assertion asks a grader
 * to produce before deciding. A relation whose `judgement` is set carries an exemption this
 * cannot decide, and its verdict is a candidate rather than a result.
 */
const EVAL_18_RELATIONS = [
  {
    id: "premium-charge-is-per-claim",
    label: "three consecutive claims at one age",
    evaluate: (shape, context) => {
      const cases = premiumCases(shape, context.sutParameters);
      const found = perClaimTriple(cases);
      return {
        holds: found !== null,
        evidence: found
          ? `age ${found.age}: claims ${found.triple.join(", ")}`
          : `no triple; claims by age ${describeClaimsByAge(cases)}`,
      };
    },
  },
  {
    id: "premium-claim-boundary",
    label: "0-against-1 claim pair at one age",
    evaluate: (shape, context) => {
      const found = claimBoundaryPair(premiumCases(shape, context.sutParameters));
      return { holds: found !== null, evidence: found ? `age ${found.age}` : "no 0/1 pair at any one age" };
    },
  },
  {
    id: "premium-age-boundary",
    label: "age 64 against 65 at one claim count",
    evaluate: (shape, context) => {
      const found = ageBoundaryPair(premiumCases(shape, context.sutParameters));
      return { holds: found !== null, evidence: found ? `at ${found.claims} claim(s)` : "no 64/65 pair" };
    },
  },
  {
    id: "premium-claim-effect-varies-by-band",
    label: "a claim pair on each side of 65",
    evaluate: (shape, context) => {
      const found = claimEffectBands(premiumCases(shape, context.sutParameters));
      return {
        holds: found !== null,
        evidence: found
          ? `pairs at ages ${found.below.join(",")} and ${found.atOrAbove.join(",")}`
          : "pairs on one side of the threshold only",
      };
    },
  },
  {
    id: "premium-age-is-banded",
    label: "two ages sharing a premium",
    evaluate: (shape, context) => {
      const found = bandedAgePair(premiumCases(shape, context.sutParameters));
      return {
        holds: found !== null,
        evidence: found
          ? `ages ${found.ages.join(" and ")} both ${found.premium} at ${found.claims} claim(s)`
          : "every age carries its own premium",
      };
    },
  },
  {
    id: "rule-falsifiable-by-a-row",
    label: "no constant expectation column",
    judgement: "the stated-invariance exemption is a reading of the title and description",
    evaluate: (shape) => {
      const constant = shape.tables.flatMap((table) =>
        constantExpectationColumns(table).map((column) => `${table.method}: ${column.header}=${column.value}`),
      );
      return {
        holds: constant.length === 0,
        evidence: constant.length === 0 ? "every expectation column varies" : constant.join("; "),
      };
    },
  },
  {
    id: "separates-decision-and-premium",
    label: "no table asserts decision and premium",
    evaluate: (shape) => {
      const both = tablesAssertingBoth(shape);
      return {
        holds: both.length === 0,
        evidence: both.length === 0 ? "no table carries both" : `both in ${both.join(", ")}`,
      };
    },
  },
  {
    id: "black-box-columns",
    label: "no column names a listed internal",
    evaluate: (shape) => {
      const internals = internalColumns(shape);
      const policy = policyColumns(shape);
      const note = policy.length > 0 ? ` (policy columns: ${policy.map((one) => one.header).join(", ")})` : "";
      return {
        holds: internals.length === 0,
        evidence:
          (internals.length === 0
            ? "no risk-score, active-policy or formula column"
            : internals.map((one) => `${one.method}: ${one.header}`).join("; ")) + note,
      };
    },
  },
];

/** The claim counts present at each age, for a failing relation to show its working. */
function describeClaimsByAge(cases) {
  const entries = [...claimsByAge(cases)].sort((a, b) => a[0] - b[0]);
  if (entries.length === 0) return "(no premium rows found)";
  return entries.map(([age, claims]) => `${age}:[${claims.join(",")}]`).join(" ");
}

// ---------------------------------------------------------------------------
// eval-14 weekly-pay
// ---------------------------------------------------------------------------

/**
 * How eval-14's answers name the quantities its assertions are about.
 *
 * Unlike eval-18 there is no method under test to read names from: the prompt says no
 * implementation exists yet and the answer stubs its own. So every role here resolves from the
 * table, and a table that hides a quantity in its body is reported unresolved rather than guessed.
 */
const EVAL_14_ROLES = {
  weekday: /weekday/i,
  // A large family of answers splits the weekday concern in two — one table classifies weekday
  // hours into regular and overtime, the next prices pre-classified bands — so the pricing table
  // has no weekday column and no threshold to apply. Its arithmetic is right and reading only
  // `weekday` calls it wrong, which is what it did on iterations 42, 44, 50, 52 and 57.
  regular: /regular|standard/i,
  overtime: /overtime/i,
  sunday: /sunday/i,
  holiday: /holiday/i,
  rate: /rate/i,
  pay: /pay|earnings/i,
  total: /weekly|total|net|gross/i,
  thrown: /throws|exception/i,
};

/** Where overtime starts, and the multipliers, exactly as the prompt states them. */
const OVERTIME_THRESHOLD = 40;
const OVERTIME_MULTIPLIER = 1.5;
const DOUBLE_TIME_MULTIPLIER = 2;

/** Pay is compared to a tolerance because a rate may be given to the penny. */
const PENCE = 0.005;

/** A column declaring a constant the code owns, not a quantity a row varies. */
const POLICY_COLUMN = /threshold|cutoff|\blimit\b|\(policy\)|multiplier|factor/i;

/**
 * An input column for `role`: never an expectation, never a policy constant.
 *
 * `Regular Hours?` is a classify table's output, and `Overtime Threshold (hrs)` is the 40 the rule
 * compares against — reading the latter as forty overtime hours priced iteration-84's whole table
 * at two and a half times its stated pay.
 */
function findInputColumn(table, role) {
  const column = findColumn(table, role);
  if (!column || column.isExpectation) return null;
  return POLICY_COLUMN.test(column.header) ? null : column;
}

/**
 * The hours a table supplies, by band, or null where it supplies none this can price.
 *
 * Two vocabularies say the same thing. A `Weekday Hours` column carries unclassified hours and the
 * 40-hour threshold splits them; `Regular Hours` and `Overtime Hours` carry them already split, so
 * the threshold has been applied by an earlier table and applying it again would double-count.
 */
function hourColumns(table) {
  const weekday = findInputColumn(table, EVAL_14_ROLES.weekday);
  const regular = findInputColumn(table, EVAL_14_ROLES.regular);
  const overtime = findInputColumn(table, EVAL_14_ROLES.overtime);
  const sunday = findInputColumn(table, EVAL_14_ROLES.sunday);
  const holiday = findInputColumn(table, EVAL_14_ROLES.holiday);
  if (!weekday && !regular && !overtime && !sunday && !holiday) return null;
  return { weekday, regular, overtime, sunday, holiday };
}

/**
 * The pay the prompt's rules give for one row.
 *
 * Weekday hours up to 40 at the base rate, beyond 40 at time-and-a-half, Sunday and holiday hours
 * at double time, and the total floored at zero. Negative hours pass straight through the same
 * arithmetic, which is what lets the floor rule be exercised at all.
 */
function payForRow({ weekday = 0, regular = 0, overtime = 0, sunday = 0, holiday = 0, rate }) {
  const fromWeekday =
    Math.min(weekday, OVERTIME_THRESHOLD) * rate +
    Math.max(weekday - OVERTIME_THRESHOLD, 0) * OVERTIME_MULTIPLIER * rate;
  const fromClassified = regular * rate + overtime * OVERTIME_MULTIPLIER * rate;
  const doubleTime = (sunday + holiday) * DOUBLE_TIME_MULTIPLIER * rate;
  return Math.max(fromWeekday + fromClassified + doubleTime, 0);
}

/**
 * The expectation column holding the week's total pay, or null.
 *
 * A class may assert intermediate bands beside the total, so the total is picked by name rather
 * than by position — and where nothing names itself the total, one pay column is unambiguous.
 */
function payColumn(table) {
  const candidates = table.expectationColumns.filter((column) => EVAL_14_ROLES.pay.test(column.header));
  if (candidates.length === 0) return null;
  const total = candidates.find((column) => EVAL_14_ROLES.total.test(column.header));
  if (total) return total;
  // A band's own pay is not the week's pay: `Weekday Pay?` is not floored at zero, because the
  // floor is a property of the total, so pricing it with the total's rules invents an error the
  // answer does not contain — iterations 40 and 72 state -100 for -5 weekday hours, correctly.
  const band = /weekday|regular|overtime|sunday|holiday|premium|double/i;
  if (candidates.length !== 1) return null;
  return band.test(candidates[0].header) ? null : candidates[0];
}

/** A blank cell says the hours were not worked, and reaches the calculator as absent. */
function hoursValue(cell) {
  const text = String(cell ?? "").trim();
  if (text === "") return 0;
  return numericValue(text);
}

/**
 * Every row stating a total pay, with the hours and rate it states, as
 * `{method, weekday, sunday, holiday, rate, pay, stated}`.
 *
 * `unresolved` counts the rows that state a pay but hide an hour count or the rate in the method
 * body, where no column and no signature can supply it. Those rows are dropped rather than
 * assumed zero: assuming would invent an arithmetic error that the answer does not contain.
 */
function payCases(shape) {
  const cases = [];
  let unresolved = 0;

  for (const table of shape.tables) {
    const pay = payColumn(table);
    const rate = findInputColumn(table, EVAL_14_ROLES.rate);
    const bands = hourColumns(table);
    if (!pay) continue;
    if (!rate || !bands) {
      unresolved += table.cases.length;
      continue;
    }
    const held = heldBandValue(table, bands);

    for (const one of table.cases) {
      const hours = {};
      let missing = held.ambiguous;
      for (const [band, column] of Object.entries(bands)) {
        if (column) {
          hours[band] = hoursValue(one[column.header]);
          continue;
        }
        if (held.band === band) hours[band] = held.value;
      }
      const values = { method: table.method, ...hours, rate: numericValue(one[rate.header]) };
      const stated = numericValue(one[pay.header]);
      if (missing || stated === null || values.rate === null || Object.values(hours).some((v) => v === null)) {
        unresolved++;
        continue;
      }
      cases.push({ ...values, stated, pay: payForRow(values) });
    }
  }
  return { cases, unresolved };
}

/**
 * The value a table holds for the one band it has no column for, or null.
 *
 * `calculateWeeklyPay(weekdayHours, sundayHours, null, hourlyRate)` states that no holiday hours
 * were worked, and `calculateWeeklyPay(40, sundayHours, 0, hourlyRate)` states that forty weekday
 * hours were. Both are data. The inference only runs where exactly one band lacks a column and the
 * act call holds exactly one literal, which is the only case where the mapping is unambiguous.
 */
function heldBandValue(table, bands) {
  const missing = ["weekday", "regular", "overtime", "sunday", "holiday"].filter((band) => {
    if (bands[band]) return false;
    // The two vocabularies stand in for each other: a table pricing classified bands is not
    // missing a weekday column, and one splitting weekday hours is not missing the bands.
    if (band === "weekday") return !bands.regular && !bands.overtime;
    return !bands.weekday || band === "sunday" || band === "holiday";
  });
  if (missing.length === 0) return { band: null, value: 0, ambiguous: false };

  const call = actCallArguments(table.body, table.params.map((param) => param.name));
  const literals = call ? call.args.map(literalArgument).filter((value) => value !== null) : [];
  // No literal in the act call means nothing is held, so a band with no column is simply not an
  // input to this table and contributes nothing.
  if (literals.length === 0) return { band: null, value: 0, ambiguous: false };
  if (literals.length !== 1 || missing.length !== 1) return { band: null, value: 0, ambiguous: true };

  const literal = literals[0];
  const value = literal === "null" ? 0 : numericValue(literal);
  return value === null ? { band: null, value: 0, ambiguous: true } : { band: missing[0], value, ambiguous: false };
}

/**
 * `1.3-depth-overtime-boundary`: 40 and a value just past it, in one column of one table.
 *
 * Overtime starts strictly above 40, so the pair has to sit in the same column of the same table —
 * two tables each holding one side state nothing about where the threshold is.
 */
function overtimeBoundary(shape) {
  for (const table of shape.tables) {
    const weekday = findColumn(table, EVAL_14_ROLES.weekday);
    if (!weekday) continue;
    const values = table.cases.map((one) => numericValue(one[weekday.header])).filter((value) => value !== null);
    const past = values.find((value) => value > OVERTIME_THRESHOLD && value <= OVERTIME_THRESHOLD + 1);
    if (values.includes(OVERTIME_THRESHOLD) && past !== undefined) {
      return { method: table.method, column: weekday.header, past };
    }
  }
  return null;
}

/**
 * `1.4-depth-combined-scenario`: one row working weekday, Sunday and holiday hours together.
 *
 * Weekday hours count however the table names them — a row carrying regular and overtime bands is
 * working weekday hours just as much as one carrying an unclassified count.
 */
function combinedScenario(shape) {
  for (const table of shape.tables) {
    const bands = hourColumns(table);
    if (!bands || !bands.sunday || !bands.holiday) continue;
    const weekdayColumns = [bands.weekday, bands.regular, bands.overtime].filter(Boolean);
    if (weekdayColumns.length === 0) continue;

    for (const one of table.cases) {
      const weekday = weekdayColumns
        .map((column) => numericValue(one[column.header]))
        .filter((value) => value !== null);
      const sunday = numericValue(one[bands.sunday.header]);
      const holiday = numericValue(one[bands.holiday.header]);
      if (weekday.length === 0 || sunday === null || holiday === null) continue;
      if (weekday.some((value) => value > 0) && sunday > 0 && holiday > 0) {
        return { method: table.method, hours: [Math.max(...weekday), sunday, holiday] };
      }
    }
  }
  return null;
}

/** Every case in the class carrying a value for `role`, with the table it sits in. */
function valuesForRole(shape, role) {
  const found = [];
  for (const table of shape.tables) {
    const column = findInputColumn(table, role);
    if (!column) continue;
    for (const one of table.cases) {
      const value = numericValue(one[column.header]);
      if (value !== null) found.push({ table, method: table.method, header: column.header, value, row: one });
    }
  }
  return found;
}

/** `1.5-depth-error-edge-cases`: a visible negative-hours row, and a negative rate rejected. */
function errorEdgeCases(shape) {
  const negativeHours = [
    EVAL_14_ROLES.weekday,
    EVAL_14_ROLES.regular,
    EVAL_14_ROLES.overtime,
    EVAL_14_ROLES.sunday,
    EVAL_14_ROLES.holiday,
  ]
    .flatMap((role) => valuesForRole(shape, role))
    .find((one) => one.value < 0);
  const negativeRate = valuesForRole(shape, EVAL_14_ROLES.rate).find((one) => one.value < 0);
  return { negativeHours: negativeHours || null, negativeRate: negativeRate || null };
}

/**
 * The Sunday and holiday hour columns that carry a blank cell, with the parameter type each feeds.
 *
 * This is `1.6-readability-empty-cells`, and its two halves are one change: a blank cell converts
 * to null, so a column written blank must feed a boxed parameter or the answer does not compile.
 * The grader has failed this slot on both counts in the same sentence.
 */
function blankHourColumns(shape) {
  const found = [];
  for (const table of shape.tables) {
    for (const role of [EVAL_14_ROLES.sunday, EVAL_14_ROLES.holiday]) {
      const column = findColumn(table, role);
      if (!column) continue;
      const blanks = table.rows.filter((row) => String(row.cells[column.index] ?? "").trim() === "").length;
      if (blanks === 0) continue;
      const type = column.param ? column.param.type : null;
      found.push({
        method: table.method,
        header: column.header,
        type,
        boxed: Boolean(type) && /^(Integer|Double|Long|Float|BigDecimal|Short|Byte)\b/.test(type),
      });
    }
  }
  return found;
}

/** `1.14-depth-zero-rate`: a row at a zero rate stating that the week pays nothing. */
function zeroRateRow(shape) {
  const { cases } = payCases(shape);
  return cases.find((one) => one.rate === 0 && Math.abs(one.stated) < PENCE) || null;
}

/**
 * `1.2-error-has-expected-column`: the rejection is stated per row, not in prose.
 *
 * Only asked of a class that rejects something — a negative rate row is what makes the rejection
 * table exist, so its own table is where the expectation column has to be.
 */
function rejectionExpectationColumn(shape) {
  const negativeRate = valuesForRole(shape, EVAL_14_ROLES.rate).find((one) => one.value < 0);
  if (!negativeRate) return { tested: false, column: null };
  const column = negativeRate.table.expectationColumns.find((one) => EVAL_14_ROLES.thrown.test(one.header));
  return { tested: true, column: column || null, method: negativeRate.method };
}

/**
 * Column headers written in code rather than in the business's words.
 *
 * An exception column is exempt: `Throws?` is TableTest's own name for the outcome and
 * `1.2-error-has-expected-column` requires it, so failing it here would set the two against
 * each other.
 */
function implementationHeaders(shape) {
  const found = [];
  for (const table of shape.tables) {
    for (const column of table.columns) {
      if (column.isScenario) continue;
      if (EVAL_14_ROLES.thrown.test(column.header)) continue;
      // A parenthesised suffix names a unit or a source — `Overtime Threshold (hrs)`,
      // `Cutoff (Policy)` — which the skill encourages and the grader passes. Strip it before
      // looking for implementation vocabulary, or every declared unit reads as an abbreviation.
      const header = column.header.replace(/\?$/, "").replace(/\s*\([^)]*\)\s*$/, "");
      const camelCase = /[a-z][A-Z]/.test(header);
      const abbreviation = /\b(hrs|hr|amt|num|qty|param\d*|val|idx|str|int|dbl)\b/i.test(header);
      if (camelCase || abbreviation) found.push({ method: table.method, header: column.header });
    }
  }
  return found;
}

/** The hours a row states, naming only the bands it actually carries. */
function describeHours(one) {
  const labels = { weekday: "weekday", regular: "regular", overtime: "overtime", sunday: "Sunday", holiday: "holiday" };
  const parts = Object.entries(labels)
    .filter(([band]) => one[band] !== undefined)
    .map(([band, label]) => `${label} ${one[band]}`);
  return parts.length === 0 ? "no hours" : parts.join(", ");
}

const EVAL_14_RELATIONS = [
  {
    id: "1.3-depth-overtime-boundary",
    label: "40 and just past it, one column, one table",
    evaluate: (shape) => {
      const found = overtimeBoundary(shape);
      return {
        holds: found !== null,
        evidence: found
          ? `${found.method}: ${found.column} holds 40 and ${found.past}`
          : "no table holds 40 beside a value in (40, 41] in the same column",
      };
    },
  },
  {
    id: "1.4-depth-combined-scenario",
    label: "one row works all three hour types",
    evaluate: (shape) => {
      const found = combinedScenario(shape);
      return {
        holds: found !== null,
        evidence: found
          ? `${found.method}: weekday ${found.hours[0]}, Sunday ${found.hours[1]}, holiday ${found.hours[2]}`
          : "no row carries weekday, Sunday and holiday hours together",
      };
    },
  },
  {
    id: "1.5-depth-error-edge-cases",
    label: "negative hours shown, negative rate rejected",
    evaluate: (shape) => {
      const { negativeHours, negativeRate } = errorEdgeCases(shape);
      const missing = [];
      if (!negativeHours) missing.push("no row states what negative hours do");
      if (!negativeRate) missing.push("no row carries a negative rate");
      return {
        holds: missing.length === 0,
        evidence:
          missing.length === 0
            ? `negative hours ${negativeHours.value} in ${negativeHours.header}, negative rate ${negativeRate.value}`
            : missing.join("; "),
      };
    },
  },
  {
    id: "1.6-readability-empty-cells",
    label: "blank Sunday/holiday cells, boxed parameters",
    evaluate: (shape) => {
      const blanks = blankHourColumns(shape);
      if (blanks.length === 0) {
        const types = [...new Set(shape.tables.flatMap((table) =>
          [EVAL_14_ROLES.sunday, EVAL_14_ROLES.holiday]
            .map((role) => findColumn(table, role))
            .filter(Boolean)
            .map((column) => (column.param ? column.param.type : "?")),
        ))];
        return {
          holds: false,
          evidence: `no blank cell in any Sunday or holiday hours column${types.length ? ` (declared ${types.join(", ")})` : ""}`,
        };
      }
      const unboxed = blanks.filter((one) => !one.boxed);
      return {
        holds: unboxed.length === 0,
        evidence:
          unboxed.length === 0
            ? `blanks in ${blanks.map((one) => `${one.header} (${one.type})`).join(", ")}`
            : `blank cells on unboxed parameters: ${unboxed.map((one) => `${one.header} (${one.type})`).join(", ")}`,
      };
    },
  },
  {
    id: "1.8-correctness-expected-values",
    label: "every stated pay is arithmetically right",
    evaluate: (shape) => {
      const { cases, unresolved } = payCases(shape);
      const wrong = cases.filter((one) => Math.abs(one.pay - one.stated) > PENCE);
      const note = unresolved > 0 ? ` (${unresolved} row(s) unresolved: hours or rate held in the body)` : "";
      if (cases.length === 0) {
        return { holds: false, evidence: `no row states a pay this could check${note}` };
      }
      return {
        holds: wrong.length === 0,
        evidence:
          wrong.length === 0
            ? `${cases.length} row(s) correct${note}`
            : wrong
                .slice(0, 4)
                .map(
                  (one) => `${one.method}: ${describeHours(one)} @ ${one.rate} → stated ${one.stated}, rules give ${one.pay}`,
                )
                .join("; ") + note,
      };
    },
  },
  {
    id: "1.9-correctness-value-set-semantics",
    label: "a value set only where the result is the same",
    evaluate: (shape) => {
      const offenders = [];
      for (const table of shape.tables) {
        const pay = payColumn(table);
        const rate = findInputColumn(table, EVAL_14_ROLES.rate);
        const bands = hourColumns(table);
        if (!pay || !rate || !bands) continue;

        for (const row of table.rows) {
          const expanded = rowCases(table.columns, row.cells);
          if (expanded.length < 2) continue;
          const paid = expanded.map((one) => {
            const hours = {};
            for (const [band, column] of Object.entries(bands)) {
              if (column) hours[band] = hoursValue(one[column.header]);
            }
            const values = { ...hours, rate: numericValue(one[rate.header]) };
            if (Object.values(values).some((value) => value === null)) return null;
            return payForRow(values);
          });
          if (paid.some((value) => value === null)) continue;
          if (new Set(paid.map((value) => value.toFixed(2))).size > 1) {
            offenders.push(`${table.method}: a value set spans pays ${[...new Set(paid)].join(", ")}`);
          }
        }
      }
      return {
        holds: offenders.length === 0,
        evidence: offenders.length === 0 ? "no value set spans differing results" : offenders.slice(0, 3).join("; "),
      };
    },
  },
  {
    id: "1.14-depth-zero-rate",
    label: "a zero rate pays nothing",
    evaluate: (shape) => {
      const found = zeroRateRow(shape);
      return {
        holds: found !== null,
        evidence: found
          ? `${found.method}: ${describeHours(found)} at rate 0 pay 0`
          : "no row states a zero rate against a zero pay",
      };
    },
  },
  {
    id: "1.2-error-has-expected-column",
    label: "the rejection is a column, not prose",
    evaluate: (shape) => {
      const { tested, column, method } = rejectionExpectationColumn(shape);
      if (!tested) return { holds: false, evidence: "no negative-rate row, so no rejection is stated at all" };
      return {
        holds: column !== null,
        evidence: column ? `${method}: ${column.header}` : `${method}: rejection row with no exception column`,
      };
    },
  },
  {
    id: "business-language-columns",
    label: "no column header written in code",
    evaluate: (shape) => {
      const found = implementationHeaders(shape);
      return {
        holds: found.length === 0,
        evidence:
          found.length === 0
            ? "every header reads as business language"
            : found.map((one) => `${one.method}: ${one.header}`).join("; "),
      };
    },
  },
];


// ---------------------------------------------------------------------------
// eval-15 reis-discount
// ---------------------------------------------------------------------------

/**
 * How eval-15's answers name the quantities its assertions are about.
 *
 * This is the widest vocabulary in the suite: eighteen draws name the travel count `Trips In
 * Window`, `Ticket Number`, `Purchase Number`, `Prior Single Tickets (30 Days)`, `Trailing Single
 * Count` and eleven other ways. A role therefore matches on what the column *is*, and the survey
 * behind these patterns is `node scripts/shape-report.js --eval 15`, which prints every header.
 */
const EVAL_15_ROLES = {
  count: /\b(counts?|number|trips?|tickets)\b/i,
  discount: /discount/i,
  category: /travell?er|category|passenger/i,
  zone: /zone/i,
  ticketType: /ticket type|^type\??$/i,
  history: /history|past purchases|^purchases\??$/i,
  time: /purchased|purchase time|days? ago|\bwhen\b/i,
};

/** A column stating a constant the code owns rather than a quantity a row varies. */
const EVAL_15_POLICY = /\(policy\)|\bmax(imum)?\b|window \(days\)/i;

/** Reis raises the discount five points every fifth ticket and stops at forty. */
const REIS_STEP = 5;
const REIS_MAX = 40;

/** The tier a ticket number earns: nothing below the fifth, then a rung every five, capped. */
function reisTier(number) {
  if (number < REIS_STEP) return 0;
  return Math.min(Math.floor(number / REIS_STEP) * REIS_STEP, REIS_MAX);
}

/**
 * The two readings of the count the ladder consumes.
 *
 * "The first discount applies to ticket number five" can be counted with the ticket being bought
 * included (five tickets means 5%) or as prior purchases only (four behind you plus this one).
 * Both are defensible readings of the prompt and `new-purchase-inclusion-published` exists because
 * the prompt leaves it open, so a relation about the ladder's *internal consistency* must not
 * quietly pick one. Every such relation tries both and reports which fits.
 */
const COUNT_READINGS = [
  { name: "count includes this ticket", tier: (count) => reisTier(count) },
  { name: "count is prior purchases only", tier: (count) => reisTier(count + 1) },
];

/** A percentage cell, with or without its sign: `20`, `20%`, `20 %`. */
function percentValue(cell) {
  return numericValue(String(cell ?? "").replace(/\s*%\s*$/, ""));
}

/** An input column for `role`, never an expectation and never a declared policy constant. */
function eval15Input(table, role) {
  const column = table.columns.find(
    (one) => !one.isScenario && !one.isExpectation && role.test(one.header) && !EVAL_15_POLICY.test(one.header),
  );
  return column || null;
}

/** An expectation column for `role`. */
function eval15Expectation(table, role) {
  return table.expectationColumns.find((one) => role.test(one.header)) || null;
}

/** The travel-count column of a table, input side, excluding the ticket *type*. */
function countInput(table) {
  const column = eval15Input(table, EVAL_15_ROLES.count);
  return column && !EVAL_15_ROLES.ticketType.test(column.header) ? column : null;
}

/**
 * The one table stating the count-to-percentage ladder, or null.
 *
 * The ladder is whichever table maps the most distinct percentages from a travel count. Three is
 * the floor for calling it an enumeration of tiers: iteration-60 splits children and adults into
 * two tables and only the adult one is a ladder, while the reference's scheme table maps two
 * percentages and is not.
 */
function ladderTables(shape) {
  const found = [];
  for (const table of shape.tables) {
    const count = countInput(table);
    const discount = eval15Expectation(table, EVAL_15_ROLES.discount);
    if (!count || !discount) continue;
    const category = eval15Input(table, EVAL_15_ROLES.category);
    const rungs = new Map();
    for (const one of table.cases) {
      // A child's flat rate is not a rung: children do not follow the ladder, so counting their
      // row makes `{0, 5, 40} → 20` look like three tiers contaminating each other. That read
      // failed iterations 44 and 50 for a table the grader was right to pass.
      if (category && /CHILD/i.test(String(one[category.header] ?? ""))) continue;
      const number = numericValue(one[count.header]);
      const percent = percentValue(one[discount.header]);
      if (number === null || percent === null) continue;
      if (!rungs.has(percent)) rungs.set(percent, []);
      rungs.get(percent).push(number);
    }
    if (rungs.size >= 3) found.push({ table, count, discount, category, rungs });
  }
  return found.sort((a, b) => b.rungs.size - a.rungs.size);
}

/** The rows of a table that state a rung, child rows excluded as `ladderTables` excludes them. */
function rungRows(ladder) {
  return ladder.table.rows.filter(
    (row) => !ladder.category || !/CHILD/i.test(String(row.cells[ladder.category.index] ?? "")),
  );
}

/**
 * The entries of a purchase-history cell.
 *
 * Notations vary and most leave the single ticket implicit — `5d WEEKLY;10d MONTHLY;15d` is one
 * period entry, another period entry and one single. So an entry naming no period type *is* the
 * counting kind, which is why this splits rather than searching for the word SINGLE.
 */
function historyEntries(cell) {
  const text = String(cell ?? "").trim();
  if (text === "") return [];
  // A bracketed history nests: `[[purchasedAt: ..., ticketType: WEEKLY, ...]]` is ONE entry whose
  // own commas separate fields. Splitting on every comma read it as four entries of which one
  // named a period ticket, and called a single-entry history mixed — iterations 71 and 87.
  const elements = parseCollectionElements(text);
  if (elements !== null) return elements.map((entry) => entry.trim()).filter(Boolean);
  return text
    .split(";")
    .map((entry) => entry.trim())
    .filter(Boolean);
}

/** True where a history cell holds both a counting and a non-counting purchase. */
function historyMixesKinds(cell) {
  const entries = historyEntries(cell);
  const period = entries.filter((entry) => /WEEKLY|MONTHLY/i.test(entry));
  return period.length > 0 && period.length < entries.length;
}

/** The ladder, or null where no table enumerates three rungs. */
function ladderTable(shape) {
  return ladderTables(shape)[0] || null;
}

/** Tables deciding a discount from the traveller category. */
function schemeTables(shape) {
  return shape.tables
    .filter((table) => eval15Input(table, EVAL_15_ROLES.category))
    .filter((table) => eval15Expectation(table, EVAL_15_ROLES.discount));
}

/**
 * Tables deciding countability or a travel count — the rolling-window concern.
 *
 * Both shapes belong to it: a boolean saying whether one past purchase counts, and an integer
 * saying how many of a history do. Neither is the discount concern, which is what separates
 * `zone-independent-counting` from `zone-irrelevance-visible`.
 */
function countingTables(shape) {
  return shape.tables.filter((table) => {
    const expectation = eval15Expectation(table, EVAL_15_ROLES.count);
    return Boolean(expectation) && !eval15Expectation(table, EVAL_15_ROLES.discount);
  });
}

/** Every cell of a table, as text — history cells included, for scanning their elements. */
function cellTexts(table) {
  return table.rows.flatMap((row) => row.cells.map((cell) => String(cell ?? "")));
}

/** The distinct zone names a table's rows mention anywhere, cells and value sets alike. */
function zonesMentioned(table) {
  const zones = new Set();
  for (const text of cellTexts(table)) {
    for (const match of text.matchAll(/ZONE[_\s]?(\d+)/gi)) zones.add(match[1]);
  }
  return zones;
}

/** True where a cell is a value set — braces at the top level of the cell. */
function isValueSet(cell) {
  const text = String(cell ?? "").trim();
  return text.startsWith("{") && text.endsWith("}");
}

/** An ISO-style absolute date or date-time, which a relative-time table must not carry. */
const ABSOLUTE_DATE = /\d{4}-\d{2}-\d{2}/;

const EVAL_15_RELATIONS = [
  {
    id: "2.19-depth-all-tiers",
    label: "all nine rungs of the ladder",
    evaluate: (shape) => {
      const ladder = ladderTable(shape);
      if (!ladder) return { holds: false, evidence: "no table enumerates three or more rungs" };
      const expected = [0, 5, 10, 15, 20, 25, 30, 35, 40];
      const present = new Set([...ladder.rungs.keys()]);
      const missing = expected.filter((percent) => !present.has(percent));
      return {
        holds: missing.length === 0,
        evidence:
          missing.length === 0
            ? `${ladder.table.method}: all of ${expected.join(", ")}`
            : `${ladder.table.method}: missing ${missing.join(", ")} (has ${[...present].sort((a, b) => a - b).join(", ")})`,
      };
    },
  },
  {
    id: "2.3-depth-tier-boundaries",
    label: "several tiers, and the maximum",
    evaluate: (shape) => {
      const ladder = ladderTable(shape);
      if (!ladder) return { holds: false, evidence: "no table enumerates three or more rungs" };
      const hasMax = ladder.rungs.has(REIS_MAX);
      return {
        holds: hasMax,
        evidence: hasMax
          ? `${ladder.rungs.size} rungs including the ${REIS_MAX}% maximum`
          : `${ladder.rungs.size} rungs, none of them the ${REIS_MAX}% maximum`,
      };
    },
  },
  {
    id: "2.20-readability-one-row-per-tier",
    label: "one tier, one row",
    evaluate: (shape) => {
      const ladder = ladderTable(shape);
      if (!ladder) return { holds: false, evidence: "no ladder table to read" };
      const split = [];
      for (const [percent] of ladder.rungs) {
        const rows = rungRows(ladder).filter(
          (row) => percentValue(row.cells[ladder.discount.index]) === percent,
        );
        if (rows.length > 1) split.push(`${percent}% over ${rows.length} rows`);
      }
      return {
        holds: split.length === 0,
        evidence: split.length === 0 ? `${ladder.rungs.size} rungs, one row each` : split.join("; "),
      };
    },
  },
  {
    id: "2.15-ticket-count-uses-value-sets",
    label: "the count column groups tiers in value sets",
    evaluate: (shape) => {
      const ladder = ladderTable(shape);
      if (!ladder) return { holds: false, evidence: "no ladder table to read" };
      // Whether a tier is *split* across rows is 2.20's question. This one is about notation: a
      // ladder that never groups counts is enumerating boundary values. One bare row beside eight
      // value sets is not that, and reading it as all-or-nothing failed iterations 40 and 80.
      // Most rungs grouping their counts is the table expressing tiers; a lone value set among
      // sixteen single counts is a table enumerating boundaries with one exception (iteration-86),
      // and nine among ten is not (iteration-80).
      const rows = rungRows(ladder);
      const grouped = rows.filter((row) => isValueSet(row.cells[ladder.count.index]));
      return {
        holds: grouped.length * 2 >= rows.length && grouped.length > 0,
        evidence: `${grouped.length} of ${rows.length} rungs group counts in a value set`,
      };
    },
  },
  {
    id: "2.9-correctness-value-set-tier-semantics",
    label: "no value set spans two tiers",
    evaluate: (shape) => {
      const ladder = ladderTable(shape);
      if (!ladder) return { holds: false, evidence: "no ladder table to read" };
      const anyValueSet = rungRows(ladder).some((row) => isValueSet(row.cells[ladder.count.index]));
      if (!anyValueSet) {
        return {
          holds: true,
          evidence: "VACUOUS: the ladder holds no value set, so none can span two tiers — this is 2.15's question, not this one",
        };
      }
      const failures = COUNT_READINGS.map((reading) => {
        const wrong = [];
        for (const [percent, numbers] of ladder.rungs) {
          for (const number of numbers) {
            if (reading.tier(number) !== percent) wrong.push(`${number}→${percent}% (rule gives ${reading.tier(number)}%)`);
          }
        }
        return { reading, wrong };
      });
      const clean = failures.find((one) => one.wrong.length === 0);
      const best = failures.reduce((a, b) => (a.wrong.length <= b.wrong.length ? a : b));
      return {
        holds: Boolean(clean),
        evidence: clean
          ? `consistent under "${clean.reading.name}"`
          : `no reading fits; closest is "${best.reading.name}" with ${best.wrong.slice(0, 4).join(", ")}`,
      };
    },
  },
  {
    id: "2.16-no-duplicate-tier-mapping",
    label: "the ladder is stated once",
    evaluate: (shape) => {
      const ladders = ladderTables(shape);
      return {
        holds: ladders.length <= 1,
        evidence:
          ladders.length <= 1
            ? ladders.length === 1
              ? `only ${ladders[0].table.method} maps counts to percentages`
              : "no table enumerates rungs"
            : `${ladders.length} tables enumerate rungs: ${ladders.map((one) => one.table.method).join(", ")}`,
      };
    },
  },
  {
    id: "2.2-children-flat-discount",
    label: "a child row states the flat 20%",
    evaluate: (shape) => {
      for (const table of schemeTables(shape)) {
        const category = eval15Input(table, EVAL_15_ROLES.category);
        const discount = eval15Expectation(table, EVAL_15_ROLES.discount);
        for (const one of table.cases) {
          if (!/CHILD/i.test(String(one[category.header]))) continue;
          if (percentValue(one[discount.header]) === 20) {
            return { holds: true, evidence: `${table.method}: CHILD → 20%` };
          }
        }
      }
      return { holds: false, evidence: "no row states a child's flat 20%" };
    },
  },
  {
    id: "2.18-adult-senior-value-set",
    label: "{ADULT, SENIOR} in one row",
    evaluate: (shape) => {
      const withCategory = shape.tables.filter((table) => eval15Input(table, EVAL_15_ROLES.category));
      if (withCategory.length === 0) {
        return { holds: false, evidence: "no table carries a traveller category at all" };
      }
      for (const table of withCategory) {
        const category = eval15Input(table, EVAL_15_ROLES.category);
        for (const row of table.rows) {
          const cell = String(row.cells[category.index] ?? "");
          if (isValueSet(cell) && /ADULT/i.test(cell) && /SENIOR/i.test(cell)) {
            return { holds: true, evidence: `${table.method}: ${cell}` };
          }
        }
      }
      return {
        holds: false,
        evidence: `adult and senior enumerated separately in ${withCategory.map((one) => one.method).join(", ")}`,
      };
    },
  },
  {
    id: "2.17-zone-irrelevance-visible",
    label: "a zone value set where the percentage is decided",
    evaluate: (shape) => {
      const deciding = shape.tables.filter((table) => eval15Expectation(table, EVAL_15_ROLES.discount));
      if (deciding.length === 0) return { holds: false, evidence: "no table decides a discount" };
      for (const table of deciding) {
        const zone = eval15Input(table, EVAL_15_ROLES.zone);
        if (!zone) continue;
        const set = table.rows.find((row) => isValueSet(row.cells[zone.index]));
        if (set) return { holds: true, evidence: `${table.method}: ${set.cells[zone.index]}` };
        if (zonesMentioned(table).size > 1) {
          return { holds: true, evidence: `${table.method}: zones ${[...zonesMentioned(table)].join(", ")} across rows` };
        }
      }
      return {
        holds: false,
        evidence: `no zone value set where the discount is decided (${deciding.map((one) => one.method).join(", ")})`,
      };
    },
  },
  {
    id: "zone-independent-counting",
    label: "the counting concern varies zone by row",
    evaluate: (shape) => {
      const counting = countingTables(shape);
      if (counting.length === 0) return { holds: false, evidence: "no table decides countability or a count" };
      for (const table of counting) {
        const zone = eval15Input(table, EVAL_15_ROLES.zone);
        if (zone) {
          const set = table.rows.find((row) => isValueSet(row.cells[zone.index]));
          if (set) return { holds: true, evidence: `${table.method}: ${set.cells[zone.index]}` };
        }
        const zones = zonesMentioned(table);
        if (zones.size > 1) {
          return { holds: true, evidence: `${table.method}: zones ${[...zones].sort().join(", ")} in its rows` };
        }
      }
      return {
        holds: false,
        evidence: `zone never varies in the counting concern (${counting.map((one) => one.method).join(", ")})`,
      };
    },
  },
  {
    id: "period-ticket-excluded-from-count",
    label: "a period ticket is refused by the counting rule",
    evaluate: (shape) => {
      const counting = countingTables(shape);
      if (counting.length === 0) return { holds: false, evidence: "no table decides countability or a count" };
      const hosts = counting.filter((table) => cellTexts(table).some((text) => /WEEKLY|MONTHLY/i.test(text)));
      return {
        holds: hosts.length > 0,
        evidence:
          hosts.length > 0
            ? `${hosts.map((one) => one.method).join(", ")} carries a period ticket`
            : `period tickets never reach the counting rule (${counting.map((one) => one.method).join(", ")})`,
      };
    },
  },
  {
    id: "count-derived-from-raw-history",
    label: "a history with both kinds produces a count",
    evaluate: (shape) => {
      for (const table of countingTables(shape)) {
        const history = eval15Input(table, EVAL_15_ROLES.history);
        if (!history) continue;
        const mixed = table.rows.find((row) => historyMixesKinds(row.cells[history.index]));
        if (mixed) {
          return { holds: true, evidence: `${table.method}: ${mixed.cells[history.index]}` };
        }
      }
      return {
        holds: false,
        evidence: "no history column carries a counting and a non-counting purchase together",
      };
    },
  },
  {
    id: "2.21-readability-relative-time",
    label: "the window table states time relatively",
    evaluate: (shape) => {
      const windowed = countingTables(shape).filter(
        (table) => eval15Input(table, EVAL_15_ROLES.time) || eval15Input(table, EVAL_15_ROLES.history),
      );
      if (windowed.length === 0) return { holds: false, evidence: "no rolling-window table to read" };
      // A column naming the purchase instant is the reference point itself, and stating it in the
      // table is what makes the table readable without knowing it from elsewhere. The assertion is
      // about the *history* entries being relative to that point.
      const absolute = [];
      for (const table of windowed) {
        const dates = table.rows.flatMap((row) =>
          row.cells
            .map((cell, index) => ({ cell: String(cell ?? ""), column: table.columns[index] }))
            .filter(({ cell, column }) => ABSOLUTE_DATE.test(cell) && !/purchase time|reference|now|\bat\b/i.test(column ? column.header : ""))
            .map(({ cell }) => cell),
        );
        if (dates.length > 0) absolute.push(`${table.method}: ${dates[0]}`);
      }
      return {
        holds: absolute.length === 0,
        evidence:
          absolute.length === 0
            ? `${windowed.map((one) => one.method).join(", ")} carry no absolute date`
            : absolute.slice(0, 3).join("; "),
      };
    },
  },
  {
    id: "rule-falsifiable-by-a-row",
    label: "no constant expectation column",
    // Advisory here, unlike on eval-18: this eval's whole subject is invariance — zone does not
    // affect the discount, a child's rate does not move with travel — so a constant expectation
    // column is the *claim* in several tables and the exemption applies more often than not.
    advisory: true,
    judgement: "the stated-invariance exemption is a reading of the title and description",
    evaluate: (shape) => {
      const constant = shape.tables.flatMap((table) =>
        constantExpectationColumns(table).map((column) => `${table.method}: ${column.header}=${column.value}`),
      );
      return {
        holds: constant.length === 0,
        evidence: constant.length === 0 ? "every expectation column varies" : constant.join("; "),
      };
    },
  },
];

/** Every eval this module can read, by eval number. */
const EVALS = {
  14: { call: null, relations: EVAL_14_RELATIONS },
  15: { call: null, relations: EVAL_15_RELATIONS },
  18: { call: EVAL_18_CALL, relations: EVAL_18_RELATIONS },
};

/** The relations and call name for an eval number, or null where none are authored. */
function relationsFor(number) {
  return EVALS[number] || null;
}

/** The eval numbers relations exist for, for a CLI to name in its error message. */
function authoredEvals() {
  return Object.keys(EVALS);
}

module.exports = {
  EVAL_14_RELATIONS,
  EVAL_15_RELATIONS,
  EVAL_18_RELATIONS,
  ageBoundaryPair,
  authoredEvals,
  bandedAgePair,
  claimBoundaryPair,
  claimEffectBands,
  blankHourColumns,
  claimsByAge,
  combinedScenario,
  countingTables,
  eval15Input,
  historyEntries,
  historyMixesKinds,
  ladderTable,
  ladderTables,
  rungRows,
  percentValue,
  reisTier,
  schemeTables,
  zonesMentioned,
  errorEdgeCases,
  implementationHeaders,
  internalColumns,
  overtimeBoundary,
  findInputColumn,
  heldBandValue,
  hourColumns,
  payCases,
  payForRow,
  payColumn,
  rejectionExpectationColumn,
  zeroRateRow,
  perClaimTriple,
  policyColumns,
  premiumCases,
  relationsFor,
  tablesAssertingBoth,
};
