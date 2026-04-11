# Adding TableTest to Your Project

## What to Add

Add the following dependency to your `pom.xml`, replacing `VERSION` with the latest version from Maven Central (`org.tabletest:tabletest-junit`):

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

## What to Watch Out For: JUnit Version Requirement

Your current JUnit Jupiter version is **5.10.0**. TableTest requires **JUnit Jupiter 5.11 or higher**.

You need to upgrade your existing dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Use the latest 5.11+ version available — check Maven Central for the current release.

## Imports

In your test classes, add:

```java
import org.tabletest.junit.TableTest;
```

Add these only when needed:

```java
import org.tabletest.junit.Scenario;         // when binding the scenario column to a parameter
import org.tabletest.junit.TypeConverter;    // when writing custom converter methods
import org.tabletest.junit.TypeConverterSources; // when referencing shared converter sources
```

## Summary

Two things to do:
1. Add `org.tabletest:tabletest-junit` as a test dependency.
2. Upgrade `junit-jupiter` from `5.10.0` to `5.11` or higher — TableTest requires it.
