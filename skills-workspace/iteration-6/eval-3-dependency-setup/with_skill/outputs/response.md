Add this to your `pom.xml`:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Check [Maven Central](https://search.maven.org/search?q=g:org.tabletest+a:tabletest-junit) for the latest version and substitute it in.

**One thing to watch out for:** Your current JUnit Jupiter version is **5.10.0**, but TableTest requires **5.11 or higher**. You'll need to bump it:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

The import you'll use in test files is:

```java
import org.tabletest.junit.TableTest;
```