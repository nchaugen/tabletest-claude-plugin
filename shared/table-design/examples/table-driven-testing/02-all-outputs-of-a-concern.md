```swift
@Test("Climate response by humidity and temperature", arguments: [
    (humidity: 80, temp: 28, vent: Vent.open,   heater: false, alert: String?.none),
    (humidity: 80, temp: 8,  vent: Vent.closed, heater: true,  alert: "Condensation risk"),
    (humidity: 55, temp: 21, vent: Vent.closed, heater: false, alert: String?.none),
])
func climateResponse(humidity: Int, temp: Int, vent: Vent, heater: Bool, alert: String?) {
    let response = ClimateController().respond(humidity: humidity, temp: temp)
    #expect(response.vent == vent)
    #expect(response.heaterOn == heater)
    #expect(response.alert == alert)
}
```
