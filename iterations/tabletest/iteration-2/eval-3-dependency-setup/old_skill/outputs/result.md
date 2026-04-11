# Adding TableTest to Your Project

## What to Add

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Replace `VERSION` with the latest version from Maven Central.

## Watch Out: JUnit Version Compatibility

Your project currently has JUnit Jupiter **5.10.0**, but TableTest requires **JUnit Jupiter 5.11 or higher**. You will need to upgrade your existing JUnit dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Use the latest 5.11.x or 5.12.x release available on Maven Central rather than exactly 5.11.0 if a newer patch is available.

## Imports

Once the dependency is in place, import `@TableTest` in your test files:

```java
import org.tabletest.junit.TableTest;
```

Additional imports, only when needed:

```java
import org.tabletest.junit.Scenario;          // only when binding the scenario column to a parameter
import org.tabletest.junit.TypeConverter;     // only when adding custom converter methods
import org.tabletest.junit.TypeConverterSources; // only when referencing shared converter sources
```

## Summary of Changes

| Change | Details |
|--------|---------|
| New dependency | `org.tabletest:tabletest-junit:VERSION` (test scope) |
| JUnit upgrade required | Bump `junit-jupiter` from `5.10.0` to `5.11+` |
