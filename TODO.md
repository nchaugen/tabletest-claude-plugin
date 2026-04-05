# TODO

## Future eval ideas

### ~~Advanced rewrite evals~~ (done — evals 25-28)
- ~~**Spock Framework tests** (eval 25)~~
- ~~**Kotest data-driven tests** (eval 26)~~
- ~~**TestNG @DataProvider tests** (eval 27)~~
- ~~**JUnit @MethodSource tests** (eval 28)~~

### Eval 19 enhancements
- Tune the email validation conversion eval to make it preferable to use more TableTest features: collection type syntax, type converters, value sets (where semantically appropriate, e.g. grouping invalid emails by failure category).

## Refactoring

### ~~Split evals.json~~ (done)

### Extract inline code to project files
- For evals working on existing code (e.g. eval 18, 19), create actual project directory with code files in the eval sub-directory
- Update run-evals.js to set per-eval cwd to the eval's directory

## Skill improvements

### More token friendly instructions
- Test if skill instructions can be shrunk by replacing elaborate examples with bullet point best-practice heuristics in various categories

