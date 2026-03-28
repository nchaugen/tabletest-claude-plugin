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

Check Maven Central for the latest version and replace `VERSION`.

**2. Upgrade JUnit Jupiter to 5.11+**

Your current version is `5.10.0`, but TableTest requires **JUnit Jupiter 5.11 or higher**. Update your existing dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

That's the main watch-out — `5.10.0` won't work with TableTest.