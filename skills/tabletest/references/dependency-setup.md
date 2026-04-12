# Dependency and Import Setup

Prefer the version already used in the project. If adding a new dependency, use the latest version shown below.

## Maven

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>1.2.1</version>
    <scope>test</scope>
</dependency>
```

## Gradle

```groovy
testImplementation "org.tabletest:tabletest-junit:1.2.1"
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
