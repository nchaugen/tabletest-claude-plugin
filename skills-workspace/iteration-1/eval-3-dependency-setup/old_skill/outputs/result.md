# Adding TableTest to Your Project

## What to Add

Add the TableTest dependency to your `pom.xml`:

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

**Your current JUnit version is a problem.** TableTest requires **JUnit 5.11 or higher**, but your `pom.xml` has:

```xml
<version>5.10.0</version>
```

You need to upgrade `junit-jupiter` to at least 5.11 before TableTest will work correctly.

## Imports

Once the dependency is in place, the standard import is:

```java
import org.tabletest.junit.TableTest;
```

Only add these if you need them:

```java
import org.tabletest.junit.Scenario;          // only when binding the scenario column to a parameter
import org.tabletest.junit.TypeConverter;      // only when adding custom converter methods
import org.tabletest.junit.TypeConverterSources; // only when referencing shared converter sources
```

## Summary

Two changes needed:

1. Upgrade `junit-jupiter` from `5.10.0` to `5.11+`
2. Add `org.tabletest:tabletest-junit:VERSION` with `test` scope
