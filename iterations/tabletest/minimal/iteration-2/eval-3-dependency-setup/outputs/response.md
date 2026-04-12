Two things to address:

**1. Add the TableTest dependency**

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**2. Upgrade JUnit Jupiter — this is the catch**

Your current version (`5.10.0`) is below the minimum required (`5.11+`). Bump it:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Replace `VERSION` with the latest `tabletest-junit` release. Once both are in place, you can import:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Scenario;         // only if binding scenario column
import org.tabletest.junit.TypeConverter;     // only for custom converter methods
import org.tabletest.junit.TypeConverterSources; // only for shared converters
```