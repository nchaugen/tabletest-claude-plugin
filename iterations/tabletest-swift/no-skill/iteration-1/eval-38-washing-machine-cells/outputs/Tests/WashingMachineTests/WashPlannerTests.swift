import TableTest
import Testing
@testable import WashingMachine

// Assumptions where the doc comment is silent (each pinned by a row below):
// - A temperature below 40 °C takes no less time than 40 °C.
// - Only each *full* 10 °C above 40 adds time: 49 °C adds nothing, 50 °C adds 10 minutes.
// - Temperatures are whole degrees, so "just above the limit" is limit + 1.
// - An unset temperature means 40 °C and is not covered here: tables always state it.

struct WashPlannerTests {

    @TableTest("""
        Scenario                    | Fabric     | Temperature | Refusal limit?
        Cotton at its limit         | cotton     | 90          |
        Cotton just above its limit | cotton     | 91          | 90
        Synthetics at its limit     | synthetics | 60          |
        Synthetics just above       | synthetics | 61          | 60
        Wool at its limit           | wool       | 40          |
        Wool just above its limit   | wool       | 41          | 40
        """)
    func refusesTemperatureAboveFabricLimit(fabric: Fabric, temperature: Int, refusalLimit: Int?) {
        let outcome = WashPlanner.outcome(of: WashProgram(fabric: fabric, temperature: temperature))

        #expect(outcome.refusalLimit == refusalLimit)
    }

    @TableTest("""
        Scenario                      | Fabric     | Temperature | Prewash | Extra rinse | Duration?
        Cotton at the base temperature| cotton     | 40          | false   | false       | 120
        Synthetics at base temperature| synthetics | 40          | false   | false       | 90
        Wool at the base temperature  | wool       | 40          | false   | false       | 50
        Below the base temperature    | cotton     | 30          | false   | false       | 120
        Short of a full 10 °C step    | cotton     | 49          | false   | false       | 120
        One full 10 °C step           | cotton     | 50          | false   | false       | 130
        Two full 10 °C steps          | cotton     | 60          | false   | false       | 140
        Three full 10 °C steps        | cotton     | 70          | false   | false       | 150
        Hottest cotton                | cotton     | 90          | false   | false       | 170
        Hottest synthetics            | synthetics | 60          | false   | false       | 110
        Prewash only                  | cotton     | 40          | true    | false       | 140
        Extra rinse only              | cotton     | 40          | false   | true        | 135
        Prewash and extra rinse       | cotton     | 40          | true    | true        | 155
        Hot wash with every addition  | cotton     | 90          | true    | true        | 205
        """)
    func addsMinutesToTheBaseWashTime(
        fabric: Fabric, temperature: Int, prewash: Bool, extraRinse: Bool, duration: Int
    ) {
        let program = WashProgram(
            fabric: fabric, temperature: temperature, prewash: prewash, extraRinse: extraRinse
        )

        #expect(WashPlanner.outcome(of: program) == .runs(minutes: duration))
    }

    @TableTest("""
        Scenario                     | Fabric | Temperature | Prewash | Delay start hours | Outcome?
        No delay set                 | cotton | 40          | false   |                   | runs(minutes: 120)
        Delay set                    | cotton | 40          | false   | 8                 | scheduled(inHours: 8, minutes: 120)
        Delay with a longer wash     | cotton | 60          | true    | 3                 | scheduled(inHours: 3, minutes: 160)
        Delay with a refused program | wool   | 60          | false   | 8                 | tooHot(limit: 40)
        """)
    func schedulesTheWashWhenStartIsDelayed(
        fabric: Fabric, temperature: Int, prewash: Bool, delayStartHours: Int?, outcome: WashOutcome
    ) {
        let program = WashProgram(
            fabric: fabric, temperature: temperature, prewash: prewash, delayStartHours: delayStartHours
        )

        #expect(WashPlanner.outcome(of: program) == outcome)
    }
}

// MARK: - Conversion helpers

extension WashOutcome {
    var refusalLimit: Int? {
        if case .tooHot(let limit) = self { limit } else { nil }
    }
}

extension Fabric: CaseIterable {
    public static var allCases: [Fabric] { [.cotton, .synthetics, .wool] }
}

extension Fabric: TableValueDecodable {}

/// Reads cells such as `scheduled(inHours: 8, minutes: 120)`.
extension WashOutcome: TableTextDecodable {
    public init?(tableText: String) {
        let text = tableText.filter { $0 != " " }
        guard let open = text.firstIndex(of: "("), text.last == ")" else { return nil }
        let name = String(text[..<open])
        let arguments = text[text.index(after: open)..<text.index(before: text.endIndex)]
            .split(separator: ",")
            .map { $0.split(separator: ":") }
        var values: [String: Int] = [:]
        for pair in arguments {
            guard pair.count == 2, let number = Int(pair[1]) else { return nil }
            values[String(pair[0])] = number
        }
        switch (name, values) {
        case ("runs", _) where Set(values.keys) == ["minutes"]:
            self = .runs(minutes: values["minutes"]!)
        case ("scheduled", _) where Set(values.keys) == ["inHours", "minutes"]:
            self = .scheduled(inHours: values["inHours"]!, minutes: values["minutes"]!)
        case ("tooHot", _) where Set(values.keys) == ["limit"]:
            self = .tooHot(limit: values["limit"]!)
        default:
            return nil
        }
    }
}
