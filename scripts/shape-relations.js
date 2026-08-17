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
  falsifiabilityRelation(),
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
  falsifiabilityRelation(),
];


// ---------------------------------------------------------------------------
// eval-30 order-splitting
// ---------------------------------------------------------------------------

/**
 * The five splitting rules eval-30 asks each to have its own exercised table, and the tokens that
 * identify each in a header or a cell.
 *
 * A facet is often carried inside a compact item shorthand rather than in a column of its own —
 * the reference writes `[camera: DELIVERY@Addr-A, mug: PICKUP]` in one Items column — so a facet
 * is looked for in the cells as much as in the headers.
 */
const EVAL_30_FACETS = {
  fulfillment: { header: /fulfil?ment|pickup|delivery/i, token: /\bDELIVERY\b|\bPICKUP\b/i },
  address: { header: /address|\baddr/i, token: /\bAddr[-\w]*\b|address/i },
  availability: {
    header: /stock|availab/i,
    token: /\bIN_STOCK\b|\bBACKORDER\w*\b|\bPRE_ORDER\w*\b|\bIMMEDIATE\b|\bWHEN_AVAILABLE\b/i,
  },
  // `wh1`, `warehouse-1` and `W1` all name a warehouse; iteration-45 writes `wh1` and reading only
  // `W\d` found no warehouse facet in a table plainly about warehouses.
  warehouse: {
    header: /warehouse|\bw(?:h|arehouse)?[-_ ]?\d+\b/i,
    token: /\bw(?:h|arehouse)?[-_ ]?\d+\b/i,
    surface: /warehouse|fewest|minimis|minimiz/i,
  },
  companion: { header: /companion/i, token: /companion/i, surface: /companion|together|co-locat/i },
};

/**
 * True where the table's published surface says it is about this facet.
 *
 * A concern is often named only in the method name — `keepsCompanionsTogetherWhenPossible` over
 * `Items | Stock` columns — so a facet that no cell and no header carries can still be the table's
 * declared subject. Four of eval-30's draws identify companions this way and nowhere else.
 */
function facetOnSurface(table, facet) {
  if (!facet.surface) return false;
  return facet.surface.test(`${table.method || ""} ${table.displayName || ""} ${table.description || ""}`);
}

/** A cell carries a facet when its own text names it or its column header does. */
function bearsFacet(table, column, cell, facet) {
  if (column.isExpectation || column.isScenario) return false;
  return facet.header.test(column.header) || facet.token.test(String(cell ?? ""));
}

/** The facet-bearing input cells of a row, joined — the row's value for that facet. */
function facetSignature(table, row, facet) {
  return table.columns
    .filter((column) => bearsFacet(table, column, row.cells[column.index], facet))
    .map((column) => String(row.cells[column.index] ?? ""))
    .join(" | ");
}

/** The expectation cells of a row, joined — the row's answer. */
function outcomeSignature(table, row) {
  return table.expectationColumns.map((column) => String(row.cells[column.index] ?? "")).join(" | ");
}

/**
 * The table that exercises `facet`, or null.
 *
 * Being *about* a rule is not enough: the assertion asks that at least one row's outcome depend on
 * it, so two rows must differ in the facet and differ in their answer. A companion table whose
 * companions already sit in one warehouse by the minimal-cover rule exercises nothing, and this is
 * what catches that.
 */
function exercisingTable(shape, facet) {
  for (const table of shape.tables) {
    const rows = table.rows;
    if (rows.length < 2 || table.expectationColumns.length === 0) continue;
    const signatures = rows.map((row) => facetSignature(table, row, facet));
    const inCells = signatures.some((signature) => signature !== "");
    const facetVaries = new Set(signatures).size > 1;
    const onSurface = facetOnSurface(table, facet);
    if (!inCells && !onSurface) continue;
    // A facet the table holds constant is not absent: the reference declares the same companion
    // pair on every row and creates the situation through the stock columns instead. Where the
    // facet does not vary, the table's declared subject is what identifies the concern.
    if (!facetVaries && !onSurface) continue;

    for (let i = 0; i < rows.length; i++) {
      for (let j = i + 1; j < rows.length; j++) {
        const outcomeDiffers = outcomeSignature(table, rows[i]) !== outcomeSignature(table, rows[j]);
        if (!outcomeDiffers) continue;
        if (!facetVaries) return { table, rows: [i + 1, j + 1], viaSurface: true };
        if (signatures[i] !== signatures[j]) return { table, rows: [i + 1, j + 1], viaSurface: false };
      }
    }
  }
  return null;
}


/** The members of a set-valued cell: `{camera, lens}` is two products, `{}` is none. */
function setMembers(cell) {
  const elements = parseCollectionElements(String(cell ?? "").trim());
  return elements === null ? [] : elements.map((element) => element.trim()).filter(Boolean);
}

/** Every subset of `items` of exactly `size`, as index lists. */
function subsetsOfSize(items, size) {
  if (size === 0) return [[]];
  if (items.length < size) return [];
  const [first, ...rest] = items;
  return [...subsetsOfSize(rest, size - 1).map((tail) => [first, ...tail]), ...subsetsOfSize(rest, size)];
}

/**
 * The smallest sets of warehouses that cover the order, all of them.
 *
 * Two or more means a tie, and a tie is what the companion rule exists to break. The search is
 * exhaustive because the domain is tiny — a handful of products across three or four warehouses.
 */
function minimalCovers(order, stock) {
  const names = Object.keys(stock);
  for (let size = 1; size <= names.length; size++) {
    const covers = subsetsOfSize(names, size).filter((chosen) =>
      order.every((product) => chosen.some((name) => stock[name].includes(product))),
    );
    if (covers.length > 0) return covers;
  }
  return [];
}

/**
 * Whether a row's companion rule decides anything, and why.
 *
 * The assertion's own failure case is "a companion table whose companions already sit in one
 * warehouse by the minimal-cover rule tests nothing". So the rule is exercised only where the
 * minimal covers *tie* and the companions can be kept together in some of those covers but not
 * all — which is a computation over the row's own cells, not a property of the facet varying. The
 * reference holds its Companions column constant and exercises the rule through the stock columns,
 * which is why the generic facet test cannot see it.
 */
function companionBreaksATie(order, stock, companions) {
  if (companions.length < 2 || order.length === 0) return null;
  const covers = minimalCovers(order, stock);
  if (covers.length < 2) return null;
  const together = covers.filter((cover) =>
    cover.some((name) => companions.every((product) => stock[name].includes(product))),
  );
  if (together.length === 0 || together.length === covers.length) return null;
  return { covers: covers.map((cover) => cover.join("+")), kept: together[0].join("+") };
}

/** The warehouse-stock columns of a table, by warehouse name. */
function stockColumns(table) {
  const found = {};
  for (const column of table.columns) {
    if (column.isScenario || column.isExpectation) continue;
    const name = column.header.match(/\b(W\d+)\b/i);
    if (name) found[name[1].toUpperCase()] = column;
  }
  return found;
}

/**
 * A row where the companion rule breaks a tie between equally minimal covers, or null.
 *
 * Needs the table to expose the order and the per-warehouse stock as columns. Where it does not,
 * the caller falls back to the generic facet test and says so.
 */
function companionTieRow(shape) {
  for (const table of shape.tables) {
    const companions = eval30Column(table, /companion/i);
    const order = eval30Column(table, /^order|items|products/i);
    const stock = stockColumns(table);
    if (!companions || !order || Object.keys(stock).length < 2) continue;

    for (const [index, row] of table.rows.entries()) {
      const stocked = {};
      for (const [name, column] of Object.entries(stock)) {
        stocked[name] = setMembers(row.cells[column.index]);
      }
      const found = companionBreaksATie(
        setMembers(row.cells[order.index]),
        stocked,
        setMembers(row.cells[companions.index]),
      );
      if (found) return { table, row: index + 1, ...found };
    }
  }
  return null;
}

/** True where some table exposes the order and per-warehouse stock, so covers can be computed. */
function tieComputable(shape) {
  return shape.tables.some(
    (table) =>
      eval30Column(table, /companion/i) &&
      eval30Column(table, /^order|items|products/i) &&
      Object.keys(stockColumns(table)).length >= 2,
  );
}

/** An input column of a table matching `pattern`. */
function eval30Column(table, pattern) {
  return (
    table.columns.find(
      (column) => !column.isScenario && !column.isExpectation && pattern.test(column.header),
    ) || null
  );
}

/** A relation asking that one splitting rule have a table whose rows turn on it. */
function concernRelation(id, name, facetKey) {
  return {
    id,
    label: `${name} is exercised by a row`,
    evaluate: (shape) => {
      const found = exercisingTable(shape, EVAL_30_FACETS[facetKey]);
      return {
        holds: found !== null,
        evidence: found
          ? `${found.table.method}: rows ${found.rows.join(" and ")} differ in ${name} and in their outcome`
          : `no table varies ${name} with the outcome following`,
      };
    },
  };
}

