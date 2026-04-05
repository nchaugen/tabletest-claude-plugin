Write a TableTest for this method:

```java
Money parse(String input)
```

The method should:
- Return a Money object for valid inputs like '10.00' and '0.01'
- Return null for null input
- Throw IllegalArgumentException for: empty string, letters-only input (e.g. 'abc'), and negative values (e.g. '-5.00')
