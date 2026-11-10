import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("Charges each started quarter after the free minutes", table: """
        Scenario                    | Charger   | Minutes | Fee in euros?
        End of the free minutes     | fast      | 5       | 0
        First started quarter       | fast      | 6       | 3
        End of the first quarter    | fast      | 15      | 3
        Second started quarter      | fast      | 16      | 6
        Capped at session maximum   | fast      | 240     | 25
        Slowest charger rate        | ac        | 40      | 3
        Fastest charger rate        | ultrafast | 40      | 15
        Capped at highest maximum   | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("An unread meter costs the session maximum", table: """
        Charger   | Fee in euros?
        ac        | 10
        fast      | 25
        ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    // Not a table: iterating `allCases` keeps the test covering any charger added later.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
