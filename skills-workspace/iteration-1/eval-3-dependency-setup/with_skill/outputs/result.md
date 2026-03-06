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

## What to Watch Out For: JUnit Version Requirement

Your current JUnit Jupiter version is **5.10.0**, but TableTest requires **JUnit Jupiter 5.11 or higher**.

You need to upgrade your existing JUnit dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Use the latest 5.11+ version available on Maven Central rather than exactly 5.11.0 if a newer patch is available.

## Imports

Once the dependency is in place, use these imports in your test files:

```java
import org.tabletest.junit.TableTest;
```

Only add the following imports if you need them:

```java
// When binding the scenario column to a parameter
import org.tabletest.junit.Scenario;

// When defining custom type converter methods
import org.tabletest.junit.TypeConverter;

// When referencing shared converter sources
import org.tabletest.junit.TypeConverterSources;
```

## Summary

| Item | Current | Required |
|------|---------|----------|
| `junit-jupiter` | 5.10.0 | 5.11+ |
| `tabletest-junit` | missing | add with `org.tabletest:tabletest-junit` |
