Add the TableTest dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**Watch out: your JUnit version needs upgrading.** TableTest requires JUnit Jupiter **5.11 or higher**, but you currently have `5.10.0`. Update your existing dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Then import as needed:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Scenario;           // only when binding scenario column
import org.tabletest.junit.TypeConverter;      // only for custom converter methods
import org.tabletest.junit.TypeConverterSources; // only for shared converter sources
```