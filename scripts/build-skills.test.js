const { test, describe } = require("node:test");
const assert = require("node:assert");

const {
  render,
  matchCase,
  findAssertionDrift,
  loadSharedAssertions,
  loadVocabulary,
  loadTableDesignRules,
  loadExample,
  renderTableDesign,
  FRAMEWORK_NOUNS,
  findRegion,
  replaceRegion,
  findRegionDrift,
  REGION_BEGIN,
  REGION_END,
  beginMarker,
  endMarker,
  renderTableDesignChecks,
} = require("./build-skills.js");

const vocabulary = {
  names: "scenario ids",
  row: "case",
  rows: "cases",
  every_table: "every parametrized test in the file",
};

describe("placeholder case", () => {
  test("renders the entry as written for a lowercase placeholder", () => {
    assert.equal(matchCase("rows", "cases"), "cases");
  });

  test("capitalises for a sentence-initial placeholder", () => {
    assert.equal(matchCase("Rows", "cases"), "Cases");
  });

  test("upper-cases for a shouted placeholder", () => {
    assert.equal(matchCase("ROWS", "cases"), "CASES");
  });

  test("capitalises only the first letter of a multi-word phrase", () => {
    assert.equal(matchCase("Names", "scenario ids"), "Scenario ids");
  });
});

describe("render", () => {
  test("substitutes every placeholder from the suite's vocabulary", () => {
    assert.equal(
      render("Judge {{every_table}}; a {{row}} states one rule.", vocabulary, "test"),
      "Judge every parametrized test in the file; a case states one rule."
    );
  });

  test("resolves a cased placeholder from the same lowercase entry", () => {
    assert.equal(render("{{ROWS}} and {{Rows}} and {{rows}}", vocabulary, "test"), "CASES and Cases and cases");
  });

  test("throws on an unknown placeholder rather than sending a literal brace to the grader", () => {
    assert.throws(
      () => render("Judge {{columns}} in isolation.", vocabulary, "shared/assertions/x.md"),
      /Unknown placeholder \{\{columns\}\} in shared\/assertions\/x\.md/
    );
  });

  test("leaves text with no placeholders untouched", () => {
    assert.equal(render("PASSES otherwise.", vocabulary, "test"), "PASSES otherwise.");
  });
});

describe("the shared sources and the copies generated from them", () => {
  test("every eval carrying a shared assertion id has the text its source renders to", () => {
    const drift = findAssertionDrift();
    assert.deepEqual(
      drift.map(d => `${d.file} — ${d.id}`),
      [],
      "run: node scripts/build-skills.js"
    );
  });

  test("every suite declares the same vocabulary keys", () => {
    const vocabularies = loadVocabulary();
    const suites = Object.keys(vocabularies);
    const keysOf = (suite) => Object.keys(vocabularies[suite]).sort();
    for (const suite of suites.slice(1)) {
      assert.deepEqual(keysOf(suite), keysOf(suites[0]), `${suite} declares different keys from ${suites[0]}`);
    }
  });

  test("every placeholder a shared text uses is declared by every suite", () => {
    const vocabularies = loadVocabulary();
    for (const [id, template] of Object.entries(loadSharedAssertions())) {
      for (const [suite, words] of Object.entries(vocabularies)) {
        assert.doesNotThrow(() => render(template, words, `shared/assertions/${id}.md`), `${id} in ${suite}`);
      }
    }
  });
});

