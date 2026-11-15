import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("Charges each started quarter after the free minutes", table: """
        Scenario                      | Charger   | Minutes | Fee (euros)?
        Last free minute              | fast      | 5       | 0
        First minute after free time  | fast      | 6       | 3
        Last minute of first quarter  | fast      | 15      | 3
        First minute of next quarter  | fast      | 16      | 6
        Long session on fast charger  | fast      | 240     | 25
        Slow charger                  | ac        | 40      | 3
        Ultrafast charger             | ultrafast | 40      | 15
        Long session on ultrafast     | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("An unread meter costs the session maximum", table: """
        Charger   | Session Maximum (euros)?
        ac        | 10
        fast      | 25
        ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    // Kept as @Test(arguments:): the chargers come from the app (CaseIterable), so a new
    // charger is covered automatically. A hand-written table would lose that.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
