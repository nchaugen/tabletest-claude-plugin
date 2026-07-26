const { test, describe } = require("node:test");
const assert = require("node:assert");

const { staleEvals } = require("./check-baseline.js");

describe("staleEvals", () => {
  const current = { "eval-20-a": "fp-current", "eval-26-b": "fp-current" };
  const fingerprintOf = (id) => current[id];

  test("finds nothing when every stamped fingerprint still matches", () => {
    const stamped = [
      { id: "eval-20-a", fingerprint: "fp-current" },
      { id: "eval-26-b", fingerprint: "fp-current" },
    ];
    assert.deepEqual(staleEvals(stamped, fingerprintOf), []);
  });

  test("names the evals a comparison against this benchmark would silently exclude", () => {
    const stamped = [
      { id: "eval-20-a", fingerprint: "fp-current" },
      { id: "eval-26-b", fingerprint: "fp-superseded" },
    ];
    assert.deepEqual(staleEvals(stamped, fingerprintOf), ["eval-26-b"]);
  });

  test("treats a pre-guard benchmark's unstamped evals as comparable, matching the guard", () => {
    assert.deepEqual(staleEvals([{ id: "eval-20-a" }], fingerprintOf), []);
  });
});
