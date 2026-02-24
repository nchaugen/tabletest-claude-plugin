# Maps for Composite Data

**It depends.** Both maps and separate columns have valid use cases. Choose based on your specific scenario:

## Use Maps When:

### 1. Not all properties are relevant for all scenarios

```java
@TableTest("""
    Scenario          | Request Config                    | Expected?
    Basic request     | [method: GET]                     | OK
    With timeout      | [method: POST, timeout: 5000]     | OK
    With auth         | [method: GET, auth: Bearer xyz]   | OK
    Full config       | [method: POST, timeout: 5000, auth: Bearer xyz, retry: 3] | OK
    """)
void testRequest(Map<String, String> config, String expected) {
    // Factory method provides sensible defaults for missing keys
    RequestConfig request = buildRequest(config);
    assertEquals(expected, process(request));
}

private RequestConfig buildRequest(Map<String, String> config) {
    return new RequestConfig(
        config.getOrDefault("method", "GET"),
        parseInt(config.getOrDefault("timeout", "3000")),
        config.get("auth"),  // null if not present
        parseInt(config.getOrDefault("retry", "1"))
    );
}
```

With separate columns, you'd have 4 columns with many blank cells, which is harder to read.

### 2. Properties form one indivisible concept

```java
@TableTest("""
    Scenario     | Credentials           | Expected?
    Valid user   | [user: alice, pw: 123]| OK
    Wrong pw     | [user: alice, pw: 456]| DENIED
    """)
```

### 3. Need to pass the map directly to the system

```java
void testValidation(Map<String, String> body, int status) {
    assertEquals(status, api.validate(body));  // API accepts Map
}
```

## Use Separate Columns When:

### 1. All scenarios use the same properties

```java
@TableTest("""
    Scenario     | Status | ms | Expected?
    Fast         | OK     | 10 | PASS
    Slow         | OK     | 100| PASS
    Error        | ERROR  | 10 | FAIL
    """)
void test(String status, Long ms, String expected) { ... }
```

Every row has status and ms, so separate columns are clearer.

### 2. Values need to align vertically for comparison

```java
// Easy to compare timing values vertically
| MDC ms | Legacy ms |
| 10     | 100       |
| 100    | 10        |
| 10     | 10        |
```

### 3. Properties are independent inputs

```java
| Build Dir | JUnit Dir | Configured Dir |  // Three independent inputs
```

## Trade-offs

- Maps: Better when properties vary by scenario, allow defaults in factory methods
- Separate columns: Better when all scenarios have same structure, easier to scan vertically

See also `references/column-design.md` for comprehensive column design guidance.
