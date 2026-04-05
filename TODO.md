# TODO

## Future eval ideas

### Advanced rewrite evals
- **Spock Framework tests**: Convert Spock `where:` blocks with inline Groovy expressions to TableTest. Tests whether the skill handles computed expected values and Groovy-specific patterns.
- **JUnit parameterized tests with method-based value providers**: Convert `@MethodSource` parameterized tests (where test data comes from a static method returning `Stream<Arguments>`) to TableTest. Tests handling of complex object construction and multi-line setup.

### Eval 19 enhancements
- Tune the email validation conversion eval to make it preferable to use more TableTest features: collection type syntax, type converters, value sets (where semantically appropriate, e.g. grouping invalid emails by failure category).

## Refactoring

### Split evals.json
- Document is long and hard to read
- Instead put each eval in separate sub-directory and spread eval values into separate files
- For evals working on existing code, create actual project directory with code files in the eval sub-directory

## Skill improvements

### More token friendly instructions
- Test if skill instructions can be shrunk by replacing elaborate examples with bullet point best-practice heuristics in various categories

