# Adding TableTest to Your Project

## Watch out: JUnit version

Your current JUnit version is **5.10.0**, but **TableTest requires JUnit 5.11 or higher**. You need to upgrade before TableTest will work. Update your existing JUnit dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

(Or use a later 5.11.x / 5.12.x release — check Maven Central for the latest.)

## Add the TableTest dependency

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Replace `VERSION` with the current release from Maven Central.

## Imports

In your test classes, import the annotation:

```java
import org.tabletest.junit.TableTest;
```

And optionally, if you need custom type converters or shared converter sources:

```java
import org.tabletest.junit.TypeConverter;
import org.tabletest.junit.TypeConverterSources;
```

## Summary of changes

1. **Upgrade** `junit-jupiter` from `5.10.0` to `5.11.0` or later (required — TableTest will not work on 5.10)
2. **Add** `org.tabletest:tabletest-junit` with `<scope>test</scope>`
3. No other configuration is needed — TableTest integrates with the standard JUnit 5 test runner
