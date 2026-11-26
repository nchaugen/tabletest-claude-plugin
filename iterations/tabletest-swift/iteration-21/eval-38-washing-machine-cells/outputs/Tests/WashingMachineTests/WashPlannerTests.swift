import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// Temperatures are in °C and durations in minutes. Prewash and extra rinse are off and the
    /// wash starts now, unless a column says otherwise.
    ///
    /// Assumption: the surcharge counts whole 10 °C steps above 40, so 49 °C adds nothing and
    /// 50 °C adds 10; a wash below 40 °C takes the 40 °C time.
    @TableTest("""
        Scenario                          | Fabric     | Temperature | Outcome?
        Cotton below the standard setting | cotton     | 30          | runs(minutes: 120)
        Cotton before the first step      | cotton     | {40, 49}    | runs(minutes: 120)
        Cotton in the first step          | cotton     | {50, 59}    | runs(minutes: 130)
        Cotton in the second step         | cotton     | {60, 69}    | runs(minutes: 140)
        Cotton in the third step          | cotton     | {70, 79}    | runs(minutes: 150)
        Cotton in the fourth step         | cotton     | {80, 89}    | runs(minutes: 160)
        Synthetics before the first step  | synthetics | {40, 49}    | runs(minutes: 90)
        Synthetics in the first step      | synthetics | 50          | runs(minutes: 100)
        Wool at its only setting          | wool       | 40          | runs(minutes: 50)
        """)
    func timesAWashFromFabricAndTemperature(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// The limit differs per fabric, so each fabric has its own pair of rows around it.
    @TableTest("""
        Scenario                          | Fabric     | Temperature | Outcome?
        Cotton at its hottest setting     | cotton     | 90          | runs(minutes: 170)
        Cotton one degree hotter          | cotton     | 91          | tooHot(limit: 90)
        Synthetics at its hottest setting | synthetics | 60          | runs(minutes: 110)
        Synthetics one degree hotter      | synthetics | 61          | tooHot(limit: 60)
        Wool at its hottest setting       | wool       | 40          | runs(minutes: 50)
        Wool one degree hotter            | wool       | 41          | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// Held fixed: cotton at 40 °C, starting now.
    @TableTest("""
        Scenario                 | Fabric | Temperature | Prewash | Extra Rinse | Outcome?
        Neither option           | cotton | 40          | false   | false       | runs(minutes: 120)
        Prewash only             | cotton | 40          | true    | false       | runs(minutes: 140)
        Extra rinse only         | cotton | 40          | false   | true        | runs(minutes: 135)
        Prewash with extra rinse | cotton | 40          | true    | true        | runs(minutes: 155)
        """)
    func addsTimeForPrewashAndExtraRinse(fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    @TableTest("""
        Scenario                         | Fabric | Temperature | Prewash | Extra Rinse | Delay Start (hours) | Outcome?
        No delay requested               | cotton | 40          | false   | false       |                     | runs(minutes: 120)
        Delay requested                  | cotton | 40          | false   | false       | 6                   | scheduled(inHours: 6, minutes: 120)
        Delay with both options          | wool   | 40          | true    | true        | 12                  | scheduled(inHours: 12, minutes: 85)
        Delay with a refused temperature | cotton | 91          | false   | false       | 6                   | tooHot(limit: 90)
        """)
    func schedulesADelayedWashWithoutChangingItsDuration(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash,
                                  extraRinse: extraRinse, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
