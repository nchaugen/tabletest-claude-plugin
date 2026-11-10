import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("Charges each started quarter after the free minutes", table: """
        Charger   | Minutes | Euros?
        fast      | 5       | 0
        fast      | 6       | 3
        fast      | 15      | 3
        fast      | 16      | 6
        fast      | 240     | 25
        ac        | 40      | 3
        ultrafast | 40      | 15
        ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("An unread meter costs the session maximum", table: """
        Charger   | Euros?
        ac        | 10
        fast      | 25
        ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    /// The chargers come from the app, so a charger added there is checked here too.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
