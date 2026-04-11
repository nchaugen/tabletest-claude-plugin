```java
@ParameterizedTest
@MethodSource("permissionCases")
void testCanPerform(Role role, Action action, boolean expected) {
    assertThat(canPerform(role, action)).isEqualTo(expected);
}

static Stream<Arguments> permissionCases() {
    return Stream.of(
        // ADMIN can perform any action
        arguments(ADMIN, READ,   true),
        arguments(ADMIN, WRITE,  true),
        arguments(ADMIN, DELETE, true),

        // USER can READ and WRITE but not DELETE
        arguments(USER,  READ,   true),
        arguments(USER,  WRITE,  true),
        arguments(USER,  DELETE, false),

        // GUEST can only READ
        arguments(GUEST, READ,   true),
        arguments(GUEST, WRITE,  false),
        arguments(GUEST, DELETE, false)
    );
}
```

Key points:
- `@MethodSource` keeps data close to the test and allows any type as a parameter (unlike `@CsvSource`)
- Each row covers exactly one `(role, action)` pair — no combinatorial shortcuts that would hide missing cases
- All 9 combinations of 3 roles × 3 actions are explicit, so adding a new `Action` or `Role` enum value will visibly break coverage

Required imports:
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.assertj.core.api.Assertions.assertThat;
```