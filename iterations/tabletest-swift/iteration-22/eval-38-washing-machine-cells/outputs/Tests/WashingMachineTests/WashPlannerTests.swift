import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// The limit is the highest temperature each fabric accepts; the first degree past it is refused
    /// and the outcome names the limit. The accepted rows sit at the limit, so each one shows the
    /// other side of its boundary. Wash times are covered by the duration tables below.
    @TableTest("Refuses a temperature above the fabric's limit", table: """
        Scenario                   | Fabric     | Temperature (°C) | Outcome?
        Cotton at its limit        | cotton     | 90               | runs(minutes: 170)
        Cotton just past its limit | cotton     | 91               | tooHot(limit: 90)
        Synthetics at their limit  | synthetics | 60               | runs(minutes: 110)
        Synthetics just past limit | synthetics | 61               | tooHot(limit: 60)
        Wool at its limit          | wool       | 40               | runs(minutes: 50)
        Wool just past its limit   | wool       | 41               | tooHot(limit: 40)
        """)
    func refusesATemperatureAboveTheFabricLimit(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Wash time of a program without prewash, extra rinse or delayed start. Each full 10 °C above
    /// 40 adds 10 minutes; below 40 °C the time stays at the 40 °C time (assumed reading of the
    /// rule). Temperatures between two steps (such as 45) are not specified and not tested.
    @TableTest("Sets the wash time from the fabric and the temperature", table: """
        Scenario                  | Fabric     | Temperature (°C) | Outcome?
        Cotton below 40 °C        | cotton     | 30               | runs(minutes: 120)
        Cotton at 40 °C           | cotton     | 40               | runs(minutes: 120)
        Cotton one step above 40  | cotton     | 50               | runs(minutes: 130)
        Cotton two steps above 40 | cotton     | 60               | runs(minutes: 140)
        Synthetics at 40 °C       | synthetics | 40               | runs(minutes: 90)
        Synthetics at 60 °C       | synthetics | 60               | runs(minutes: 110)
        Wool at 40 °C             | wool       | 40               | runs(minutes: 50)
        """)
    func setsTheWashTimeFromFabricAndTemperature(fabric: Fabric, temperature: Int, outcome: WashOutcome) {
        #expect(WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature)) == outcome)
    }

    /// Prewash adds 20 minutes and an extra rinse adds 15, at 40 °C with a start now. The wool row
    /// shows that the additions do not depend on the fabric.
    @TableTest("Adds time for a prewash and an extra rinse", table: """
        Scenario                 | Fabric | Temperature (°C) | Prewash | Extra Rinse | Outcome?
        No extra cycles          | cotton | 40               | false   | false       | runs(minutes: 120)
        Prewash alone            | cotton | 40               | true    | false       | runs(minutes: 140)
        Extra rinse alone        | cotton | 40               | false   | true        | runs(minutes: 135)
        Prewash with extra rinse | cotton | 40               | true    | true        | runs(minutes: 155)
        Both cycles on wool      | wool   | 40               | true    | true        | runs(minutes: 85)
        """)
    func addsTimeForAPrewashAndAnExtraRinse(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// A delay start is in hours; blank starts now. A scheduled wash takes the same time as the
    /// same program started now. A refused temperature is refused whether or not a delay is set.
    /// A delay of 0 hours is not specified and not tested.
    @TableTest("Schedules a wash with a delayed start", table: """
        Scenario                    | Fabric | Temperature (°C) | Prewash | Delay Start (h) | Outcome?
        Start now                   | cotton | 40               | true    |                 | runs(minutes: 140)
        Delayed by a few hours      | cotton | 40               | true    | 3               | scheduled(inHours: 3, minutes: 140)
        Delayed by half a day       | cotton | 40               | true    | 12              | scheduled(inHours: 12, minutes: 140)
        Delayed with a refused heat | wool   | 60               | false   | 3               | tooHot(limit: 40)
        """)
    func schedulesAWashWithADelayedStart(
        fabric: Fabric, temperature: Int, prewash: Bool, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(
            fabric: fabric, temperature: temperature, prewash: prewash, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}
