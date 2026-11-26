import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// Fabric limits: cotton 90 °C, synthetics 60 °C, wool 40 °C. The temperature is whole degrees,
    /// so the straddling pairs are one degree apart. A refusal takes precedence over a delayed start.
    /// The rows at the limit also show the wash time; its rules have their own table.
    @TableTest("""
        Scenario                     | Fabric     | Temperature | Delay Start (Hours) | Outcome?
        Cotton at its limit          | cotton     | 90          |                     | runs(minutes: 170)
        Cotton just past its limit   | cotton     | 91          |                     | tooHot(limit: 90)
        Synthetics at their limit    | synthetics | 60          |                     | runs(minutes: 110)
        Synthetics just past limit   | synthetics | 61          |                     | tooHot(limit: 60)
        Wool at its limit            | wool       | 40          |                     | runs(minutes: 50)
        Wool just past its limit     | wool       | 41          |                     | tooHot(limit: 40)
        Too hot with a delayed start | cotton     | 91          | 3                   | tooHot(limit: 90)
        """)
    func refusesATemperatureAboveTheFabricLimit(
        fabric: Fabric, temperature: Int, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// Base time at 40 °C: cotton 120, synthetics 90, wool 50 minutes. Every full 10 °C above 40 adds
    /// 10 minutes; assumed from the code: a partial step adds nothing and a lower temperature does not
    /// shorten the wash. The surcharge and the options are the same for every fabric. No delayed start.
    @TableTest("""
        Scenario                           | Fabric     | Temperature | Prewash | Extra Rinse | Outcome?
        Cotton at the standard temperature | cotton     | 40          | false   | false       | runs(minutes: 120)
        Synthetics at standard             | synthetics | 40          | false   | false       | runs(minutes: 90)
        Wool at standard                   | wool       | 40          | false   | false       | runs(minutes: 50)
        Cotton below the standard          | cotton     | 30          | false   | false       | runs(minutes: 120)
        Cotton just under the first step   | cotton     | 49          | false   | false       | runs(minutes: 120)
        Cotton first 10 degree step        | cotton     | 50          | false   | false       | runs(minutes: 130)
        Cotton second 10 degree step       | cotton     | 60          | false   | false       | runs(minutes: 140)
        Prewash only                       | cotton     | 40          | true    | false       | runs(minutes: 140)
        Extra rinse only                   | cotton     | 40          | false   | true        | runs(minutes: 135)
        Prewash and extra rinse            | cotton     | 40          | true    | true        | runs(minutes: 155)
        Hot synthetics with both options   | synthetics | 60          | true    | true        | runs(minutes: 145)
        """)
    func addsWashTimeForTemperatureAndOptions(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// A delayed start schedules the wash and leaves its time unchanged. A zero-hour delay is not
    /// specified, so no row covers it.
    @TableTest("""
        Scenario                     | Fabric | Prewash | Delay Start (Hours) | Outcome?
        Start now                    | cotton | false   |                     | runs(minutes: 120)
        Start delayed                | cotton | false   | 3                   | scheduled(inHours: 3, minutes: 120)
        Start delayed with a prewash | cotton | true    | 3                   | scheduled(inHours: 3, minutes: 140)
        """)
    func schedulesADelayedStart(fabric: Fabric, prewash: Bool, delayStartHours: Int?, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, prewash: prewash, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
