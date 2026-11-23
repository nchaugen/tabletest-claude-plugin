import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// Each fabric has its own limit, so each one straddles its own boundary: the limit itself runs,
    /// one degree above is refused. Starts now, no prewash, no extra rinse.
    @TableTest("""
        Scenario                  | Fabric     | Temperature (°C) | Outcome?
        Cotton at its limit       | cotton     | 90               | runs(minutes: 170)
        Cotton past its limit     | cotton     | 91               | tooHot(limit: 90)
        Synthetics at its limit   | synthetics | 60               | runs(minutes: 110)
        Synthetics past its limit | synthetics | 61               | tooHot(limit: 60)
        Wool at its limit         | wool       | 40               | runs(minutes: 50)
        Wool past its limit       | wool       | 41               | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Starts now, no prewash, no extra rinse. A temperature that is not a whole 10 °C above 40
    /// (such as 45) is not specified, so it is not tested.
    @TableTest("""
        Scenario                  | Fabric     | Temperature (°C) | Outcome?
        Cotton at 40 °C           | cotton     | 40               | runs(minutes: 120)
        Synthetics at 40 °C       | synthetics | 40               | runs(minutes: 90)
        Wool at 40 °C             | wool       | 40               | runs(minutes: 50)
        Cotton below 40 °C        | cotton     | 30               | runs(minutes: 120)
        Cotton 10 °C above 40     | cotton     | 50               | runs(minutes: 130)
        Cotton 20 °C above 40     | cotton     | 60               | runs(minutes: 140)
        Cotton 30 °C above 40     | cotton     | 70               | runs(minutes: 150)
        Synthetics 20 °C above 40 | synthetics | 60               | runs(minutes: 110)
        """)
    func timesAWashFromFabricAndTemperature(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Cotton at 40 °C, starts now.
    @TableTest("""
        Scenario                | Prewash | Extra Rinse | Outcome?
        Neither option          | false   | false       | runs(minutes: 120)
        Prewash only            | true    | false       | runs(minutes: 140)
        Extra rinse only        | false   | true        | runs(minutes: 135)
        Prewash and extra rinse | true    | true        | runs(minutes: 155)
        """)
    func addsTimeForPrewashAndExtraRinse(prewash: Bool, extraRinse: Bool, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: .cotton, prewash: prewash, extraRinse: extraRinse)) == outcome)
    }

    /// A delay of 0 hours is not specified, so it is not tested. A blank delay starts now.
    @TableTest("""
        Scenario                       | Fabric | Temperature (°C) | Prewash | Extra Rinse | Delay Start (hours) | Outcome?
        Starts now                     | cotton | 60               | true    | true        |                     | runs(minutes: 175)
        Delayed three hours            | cotton | 60               | true    | true        | 3                   | scheduled(inHours: 3, minutes: 175)
        Delayed twelve hours           | cotton | 60               | true    | true        | 12                  | scheduled(inHours: 12, minutes: 175)
        Delayed, over the fabric limit | wool   | 60               | false   | false       | 3                   | tooHot(limit: 40)
        """)
    func schedulesADelayedWashWithTheSameDuration(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash,
                                  extraRinse: extraRinse, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
