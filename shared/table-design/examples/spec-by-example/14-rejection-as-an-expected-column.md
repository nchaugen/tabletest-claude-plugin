One rule — the whole table asks whether the registration is accepted:

| Scenario              | Name  | Email           | Valid? | Rejection Reason?  |
|-----------------------|-------|-----------------|--------|--------------------|
| Complete registration | Ada   | ada@example.com | yes    |                    |
| Missing name          |       | ada@example.com | no     | Name is required   |
| Email without @       | Ada   | ada.example.com | no     | Email is malformed |

`Rejection Reason?` is blank where nothing is rejected — the same column, not a second table.
