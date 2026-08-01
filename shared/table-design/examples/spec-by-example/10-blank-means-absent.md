| Scenario                  | Humidity % | Override Setpoint | Vent Position? |
|---------------------------|------------|-------------------|----------------|
| No override configured    | 80         |                   | OPEN           |
| Override supplied         | 80         | 90                | CLOSED         |
| Override cleared to empty | 80         | `''`              | OPEN           |

The blank row specifies what the controller does with *no* override. Writing `0` there would specify
something else.
