Two things to do:

**1. Add the TableTest dependency:**
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**2. Upgrade JUnit Jupiter — this is the key thing to watch out for.**

TableTest requires JUnit Jupiter **5.11 or higher**. Your current `5.10.0` won't work. Bump it:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Once both are in place, the imports you'll need:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Scenario;         // only if binding the scenario column to a parameter
import org.tabletest.junit.TypeConverter;     // only if writing custom type converters
import org.tabletest.junit.TypeConverterSources; // only if sharing converters across test classes
```