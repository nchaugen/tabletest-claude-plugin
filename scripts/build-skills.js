#!/usr/bin/env node
/**
 * Renders the shared sources in `shared/` into the copies each suite ships.
 *
 * Today that is one thing: an assertion carrying the same id in more than one eval suite gets its
 * text from `shared/assertions/<id>.md`, rendered through the suite's vocabulary and written into
 * every `eval.json` that carries the id. The three suites had drifted to different wordings of the
 * same judgement — `business-language-columns` alone carried eleven texts across eight tabletest
 * evals — and a grader asked a differently worded question gives a differently worded answer. That
 * is instrument noise attributed to the skill.
 *
 * The vocabulary is what makes one text legitimate in three places: the artefact under judgement is
 * a `@TableTest` method, a markdown table or a parametrized test, so the noun has to change even
 * though the rule must not. Anything a vocabulary entry cannot express is not shared guidance —
 * leave it in the eval.
 *
 * The second source is `shared/table-design/`: one framework-neutral rule per file, interleaved at
 * render time with that skill's own illustration from `examples/<skill>/`, and written into the
 * region a `SKILL.md` delimits with BEGIN/END GENERATED markers. **A skill with no region is not
 * drift** — it has not been migrated yet, which step 7 does one skill at a time so each migration
 * gets its own measurement.
 *
 * It has to be a region of `SKILL.md` rather than a reference file: `SKILL.md` is the only thing
 * loaded when a skill triggers, and measured across the stored transcripts a `references/` file is
 * opened in 14 of 103 runs while a second skill is invoked in none of 123. See
 * products/claude-plugin/decisions/measured-guidance-lives-in-skill-md.md in the private docs repo.
 *
 *   node scripts/build-skills.js            write the generated copies
 *   node scripts/build-skills.js --check    report drift, write nothing, exit 1 when it finds any
 */

const fs = require("fs");
const path = require("path");

const repoRoot = path.resolve(__dirname, "..");
const sharedDir = path.join(repoRoot, "shared");
const sharedAssertionsDir = path.join(sharedDir, "assertions");
const sharedTableDesignDir = path.join(sharedDir, "table-design");
const evalsDir = path.join(repoRoot, "evals");

/**
 * Apply the case of the placeholder to the phrase it resolves to: `{{rows}}` renders the entry as
 * written, `{{Rows}}` capitalises it, `{{ROWS}}` upper-cases it. One vocabulary entry then serves
 * mid-sentence, sentence-initial and shouted positions without three near-duplicate keys to keep
 * in step.
 */
function matchCase(placeholder, phrase) {
  if (placeholder === placeholder.toUpperCase()) return phrase.toUpperCase();
  if (placeholder[0] === placeholder[0].toUpperCase()) {
    return phrase[0].toUpperCase() + phrase.slice(1);
  }
  return phrase;
}

/**
 * Render one shared text for one suite.
 *
 * An unknown placeholder throws rather than surviving into the output: an assertion text is read by
 * the grader, and a literal `{{rows}}` reaching it is a silently changed question, not a typo the
 * reader will notice.
 */
function render(template, vocabulary, where) {
  return template.replace(/\{\{(\w+)\}\}/g, (_, key) => {
    const phrase = vocabulary[key.toLowerCase()];
    if (phrase === undefined) {
      throw new Error(`Unknown placeholder {{${key}}} in ${where} — add it to shared/vocabulary.json`);
    }
    return matchCase(key, phrase);
  });
}

/** The shared assertion texts, by id. */
function loadSharedAssertions() {
  if (!fs.existsSync(sharedAssertionsDir)) return {};
  const texts = {};
  for (const file of fs.readdirSync(sharedAssertionsDir).filter(f => f.endsWith(".md")).sort()) {
    texts[path.basename(file, ".md")] = fs.readFileSync(path.join(sharedAssertionsDir, file), "utf-8").trim();
  }
  return texts;
}

function loadVocabulary() {
  const raw = JSON.parse(fs.readFileSync(path.join(sharedDir, "vocabulary.json"), "utf-8"));
  delete raw._comment;
  return raw;
}

/**
 * Nouns that make a passage mechanics rather than shared design guidance.
 *
 * The plan's test for whether a rule belongs in `shared/table-design/rules/` is "does it survive
 * with no framework noun in it?" — so it is enforced rather than remembered. A rule needing one of
 * these to state itself belongs in the skill, and the *example* is where the framework noun goes.
 */
const FRAMEWORK_NOUNS = [
  /@TableTest/, /@TypeConverter/, /@Description/, /@DisplayName/, /\bJUnit\b/, /\bJava\b/, /\bKotlin\b/,
  /\bpytest\b/, /\bparametrize\b/, /\bSwift\b/, /\bJest\b/, /\bVitest\b/, /\bxUnit\b/, /\bTheory\b/,
  /\bGolang\b/, /\bmarkdown\b/i,
];

