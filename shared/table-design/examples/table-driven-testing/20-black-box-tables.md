```javascript
test.each`
  humidity | temp  | vent
  ${80}    | ${28} | ${'OPEN'}
  ${55}    | ${21} | ${'CLOSED'}
`('humidity $humidity at $temp C sets the vent $vent', ({ humidity, temp, vent }) => {
  expect(new ClimateController().respond(humidity, temp).vent).toBe(vent);
});
```
