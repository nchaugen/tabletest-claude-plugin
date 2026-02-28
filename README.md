# tabletest

A Claude Code plugin for writing [TableTest](https://tabletest.org)-style JUnit tests in Java and Kotlin.

## Features

- **Spec-by-example skill** -- Guided workflow for clarifying behaviour with multiple cases or rules by working through concrete examples as a table, using business language throughout
- **TableTest skill** -- Guided workflow for writing `@TableTest` methods, with pre-checks for dependencies, table design rules, and quoting conventions
- **Auto-formatting** -- Tables are automatically aligned after every file write or edit. The formatter is downloaded automatically on first use; requires Java on the PATH
- **Reference guides** -- Detailed guides on column design, common patterns, type converters, value sets, and more

## Installation

First, add the marketplace:

```
/plugin marketplace add https://tabletest.org/marketplace.json
```

Then install the plugin:

```
/plugin install tabletest@tabletest.org
```

## Usage

### Spec by Example

Invoke `/spec-by-example` when behaviour has multiple cases or business rules that need to be clarified through concrete examples:

- Work out rules that depend on multiple conditions ("it depends on the customer's age and whether they hold a licence")
- Sharpen vague requirements ("eligible customers", "appropriate discount") through a table of specific scenarios
- Surface complexity discovered mid-implementation and pin down the intended behaviour before continuing

The example table becomes the starting point for a `@TableTest` when implementation begins.

### TableTest

Invoke `/tabletest` when you want to:

- Convert two or more similar `@Test` methods into a single `@TableTest`
- Add a new data-driven test with multiple input/output examples
- Refine an example table into a running executable test

Auto-formatting runs automatically whenever a Java or Kotlin file containing a table is written or edited.

## Requirements

- JUnit 5.11 or higher
- [`tabletest-junit`](https://tabletest.org) dependency in your project

## Links

- [tabletest.org](https://tabletest.org)
- [GitHub repository](https://github.com/nchaugen/tabletest-claude-plugin)

## Licence

[Apache 2.0](LICENSE)
