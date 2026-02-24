# Dependency and Import Setup

Prefer the version already used in the project. If adding a new dependency, check Maven Central and replace `VERSION`.

## Maven

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

## Gradle

```groovy
testImplementation "org.tabletest:tabletest-junit:VERSION"
```

## Imports

```java
import org.tabletest.junit.TableTest;
```

Import `@Scenario` only when binding the scenario column:
```java
import org.tabletest.junit.Scenario;
```

Import `@TypeConverter` for custom converter methods:
```java
import org.tabletest.junit.TypeConverter;
```

Import `@TypeConverterSources` for shared converter sources:
```java
import org.tabletest.junit.TypeConverterSources;
```
