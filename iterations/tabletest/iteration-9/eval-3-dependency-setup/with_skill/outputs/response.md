Add this to your `pom.xml`:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Check Maven Central for the latest version and replace `VERSION`.

**One thing to watch out for**: TableTest requires JUnit Jupiter **5.11 or higher**, and you currently have **5.10.0**. You'll need to bump that:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

The import you'll use in test files is `import org.tabletest.junit.TableTest;`.