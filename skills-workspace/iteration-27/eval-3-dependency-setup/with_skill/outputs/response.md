Two things to do:

**1. Add the TableTest dependency**

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Replace `VERSION` with the latest from Maven Central.

**2. Upgrade JUnit Jupiter to 5.11+**

Your current version is 5.10.0, but TableTest requires **5.11 or higher**. Update your existing dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

That's it — no other changes needed. The import for your tests will be:

```java
import org.tabletest.junit.TableTest;
```