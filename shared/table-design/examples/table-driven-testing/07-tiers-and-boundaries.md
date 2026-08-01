```javascript
test.each`
  haemoglobin | band
  ${124}      | ${'DEFER'}
  ${125}      | ${'STANDARD'}
  ${159}      | ${'STANDARD'}
  ${160}      | ${'REVIEW'}
`('haemoglobin $haemoglobin falls in the $band band', ({ haemoglobin, band }) => {
  expect(donationBand(haemoglobin)).toBe(band);
});
```
