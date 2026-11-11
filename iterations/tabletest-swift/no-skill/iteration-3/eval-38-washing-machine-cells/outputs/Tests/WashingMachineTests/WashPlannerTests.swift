import TableTest
import Testing
@testable import WashingMachine

// Cells name a fabric by its case name: cotton, synthetics, wool.
extension Fabric: TableTextDecodable {
    public init?(tableText: String) {
        switch tableText {
        case "cotton": self = .cotton
        case "synthetics": self = .synthetics
        case "wool": self = .wool
        default: return nil
        }
    }
}

// Reads one part of an outcome, so each table can state its expectation as a plain column.
private extension WashOutcome {
    /// The temperature limit the machine refused the program at; nil when it accepted the program.
    var refusedAtLimit: Int? {
        if case let .tooHot(limit) = self { limit } else { nil }
    }

    /// Hours until the wash starts; nil when it starts now or the program was refused.
    var startsInHours: Int? {
        if case let .scheduled(inHours, _) = self { inHours } else { nil }
    }

    /// How long the wash takes, whether it starts now or later; nil when the program was refused.
    var washMinutes: Int? {
        switch self {
        case let .runs(minutes), let .scheduled(_, minutes): minutes
        case .tooHot: nil
        }
    }
}

// Assumptions where the doc comment is silent:
// - Temperatures are whole °C. The refusal is strictly above the limit, so the limit itself runs
//   and one degree more is refused. Temperatures between the 10 °C steps are not specified and not tested.
// - A temperature below 40 °C adds nothing and does not shorten the wash.
// - Refusal comes before scheduling: a too-hot program with a delayed start is refused, not scheduled.
// - A delay of 0 hours is not specified and not tested.
struct WashPlannerTests {

    // The ladder differs per fabric, so each fabric has its own pair around its limit.
    @TableTest("Refuses a temperature above the fabric's limit", table: """
        Scenario                    | Fabric     | Temperature | Refused at limit?
        Cotton at its limit         | cotton     | 90          |
        Cotton just above its limit | cotton     | 91          | 90
        Synthetics at its limit     | synthetics | 60          |
        Synthetics just above       | synthetics | 61          | 60
        Wool at its limit           | wool       | 40          |
        Wool just above its limit   | wool       | 41          | 40
        """)
    func refusesTemperatureAboveLimit(fabric: Fabric, temperature: Int, refusedAtLimit: Int?) {
        let outcome = WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature))
        #expect(outcome.refusedAtLimit == refusedAtLimit)
    }

    // Fabric, temperature, prewash and extra rinse are contributions to one wash time, so they share a table.
    @TableTest("Adds up the wash time from fabric, temperature, prewash and extra rinse", table: """
        Scenario                      | Fabric     | Temperature | Prewash | Extra rinse | Minutes?
        Cotton base time              | cotton     | 40          | false   | false       | 120
        Synthetics base time          | synthetics | 40          | false   | false       | 90
        Wool base time                | wool       | 40          | false   | false       | 50
        Cotton below 40 °C            | cotton     | 30          | false   | false       | 120
        Cotton 10 °C above 40         | cotton     | 50          | false   | false       | 130
        Cotton 20 °C above 40         | cotton     | 60          | false   | false       | 140
        Cotton 30 °C above 40         | cotton     | 70          | false   | false       | 150
        Cotton at its limit           | cotton     | 90          | false   | false       | 170
        Synthetics at its limit       | synthetics | 60          | false   | false       | 110
        Cotton with prewash           | cotton     | 40          | true    | false       | 140
        Cotton with extra rinse       | cotton     | 40          | false   | true        | 135
        Wool with both options        | wool       | 40          | true    | true        | 85
        Everything adds up            | synthetics | 60          | true    | true        | 145
        """)
    func addsUpTheWashTime(fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, minutes: Int) {
        let program = WashProgram(fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse)
        #expect(WashPlanner.outcome(of: program) == .runs(minutes: minutes))
    }

    // The delay is the only thing that changes; the wash time must match the same program started now.
    @TableTest("Schedules a delayed start without changing the wash time", table: """
        Scenario                       | Fabric | Prewash | Extra rinse | Delay start (hours) | Starts in hours? | Minutes?
        No delay                       | cotton | false   | false       |                     |                  | 120
        Delayed one hour               | cotton | false   | false       | 1                   | 1                | 120
        Delayed with both options      | wool   | true    | true        | 12                  | 12               | 85
        """)
    func schedulesDelayedStart(fabric: Fabric, prewash: Bool, extraRinse: Bool, delayStartHours: Int?,
                               startsInHours: Int?, minutes: Int) {
        let program = WashProgram(fabric: fabric, prewash: prewash, extraRinse: extraRinse,
                                  delayStartHours: delayStartHours)
        let outcome = WashPlanner.outcome(of: program)
        #expect(outcome.startsInHours == startsInHours)
        #expect(outcome.washMinutes == minutes)
    }

    // Precedence: the temperature check happens before scheduling.
    @TableTest("Refuses a too-hot program even when its start is delayed", table: """
        Scenario                     | Fabric | Temperature | Delay start (hours) | Refused at limit?
        Delayed cotton above limit   | cotton | 95          | 3                   | 90
        """)
    func refusesBeforeScheduling(fabric: Fabric, temperature: Int, delayStartHours: Int?, refusedAtLimit: Int) {
        let program = WashProgram(fabric: fabric, temperature: temperature, delayStartHours: delayStartHours)
        #expect(WashPlanner.outcome(of: program) == .tooHot(limit: refusedAtLimit))
    }
}
