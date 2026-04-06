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

**One thing to watch out for**: TableTest requires JUnit Jupiter **5.11 or higher**. You're currently on `5.10.0`, so you'll need to bump that too:

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