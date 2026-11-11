import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// The refusal limit differs per fabric, so each fabric has its own pair around its limit. The
    /// accepted side shows the wash time, because the planner's outcome carries both.
    @TableTest("Refuses a temperature above the fabric's limit", table: """
        Scenario                | Fabric     | Temperature (°C) | Outcome?
        Cotton at its limit     | cotton     | 90               | runs(minutes: 170)
        Cotton just past        | cotton     | 91               | tooHot(limit: 90)
        Synthetics at its limit | synthetics | 60               | runs(minutes: 110)
        Synthetics just past    | synthetics | 61               | tooHot(limit: 60)
        Wool at its limit       | wool       | 40               | runs(minutes: 50)
        Wool just past          | wool       | 41               | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Assumptions: "each 10 °C above 40" counts full steps only (49 °C adds nothing), and a
    /// temperature below 40 °C adds nothing and takes the base time. The ladder repeats for every
    /// fabric, so its steps are pinned on cotton and the other fabrics get their base and one more row.
    @TableTest("Takes the wash time from the fabric and each full 10 °C above 40", table: """
        Scenario                  | Fabric     | Temperature (°C) | Outcome?
        Cotton below the base     | cotton     | 30               | runs(minutes: 120)
        Cotton at the base        | cotton     | 40               | runs(minutes: 120)
        Cotton part-way to a step | cotton     | 49               | runs(minutes: 120)
        Cotton first step         | cotton     | 50               | runs(minutes: 130)
        Cotton second step        | cotton     | 60               | runs(minutes: 140)
        Cotton third step         | cotton     | 70               | runs(minutes: 150)
        Synthetics at the base    | synthetics | 40               | runs(minutes: 90)
        Synthetics first step     | synthetics | 50               | runs(minutes: 100)
        Wool at the base          | wool       | 40               | runs(minutes: 50)
        """)
    func takesTheWashTimeFromTheFabricAndTemperature(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    @TableTest("Adds time for a prewash and an extra rinse", table: """
        Scenario                           | Fabric | Temperature (°C) | Prewash | Extra Rinse | Outcome?
        Neither option                     | cotton | 40               | false   | false       | runs(minutes: 120)
        Prewash only                       | cotton | 40               | true    | false       | runs(minutes: 140)
        Extra rinse only                   | cotton | 40               | false   | true        | runs(minutes: 135)
        Both options                       | cotton | 40               | true    | true        | runs(minutes: 155)
        Both options on another fabric     | wool   | 40               | true    | true        | runs(minutes: 85)
        Both options at a high temperature | cotton | 60               | true    | true        | runs(minutes: 175)
        """)
    func addsTimeForAPrewashAndAnExtraRinse(fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool,
                                            outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// A blank delay starts the wash now. Assumption: a refusal takes precedence over scheduling.
    @TableTest("Schedules a delayed start without changing the wash time", table: """
        Scenario                      | Fabric | Temperature (°C) | Prewash | Delay Start (hours) | Outcome?
        Start now                     | cotton | 40               | false   |                     | runs(minutes: 120)
        Delayed start                 | cotton | 40               | false   | 3                   | scheduled(inHours: 3, minutes: 120)
        Delayed start with extra time | cotton | 60               | true    | 8                   | scheduled(inHours: 8, minutes: 160)
        Delayed start, too hot        | wool   | 60               | false   | 2                   | tooHot(limit: 40)
        """)
    func schedulesADelayedStartWithTheSameWashTime(fabric: Fabric, temperature: Int, prewash: Bool,
                                                   delayStartHours: Int?, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash,
                                  delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
