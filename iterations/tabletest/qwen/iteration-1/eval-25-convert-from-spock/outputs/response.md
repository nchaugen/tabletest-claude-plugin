**Conversion complete.** All 17 test scenarios pass (0 failures) with a clean build after removing Spock.

### What changed:

**`ShippingCostCalculatorSpec.groovy`:**
| Before (Spock) | After (TableTest) |
|---|---|
| `@Unroll` + `where:` block | `@TableTest("""...""")` annotation |
| `new ShippingZone()` + setter calls per row | Same setup logic, now called once with bound parameters |
| `if (fragile) opts.setFragile(true)` inline | `Boolean fragile` parameter, null check in method body |
| Spock matchers (`==`) | JUnit `assertEquals(expected.stripTrailingZeros(), actual.stripTrailingZeros())` |
| `import spock.lang.Specification`, `spock.lang.Unroll` | `import org.tabletest.junit.TableTest` only |

**`build.gradle.kts`:**
- Added `org.tabletest:tabletest-junit:1.2.1` dependency
- Removed `org.spockframework:spock-core:2.4-groovy-5.0` (no longer used)