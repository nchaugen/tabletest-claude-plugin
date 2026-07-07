I found the exact setup requirements in the project's TableTest skill reference. Here's what you need:

## Add the dependency

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>1.2.1</version>
    <scope>test</scope>
</dependency>
```

## Watch out for: JUnit Jupiter version

TableTest requires **JUnit Jupiter 5.11 or higher**. Your pom.xml currently pins `5.10.0`, which is below the minimum — you'll need to bump it:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>  <!-- or later -->
    <scope>test</scope>
</dependency>
```

(Pick whatever current 5.11.x/5.12.x you're comfortable with — just confirm it's ≥ 5.11.)

## Imports you'll use

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Description;          // only if adding descriptive text to a table
import org.tabletest.junit.Scenario;              // only if binding a scenario column
import org.tabletest.junit.TypeConverter;         // only for custom converter methods
import org.tabletest.junit.TypeConverterSources;  // only for shared converter sources
```

That's the full setup — no extra test engine registration or Gradle/Maven plugin config needed beyond the two dependency entries.