Add the `tabletest-junit` dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**One thing to watch out for:** Your JUnit Jupiter version is `5.10.0`, but TableTest requires **5.11 or higher**. You'll need to upgrade:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Replace `VERSION` with the latest `tabletest-junit` release — check Maven Central for the current version.