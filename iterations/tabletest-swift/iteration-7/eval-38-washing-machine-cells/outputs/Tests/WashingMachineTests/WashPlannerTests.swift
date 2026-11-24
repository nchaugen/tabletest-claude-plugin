import TableTest
import Testing
@testable import WashingMachine

struct WashPlannerTests {
    /// A wash that is refused reports the fabric's limit; one that is accepted reports none.
    /// Programs without a delay start now. Temperatures are whole degrees, so 1 °C is the finest step.
    @TableTest("Refuses a temperature above the fabric's limit", table: """
        Scenario                              | Fabric     | Temperature (°C) | Prewash | Extra Rinse | Delay Start (h) | Refusal Limit (°C)?
        Cotton at its limit                   | cotton     | 90               | false   | false       |                 |
        Cotton just above its limit           | cotton     | 91               | false   | false       |                 | 90
        Synthetics at their limit             | synthetics | 60               | false   | false       |                 |
        Synthetics just above their limit     | synthetics | 61               | false   | false       |                 | 60
        Wool at its limit                     | wool       | 40               | false   | false       |                 |
        Wool just above its limit             | wool       | 41               | false   | false       |                 | 40
        Too hot with extras and delayed start | cotton     | 91               | true    | true        | 4               | 90
        """)
    func refusesATemperatureAboveTheLimit(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, delayStartHours: Int?,
        refusalLimit: Int?
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash,
                                  extraRinse: extraRinse, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program).refusalLimit == refusalLimit)
    }

    /// Programs start now and are within the fabric's temperature limit.
    /// Temperature adds time per full 10 °C above 40 (assumed reading of "each 10 °C above 40"),
    /// and nothing below 40 °C.
    @TableTest("Adds wash time for fabric, temperature, prewash and extra rinse", table: """
        Scenario                           | Fabric     | Temperature (°C) | Prewash | Extra Rinse | Outcome?
        Cotton at the base temperature     | cotton     | 40               | false   | false       | runs(minutes: 120)
        Synthetics at the base temperature | synthetics | 40               | false   | false       | runs(minutes: 90)
        Wool at the base temperature       | wool       | 40               | false   | false       | runs(minutes: 50)
        Below the base temperature         | cotton     | 30               | false   | false       | runs(minutes: 120)
        Part-way to the first step         | cotton     | 49               | false   | false       | runs(minutes: 120)
        First full step                    | cotton     | 50               | false   | false       | runs(minutes: 130)
        Part-way to the second step        | cotton     | 59               | false   | false       | runs(minutes: 130)
        Second full step                   | cotton     | 60               | false   | false       | runs(minutes: 140)
        Prewash only                       | cotton     | 40               | true    | false       | runs(minutes: 140)
        Extra rinse only                   | cotton     | 40               | false   | true        | runs(minutes: 135)
        Prewash and extra rinse            | cotton     | 40               | true    | true        | runs(minutes: 155)
        Every contribution at once         | synthetics | 60               | true    | true        | runs(minutes: 145)
        """)
    func addsWashTime(fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, outcome: WashOutcome) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash,
                                  extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }

    /// A blank delay starts the wash now. A delay of 0 hours is present, so it is assumed to schedule.
    @TableTest("Schedules a wash with a delayed start without changing its duration", table: """
        Scenario                  | Fabric     | Temperature (°C) | Prewash | Extra Rinse | Delay Start (h) | Outcome?
        No delay                  | cotton     | 40               | false   | false       |                 | runs(minutes: 120)
        Zero hours delay          | cotton     | 40               | false   | false       | 0               | scheduled(inHours: 0, minutes: 120)
        Delay in hours            | cotton     | 40               | false   | false       | 8               | scheduled(inHours: 8, minutes: 120)
        Delay with every addition | synthetics | 60               | true    | true        | 6               | scheduled(inHours: 6, minutes: 145)
        """)
    func schedulesADelayedStart(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, delayStartHours: Int?,
        outcome: WashOutcome
    ) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash,
                                  extraRinse: extraRinse, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}

private extension WashOutcome {
    /// The limit a refused wash reports; nil when the wash was accepted.
    var refusalLimit: Int? {
        if case .tooHot(let limit) = self { limit } else { nil }
    }
}
