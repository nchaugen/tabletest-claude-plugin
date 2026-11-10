import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    static let sessions: [(charger: Charger, minutes: Int, euros: Int)] = [
        (charger: .fast, minutes: 5, euros: 0),
        (charger: .fast, minutes: 6, euros: 3),
        (charger: .fast, minutes: 15, euros: 3),
        (charger: .fast, minutes: 16, euros: 6),
        (charger: .fast, minutes: 240, euros: 25),
        (charger: .ac, minutes: 40, euros: 3),
        (charger: .ultrafast, minutes: 40, euros: 15),
        (charger: .ultrafast, minutes: 600, euros: 40),
    ]

    @Test("Charges each started quarter after the free minutes", arguments: sessions)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @Test("An unread meter costs the session maximum", arguments: zip([Charger.ac, .fast, .ultrafast], [10, 25, 40]))
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