/**
 * A native TableTest collection: a list, set or map written in the table's own notation.
 *
 * `[W1: {camera, lens}]` is native; `"W1:[camera,lens]"` is a quoted scalar that encodes the same
 * structure in a string a helper has to build and parse. The quotes are what separate them.
 */
function isNativeCollection(cell) {
  const text = String(cell ?? "").trim();
  if (text.startsWith('"') || text.startsWith("'")) return false;
  return (text.startsWith("[") && text.endsWith("]")) || (text.startsWith("{") && text.endsWith("}"));
}

/**
 * A scalar output, which the assertion exempts: a number, an enum, or **a single message**.
 *
 * A message is prose and may well contain a colon — `Unknown product: gadget`, `Coupon applied:
 * SAVE10`. Requiring a scalar to be one bare word failed every message column in eval-29's
 * reference. What makes a cell a collection is a bracket opening it, not punctuation inside it.
 */
function isScalarOutput(cell) {
  const text = String(cell ?? "").trim();
  if (text === "") return true;
  if (text.startsWith("[") || text.startsWith("{")) return false;
  // Prose is scalar; an ad-hoc encoding that reaches for a bracket mid-cell is not.
  return !/[\[\]{}]/.test(text);
}

/**
 * Expectation cells that encode a structure inside a scalar.
 *
 * The assertion judges expectation columns only, and exempts scalars. What fails is a cell packing
 * several values into one quoted scalar — structural punctuation inside quotes, or outside them
 * with no bracket to make it a collection.
 */
function stringEncodedOutputs(shape) {
  const found = [];
  for (const table of shape.tables) {
    for (const column of table.expectationColumns) {
      for (const row of table.rows) {
        const cell = String(row.cells[column.index] ?? "").trim();
        const quoted = quotedStructureIn(cell);
        if (quoted) {
          found.push({ method: table.method, header: column.header, cell, quoted });
          break;
        }
        if (isNativeCollection(cell) || isScalarOutput(cell)) continue;
        found.push({ method: table.method, header: column.header, cell, quoted: cell });
        break;
      }
    }
  }
  return found;
}

/**
 * A quoted scalar inside a cell that encodes a structure, or null.
 *
 * A native list whose *elements* are strings encoding the structure is the same defect one level
 * down: `["W1:[camera,lens]"]` is a list of one hand-rolled string, and iteration-40 wrote every
 * expectation that way while the outer brackets made it look native.
 */
function quotedStructureIn(cell) {
  for (const match of String(cell ?? "").matchAll(/"([^"]*)"|'([^']*)'/g)) {
    const inner = match[1] ?? match[2] ?? "";
    // A bracket inside the quotes is an encoded collection — `"W1:[camera,lens]"`.
    if (/[\[\]{}]/.test(inner)) return match[0];
    // So are several key-and-value segments. One is prose: `"Unknown product: bogus"` is a message
    // quoted because a cell may be, not a structure packed into a scalar.
    const segments = inner.split(/[;,]/).map((one) => one.trim()).filter(Boolean);
    if (segments.length >= 2 && segments.filter((segment) => segment.includes(":")).length >= 2) return match[0];
  }
  return null;
}

/**
 * Map keys built by joining several independent facets, with how many parts each has.
 *
 * Reported, never decided. The reference's `DELIVERY@Addr-A` joins two parts and passes — its own
 * description argues the fulfilment is "one value with two shapes" — while iteration-85's
 * `DELIVERY@addr-1@W1@IMMEDIATE` joins four and fails. Whether a joined key names one thing or
 * several is a domain reading, not a parse, so this hands the count to a human.
 */
