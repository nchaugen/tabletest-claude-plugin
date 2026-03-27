Add this to your `pom.xml`:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Replace `VERSION` with the latest from Maven Central.

**One thing to watch out for**: your current JUnit Jupiter version is **5.10.0**, but TableTest requires **5.11 or higher**. You'll need to bump it:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Then import in your test files:

```java
import org.tabletest.junit.TableTest;
```