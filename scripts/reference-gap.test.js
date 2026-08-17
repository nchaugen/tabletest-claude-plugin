const { test, describe } = require("node:test");
const assert = require("node:assert/strict");

const { band, extremity, gapsForEval, parseArgs, rankedGaps, SATURATED_FLOOR, TARGET_CEILING } = require("./reference-gap.js");

describe("band and extremity", () => {
  test("puts a slot no draw reaches in target, and one every draw reaches in saturated", () => {
    assert.deepEqual([band(0), band(1)], ["target", "saturated"]);
  });

  test("splits eval-18's worst target from eval-15's best coin flip, which nearly touch", () => {
    // 2 of 11 is eval-18's `premium-charge-is-per-claim`; 2 of 10 is eval-15's `2.20`, which the
    // variance gotcha names as a slot that moves between identical runs.
    assert.equal(band(2 / 11), "target");
    assert.equal(band(2 / 10), "coin-flip");
  });

  test("treats a coin flip as the least readable rate there is", () => {
    assert.equal(extremity(0.5), 0);
    assert.ok(extremity(0) > extremity(0.2));
    assert.ok(extremity(0.2) > extremity(0.4));
  });

  test("keeps the two thresholds apart, so no rate lands in two bands", () => {
    assert.ok(TARGET_CEILING < SATURATED_FLOOR);
  });
});

describe("rankedGaps", () => {
  const { gaps } = rankedGaps("tabletest");

  test("orders by band before extremity, so a saturated slot never outranks a target", () => {
    const firstSaturated = gaps.findIndex((one) => one.band === "saturated");
    const lastTarget = gaps.map((one) => one.band).lastIndexOf("target");
    // Both bands sit at extremity 0.5 at their extremes, which is exactly what would interleave them.
    assert.ok(lastTarget < firstSaturated, "a target ranked below a saturated slot");
  });

  test("ranks a slot no draw reaches above one a single draw reaches", () => {
    const rates = gaps.filter((one) => one.band === "target").map((one) => one.rate);
    assert.deepEqual(rates, [...rates].sort((a, b) => a - b));
  });

  test("finds eval-18's premium cluster, which one draw can read", () => {
    const targets = gaps.filter((one) => one.band === "target" && one.eval === 18).map((one) => one.id);
    assert.deepEqual(targets.sort(), [
      "premium-age-is-banded",
      "premium-charge-is-per-claim",
      "premium-claim-effect-varies-by-band",
      "separates-decision-and-premium",
    ]);
  });

  test("does not rank eval-15's four moving slots as targets", () => {
    // They are the widest gaps in the suite and the least readable — the whole reason the ranking
    // is by extremity rather than by gap width.
    const moving = ["2.20-readability-one-row-per-tier", "2.17-zone-irrelevance-visible", "zone-independent-counting", "2.15-ticket-count-uses-value-sets"];
    for (const id of moving) {
      const one = gaps.find((row) => row.eval === 15 && row.id === id);
      assert.equal(one.band, "coin-flip", `${id} should not be a target`);
    }
  });

  test("reports no relation the reference itself fails", () => {
    assert.deepEqual(rankedGaps("tabletest").broken, []);
  });
});

describe("gapsForEval and parseArgs", () => {
  test("skips an eval with no relations authored", () => {
    assert.deepEqual(gapsForEval(999, "tabletest"), { slug: null, gaps: [], broken: [] });
  });

  test("counts every draw of one eval against its reference", () => {
    const { gaps } = gapsForEval(9, "tabletest");
    assert.ok(gaps.every((one) => one.of === 9), "eval-9 stores nine draws beside its reference");
  });

  test("defaults the skill and reads the band filter", () => {
    assert.deepEqual(parseArgs(["--band", "target"]), { skill: "tabletest", band: "target", json: false });
  });
});
