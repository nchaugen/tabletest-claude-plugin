Add this dependency to your `pom.xml` (check Maven Central for the latest version):

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**One thing to watch out for**: The skill's pre-check requires JUnit Jupiter **5.11 or higher**, but you're on **5.10.0**. You'll need to bump that:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

The import you'll use in test files:

```java
import org.tabletest.junit.TableTest;
```