function compoundKeys(shape) {
  const found = [];
  for (const table of shape.tables) {
    for (const column of table.expectationColumns) {
      for (const row of table.rows) {
        const cell = String(row.cells[column.index] ?? "");
        for (const key of cell.matchAll(/[\[{,]\s*([^,\[\]{}:]+?)\s*:/g)) {
          const parts = key[1].trim().split(/[@|\/]/).filter(Boolean);
          if (parts.length >= 3) {
            found.push({ method: table.method, header: column.header, key: key[1].trim(), parts: parts.length });
          }
        }
      }
    }
  }
  return found;
}

/** Comparison criteria a body can apply, each a rule the test enforces while no table claims it. */
const COMPARISON_CRITERIA = [
  // The surface pattern must name *comparison* order. A bare /order/ matches the domain noun —
  // this eval splits customer orders and its cells say BACKORDERED — so iteration-40's three
  // `.sorted()` helpers read as declared by any description that mentioned an order at all.
  {
    name: "ordering",
    // Not TreeSet/TreeMap: choosing a sorted collection inside a helper is a data-structure choice,
    // not "sorting either side before comparing" — iteration-45 builds a TreeSet of addresses and
    // asserts with plain equality on a map, which the grader correctly passes.
    body: /\bsorted\s*\(|\.sort\s*\(|Comparator\.|sortedBy/,
    surface: /regardless of order|without regard to order|order[- ]?independent|unordered|in any order|any order|sorted|sort order|canonical/i,
  },
  // Wrapping either side of an assert in a Set makes the comparison order-insensitive, which is a
  // rule the test enforces and no table claims. Building a Set an input column names is not this,
  // so the pattern requires the call to sit inside the assertion — iteration-63 writes
  // `assertEquals(Set.copyOf(shipments), groupsOf(actual))`.
  {
    name: "unordered comparison",
    // The call must wrap something: `Set.of()` with no argument is an empty-set default for a
    // `getOrDefault`, which iteration-85 uses and which normalises nothing.
    body: /assert\w*\(\s*[^;]{0,200}?\b(?:Set\.(?:copyOf|of)|new\s+(?:Hash|LinkedHash)Set)\s*\(\s*[^)\s]/,
    surface: /regardless of order|without regard to order|order[- ]?independent|unordered|in any order|any order|as a set|set of/i,
  },
  { name: "subset or contains matching", body: /containsAll|\.contains\s*\(|containsExactlyInAnyOrder|assertTrue\s*\(\s*\w+\.contains/, surface: /contain|substring|subset/i },
  { name: "normalisation", body: /toLowerCase\s*\(|toUpperCase\s*\(|\.strip\s*\(|replaceAll\s*\(/, surface: /normalis|case|whitespace|trim/i },
];

/** Criteria a table's body applies without its published surface naming them. */
function undeclaredCriteria(shape) {
  // The criterion is usually applied in a private helper, not in the table's own body —
  // iteration-40's three `describeBy…` helpers each call `.sorted()` — so the whole class is the
  // scope, and the published surface it is checked against is every title and description in it.
  const surface = shape.tables
    .map((table) => `${table.displayName || ""} ${table.description || ""}`)
    .join(" ");
  return COMPARISON_CRITERIA.filter(
    (criterion) => criterion.body.test(shape.source || "") && !criterion.surface.test(surface),
  ).map((criterion) => ({ criterion: criterion.name }));
}

/**
 * Tables sharing a concern but splitting its outputs, keyed by their input signature.
 *
 * `all-outputs-same-table` asks that all outputs of one concern sit together. Two methods taking
 * the same inputs and asserting different outputs is that failure in its decidable form.
 */
function splitOutputs(shape) {
  const byInputs = new Map();
  for (const table of shape.tables) {
    const inputs = table.columns
      .filter((column) => !column.isScenario && !column.isExpectation)
      .map((column) => column.header.toLowerCase())
      .sort()
      .join("|");
    if (inputs === "") continue;
    if (!byInputs.has(inputs)) byInputs.set(inputs, []);
    byInputs.get(inputs).push(table);
  }
  const found = [];
  for (const [inputs, tables] of byInputs) {
    if (tables.length < 2) continue;
    const outputs = new Set(
      tables.map((table) => table.expectationColumns.map((column) => column.header.toLowerCase()).sort().join("|")),
    );
    if (outputs.size > 1) found.push({ inputs, methods: tables.map((table) => table.method) });
  }
  return found;
}


/**
 * Scalar expectation cells that enumerate several entities in prose.
 *
 * `Insufficient stock for widget: requested 5, available 2; gadget: requested 4, available 1` is one
 * message and also a list of two shortfalls. The assertion exempts "a single message" and fails "a
 * scalar packing multiple values", and says nothing about where one becomes the other — so this is
 * reported and never decided.
 */
function enumeratingMessages(shape) {
  const found = [];
  for (const table of shape.tables) {
    for (const column of table.expectationColumns) {
      for (const row of table.rows) {
        const cell = String(row.cells[column.index] ?? "").trim();
        if (!isScalarOutput(cell) || cell === "") continue;
        const segments = cell.split(";").map((one) => one.trim()).filter(Boolean);
        if (segments.length >= 2 && segments.every((segment) => segment.includes(":"))) {
          found.push({ method: table.method, header: column.header, cell });
          break;
        }
      }
    }
  }
  return found;
}

/**
 * `native-collection-output`, shared by every eval that carries it.
 *
 * The decidable half is the quoted scalar: a cell — or an element inside a native collection — that
 * encodes structure in a string a helper builds and parses. The compound-key half is a domain
 * reading and is reported as advisory, because eval-30's own reference answer keys on a two-part
 * `DELIVERY@Addr-A` and passes while a four-part key fails.
 */
function nativeCollectionRelation() {
  return {
    id: "native-collection-output",
    label: "no expectation encodes a structure in a string",
    evaluate: (shape) => {
      const encoded = stringEncodedOutputs(shape);
      if (shape.tables.every((table) => table.expectationColumns.length === 0)) {
        return { holds: false, evidence: "no expectation column to judge" };
      }
      if (encoded.length > 0) {
        return {
          holds: false,
          evidence: encoded.map((one) => `${one.method}: ${one.header} packs ${one.quoted}`).join("; "),
        };
      }
      const enumerating = enumeratingMessages(shape);
      if (enumerating.length > 0) {
        const first = enumerating[0];
        return {
          holds: false,
          advisory: true,
          evidence: `ADVISORY: ${first.method}: ${first.header} = "${first.cell}" — a message enumerating several entities; the assertion exempts "a single message" and does not say where a message stops being one`,
        };
      }
      const compound = compoundKeys(shape);
      if (compound.length > 0) {
        const first = compound[0];
        return {
          holds: false,
          advisory: true,
          evidence: `ADVISORY: ${first.method}: ${first.header} keys on ${first.key} — ${first.parts} facets joined into one key; whether that names one thing is a domain reading`,
        };
      }
      return { holds: true, evidence: "every expectation column is a native collection or a scalar" };
    },
  };
}

/**
 * `rule-falsifiable-by-a-row` condition (1) — a constant expectation column.
 *
 * Advisory on every host, because it decides **one of the assertion's three conditions** and cannot
 * read the exemption for a table whose claim *is* an invariance. It agreed with the grader on all 12
 * of eval-18's draws, which is a useful observation rather than a verdict.
 */
function falsifiabilityRelation() {
  return {
    id: "rule-falsifiable-by-a-row",
    label: "no constant expectation column",
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
  };
}

const EVAL_30_RELATIONS = [
  nativeCollectionRelation(),
  concernRelation("concern-fulfillment-method", "the fulfillment split", "fulfillment"),
  concernRelation("concern-delivery-address", "the delivery-address split", "address"),
  concernRelation("concern-availability", "the availability split", "availability"),
  concernRelation("concern-warehouse-allocation", "warehouse allocation", "warehouse"),
  {
    id: "concern-companion-products",
    label: "companion grouping decides a tie",
    evaluate: (shape) => {
      const tie = companionTieRow(shape);
      if (tie) {
        return {
          holds: true,
          evidence: `${tie.table.method} row ${tie.row}: covers ${tie.covers.join(" and ")} tie, ${tie.kept} keeps the companions together`,
        };
      }
      // The assertion's own test is whether the rule decides between equally minimal covers, and
      // that is only computable where a table exposes the order and the per-warehouse stock as
      // columns. Where one does and no row ties, the verdict is decided — that is the failure the
      // assertion names. Where none does, any verdict is a candidate, so it is advisory.
      const generic = exercisingTable(shape, EVAL_30_FACETS.companion);
      const declares = shape.tables.some((table) => eval30Column(table, /companion/i));
      if (tieComputable(shape)) {
        return {
          holds: false,
          evidence: "a table exposes the order and per-warehouse stock, and no row's covers tie — the rule decides nothing",
        };
      }
      return {
        holds: generic !== null,
        advisory: true,
        evidence: generic
          ? `ADVISORY: ${generic.table.method} rows ${generic.rows.join(" and ")} differ in their outcome, but no table exposes the order and per-warehouse stock, so no tie can be computed`
          : declares
            ? "ADVISORY: companions are declared, no row's outcome varies, and no tie is computable"
            : "ADVISORY: no table declares companions, and no tie is computable",
      };
    },
  },
  {
    id: "assertion-criteria-declared",
    label: "a comparison criterion is named on the surface",
    evaluate: (shape) => {
      const undeclared = undeclaredCriteria(shape);
      return {
        holds: undeclared.length === 0,
        evidence:
          undeclared.length === 0
            ? "no body applies an unnamed ordering, subset or normalisation rule"
            : undeclared.map((one) => `the class applies ${one.criterion} and no title or description names it`).join("; "),
      };
    },
  },
  falsifiabilityRelation(),
];


// ---------------------------------------------------------------------------
// eval-29 shopping-cart
// ---------------------------------------------------------------------------

/**
 * The quantities eval-29's tables recur over, and how their columns name them.
 *
 * `consistent-quantity-naming` asks that one observable quantity carry one name across sibling
 * tables, which means a relation has to know which columns are the same quantity before it can see
 * a second name for it. That knowledge is per-eval and lives here.
 */
const EVAL_29_QUANTITIES = {
  // The cart's contents are headed `Cart`, `Basket` or plain `Items` — iteration-59 writes
  // `Items Before`/`Items After?` throughout — while the single product acted on is headed
  // `Product Id` or `Item Id`. Plural names the collection, singular names the one product.
  cart: /\bcart\b|\bbasket\b|\bitems\b/i,
  total: /\btotal\b|amount due|\bnet\b/i,
  message: /\bmessage\b|\bfeedback\b/i,
  product: /\bproduct\b|\bsku\b|\bitem id\b/i,
  quantity: /\bquantity\b|\bqty\b/i,
  coupon: /\bcoupon\b|\bpromo\b|\bdiscount code\b/i,
  stock: /\bstock\b|\binventory\b/i,
  catalogue: /\bcatalogue\b|\bcatalog\b|\bprices?\b/i,
};

/** Words that mark a column as one side of a before/after pair rather than a second name. */
const BEFORE_AFTER = /\b(before|after|prior|resulting|initial|final|pre|post)\b/i;

/** A header reduced to the quantity it names: `Cart After?` and `Cart Before` are both `cart`. */
function quantityCore(header) {
  return String(header)
    .replace(/\?+\s*$/, "")
    .replace(BEFORE_AFTER, " ")
    .replace(/\s+/g, " ")
    .trim()
    .toLowerCase();
}

/** Every non-scenario column of the class, with the table it belongs to. */
function allColumns(shape) {
  return shape.tables.flatMap((table) =>
    table.columns.filter((column) => !column.isScenario).map((column) => ({ table, column })),
  );
}

/**
 * A quantity named more than one way across sibling tables.
 *
 * A before/after pair is explicitly not a second name: the assertion says `Cart Before`/`Cart After?`
 * beside a plain `Cart` in a read-only table is one naming rule. So the check is on the core the
 * header reduces to once a before/after word and the `?` are stripped — a genuine second name shows
 * up as a different core, like `Cart Items` beside `Cart`.
 */
function inconsistentNames(shape) {
  const found = [];
  for (const [quantity, pattern] of Object.entries(EVAL_29_QUANTITIES)) {
    const cores = new Map();
    // Output quantities only. The assertion is about "one output quantity under two or more names",
    // and an input word covers several distinct quantities: `Coupon Code`, `Coupon Store` and
    // `Coupon` are the code entered, the catalogue of codes, and the coupon itself.
    for (const { table, column } of allColumns(shape).filter(({ column }) => column.isExpectation)) {
      if (!pattern.test(column.header)) continue;
      const core = quantityCore(column.header);
      if (core === "") continue;
      if (!cores.has(core)) cores.set(core, []);
      cores.get(core).push(`${table.method}: ${column.header}`);
    }
    if (cores.size > 1) {
      found.push({ quantity, names: [...cores.keys()], where: [...cores.values()].map((one) => one[0]) });
    }
  }
  return found;
}

/**
 * A `Before` column in a table with no matching `After?` expectation.
 *
 * The assertion names this as the other way the pair fails: a before name only earns its keep where
 * the table mutates the quantity and states the post-state.
 */
function beforeWithoutAfter(shape) {
  const found = [];
  for (const table of shape.tables) {
    for (const column of table.columns) {
      if (column.isScenario || column.isExpectation) continue;
      if (!/\b(before|prior|initial|pre)\b/i.test(column.header)) continue;
      const core = quantityCore(column.header);
      const paired = table.expectationColumns.some((one) => quantityCore(one.header) === core);
      if (!paired) found.push({ method: table.method, header: column.header });
    }
  }
  return found;
}

/** An input column of a table matching `pattern`, policy columns included. */
function eval29Input(table, pattern) {
  return table.columns.find((column) => !column.isScenario && !column.isExpectation && pattern.test(column.header)) || null;
}

/** An expectation column of a table matching `pattern`. */
function eval29Expectation(table, pattern) {
  return table.expectationColumns.find((column) => pattern.test(column.header)) || null;
}

/**
 * Which of eval-29's four concerns each table serves, by the columns it carries.
 *
 * The concerns are the requirement's own operations: adding or removing items, deciding which coupon
 * is active, totalling, and checking stock at checkout.
 */
function eval29Concerns(shape) {
  const concerns = { item: [], coupon: [], total: [], checkout: [] };
  for (const table of shape.tables) {
    const hasTotal = Boolean(eval29Expectation(table, EVAL_29_QUANTITIES.total));
    const hasStock = Boolean(eval29Input(table, EVAL_29_QUANTITIES.stock));
    const hasCouponCode = Boolean(eval29Input(table, /coupon code|\bcode\b|coupon/i));
    const hasProduct = Boolean(eval29Input(table, EVAL_29_QUANTITIES.product));

    // Most draws state the coupon concern's post-state as the coupon itself — `Active Coupon After?`
    // — rather than as the cart that carries it. Requiring a cart expectation classified eight of
    // twelve draws as having no coupon table at all, and cascaded into three relations each.
    if (hasTotal) concerns.total.push(table);
    else if (hasStock) concerns.checkout.push(table);
    else if (hasCouponCode) concerns.coupon.push(table);
    else if (hasProduct) concerns.item.push(table);
  }
  return concerns;
}

/**
 * Static mutable fields a table body writes to — the only way one row can inherit another's state.
 *
 * JUnit creates a fresh test instance per invocation, so an instance field cannot carry state from
 * row to row; a static one can.
 */
function sharedMutableState(shape) {
  const fields = [...String(shape.source || "").matchAll(/\bstatic\s+(?!final\b)[\w<>,\[\].]+\s+(\w+)\s*[=;]/g)].map(
    (match) => match[1],
  );
  const found = [];
  for (const table of shape.tables) {
    for (const field of fields) {
      const writes = new RegExp(`\\b${field}\\s*(?:=[^=]|\\.(?:add|remove|put|clear|set)\\w*\\s*\\()`);
      if (writes.test(table.body || "")) found.push({ method: table.method, field });
    }
  }
  return found;
}

/** Map-valued columns — the cart, the catalogue and the stock levels the assertion names. */
const MAP_VALUED = /\bcart\b|\bbasket\b|catalogue|catalog|\bstock\b|inventory|\bprices?\b|coupon store/i;

/** TableTest's own map notation: `[k: v, k2: v2]`, or `[:]` for the empty map. */
function isStandardMap(cell) {
  const text = String(cell ?? "").trim();
  if (text === "" || text === "[:]") return true;
  // A value set over maps is still standard notation: `{[:], [widget: 5]}` runs the row twice, once
  // per map, and each member is what this judges.
  if (text.startsWith("{") && text.endsWith("}")) {
    const members = parseCollectionElements(text);
    return members !== null && members.length > 0 && members.every((member) => isStandardMap(member));
  }
  if (!text.startsWith("[") || !text.endsWith("]")) return false;
  const elements = parseCollectionElements(text);
  if (elements === null) return false;
  return elements.every((element) => /:/.test(element));
}

/** Map-valued cells written in a bespoke grammar rather than TableTest's. */
function bespokeMapCells(shape) {
  const found = [];
  for (const table of shape.tables) {
    for (const column of table.columns) {
      if (column.isScenario) continue;
      if (!MAP_VALUED.test(column.header)) continue;
      // A column of scalars is not a map-valued column, whatever it is called: `Cart Total?` holds
      // a number and `Product` holds a name.
      const cells = table.rows.map((row) => String(row.cells[column.index] ?? "").trim()).filter((cell) => cell !== "");
      if (cells.length === 0 || cells.every((cell) => !/[\[\]{}:=;]/.test(cell))) continue;
      const bad = cells.find((cell) => !isStandardMap(cell));
      if (bad) found.push({ method: table.method, header: column.header, cell: bad });
    }
  }
  return found;
}

/** Coupon facets spread across sparse columns instead of one converted column. */
function spreadCouponColumns(shape) {
  const facets = [/coupon type|\btype\b/i, /coupon (value|amount)|\bamount\b|\bpercent/i, /coupon (target|product)|target product/i];
  const found = [];
  for (const table of shape.tables) {
    const hit = facets
      .map((facet) => table.columns.find((column) => !column.isScenario && !column.isExpectation && facet.test(column.header)))
      .filter(Boolean);
    if (hit.length >= 2) found.push({ method: table.method, headers: hit.map((column) => column.header) });
  }
  return found;
}

const EVAL_29_RELATIONS = [
  {
    id: "separates-item-coupon-total-checkout",
    label: "four concerns, four methods",
    evaluate: (shape) => {
      const concerns = eval29Concerns(shape);
      const missing = Object.entries(concerns)
        .filter(([, tables]) => tables.length === 0)
        .map(([name]) => name);
      if (missing.length > 0) {
        return { holds: false, evidence: `no table serves: ${missing.join(", ")}` };
      }
      const shared = Object.entries(concerns).flatMap(([name, tables]) =>
        tables.map((table) => ({ name, method: table.method })),
      );
      const byMethod = new Map();
      for (const one of shared) {
        if (!byMethod.has(one.method)) byMethod.set(one.method, []);
        byMethod.get(one.method).push(one.name);
      }
      const doubled = [...byMethod].filter(([, names]) => names.length > 1);
      return {
        holds: doubled.length === 0,
        evidence:
          doubled.length === 0
            ? [...byMethod].map(([method, names]) => `${names[0]}: ${method}`).join("; ")
            : doubled.map(([method, names]) => `${method} serves ${names.join(" and ")}`).join("; "),
      };
    },
  },
  {
    id: "coupon-before-after-columns",
    label: "the coupon table states the state change",
    evaluate: (shape) => {
      const [coupon] = eval29Concerns(shape).coupon;
      if (!coupon) return { holds: false, evidence: "no table decides which coupon is active" };
      const before = eval29Input(coupon, /\b(before|prior|initial|pre)\b/i);
      const after = eval29Expectation(coupon, /\b(after|resulting|final|post)\b/i);
      return {
        holds: Boolean(before && after),
        evidence:
          before && after
            ? `${coupon.method}: ${before.header} → ${after.header}`
            : `${coupon.method}: ${before ? "no after column" : "no before column"}`,
      };
    },
  },
  {
    id: "coupon-validity-not-coupon-types",
    label: "the coupon table judges validity, not worth",
    evaluate: (shape) => {
      // The concern is often spread over two sibling tables — one for activation and replacement,
      // one for rejection — so the union of them is what carries validity, not the first alone.
      const coupons = eval29Concerns(shape).coupon;
      if (coupons.length === 0) return { holds: false, evidence: "no table decides which coupon is active" };
      const priced = coupons.map((table) => eval29Expectation(table, EVAL_29_QUANTITIES.total)).find(Boolean);
      const cells = coupons.flatMap((table) => table.rows.flatMap((row) => row.cells.map((cell) => String(cell ?? ""))));
      const surface = coupons.map((table) => `${table.displayName || ""} ${table.method || ""}`).join(" ");
      const expired = cells.some((cell) => /expir|lapsed|stale/i.test(cell));
      const unknown = cells.some((cell) => /unknown|unrecognis|nonexistent|not found|invalid|bogus|ghost|fake/i.test(cell)) ||
        /invalid|not found|unknown/i.test(surface);
      const missing = [];
      if (priced) missing.push(`it also asserts ${priced.header}`);
      if (!expired) missing.push("no row carries an expired code");
      if (!unknown) missing.push("no row carries an unrecognised code");
      const where = coupons.map((table) => table.method).join(" + ");
      return {
        holds: missing.length === 0,
        evidence: missing.length === 0 ? `${where}: expired and unrecognised codes, no price asserted` : `${where}: ${missing.join("; ")}`,
      };
    },
  },
  {
    id: "coupon-as-single-column",
    label: "one coupon column, not sparse facets",
    evaluate: (shape) => {
      const spread = spreadCouponColumns(shape);
      return {
        holds: spread.length === 0,
        evidence:
          spread.length === 0
            ? "no table spreads a coupon across separate facet columns"
            : spread.map((one) => `${one.method}: ${one.headers.join(", ")}`).join("; "),
      };
    },
  },
  {
    id: "uses-standard-map-syntax",
    label: "map cells use TableTest's own notation",
    evaluate: (shape) => {
      const bespoke = bespokeMapCells(shape);
      return {
        holds: bespoke.length === 0,
        evidence:
          bespoke.length === 0
            ? "every map-valued cell is written [k: v] or [:]"
            : bespoke.map((one) => `${one.method}: ${one.header} = ${one.cell}`).join("; "),
      };
    },
  },
  {
    id: "consistent-quantity-naming",
    label: "one quantity, one name",
    // Advisory: whether two names denote one observable quantity is a domain reading, not a parse.
    // `Message?` beside `Message Mentions?` is the full message beside the substrings it must
    // contain — two quantities — while the assertion's own example, `Base Rate?` beside `Rate?`, is
    // one quantity under two names. Nothing in the headers separates those two cases.
    advisory: true,
    judgement: "whether two names denote one quantity is a domain reading",
    evaluate: (shape) => {
      if (shape.tables.length < 2) return { holds: true, evidence: "a single table cannot disagree with itself" };
      const inconsistent = inconsistentNames(shape);
      const unpaired = beforeWithoutAfter(shape);
      if (inconsistent.length === 0 && unpaired.length === 0) {
        return { holds: true, evidence: "each recurring quantity carries one name throughout" };
      }
      return {
        holds: false,
        evidence: [
          ...inconsistent.map((one) => `${one.quantity} appears as ${one.names.join(" and ")} (${one.where.join(", ")})`),
          ...unpaired.map((one) => `${one.method}: ${one.header} with no matching after column`),
        ].join("; "),
      };
    },
  },
  {
    id: "rows-independently-executable",
    label: "no row inherits state from another",
    evaluate: (shape) => {
      // A post-state expectation with no pre-state column is NOT the defect: a table that adds to a
      // fresh empty cart on every row is independent, and iteration-65 does exactly that. Row
      // dependence needs state that survives between invocations, and JUnit builds a new test
      // instance per invocation — so the mechanism is a *static* mutable field a body writes to.
      const shared = sharedMutableState(shape);
      return {
        holds: shared.length === 0,
        evidence:
          shared.length === 0
            ? "no table writes to static mutable state between rows"
            : shared.map((one) => `${one.method} writes to static ${one.field}`).join("; "),
      };
    },
  },
  {
    id: "test-data-visible",
    label: "prices reach the totalling table",
    evaluate: (shape) => {
      const [total] = eval29Concerns(shape).total;
      if (!total) return { holds: false, evidence: "no table totals the cart" };
      const priced = eval29Input(total, EVAL_29_QUANTITIES.catalogue);
      const inSurface = /price|catalogue|catalog|costs?\b/i.test(`${total.displayName || ""} ${total.description || ""}`);
      return {
        holds: Boolean(priced || inSurface),
        evidence: priced
          ? `${total.method}: ${priced.header}`
          : inSurface
            ? `${total.method}: prices named in the title or description`
            : `${total.method}: prices reach the total from nowhere the reader can see`,
      };
    },
  },
  nativeCollectionRelation(),
  falsifiabilityRelation(),
];


// ---------------------------------------------------------------------------
// eval-25 convert-from-spock
// ---------------------------------------------------------------------------

/** The Spock spec's own column names, which a good conversion collapses rather than copies. */
const EVAL_25_SPARSE_OPTIONS = [/^fragile/i, /insured\s*value/i, /^handling/i];

/** The three dimension columns a conversion should have collapsed into one list. */
const EVAL_25_SPARSE_DIMENSIONS = [/^length/i, /^width/i, /^height/i];

/** The method under test, whose held arguments a reader has to be able to find. */
const EVAL_25_CALL = "calculateShippingCost";

/** Everything the class publishes: every title, every description, every header and every cell. */
function publishedSurface(shape) {
  const parts = [];
  for (const table of shape.tables) {
    parts.push(table.displayName || "", table.description || "", table.method || "");
    parts.push(table.headers.join(" "));
    for (const row of table.rows) parts.push(row.cells.join(" "));
  }
  return parts.join(" \n ");
}

/**
 * The literal arguments the class hands the method under test, with the table each comes from.
 *
 * These are the values a table holds constant for every row. `rule-statable-from-table` clause (1)
 * fails a value needed to predict the expectation that appears *only* in the body, so each one has to
 * be looked for on the published surface — and the assertion counts any title, description or column
 * of any table in the class as published.
 */
function heldCallValues(shape) {
  const found = [];
  for (const table of shape.tables) {
    const call = callArguments(table.body, EVAL_25_CALL);
    if (!call) continue;
    for (const argument of call) {
      for (const literal of literalsIn(argument)) {
        found.push({ method: table.method, literal, argument });
      }
    }
  }
  return found;
}

/** The literals inside one argument expression, constructor calls unwrapped. */
function literalsIn(argument) {
  const text = String(argument ?? "");
  const found = [];
  for (const match of text.matchAll(/"([^"]*)"|'([^']*)'|\b(\d+(?:\.\d+)?)\b|\b([A-Z][A-Z_]{2,})\b/g)) {
    const value = match[1] ?? match[2] ?? match[3] ?? match[4] ?? "";
    if (value === "" || value === "0") continue;
    found.push(value);
  }
  return found;
}

/** A number stated in a cell, header, title or description anywhere in the class. */
function publishesValue(surface, literal) {
  const text = String(literal);
  if (/^\d+(\.\d+)?$/.test(text)) {
    // `3.0` held in the body is published by a description saying "a 3 kg package", so the numeral
    // is matched with its trailing zeros trimmed and on a digit boundary.
    const trimmed = text.replace(/\.0+$/, "").replace(/(\.\d*[1-9])0+$/, "$1");
    return new RegExp(`(?<!\\d)${trimmed.replace(".", "\\.")}(?!\\d)`).test(surface);
  }
  return new RegExp(text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"), "i").test(surface);
}

/** The shipping-cost expectation column of a table, or null. */
function costColumn(table) {
  return table.expectationColumns.find((column) => /cost|price|charge|total/i.test(column.header)) || null;
}

/**
 * Pairs of rows differing in exactly one numeric input and disagreeing on the cost.
 *
 * `row-values-traceable-to-requirement` asks whether a column, a title or a description could say why
 * *these* two values — so a boundary pair whose values appear nowhere on the published surface is the
 * decidable half of it.
 */
function boundaryPairs(table) {
  const cost = costColumn(table);
  if (!cost) return [];
  const inputs = table.columns.filter((column) => !column.isScenario && !column.isExpectation);
  const pairs = [];

  for (const column of inputs) {
    // Rows that agree on everything else, so the one column is what moves. Grouping first is what
    // makes "adjacent" meaningful.
    const groups = new Map();
    for (const row of table.rows) {
      const others = inputs
        .filter((one) => one !== column)
        .map((one) => String(row.cells[one.index] ?? ""))
        .join(" | ");
      if (!groups.has(others)) groups.set(others, []);
      groups.get(others).push(row);
    }

    for (const rows of groups.values()) {
      const sampled = rows
        .map((row) => ({ value: numericValue(row.cells[column.index]), cost: String(row.cells[cost.index] ?? "") }))
        .filter((one) => one.value !== null)
        .sort((a, b) => a.value - b.value);
      // A threshold sits between *adjacent* samples. Pairing every two rows called 1.01 against
      // 5.01 a boundary, which is two ordinary rows and not a boundary at all.
      for (let i = 0; i + 1 < sampled.length; i++) {
        if (sampled[i].cost === sampled[i + 1].cost) continue;
        pairs.push({ method: table.method, header: column.header, values: [sampled[i].value, sampled[i + 1].value] });
      }
    }
  }
  return pairs;
}


/**
 * The calculator's own constants, and what makes a row rely on each.
 *
 * `rule-statable-from-table` clause (1) fails a value needed to predict the expectation that appears
 * in neither a column, a `@DisplayName` nor a `@Description`. The values that matter here are the
 * calculator's, not the test's: a reader cannot predict 12.50 from `[70, 50, 10]` without the
 * volumetric divisor, and the grader fails ten of eleven draws on exactly that. Values are read from
 * the eval's own project so they cannot drift from the code under test.
 *
 * `relies` decides from a row's own cells whether the constant is load-bearing for it. The weight
 * brackets are deliberately absent: a bracket bound is a boundary the table's own rows show, which
 * the assertion treats as published.
 *
 * Money constants must appear in their decimal form. A bare `\b3\b` matched a description saying
 * "a 3 kg package" and read the insurance minimum as published, which passed iteration-50 where the
 * grader correctly failed it. The reference states each one as `10.00`, `3.00`, `8.00`, `1.15` and
 * `0.6%`, so the decimal form is what a publishing description looks like.
 */
const EVAL_25_CONSTANTS = [
  {
    constant: "the volumetric divisor",
    value: /\b5[,.]?000\b/,
    name: /volumetric|dimensional/i,
    why: "a row where volumetric weight beats the actual weight",
    // Where the weight is nowhere to be found, whether volumetric weight wins is unknowable, so the
    // constant is not claimed as relied on.
    relies: ({ dimensions, weight, divisor }) =>
      dimensions.length === 3 && weight !== null && volumetricWeight(dimensions, divisor) > weight,
  },
  {
    constant: "the oversize threshold",
    value: /\b100\b/,
    name: /oversize|longest side/i,
    why: "a row with a side past the oversize limit",
    relies: ({ dimensions, oversize }) => dimensions.some((side) => side > oversize),
  },
  {
    constant: "the oversize fee",
    value: /\b10\.0+\b/,
    name: /oversize fee/i,
    why: "a row with a side past the oversize limit",
    relies: ({ dimensions, oversize }) => dimensions.some((side) => side > oversize),
  },
  {
    constant: "the fragile multiplier",
    value: /\b1\.15\b|\b15\s*%/,
    name: /fragile multiplier/i,
    why: "a row marking the package fragile",
    relies: ({ options }) => /fragile\s*[:=]\s*true|\bfragile\b/i.test(options),
  },
  {
    constant: "the insurance rate",
    value: /\b0\.006\b|\b0\.6\s*%/,
    name: /insurance rate|premium rate/i,
    why: "a row carrying an insured value",
    relies: ({ options }) => /insur/i.test(options),
  },
  {
    constant: "the minimum insurance premium",
    value: /\b3\.0+\b/,
    name: /minimum premium|insurance minimum/i,
    why: "a row carrying an insured value",
    relies: ({ options }) => /insur/i.test(options),
  },
  {
    constant: "the hazmat fee",
    value: /\b8\.0+\b/,
    name: /hazmat fee/i,
    why: "a row with hazmat handling",
    relies: ({ options }) => /hazmat/i.test(options),
  },
];

/** The first bare number a table hands the calculator — the weight it holds for every row. */
function heldNumericArgument(table) {
  const call = callArguments(table.body, EVAL_25_CALL);
  if (!call) return null;
  for (const argument of call) {
    const literal = literalArgument(argument);
    const value = literal === null ? null : numericValue(literal);
    if (value !== null) return value;
  }
  return null;
}

/** Volumetric weight: the package's volume over the divisor. */
function volumetricWeight(dimensions, divisor) {
  return (dimensions[0] * dimensions[1] * dimensions[2]) / divisor;
}

/** The dimensions a row states, as three numbers, or an empty list. */
function dimensionsOf(table, row) {
  const column = table.columns.find(
    (one) => !one.isScenario && /dimension|\bdims\b|\bl\s*x\s*w\s*x\s*h\b/i.test(one.header),
  );
  if (!column) return [];
  const members = parseCollectionElements(String(row.cells[column.index] ?? "").trim());
  if (members === null) return [];
  const numbers = members.map((member) => numericValue(member)).filter((value) => value !== null);
  return numbers.length === 3 ? numbers : [];
}

/** What a row states, as far as the constants care. */
function rowFacts(table, row, constants) {
  const weightColumn = table.columns.find((one) => !one.isScenario && /weight/i.test(one.header));
  // A table varying dimensions usually holds the weight in the body. Reading it as absent made a
  // 10x10x10 package's 0.2 kg volumetric weight "beat" a weight of zero, so every table relied on
  // the divisor.
  const heldWeight = heldNumericArgument(table);
  const optionColumns = table.columns.filter(
    (one) => !one.isScenario && /option|fragile|insur|handling|hazmat/i.test(one.header),
  );
  return {
    dimensions: dimensionsOf(table, row),
    weight: weightColumn ? numericValue(row.cells[weightColumn.index]) : heldWeight,
    options: optionColumns.map((one) => `${one.header} ${row.cells[one.index] ?? ""}`).join(" "),
    divisor: constants.divisor,
    oversize: constants.oversize,
  };
}

/**
 * Constants a table's rows rely on that the class publishes nowhere.
 *
 * "Published" is as generous as the assertion says: the value in any title, description or header, or
 * a header naming the constant — the reference publishes the divisor as a column called
 * `Volumetric divisor (cm3 per kg)` and the surcharges in its descriptions.
 */
function unpublishedConstants(shape, sutConstants = { divisor: 5000, oversize: 100 }) {
  const headers = shape.tables.flatMap((table) => table.headers).join(" \\n ");
  const prose = shape.tables
    .map((table) => `${table.displayName || ""} ${table.description || ""} ${table.method || ""}`)
    .join(" \\n ");
  const surface = `${prose} \\n ${headers}`;

  const found = [];
  for (const table of shape.tables) {
    for (const definition of EVAL_25_CONSTANTS) {
      const relied = table.rows.some((row) => {
        try {
          return definition.relies(rowFacts(table, row, sutConstants));
        } catch {
          return false;
        }
      });
      if (!relied) continue;
      // A column named for the constant carries its value in the cells, so naming it in a *header*
      // publishes it. Prose naming the concept does not: iteration-50 says "dimensional weight
      // 0.2 kg" and never states the divisor, which is the value a reader needs.
      const published = definition.value.test(surface) || definition.name.test(headers);
      if (!published) found.push({ method: table.method, constant: definition.constant, why: definition.why });
    }
  }
  return found;
}

const EVAL_25_RELATIONS = [
  {
    id: "options-as-map",
    label: "options are one map column, empty as [:]",
    evaluate: (shape) => {
      const sparse = [];
      for (const table of shape.tables) {
        const hit = EVAL_25_SPARSE_OPTIONS.map((pattern) =>
          table.columns.find((column) => !column.isScenario && !column.isExpectation && pattern.test(column.header)),
        ).filter(Boolean);
        if (hit.length >= 2) sparse.push(`${table.method}: ${hit.map((column) => column.header).join(", ")}`);
      }
      if (sparse.length > 0) return { holds: false, evidence: `options kept as separate columns — ${sparse.join("; ")}` };

      const optionColumns = shape.tables.flatMap((table) =>
        table.columns
          .filter((column) => !column.isScenario && !column.isExpectation && /^options|package options/i.test(column.header))
          .map((column) => ({ table, column })),
      );
      if (optionColumns.length === 0) {
        // The assertion names two failures: sparse columns, and an options column whose no-options
        // row is blank. A class that holds options in the body has neither, and the grader passes it.
        return { holds: true, evidence: "no options column and none of the sparse three — options are held in the bodies" };
      }
      // A blank no-options cell converts to null and bypasses the converter entirely, so the
      // assertion fails it explicitly; `[:]` parses to an empty map and lets the converter default.
      const blanks = optionColumns.flatMap(({ table, column }) =>
        table.rows
          .filter((row) => String(row.cells[column.index] ?? "").trim() === "")
          .map(() => `${table.method}: ${column.header} blank where it should be [:]`),
      );
      return {
        holds: blanks.length === 0,
        evidence: blanks.length === 0 ? `one options column, empty rows written [:]` : blanks.slice(0, 3).join("; "),
      };
    },
  },
  {
    id: "dimensions-as-list",
    label: "dimensions are one [L, W, H] list",
    evaluate: (shape) => {
      const sparse = [];
      for (const table of shape.tables) {
        const hit = EVAL_25_SPARSE_DIMENSIONS.map((pattern) =>
          table.columns.find((column) => !column.isScenario && pattern.test(column.header)),
        ).filter(Boolean);
        if (hit.length >= 2) sparse.push(`${table.method}: ${hit.map((column) => column.header).join(", ")}`);
      }
      if (sparse.length > 0) return { holds: false, evidence: `dimensions spread over columns — ${sparse.join("; ")}` };
      const list = shape.tables.flatMap((table) =>
        table.columns
          .filter((column) => !column.isScenario && /dimension|^dims\b|\bsize\b/i.test(column.header))
          .map((column) => ({ table, column })),
      );
      if (list.length === 0) {
        return { holds: true, evidence: "no dimensions column, and none of the sparse three — dimensions are held in the body" };
      }
      const bad = list.filter(({ table, column }) =>
        table.rows.some((row) => {
          const cell = String(row.cells[column.index] ?? "").trim();
          return cell !== "" && !cell.startsWith("[");
        }),
      );
      return {
        holds: bad.length === 0,
        evidence: bad.length === 0 ? `${list.map((one) => one.column.header).join(", ")} written as lists` : `${bad[0].table.method}: ${bad[0].column.header} is not a list`,
      };
    },
  },
  {
    id: "options-type-converter",
    label: "a converter builds PackageOptions",
    evaluate: (shape) => {
      const source = String(shape.source || "");
      const converters = [...source.matchAll(/@TypeConverter[\s\S]{0,400}?\bfun\s+(\w+)\s*\(([^)]*)\)\s*:\s*([\w<>,\s]+)/g)];
      const found = converters.find((match) => /PackageOptions/.test(match[3]));
      if (found) return { holds: true, evidence: `${found[1]}(${found[2].trim()}) : ${found[3].trim()}` };
      const any = /@TypeConverter/.test(source);
      return {
        holds: false,
        evidence: any ? "a @TypeConverter exists but none returns PackageOptions" : "no @TypeConverter in the class",
      };
    },
  },
  {
    id: "numeric-types-correct",
    label: "types follow the calculator's signature",
    evaluate: (shape) => {
      const wrong = [];
      for (const table of shape.tables) {
        for (const column of table.columns) {
          if (!column.param) continue;
          const type = column.param.type;
          if (/weight/i.test(column.header) && !/Double|double/.test(type) && !/BigDecimal/.test(type) === false) {
            wrong.push(`${table.method}: ${column.header} is ${type}, the signature takes a Double`);
          }
          if (/dimension|^dims/i.test(column.header) && !/List<\s*Int(eger)?\s*>/.test(type)) {
            wrong.push(`${table.method}: ${column.header} is ${type}, the signature takes a List<Int>`);
          }
          if (column.isExpectation && /cost|price/i.test(column.header) && !/BigDecimal/.test(type)) {
            wrong.push(`${table.method}: ${column.header} is ${type}, the calculator returns BigDecimal`);
          }
        }
      }
      return {
        holds: wrong.length === 0,
        evidence: wrong.length === 0 ? "weight, dimensions and cost match the signature" : wrong.slice(0, 3).join("; "),
      };
    },
  },
  {
    id: "rule-statable-from-table",
    label: "each constant a row relies on is published",
    evaluate: (shape) => {
      const unpublished = unpublishedConstants(shape);
      return {
        holds: unpublished.length === 0,
        evidence:
          unpublished.length === 0
            ? "every constant the rows rely on is named in a column, a title or a description"
            : unpublished
                .slice(0, 4)
                .map((one) => `${one.method} relies on ${one.constant} (${one.why}) and publishes it nowhere`)
                .join("; "),
      };
    },
  },
  {
    id: "row-values-traceable-to-requirement",
    label: "each boundary pair's values are published",
    evaluate: (shape) => {
      const surface = publishedSurface(shape);
      const unexplained = [];
      for (const table of shape.tables) {
        for (const pair of boundaryPairs(table)) {
          // The pair's values are in cells, so they are trivially "on the surface". What the
          // assertion asks is whether anything *says why these two* — a title or a description
          // naming the boundary they bracket.
          const prose = `${table.displayName || ""} ${table.description || ""} ${table.method || ""}`;
          const named = pair.values.some((value) => publishesValue(prose, value));
          if (!named) unexplained.push(`${pair.method}: ${pair.header} ${pair.values.join(" against ")} with no title or description naming the boundary`);
        }
      }
      const distinct = [...new Set(unexplained)];
      return {
        holds: distinct.length === 0,
        evidence:
          distinct.length === 0
            ? "every outcome-changing pair has its boundary named in prose"
            : distinct.slice(0, 3).join("; "),
        // Whether an unnamed pair is a defect depends on the rule the table states, which the
        // assertion decides case by case ("a boundary of a rule the table's own columns show" passes).
        advisory: distinct.length > 0,
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
  {
    id: "consistent-quantity-naming",
    label: "one quantity, one name",
    advisory: true,
    judgement: "whether two names denote one quantity is a domain reading",
    evaluate: (shape) => {
      if (shape.tables.length < 2) return { holds: true, evidence: "a single table cannot disagree with itself" };
      const unpaired = beforeWithoutAfter(shape);
      return {
        holds: unpaired.length === 0,
        evidence:
          unpaired.length === 0
            ? "no before column without a matching after"
            : unpaired.map((one) => `${one.method}: ${one.header} with no matching after column`).join("; "),
      };
    },
  },
  falsifiabilityRelation(),
];


// ---------------------------------------------------------------------------
// eval-22 event-registration
// ---------------------------------------------------------------------------

const EVAL_22_ROLES = {
  name: /\bname\b/i,
  email: /\be-?mail\b/i,
  dietary: /dietary|diet\b/i,
  accessibility: /accessibilit|access needs/i,
  timing: /\bdate\b|timing|when\b|registered/i,
  groupSize: /group size|\bgroup\b|attendees|party size/i,
  discount: /discount/i,
  price: /\bprice\b|\bcost\b|\bfee\b|amount/i,
  accepted: /accepted|approved|valid\b|status|result|success/i,
  error: /error|message|reason/i,
  cutoff: /cutoff|cut-off|early.?bird/i,
  basePrice: /base price|list price|standard price/i,
};

/** A raw date literal, which the assertion prefers a descriptive value over. */
const DATE_LITERAL = /\b\d{4}-\d{2}-\d{2}\b|\b\d{1,2}\/\d{1,2}\/\d{2,4}\b/;

/** An absent optional is a blank cell; `N/A` and `none` are values that say something else. */
const STANDS_FOR_ABSENT = /^(n\/?a|none|null|nil|-{1,2}|empty|not (given|provided|specified))$/i;

/** An input column of a table matching `pattern`. */
function eval22Input(table, pattern) {
  return table.columns.find((column) => !column.isScenario && !column.isExpectation && pattern.test(column.header)) || null;
}

/** An expectation column of a table matching `pattern`. */
function eval22Expectation(table, pattern) {
  return table.expectationColumns.find((column) => pattern.test(column.header)) || null;
}

/**
 * Which concern each table serves: validating a registration, or pricing one.
 *
 * A discount or a price expectation makes a table the pricing one; an acceptance or error
 * expectation beside a name or an email makes it the validation one.
 */
function eval22Concerns(shape) {
  const concerns = { validation: [], pricing: [] };
  for (const table of shape.tables) {
    const prices = eval22Expectation(table, EVAL_22_ROLES.discount) || eval22Expectation(table, EVAL_22_ROLES.price);
    if (prices) {
      concerns.pricing.push(table);
      continue;
    }
    const judges = eval22Expectation(table, EVAL_22_ROLES.accepted) || eval22Expectation(table, EVAL_22_ROLES.error);
    if (judges && (eval22Input(table, EVAL_22_ROLES.name) || eval22Input(table, EVAL_22_ROLES.email))) {
      concerns.validation.push(table);
    }
  }
  return concerns;
}

/** The optional-field columns of a table — dietary requirements and accessibility needs. */
function optionalColumns(table) {
  return [EVAL_22_ROLES.dietary, EVAL_22_ROLES.accessibility]
    .map((role) => eval22Input(table, role))
    .filter(Boolean);
}

/** Cells standing in for an absent optional with a word instead of a blank. */
function wordsForAbsent(shape) {
  const found = [];
  for (const table of shape.tables) {
    for (const column of optionalColumns(table)) {
      for (const row of table.rows) {
        const cell = String(row.cells[column.index] ?? "").trim();
        if (STANDS_FOR_ABSENT.test(cell)) found.push({ method: table.method, header: column.header, cell });
      }
    }
  }
  return found;
}

const EVAL_22_RELATIONS = [
  {
    id: "separates-validation-and-pricing",
    label: "validation and pricing are separate methods",
    evaluate: (shape) => {
      const { validation, pricing } = eval22Concerns(shape);
      const missing = [];
      if (validation.length === 0) missing.push("no validation table");
      if (pricing.length === 0) missing.push("no pricing table");
      if (missing.length > 0) return { holds: false, evidence: missing.join("; ") };
      const shared = validation.filter((table) => pricing.includes(table));
      return {
        holds: shared.length === 0,
        evidence:
          shared.length === 0
            ? `validation: ${validation.map((one) => one.method).join(", ")}; pricing: ${pricing.map((one) => one.method).join(", ")}`
            : `${shared.map((one) => one.method).join(", ")} does both`,
      };
    },
  },
  {
    id: "validation-rules-covered",
    label: "an invalid email row and a missing name row",
    evaluate: (shape) => {
      const { validation } = eval22Concerns(shape);
      if (validation.length === 0) return { holds: false, evidence: "no validation table" };
      let badEmail = null;
      let noName = null;
      for (const table of validation) {
        const email = eval22Input(table, EVAL_22_ROLES.email);
        const name = eval22Input(table, EVAL_22_ROLES.name);
        for (const row of table.rows) {
          // An email is invalid when it has no at sign or nothing after it; a name is missing when
          // its cell is blank.
          if (email) {
            const cell = String(row.cells[email.index] ?? "").trim();
            if (cell !== "" && !/^[^@\s]+@[^@\s.]+\.[^@\s]+$/.test(cell)) badEmail = badEmail || cell;
          }
          // A blank cell means absent and `''` means present-but-empty. Both are a registration with
          // no name, and iteration-50 writes the empty string.
          if (name) {
            const cell = String(row.cells[name.index] ?? "").trim();
            if (cell === "" || /^(''|"")$/.test(cell)) noName = noName || table.method;
          }
        }
      }
      const missing = [];
      if (!badEmail) missing.push("no row carries a malformed email");
      if (!noName) missing.push("no row leaves the name out");
      return {
        holds: missing.length === 0,
        evidence: missing.length === 0 ? `malformed email "${badEmail}", and a row with no name` : missing.join("; "),
      };
    },
  },
  {
    id: "validation-includes-optional-fields",
    label: "the optionals vary in the validation table",
    evaluate: (shape) => {
      // A dedicated acceptance table carrying only the optionals is a validation table too, even
      // with no name or email column — iterations 82 and 83 write exactly that, and the assertion
      // asks only that a validation table show the optionals given and absent.
      const hosts = shape.tables.filter(
        (table) =>
          optionalColumns(table).length > 0 &&
          (eval22Expectation(table, EVAL_22_ROLES.accepted) || eval22Expectation(table, EVAL_22_ROLES.error)),
      );
      if (hosts.length === 0) return { holds: false, evidence: "no table asserts acceptance beside the optional fields" };
      for (const table of hosts) {
        const optionals = optionalColumns(table);
        const varies = optionals.some((column) => {
          const cells = table.rows.map((row) => String(row.cells[column.index] ?? "").trim());
          return cells.some((cell) => cell === "") && cells.some((cell) => cell !== "");
        });
        if (varies) {
          return { holds: true, evidence: `${table.method}: ${optionals.map((one) => one.header).join(" and ")} shown given and absent` };
        }
      }
      return {
        holds: false,
        evidence: `no table shows an optional field both given and absent (${hosts.map((one) => one.method).join(", ")})`,
      };
    },
  },
  {
    id: "blank-for-absent-optional",
    label: "an absent optional is blank, not a word",
    evaluate: (shape) => {
      const words = wordsForAbsent(shape);
      const anyOptional = shape.tables.some((table) => optionalColumns(table).length > 0);
      if (!anyOptional) return { holds: false, evidence: "no optional-field column anywhere" };
      return {
        holds: words.length === 0,
        evidence:
          words.length === 0
            ? "absent optionals are written as blank cells"
            : words.slice(0, 3).map((one) => `${one.method}: ${one.header} = "${one.cell}"`).join("; "),
      };
    },
  },
  {
    id: "blank-vs-value-set-correct",
    label: "the pricing table does not blank an irrelevant input",
    evaluate: (shape) => {
      const { pricing } = eval22Concerns(shape);
      if (pricing.length === 0) return { holds: false, evidence: "no pricing table" };
      // A blank means null. An input the pricing rule ignores should carry a value set or a
      // representative value, so a blank there says the field was absent rather than irrelevant.
      const blanked = [];
      for (const table of pricing) {
        for (const column of optionalColumns(table)) {
          const blanks = table.rows.filter((row) => String(row.cells[column.index] ?? "").trim() === "").length;
          if (blanks > 0) blanked.push(`${table.method}: ${column.header} blank in ${blanks} row(s)`);
        }
      }
      return {
        holds: blanked.length === 0,
        evidence: blanked.length === 0 ? "no irrelevant input is left blank where price is decided" : blanked.join("; "),
      };
    },
  },
  {
    id: "descriptive-registration-date",
    label: "the date reads as a description, not a literal",
    evaluate: (shape) => {
      const { pricing } = eval22Concerns(shape);
      if (pricing.length === 0) return { holds: false, evidence: "no pricing table" };
      const literals = [];
      for (const table of pricing) {
        const timing = eval22Input(table, EVAL_22_ROLES.timing);
        if (!timing) continue;
        const raw = table.rows
          .map((row) => String(row.cells[timing.index] ?? "").trim())
          .find((cell) => DATE_LITERAL.test(cell));
        if (raw) literals.push({ method: table.method, header: timing.header, cell: raw });
      }
      return {
        holds: literals.length === 0,
        evidence:
          literals.length === 0
            ? "registration timing is stated descriptively"
            : literals.map((one) => `${one.method}: ${one.header} = ${one.cell}`).join("; "),
      };
    },
  },
  {
    id: "cutoff-date-column-if-literal-dates",
    label: "a literal date brings the cutoff with it",
    evaluate: (shape) => {
      const { pricing } = eval22Concerns(shape);
      if (pricing.length === 0) return { holds: false, evidence: "no pricing table" };
      const missing = [];
      for (const table of pricing) {
        const timing = eval22Input(table, EVAL_22_ROLES.timing);
        if (!timing) continue;
        const literal = table.rows.some((row) => DATE_LITERAL.test(String(row.cells[timing.index] ?? "")));
        // Descriptive values pass automatically; only a literal date owes the reader the cutoff it
        // is being compared against. This is repair 11's shape — a policy constant as a column.
        if (!literal) continue;
        const cutoff = table.columns.find((column) => !column.isScenario && EVAL_22_ROLES.cutoff.test(column.header));
        if (!cutoff) missing.push(table.method);
      }
      return {
        holds: missing.length === 0,
        evidence:
          missing.length === 0
            ? "dates are descriptive, or the cutoff is a column beside them"
            : `${missing.join(", ")} carries literal dates with no cutoff column`,
      };
    },
  },
  {
    id: "discount-column-preferred",
    label: "the discount is the output, or the base price is shown",
    evaluate: (shape) => {
      const { pricing } = eval22Concerns(shape);
      if (pricing.length === 0) return { holds: false, evidence: "no pricing table" };
      const failures = [];
      for (const table of pricing) {
        if (eval22Expectation(table, EVAL_22_ROLES.discount)) continue;
        // A price alone makes the reader supply the base price from memory; showing it as a column
        // is the other way the assertion accepts.
        const base = table.columns.find((column) => !column.isScenario && EVAL_22_ROLES.basePrice.test(column.header));
        if (!base) failures.push(table.method);
      }
      return {
        holds: failures.length === 0,
        evidence:
          failures.length === 0
            ? "every pricing table states a discount, or a base price beside its price"
            : `${failures.join(", ")} states a price with no discount and no base price`,
      };
    },
  },
  {
    id: "optional-fields-has-expected-column",
    label: "the optional-fields rows assert something",
    evaluate: (shape) => {
      const hosts = shape.tables.filter((table) => optionalColumns(table).length > 0);
      if (hosts.length === 0) return { holds: false, evidence: "no optional-field column anywhere" };
      const bare = hosts.filter((table) => table.expectationColumns.length === 0);
      return {
        holds: bare.length === 0,
        evidence:
          bare.length === 0
            ? `${hosts.map((one) => one.method).join(", ")} each assert an outcome`
            : `${bare.map((one) => one.method).join(", ")} varies the optionals with nothing expected`,
      };
    },
  },
  falsifiabilityRelation(),
];

/** Every eval this module can read, by eval number. */
const EVALS = {
  14: { call: null, relations: EVAL_14_RELATIONS },
  15: { call: null, relations: EVAL_15_RELATIONS },
  18: { call: EVAL_18_CALL, relations: EVAL_18_RELATIONS },
  22: { call: "register", relations: EVAL_22_RELATIONS },
  25: { call: EVAL_25_CALL, relations: EVAL_25_RELATIONS },
  29: { call: null, relations: EVAL_29_RELATIONS },
  30: { call: null, relations: EVAL_30_RELATIONS },
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
  EVAL_22_RELATIONS,
  EVAL_25_RELATIONS,
  EVAL_29_RELATIONS,
  EVAL_30_RELATIONS,
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
  bearsFacet,
  companionBreaksATie,
  companionTieRow,
  exercisingTable,
  minimalCovers,
  setMembers,
  tieComputable,
  historyEntries,
  bespokeMapCells,
  boundaryPairs,
  eval22Concerns,
  optionalColumns,
  wordsForAbsent,
  costColumn,
  dimensionsOf,
  unpublishedConstants,
  volumetricWeight,
  heldCallValues,
  literalsIn,
  publishedSurface,
  publishesValue,
  beforeWithoutAfter,
  compoundKeys,
  enumeratingMessages,
  eval29Concerns,
  sharedMutableState,
  inconsistentNames,
  isStandardMap,
  quantityCore,
  spreadCouponColumns,
  facetOnSurface,
  isNativeCollection,
  quotedStructureIn,
  stringEncodedOutputs,
  undeclaredCriteria,
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
