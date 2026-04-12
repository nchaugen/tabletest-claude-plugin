Add the TableTest dependency:

```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**Watch out**: Your JUnit Jupiter version (`5.10.0`) is too old. TableTest requires **5.11 or higher**. You'll need to upgrade:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

Do both changes together, otherwise TableTest won't work.