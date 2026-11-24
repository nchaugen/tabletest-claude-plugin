import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// Every fabric has its own temperature limit, so each limit is straddled in its own rows.
    /// The accepted rows run with no prewash, no extra rinse and no delayed start.
    @TableTest("""
        Scenario                | Fabric     | Temperature (°C) | Outcome?
        Cotton at its limit     | cotton     | 90               | runs(minutes: 170)
        Cotton just past        | cotton     | 91               | tooHot(limit: 90)
        Synthetics at its limit | synthetics | 60               | runs(minutes: 110)
        Synthetics just past    | synthetics | 61               | tooHot(limit: 60)
        Wool at its limit       | wool       | 40               | runs(minutes: 50)
        Wool just past          | wool       | 41               | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// Washes start now. Assumption: only each full 10 °C above 40 adds time, so 49 °C adds nothing
    /// and 50 °C adds 10 minutes; temperatures below 40 °C do not shorten the wash.
    @TableTest("""
        Scenario                       | Fabric     | Temperature (°C) | Prewash | Extra Rinse | Outcome?
        Cotton at the base temperature | cotton     | 40               | false   | false       | runs(minutes: 120)
        Synthetics at base temperature | synthetics | 40               | false   | false       | runs(minutes: 90)
        Wool at the base temperature   | wool       | 40               | false   | false       | runs(minutes: 50)
        Cooler than the base           | cotton     | 30               | false   | false       | runs(minutes: 120)
        Short of the next 10 °C step   | cotton     | 49               | false   | false       | runs(minutes: 120)
        First full 10 °C step          | cotton     | 50               | false   | false       | runs(minutes: 130)
        Several 10 °C steps            | cotton     | 70               | false   | false       | runs(minutes: 150)
        Prewash only                   | cotton     | 40               | true    | false       | runs(minutes: 140)
        Extra rinse only               | cotton     | 40               | false   | true        | runs(minutes: 135)
        Prewash, rinse and heat        | cotton     | 60               | true    | true        | runs(minutes: 175)
        """)
    func addsTimeForHeatPrewashAndExtraRinse(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// Assumption: a delay of 0 hours is still a scheduled wash, since only a missing delay starts now.
    @TableTest("""
        Scenario                       | Fabric | Temperature (°C) | Delay Start (hours) | Outcome?
        No delay chosen                | cotton | 40               |                     | runs(minutes: 120)
        Delay of several hours         | cotton | 40               | 8                   | scheduled(inHours: 8, minutes: 120)
        Delay of zero hours            | cotton | 40               | 0                   | scheduled(inHours: 0, minutes: 120)
        Delay on a hotter wash         | cotton | 60               | 8                   | scheduled(inHours: 8, minutes: 140)
        Delay on a refused temperature | wool   | 60               | 8                   | tooHot(limit: 40)
        """)
    func schedulesAWashWhenAStartDelayIsChosen(
        fabric: Fabric, temperature: Int, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
