import TableTest
import Testing
@testable import WashingMachine

// Assumptions (the doc comment leaves these open):
// - Temperatures are whole degrees; only multiples of 10 are used above 40, so how a
//   non-multiple such as 45 °C rounds is deliberately not pinned.
// - A temperature below 40 °C neither adds nor removes minutes.
// - Temperature above the limit is refused whatever else is set on the program.
// - Delay combined with a too-hot temperature is not specified, so it is not tested.

struct WashPlannerTests {
    @TableTest("Refuses a temperature above the fabric's limit", table: """
        Scenario                    | Fabric     | Temperature | Limit | Refused?
        Cotton at its limit         | cotton     | 90          | 90    | false
        Cotton just above its limit | cotton     | 91          | 90    | true
        Synthetics at its limit     | synthetics | 60          | 60    | false
        Synthetics just above limit | synthetics | 61          | 60    | true
        Wool at its limit           | wool       | 40          | 40    | false
        Wool just above its limit   | wool       | 41          | 40    | true
        """)
    func refusesTemperatureAboveLimit(fabric: Fabric, temperature: Int, limit: Int, refused: Bool) {
        let outcome = WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature))

        #expect((outcome == .tooHot(limit: limit)) == refused)
    }

    @TableTest("Adds minutes for temperature, prewash and extra rinse to the fabric's wash time", table: """
        Scenario                          | Fabric     | Temperature | Prewash | Extra Rinse | Minutes?
        Cotton at 40 °C                   | cotton     | 40          | false   | false       | 120
        Synthetics at 40 °C               | synthetics | 40          | false   | false       | 90
        Wool at 40 °C                     | wool       | 40          | false   | false       | 50
        Cotton below 40 °C                | cotton     | 30          | false   | false       | 120
        Cotton 10 °C above 40             | cotton     | 50          | false   | false       | 130
        Cotton 20 °C above 40             | cotton     | 60          | false   | false       | 140
        Cotton 50 °C above 40             | cotton     | 90          | false   | false       | 170
        Wool with prewash                 | wool       | 40          | true    | false       | 70
        Wool with extra rinse             | wool       | 40          | false   | true        | 65
        Wool with prewash and extra rinse | wool       | 40          | true    | true        | 85
        Hot synthetics with both options  | synthetics | 60          | true    | true        | 145
        """)
    func addsMinutesToTheWashTime(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, minutes: Int
    ) {
        let program = WashProgram(
            fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse
        )

        #expect(WashPlanner.outcome(of: program) == .runs(minutes: minutes))
    }

    @TableTest("Schedules a delayed start without changing the wash time", table: """
        Scenario                      | Fabric | Temperature | Prewash | Extra Rinse | Delay Start Hours | Outcome?
        No delay                      | cotton | 40          | false   | false       |                   | runs(minutes: 120)
        Delayed one hour              | cotton | 40          | false   | false       | 1                 | scheduled(inHours: 1, minutes: 120)
        Delayed several hours         | cotton | 40          | false   | false       | 8                 | scheduled(inHours: 8, minutes: 120)
        Delayed with options and heat | wool   | 40          | true    | true        | 3                 | scheduled(inHours: 3, minutes: 85)
        """)
    func schedulesADelayedStart(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool,
        delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(
            fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse,
            delayStartHours: delayStartHours
        )

        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}

// Lets a table cell spell an outcome the way Swift does, e.g. `scheduled(inHours: 3, minutes: 85)`.
extension WashOutcome: TableTextDecodable {
    public init?(tableText: String) {
        guard tableText.hasSuffix(")"), let open = tableText.firstIndex(of: "(") else { return nil }
        let name = tableText[..<open]
        let arguments = tableText[tableText.index(after: open)..<tableText.index(before: tableText.endIndex)]
            .split(separator: ",")
            .map { $0.split(separator: ":").map { $0.trimmingCharacters(in: .whitespaces) } }
        var values: [String: Int] = [:]
        for pair in arguments {
            guard pair.count == 2, let number = Int(pair[1]) else { return nil }
            values[pair[0]] = number
        }
        switch name {
        case "runs" where Set(values.keys) == ["minutes"]:
            self = .runs(minutes: values["minutes"]!)
        case "scheduled" where Set(values.keys) == ["inHours", "minutes"]:
            self = .scheduled(inHours: values["inHours"]!, minutes: values["minutes"]!)
        case "tooHot" where Set(values.keys) == ["limit"]:
            self = .tooHot(limit: values["limit"]!)
        default:
            return nil
        }
    }
}

// Lets a table cell name a fabric: `cotton`, `synthetics` or `wool`.
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
