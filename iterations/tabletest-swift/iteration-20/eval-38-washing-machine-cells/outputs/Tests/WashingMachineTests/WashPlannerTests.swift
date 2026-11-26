import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// Every fabric has its own limit, so each gets its own pair of rows around it. Other options
    /// are off and there is no delay, so an accepted program runs at once. The duration is
    /// not observable apart from the decision, so both are verified together (see the duration tables).
    @TableTest("""
        Scenario                      | Fabric     | Temperature (°C) | Outcome?
        Cotton at its limit           | cotton     | 90               | runs(minutes: 170)
        Cotton one degree past it     | cotton     | 91               | tooHot(limit: 90)
        Synthetics at their limit     | synthetics | 60               | runs(minutes: 110)
        Synthetics one degree past it | synthetics | 61               | tooHot(limit: 60)
        Wool at its limit             | wool       | 40               | runs(minutes: 50)
        Wool one degree past it       | wool       | 41               | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Whole 10 °C steps above 40 °C add 10 minutes each (assumed: a part step adds nothing, and
    /// below 40 °C the wash is not shortened). Prewash and extra rinse are off; no delay.
    @TableTest("""
        Scenario                         | Fabric     | Temperature (°C) | Outcome?
        Cotton at 40                     | cotton     | 40               | runs(minutes: 120)
        Synthetics at 40                 | synthetics | 40               | runs(minutes: 90)
        Wool below 40                    | wool       | 30               | runs(minutes: 50)
        Cotton just below 40             | cotton     | 39               | runs(minutes: 120)
        Cotton part way to the next step | cotton     | 49               | runs(minutes: 120)
        Cotton one step above 40         | cotton     | 50               | runs(minutes: 130)
        Cotton two steps above 40        | cotton     | 60               | runs(minutes: 140)
        Synthetics one step above 40     | synthetics | 50               | runs(minutes: 100)
        """)
    func lengthensTheWashForEachTenDegreesAboveForty(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Prewash and extra rinse each add their own time, and add up when both are chosen.
    /// Held fixed: no delay.
    @TableTest("""
        Scenario                       | Fabric | Temperature (°C) | Prewash | Extra Rinse | Outcome?
        Neither option                 | cotton | 40               | false   | false       | runs(minutes: 120)
        Prewash alone                  | cotton | 40               | true    | false       | runs(minutes: 140)
        Extra rinse alone              | cotton | 40               | false   | true        | runs(minutes: 135)
        Both options                   | cotton | 40               | true    | true        | runs(minutes: 155)
        Both options on another fabric | wool   | 40               | true    | true        | runs(minutes: 85)
        """)
    func addsTimeForPrewashAndExtraRinse(fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool,
                                         outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// A delay schedules the wash without changing its duration, and a refusal wins over a delay.
    /// Open question: a delay of 0 hours is not pinned, as the rules do not say whether it counts as delayed.
    @TableTest("""
        Scenario                           | Fabric     | Temperature (°C) | Prewash | Extra Rinse | Delay Start (hours) | Outcome?
        No delay                           | cotton     | 40               | false   | false       |                     | runs(minutes: 120)
        Delayed plain wash                 | cotton     | 40               | false   | false       | 6                   | scheduled(inHours: 6, minutes: 120)
        Long wash without a delay          | synthetics | 60               | true    | true        |                     | runs(minutes: 145)
        Same long wash, delayed            | synthetics | 60               | true    | true        | 3                   | scheduled(inHours: 3, minutes: 145)
        Too hot for the fabric and delayed | wool       | 60               | false   | false       | 3                   | tooHot(limit: 40)
        """)
    func schedulesADelayedStartWithoutChangingTheWash(fabric: Fabric, temperature: Int, prewash: Bool,
                                                      extraRinse: Bool, delayStartHours: Int?,
                                                      outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash,
                                  extraRinse: extraRinse, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
