```javascript
// Test 1 — the classification
test.each`
  dutyHours | normalHours | extendedHours
  ${8}      | ${8}        | ${0}
  ${13}     | ${13}       | ${0}
  ${14}     | ${13}       | ${1}
`('$dutyHours duty hours divide into normal and extended', ({ dutyHours, normalHours, extendedHours }) => {
  expect(splitDutyHours(dutyHours)).toEqual({ normalHours, extendedHours });
});

// Test 2 — the arithmetic, taking the classification as input
test.each`
  normalHours | extendedHours | restCredit
  ${13}       | ${0}          | ${13.0}
  ${13}       | ${1}          | ${15.0}
`('rest credit for $normalHours normal and $extendedHours extended', ({ normalHours, extendedHours, restCredit }) => {
  expect(restCreditFor(normalHours, extendedHours)).toBe(restCredit);
});
```
