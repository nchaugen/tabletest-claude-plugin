| Scenario                | Days Since Last Donation | Min Interval (Policy) | Eligible? |
|-------------------------|--------------------------|-----------------------|-----------|
| Exactly at the interval | 90                       | 90                    | yes       |
| One day short           | 89                       | 90                    | no        |

Without `Min Interval (Policy)`, 90 is invisible and no reader can tell whether the boundary is
inclusive.
