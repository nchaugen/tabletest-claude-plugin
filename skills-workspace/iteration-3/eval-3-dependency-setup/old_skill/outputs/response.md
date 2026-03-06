# Adding TableTest to Your Project

## Dependency to Add

Add the TableTest dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version><!-- check Maven Central for the latest version --></version>
    <scope>test</scope>
</dependency>
```

> **Note**: I am not certain of the exact coordinates above. The groupId `org.tabletest` and artifactId `tabletest-junit` are my best guess based on common naming conventions, but you should verify them on [Maven Central](https://search.maven.org) before adding them. Using the wrong coordinates will result in a dependency resolution failure.

## JUnit Version Compatibility

Your current JUnit version (`5.10.0`) **may be below the minimum required** by TableTest. Many TableTest libraries require JUnit 5.11 or later to take advantage of the parameterised test infrastructure improvements introduced in that release. I recommend upgrading your JUnit dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

## Things to Watch Out For

1. **JUnit version**: As noted above, upgrade to 5.11+ if TableTest requires it. Check the TableTest release notes or README for the stated minimum.

2. **Maven Surefire plugin version**: Make sure you are using Maven Surefire plugin 3.x (3.0.0 or later). Older versions do not fully support JUnit 5 test discovery. Add or update:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

3. **Dependency conflicts**: If you have other JUnit 5 modules pinned to specific versions (e.g. `junit-jupiter-api`, `junit-jupiter-engine` listed separately), make sure they are aligned with the version you upgrade to. Mismatched JUnit module versions cause subtle runtime failures.

4. **IDE support**: IntelliJ IDEA and Eclipse should pick up TableTest tests automatically once the dependency is on the classpath, but you may need to trigger a Maven reimport.