/** The shared table-design rules, in file order, as {slug, template}. */
function loadTableDesignRules() {
  const rulesDir = path.join(sharedTableDesignDir, "rules");
  if (!fs.existsSync(rulesDir)) return [];
  return fs.readdirSync(rulesDir)
    .filter(f => f.endsWith(".md"))
    .sort()
    .map(f => ({
      slug: path.basename(f, ".md"),
      template: fs.readFileSync(path.join(rulesDir, f), "utf-8").trim(),
    }));
}

/** One skill's illustration of one rule, or null when it has none. */
function loadExample(skill, slug) {
  const file = path.join(sharedTableDesignDir, "examples", skill, `${slug}.md`);
  return fs.existsSync(file) ? fs.readFileSync(file, "utf-8").trim() : null;
}

/**
 * The rendered table-design core for one skill: every rule in file order, each interleaved with
 * that skill's own example.
 *
 * **A rule with no example for a skill throws rather than rendering bare.** Guidance is applied far
 * more reliably when an illustration sits beside it, so an unillustrated rule is a defect to fix at
 * build time, not something to discover in a score.
 */
function renderTableDesign(skill, vocabulary) {
  return loadTableDesignRules().map(({ slug, template }) => {
    const where = `shared/table-design/rules/${slug}.md`;
    const example = loadExample(skill, slug);
    if (example === null) {
      throw new Error(`No ${skill} example for rule ${slug} — add shared/table-design/examples/${skill}/${slug}.md`);
    }
    return render(template.replace("{{example}}", () => example), vocabulary, where);
  }).join("\n\n");
}

const REGION_BEGIN = "<!-- BEGIN GENERATED table-design — do not edit here; source is shared/table-design/ -->";
const REGION_END = "<!-- END GENERATED table-design -->";

/**
 * Split a `SKILL.md` around its generated region, or null when it carries none.
 *
 * A skill without a region has simply not been migrated yet, which is not drift. A skill with an
 * opening marker and no closing one *is* an error and throws: treating the rest of the file as
 * region body would let the next build delete everything below it.
 */
function findRegion(content) {
  const start = content.indexOf(REGION_BEGIN);
  if (start === -1) {
    if (content.includes(REGION_END)) {
      throw new Error("SKILL.md has a closing table-design marker with no opening one");
    }
    return null;
  }
  const end = content.indexOf(REGION_END, start);
  if (end === -1) {
    throw new Error("SKILL.md has an opening table-design marker with no closing one — refusing to treat the rest of the file as generated");
  }
  return {
    before: content.slice(0, start + REGION_BEGIN.length),
    body: content.slice(start + REGION_BEGIN.length, end).trim(),
    after: content.slice(end),
  };
}

/** The same `SKILL.md` with `body` between its markers. Throws when there is no region to fill. */
function replaceRegion(content, body) {
  const region = findRegion(content);
  if (region === null) {
    throw new Error("SKILL.md carries no table-design region to fill — add the markers first");
  }
  return `${region.before}\n\n${body.trim()}\n\n${region.after}`;
}

/** Each skill's `SKILL.md` path, whether or not it carries a region. */
function skillFiles() {
  const skillsDir = path.join(repoRoot, "skills");
  if (!fs.existsSync(skillsDir)) return [];
  return fs.readdirSync(skillsDir)
    .filter(name => fs.existsSync(path.join(skillsDir, name, "SKILL.md")))
    .sort()
    .map(name => ({ skill: name, file: path.join(skillsDir, name, "SKILL.md") }));
}

/**
 * Skills whose generated region no longer matches what `shared/table-design/` renders to.
 *
 * Skills with no region are skipped, not reported — until step 7 migrates a skill, having no region
 * is the correct state.
 */
function findRegionDrift() {
  const vocabulary = loadVocabulary();
  const drift = [];
  for (const { skill, file } of skillFiles()) {
    const region = findRegion(fs.readFileSync(file, "utf-8"));
    if (region === null) continue;
    if (!vocabulary[skill]) {
      throw new Error(`No vocabulary for skill "${skill}" — add it to shared/vocabulary.json`);
    }
    const expected = renderTableDesign(skill, vocabulary[skill]).trim();
    if (region.body !== expected) {
      drift.push({ skill, file: path.relative(repoRoot, file) });
    }
  }
  return drift;
}

