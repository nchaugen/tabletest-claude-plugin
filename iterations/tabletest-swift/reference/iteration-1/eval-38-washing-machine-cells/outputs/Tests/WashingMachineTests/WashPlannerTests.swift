import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {

    @TableTest("Refuses a temperature above the fabric's limit", table: """
        Fabric     | Temperature | Outcome?
        cotton     | 90          | runs(minutes: 170)
        cotton     | 100         | tooHot(limit: 90)
        synthetics | 60          | runs(minutes: 110)
        synthetics | 70          | tooHot(limit: 60)
        wool       | 40          | runs(minutes: 50)
        wool       | 50          | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    @TableTest("Washes longer for each 10 °C above 40", table: """
        Fabric     | Temperature | Outcome?
        cotton     | 40          | runs(minutes: 120)
        cotton     | 50          | runs(minutes: 130)
        cotton     | 60          | runs(minutes: 140)
        synthetics | 40          | runs(minutes: 90)
        wool       | 40          | runs(minutes: 50)
        """)
    func washesLongerWhenHotter(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Cotton at 40 °C, which takes 120 minutes on its own.
    @TableTest("Adds time for a prewash and an extra rinse", table: """
        Prewash | Extra rinse | Outcome?
        false   | false       | runs(minutes: 120)
        true    | false       | runs(minutes: 140)
        false   | true        | runs(minutes: 135)
        true    | true        | runs(minutes: 155)
        """)
    func addsTimeForOptions(prewash: Bool, extraRinse: Bool, outcome: WashOutcome) {
        let program = WashProgram(fabric: .cotton, prewash: prewash, extraRinse: extraRinse)

        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// Cotton at 40 °C, which takes 120 minutes.
    @TableTest("Schedules a wash with a delayed start", table: """
        Delay start hours | Outcome?
                          | runs(minutes: 120)
        3                 | scheduled(inHours: 3, minutes: 120)
        """)
    func schedulesADelayedStart(delayStartHours: Int?, outcome: WashOutcome) {
        let program = WashProgram(fabric: .cotton, delayStartHours: delayStartHours)

        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
