const { test, describe } = require("node:test");
const assert = require("node:assert");

const { staleEvals, retiredEvals } = require("./check-baseline.js");

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

  test("does not report a retired eval as stale, so a cut suite can still read live", () => {
    const stamped = [
      { id: "eval-20-a", fingerprint: "fp-current" },
      { id: "eval-26-b", fingerprint: "fp-whatever" },
    ];
    const hasDefinition = (id) => id !== "eval-26-b";
    assert.deepEqual(staleEvals(stamped, fingerprintOf, hasDefinition), []);
  });

  test("still reports a stale eval that does have a definition", () => {
    const stamped = [
      { id: "eval-20-a", fingerprint: "fp-superseded" },
      { id: "eval-26-b", fingerprint: "fp-whatever" },
    ];
    const hasDefinition = (id) => id !== "eval-26-b";
    assert.deepEqual(staleEvals(stamped, fingerprintOf, hasDefinition), ["eval-20-a"]);
  });
});

describe("retiredEvals", () => {
  test("names the benchmark entries whose eval has been removed from the suite", () => {
    const stamped = [{ id: "eval-20-a" }, { id: "eval-26-b" }, { id: "eval-27-c" }];
    const hasDefinition = (id) => id === "eval-20-a";
    assert.deepEqual(retiredEvals(stamped, hasDefinition), ["eval-26-b", "eval-27-c"]);
  });

  test("finds nothing when every stamped eval is still in the suite", () => {
    assert.deepEqual(retiredEvals([{ id: "eval-20-a" }], () => true), []);
  });
});
