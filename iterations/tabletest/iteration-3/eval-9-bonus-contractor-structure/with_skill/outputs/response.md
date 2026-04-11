# TableTest for calculateBonus

## Recognising the structural issue

The four employee rules form a clean orthogonal grid — two levels (SENIOR, JUNIOR) × two departments (SALES, ENGINEERING). But CONTRACTOR breaks that structure: the department is irrelevant for contractors, so there is no meaningful department value to put in a shared column.

Stuffing CONTRACTOR into the same table would require a dummy department entry (e.g. `ANY`, `N/A`, or an arbitrary real department) just to satisfy the column structure. That misleads the reader — it implies department matters when it doesn't.

The clean solution is two separate `@TableTest` methods: one for the level×department grid, and one dedicated to CONTRACTOR.

---

## Table 1 — Level × Department grid

```java
@TableTest("""
    Scenario                   | Level   | Department  | Bonus%?
    Senior sales bonus         | SENIOR  | SALES       | 0.15
    Senior engineering bonus   | SENIOR  | ENGINEERING | 0.12
    Junior sales bonus         | JUNIOR  | SALES       | 0.08
    Junior engineering bonus   | JUNIOR  | ENGINEERING | 0.05
    """)
void shouldCalculateBonusByLevelAndDepartment(Level level, Department department, double expectedBonus) {
    Employee employee = new Employee(level, department);
    assertEquals(expectedBonus, calculateBonus(employee), 0.001);
}
```

## Table 2 — Contractor (department irrelevant)

Using a value set `{SALES, ENGINEERING}` makes the "regardless of department" rule explicit — the table expands into two test runs, both asserting 0.0:

```java
@TableTest("""
    Scenario                 | Department           | Bonus%?
    Contractor gets no bonus | {SALES, ENGINEERING} | 0.0
    """)
void contractorAlwaysGetsZeroBonus(Department department, double expectedBonus) {
    Employee employee = new Employee(Level.CONTRACTOR, department);
    assertEquals(expectedBonus, calculateBonus(employee), 0.001);
}
```

---

## Why this design

- **Table 1** has a uniform structure: every row is a level×department combination, and every cell is meaningful.
- **Table 2** uses a value set to document the "regardless of" relationship explicitly rather than picking an arbitrary department. The structural asymmetry between CONTRACTOR and the other levels is now visible in the table design itself, not hidden behind a dummy cell value.
- Neither method contains `if` or `switch` — the tables are pure data, the methods are pure assertion.
