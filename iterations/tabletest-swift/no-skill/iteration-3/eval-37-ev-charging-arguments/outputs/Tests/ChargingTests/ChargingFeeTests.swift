import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("Charges each started quarter after the free minutes", table: """
        Scenario                           | Charger   | Minutes | Fee (€)?
        Last free minute                   | fast      | 5       | 0
        First minute after free minutes    | fast      | 6       | 3
        Last minute of the first quarter   | fast      | 15      | 3
        First minute of the second quarter | fast      | 16      | 6
        Session maximum reached            | fast      | 240     | 25
        AC rate                            | ac        | 40      | 3
        Ultrafast rate                     | ultrafast | 40      | 15
        Ultrafast session maximum          | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("An unread meter costs the session maximum", table: """
        Charger   | Fee (€)?
        ac        | 10
        fast      | 25
        ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    // Kept as @Test: a single column of every case, so a table adds nothing,
    // and `allCases` picks up new chargers automatically.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