describe("the shared table-design core", () => {
  const vocabularies = loadVocabulary();
  const skills = Object.keys(vocabularies);
  const rules = loadTableDesignRules();

  test("renders for every skill, so every rule has an illustration in each", () => {
    for (const skill of skills) {
      assert.doesNotThrow(() => renderTableDesign(skill, vocabularies[skill]), skill);
    }
  });

  test("states every rule without a framework noun", () => {
    const offenders = [];
    for (const { slug, template } of rules) {
      for (const noun of FRAMEWORK_NOUNS) {
        const found = template.match(noun);
        if (found) offenders.push(`${slug}: "${found[0]}"`);
      }
    }
    assert.deepEqual(offenders, [], "a rule needing a framework noun is mechanics — move it to the skill, or into the example");
  });

  test("leaves no unresolved placeholder in the rendered core", () => {
    for (const skill of skills) {
      const rendered = renderTableDesign(skill, vocabularies[skill]);
      const leftover = rendered.match(/\{\{\w+\}\}/g);
      assert.equal(leftover, null, `${skill}: ${leftover && leftover.join(", ")}`);
    }
  });

  test("gives every rule an h3 heading, so it nests under each skill's Table Design section", () => {
    for (const { slug, template } of rules) {
      assert.match(template, /^### \S/m, slug);
    }
  });

  test("reports a missing example rather than rendering the rule bare", () => {
    assert.equal(loadExample("tabletest", "no-such-rule"), null);
    for (const { slug } of rules) {
      for (const skill of skills) {
        assert.notEqual(loadExample(skill, slug), null, `${skill} has no example for ${slug}`);
      }
    }
  });

  test("illustrates every rule with a table or code, never with more prose", () => {
    for (const { slug } of rules) {
      for (const skill of skills) {
        const example = loadExample(skill, slug);
        assert.ok(/\|/.test(example) || /```/.test(example), `${skill}/${slug} shows nothing concrete`);
      }
    }
  });

  test("never writes one skill's example in another skill's notation", () => {
    // The point of per-skill examples is that the notation matches the artefact the reader is
    // producing. A pytest snippet in the tabletest core, or a @TableTest in the pytest core, is the
    // copy-paste error this catches — and it would otherwise ship looking plausible.
    const foreign = {
      tabletest: /\bpytest\b|test\.each|\[Theory\]|#expect\(/,
      "spec-by-example": /@TableTest|\bpytest\b|test\.each|\[Theory\]/,
      "table-driven-testing": /@TableTest|@TypeConverter/,
    };
    for (const { slug } of rules) {
      for (const skill of skills) {
        assert.doesNotMatch(loadExample(skill, slug), foreign[skill], `${skill}/${slug}`);
      }
    }
  });

  test("gives spec-by-example a plain table, since its readers write no code", () => {
    for (const { slug } of rules) {
      assert.match(loadExample("spec-by-example", slug), /\|/, slug);
    }
  });
});

describe("the generated SKILL.md region", () => {
  const wrap = (body) => `# Skill\n\nIntro prose.\n\n${REGION_BEGIN}\n\n${body}\n\n${REGION_END}\n\n## Mechanics\n\nTail prose.\n`;

  test("reports no region when a skill has not been migrated", () => {
    assert.equal(findRegion("# Skill\n\nAll hand-written.\n"), null);
  });

  test("extracts the body between the markers", () => {
    assert.equal(findRegion(wrap("## One Rule\n\nBody.")).body, "## One Rule\n\nBody.");
  });

  test("refuses an opening marker with no closing one, rather than eating the rest of the file", () => {
    assert.throws(
      () => findRegion(`# Skill\n${REGION_BEGIN}\n\n## Mechanics\n\nTail that must not be swallowed.\n`),
      /opening table-design marker with no closing one/
    );
  });

  test("refuses a closing marker with no opening one", () => {
    assert.throws(() => findRegion(`# Skill\n\n${REGION_END}\n`), /closing table-design marker with no opening one/);
  });

  test("replaces only what is between the markers", () => {
    const updated = replaceRegion(wrap("stale"), "fresh");
    assert.match(updated, /Intro prose\./);
    assert.match(updated, /## Mechanics/);
    assert.match(updated, /Tail prose\./);
    assert.doesNotMatch(updated, /stale/);
    assert.equal(findRegion(updated).body, "fresh");
  });

  test("is idempotent, so a second build changes nothing", () => {
    const once = replaceRegion(wrap("stale"), "fresh");
    assert.equal(replaceRegion(once, "fresh"), once);
  });

  test("refuses to fill a file with no region", () => {
    assert.throws(() => replaceRegion("# Skill\n", "body"), /carries no table-design region/);
  });

  test("detects a hand-edited region, which is what the drift guard exists for", () => {
    // findRegionDrift is vacuous until step 7 migrates a skill, so the comparison it makes is
    // exercised here directly: a region whose body is not what the source renders to is drift.
    const vocabularies = loadVocabulary();
    const rendered = renderTableDesign("tabletest", vocabularies.tabletest).trim();
    assert.equal(findRegion(wrap(rendered)).body, rendered, "a freshly built region is not drift");
    const tampered = wrap(rendered.replace("One Rule, One Axis", "One Rule, One Axis (edited by hand)"));
    assert.notEqual(findRegion(tampered).body, rendered, "a hand edit must register as drift");
  });

  test("keeps every migrated skill's region in step with the shared source", () => {
    assert.deepEqual(findRegionDrift().map(d => d.file), [], "run: node scripts/build-skills.js");
  });
});

describe("the generated checklist", () => {
  const vocabularies = loadVocabulary();

  test("gives every rule a check, so the checklist cannot fall behind the rules", () => {
    for (const { slug, check } of loadTableDesignRules()) {
      assert.ok(check && check.length > 0, `${slug} has an empty **Check:** line`);
    }
  });

  test("splits the check off the rule body rather than rendering it twice", () => {
    for (const { slug, template } of loadTableDesignRules()) {
      assert.doesNotMatch(template, /\*\*Check:\*\*/, `${slug} still carries its check in the body`);
    }
    for (const skill of Object.keys(vocabularies)) {
      assert.doesNotMatch(renderTableDesign(skill, vocabularies[skill]), /\*\*Check:\*\*/, skill);
    }
  });

  test("emits one checklist item per rule, for every skill", () => {
    const ruleCount = loadTableDesignRules().length;
    for (const skill of Object.keys(vocabularies)) {
      const lines = renderTableDesignChecks(skill, vocabularies[skill]).split("\n");
      assert.equal(lines.length, ruleCount, skill);
      for (const line of lines) assert.match(line, /^- \[ \] \*\*/, `${skill}: ${line}`);
    }
  });

  test("renders each skill's checklist in its own vocabulary", () => {
    assert.match(renderTableDesignChecks("table-driven-testing", vocabularies["table-driven-testing"]), /one case per obligation/i);
    assert.match(renderTableDesignChecks("spec-by-example", vocabularies["spec-by-example"]), /one row per obligation/i);
  });

  test("keeps the two regions independent, so a skill may carry either or both", () => {
    const both = `x\n${beginMarker("table-design")}\nA\n${endMarker("table-design")}\ny\n${beginMarker("table-design-checks")}\nB\n${endMarker("table-design-checks")}\nz`;
    assert.equal(findRegion(both, "table-design").body, "A");
    assert.equal(findRegion(both, "table-design-checks").body, "B");
    const onlyChecks = `x\n${beginMarker("table-design-checks")}\nB\n${endMarker("table-design-checks")}`;
    assert.equal(findRegion(onlyChecks, "table-design"), null);
    assert.equal(findRegion(onlyChecks, "table-design-checks").body, "B");
  });
});