/** Rewrite every skill's generated region from the shared source. Returns the files changed. */
function writeSkillRegions() {
  const vocabulary = loadVocabulary();
  const written = [];
  for (const { skill, file } of skillFiles()) {
    const content = fs.readFileSync(file, "utf-8");
    if (findRegion(content) === null) continue;
    const updated = replaceRegion(content, renderTableDesign(skill, vocabulary[skill]));
    if (updated !== content) {
      fs.writeFileSync(file, updated);
      written.push(path.relative(repoRoot, file));
    }
  }
  return written;
}

/** Every `eval.json` in the suite, as {suite, path, definition}. */
function loadEvalDefinitions() {
  const definitions = [];
  for (const suite of fs.readdirSync(evalsDir).sort()) {
    const suiteDir = path.join(evalsDir, suite);
    if (!fs.statSync(suiteDir).isDirectory()) continue;
    for (const dir of fs.readdirSync(suiteDir).sort()) {
      const file = path.join(suiteDir, dir, "eval.json");
      if (!fs.existsSync(file)) continue;
      definitions.push({ suite, file, definition: JSON.parse(fs.readFileSync(file, "utf-8")) });
    }
  }
  return definitions;
}

/**
 * The assertion texts that differ from what the shared source renders to, as
 * {file, id, current, expected}. Empty means every copy is in step with its source.
 */
function findAssertionDrift() {
  const shared = loadSharedAssertions();
  const vocabulary = loadVocabulary();
  const drift = [];

  for (const { suite, file, definition } of loadEvalDefinitions()) {
    for (const assertion of definition.assertions || []) {
      const template = shared[assertion.id];
      if (!template) continue;
      if (!vocabulary[suite]) {
        throw new Error(`No vocabulary for suite "${suite}" — add it to shared/vocabulary.json`);
      }
      const expected = render(template, vocabulary[suite], `shared/assertions/${assertion.id}.md`);
      if (assertion.text !== expected) {
        drift.push({ file: path.relative(repoRoot, file), id: assertion.id, current: assertion.text, expected });
      }
    }
  }

  return drift;
}

/** Write the rendered text into every eval that carries a shared assertion id. Returns the files changed. */
function writeAssertionTexts() {
  const shared = loadSharedAssertions();
  const vocabulary = loadVocabulary();
  const written = [];

  for (const { suite, file, definition } of loadEvalDefinitions()) {
    let changed = false;
    for (const assertion of definition.assertions || []) {
      const template = shared[assertion.id];
      if (!template) continue;
      const expected = render(template, vocabulary[suite], `shared/assertions/${assertion.id}.md`);
      if (assertion.text !== expected) {
        assertion.text = expected;
        changed = true;
      }
    }
    if (changed) {
      fs.writeFileSync(file, JSON.stringify(definition, null, 2) + "\n");
      written.push(path.relative(repoRoot, file));
    }
  }

  return written;
}

function main() {
  const check = process.argv.includes("--check");

  if (check) {
    const assertionDrift = findAssertionDrift();
    const regionDrift = findRegionDrift();
    if (assertionDrift.length === 0 && regionDrift.length === 0) {
      const regions = skillFiles().filter(({ file }) => findRegion(fs.readFileSync(file, "utf-8")) !== null).length;
      console.log(`Every generated copy matches its source in shared/ (${regions} skill region(s) checked).`);
      return 0;
    }
    if (assertionDrift.length > 0) {
      console.error(`${assertionDrift.length} assertion text(s) diverge from shared/assertions/:`);
      for (const d of assertionDrift) {
        console.error(`  ${d.file} — ${d.id}`);
        console.error(`    is:       ${d.current.slice(0, 120)}`);
        console.error(`    expected: ${d.expected.slice(0, 120)}`);
      }
    }
    if (regionDrift.length > 0) {
      console.error(`${regionDrift.length} skill region(s) diverge from shared/table-design/:`);
      for (const d of regionDrift) console.error(`  ${d.file}`);
    }
    console.error("Edit the source under shared/, then run: node scripts/build-skills.js");
    return 1;
  }

  const written = [...writeAssertionTexts(), ...writeSkillRegions()];
  if (written.length === 0) {
    console.log("Every generated copy is already up to date.");
    return 0;
  }
  console.log(`Rewrote ${written.length} file(s):`);
  for (const f of written) console.log(`  ${f}`);
  return 0;
}

if (require.main === module) {
  try {
    process.exit(main());
  } catch (err) {
    console.error(err.message);
    process.exit(1);
  }
}

module.exports = {
  render,
  matchCase,
  findAssertionDrift,
  writeAssertionTexts,
  loadSharedAssertions,
  loadVocabulary,
  loadTableDesignRules,
  loadExample,
  renderTableDesign,
  FRAMEWORK_NOUNS,
  findRegion,
  replaceRegion,
  findRegionDrift,
  writeSkillRegions,
  skillFiles,
  REGION_BEGIN,
  REGION_END,
};
