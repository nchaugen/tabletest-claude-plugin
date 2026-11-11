import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// Each fabric has its own temperature limit, so each has its own pair straddling it. The
    /// temperature is a whole multiple of 10 °C throughout. Rows at the limit show the wash is
    /// accepted; their minutes are covered by the wash time tables.
    @TableTest("""
        Scenario                   | Fabric     | Temperature (°C) | Outcome?
        Cotton at its limit        | cotton     | 90               | runs(minutes: 170)
        Cotton above its limit     | cotton     | 100              | tooHot(limit: 90)
        Synthetics at its limit    | synthetics | 60               | runs(minutes: 110)
        Synthetics above its limit | synthetics | 70               | tooHot(limit: 60)
        Wool at its limit          | wool       | 40               | runs(minutes: 50)
        Wool above its limit       | wool       | 50               | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// Prewash, extra rinse and delayed start are off; the wash starts now.
    /// Base time is for 40 °C; each 10 °C above adds 10 minutes, and a colder wash takes no less.
    @TableTest("""
        Scenario                     | Fabric     | Temperature (°C) | Outcome?
        Cotton colder than 40        | cotton     | 30               | runs(minutes: 120)
        Cotton at 40                 | cotton     | 40               | runs(minutes: 120)
        Cotton 10 above 40           | cotton     | 50               | runs(minutes: 130)
        Cotton 20 above 40           | cotton     | 60               | runs(minutes: 140)
        Cotton 30 above 40           | cotton     | 70               | runs(minutes: 150)
        Synthetics at 40             | synthetics | 40               | runs(minutes: 90)
        Synthetics 20 above 40       | synthetics | 60               | runs(minutes: 110)
        Wool colder than 40          | wool       | 30               | runs(minutes: 50)
        Wool at 40                   | wool       | 40               | runs(minutes: 50)
        """)
    func timesAWashFromFabricAndTemperature(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// The washes are at 40 °C and start now. Prewash and extra rinse each add their own time.
    @TableTest("""
        Scenario              | Fabric | Prewash | Extra Rinse | Outcome?
        Neither option        | cotton | false   | false       | runs(minutes: 120)
        Prewash only          | cotton | true    | false       | runs(minutes: 140)
        Extra rinse only      | cotton | false   | true        | runs(minutes: 135)
        Both options          | cotton | true    | true        | runs(minutes: 155)
        Both options on wool  | wool   | true    | true        | runs(minutes: 85)
        """)
    func addsTimeForPrewashAndExtraRinse(fabric: Fabric, prewash: Bool, extraRinse: Bool, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// A delayed start keeps the wash time of the same program. The refusal row shows that a
    /// temperature above the limit is refused rather than scheduled.
    @TableTest("""
        Scenario                    | Fabric | Temperature (°C) | Prewash | Delay Start (hours) | Outcome?
        Start now                   | cotton | 40               | true    |                     | runs(minutes: 140)
        Short delay                 | cotton | 40               | true    | 3                   | scheduled(inHours: 3, minutes: 140)
        Long delay                  | cotton | 40               | true    | 12                  | scheduled(inHours: 12, minutes: 140)
        Delay on a different fabric| wool   | 40               | false   | 3                   | scheduled(inHours: 3, minutes: 50)
        Delay above the limit       | wool   | 50               | false   | 3                   | tooHot(limit: 40)
        """)
    func schedulesADelayedStartWithTheSameWashTime(
        fabric: Fabric, temperature: Int, prewash: Bool, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash,
                                  delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
