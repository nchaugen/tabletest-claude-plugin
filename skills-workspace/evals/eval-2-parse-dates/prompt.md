I want to write a TableTest for a method that parses date strings. The method signature is:

```java
LocalDate parseDate(String input)
```

I want to test: ISO format (2024-01-15), slash format (15/01/2024), short year (24-01-15), null input should return null, and empty string should throw IllegalArgumentException.
