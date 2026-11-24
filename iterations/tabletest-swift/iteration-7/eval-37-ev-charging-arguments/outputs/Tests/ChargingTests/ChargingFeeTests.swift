import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("Charges each started quarter after the free minutes", table: """
        Scenario                        | Charger   | Minutes | Fee (€)?
        Last free minute                | fast      | 5       | 0
        First charged minute            | fast      | 6       | 3
        Last minute of first quarter    | fast      | 15      | 3
        First minute of next quarter    | fast      | 16      | 6
        Long session on a fast charger  | fast      | 240     | 25
        Mid-length session on AC        | ac        | 40      | 3
        Mid-length session on ultrafast | ultrafast | 40      | 15
        Long session on ultrafast       | ultrafast | 600     | 40
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

    // Stays a plain @Test: the arguments come from the app, so a charger added later is
    // covered without touching this test. A hand-written table would lose that.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
