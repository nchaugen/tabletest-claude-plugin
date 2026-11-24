import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// Held fixed: no prewash, no extra rinse, and the wash starts now. The refused outcome carries
    /// the fabric's limit; an accepted wash shows its duration, which the duration tables below explain.
    /// Temperatures are whole degrees, so the pair around each limit is one degree apart.
    @TableTest("""
        Scenario                    | Fabric      | Temperature | Outcome?
        Cotton at its limit         | cotton      | 90          | runs(minutes: 170)
        Cotton just past its limit  | cotton      | 91          | tooHot(limit: 90)
        Synthetics at their limit   | synthetics  | 60          | runs(minutes: 110)
        Synthetics just past        | synthetics  | 61          | tooHot(limit: 60)
        Wool at its limit           | wool        | 40          | runs(minutes: 50)
        Wool just past its limit    | wool        | 41          | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Held fixed: no prewash, no extra rinse, and the wash starts now.
    /// Assumptions the rule text leaves open: a temperature below 40 °C adds no time, and only
    /// multiples of 10 °C are used, since the effect of a part-step above 40 is not specified.
    @TableTest("""
        Scenario                   | Fabric     | Temperature | Outcome?
        Cotton below the base      | cotton     | 30          | runs(minutes: 120)
        Cotton at the base         | cotton     | 40          | runs(minutes: 120)
        Cotton one step above      | cotton     | 50          | runs(minutes: 130)
        Cotton two steps above     | cotton     | 60          | runs(minutes: 140)
        Cotton three steps above   | cotton     | 70          | runs(minutes: 150)
        Synthetics at the base     | synthetics | 40          | runs(minutes: 90)
        Wool at the base           | wool       | 40          | runs(minutes: 50)
        """)
    func addsTenMinutesForEachTenDegreesAboveForty(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Held fixed: the wash starts now.
    @TableTest("""
        Scenario                   | Fabric | Temperature | Prewash | Extra Rinse | Outcome?
        Neither option             | cotton | 40          | false   | false       | runs(minutes: 120)
        Prewash only               | cotton | 40          | true    | false       | runs(minutes: 140)
        Extra rinse only           | cotton | 40          | false   | true        | runs(minutes: 135)
        Both options               | cotton | 40          | true    | true        | runs(minutes: 155)
        Both options on wool       | wool   | 40          | true    | true        | runs(minutes: 85)
        Both options, hot cotton   | cotton | 60          | true    | true        | runs(minutes: 175)
        """)
    func addsTimeForPrewashAndExtraRinse(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// A blank delay means the wash starts now. A delay of zero hours is assumed to still be a
    /// scheduled wash, since the rule text only says a wash with no delay starts now.
    @TableTest("""
        Scenario                  | Fabric | Temperature | Prewash | Extra Rinse | Delay Start (hours) | Outcome?
        No delay set              | cotton | 40          | false   | false       |                     | runs(minutes: 120)
        Delay of zero hours       | cotton | 40          | false   | false       | 0                   | scheduled(inHours: 0, minutes: 120)
        Delay of eight hours      | cotton | 40          | false   | false       | 8                   | scheduled(inHours: 8, minutes: 120)
        Delay with both options   | cotton | 40          | true    | true        | 8                   | scheduled(inHours: 8, minutes: 155)
        Delay on a refused wash   | wool   | 60          | false   | false       | 6                   | tooHot(limit: 40)
        """)
    func schedulesADelayedWashWithoutChangingItsDuration(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(
            fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse,
            delayStartHours: delayStartHours
        )
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
