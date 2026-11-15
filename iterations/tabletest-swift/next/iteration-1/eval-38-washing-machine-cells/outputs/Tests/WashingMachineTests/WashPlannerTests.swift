import TableTest
import Testing
@testable import WashingMachine

/// Assumptions: temperatures are whole degrees; the extra time counts whole 10 °C steps above
/// 40 °C (49 °C adds nothing, 50 °C adds 10 minutes); below 40 °C nothing is subtracted.
struct WashPlannerTests {
    /// Refusal is checked for each fabric at its own limit, on both sides of it.
    /// Every other option is left at its default: no prewash, no extra rinse, starts now.
    @TableTest("""
        Scenario                   | Fabric     | Temperature | Refused Above?
        Cotton at its limit        | cotton     | 90          |
        Cotton just past its limit | cotton     | 91          | 90
        Synthetics at its limit    | synthetics | 60          |
        Synthetics past its limit  | synthetics | 61          | 60
        Wool at its limit          | wool       | 40          |
        Wool past its limit        | wool       | 41          | 40
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, refusedAbove: Int?) {
        let outcome = WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature))
        #expect(Self.limitExceeded(by: outcome) == refusedAbove)
    }

    /// Washes without prewash or extra rinse, starting now.
    @TableTest("""
        Scenario                       | Fabric     | Temperature | Outcome?
        Cotton at the base temperature | cotton     | 40          | runs(minutes: 120)
        Synthetics at base temperature | synthetics | 40          | runs(minutes: 90)
        Wool at the base temperature   | wool       | 40          | runs(minutes: 50)
        Cotton below the base          | cotton     | 30          | runs(minutes: 120)
        Cotton just under one step     | cotton     | 49          | runs(minutes: 120)
        Cotton one step above          | cotton     | 50          | runs(minutes: 130)
        Cotton two steps above         | cotton     | 60          | runs(minutes: 140)
        Cotton at its hottest          | cotton     | 90          | runs(minutes: 170)
        Synthetics at its hottest      | synthetics | 60          | runs(minutes: 110)
        """)
    func lengthensTheWashForFabricAndHeat(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Washes at 40 °C, starting now.
    @TableTest("""
        Scenario                 | Fabric | Prewash | Extra Rinse | Outcome?
        Neither option           | cotton | false   | false       | runs(minutes: 120)
        Prewash only             | cotton | true    | false       | runs(minutes: 140)
        Extra rinse only         | cotton | false   | true        | runs(minutes: 135)
        Both options             | cotton | true    | true        | runs(minutes: 155)
        Both options on a wool   | wool   | true    | true        | runs(minutes: 85)
        """)
    func addsTimeForPrewashAndExtraRinse(fabric: Fabric, prewash: Bool, extraRinse: Bool, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// Washes without prewash or extra rinse. A delay of 0 hours is not specified and not tested.
    @TableTest("""
        Scenario                    | Fabric | Temperature | Delay Start (hours) | Outcome?
        Starts now                  | cotton | 40          |                     | runs(minutes: 120)
        Delayed by hours            | cotton | 40          | 6                   | scheduled(inHours: 6, minutes: 120)
        Delayed with another length | wool   | 40          | 12                  | scheduled(inHours: 12, minutes: 50)
        Delayed hot wash            | cotton | 60          | 6                   | scheduled(inHours: 6, minutes: 140)
        Delayed beyond the limit    | cotton | 100         | 6                   | tooHot(limit: 90)
        """)
    func schedulesADelayedWashWithTheSameDuration(
        fabric: Fabric, temperature: Int, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    private static func limitExceeded(by outcome: WashOutcome) -> Int? {
        if case .tooHot(let limit) = outcome { return limit }
        return nil
    }
}
