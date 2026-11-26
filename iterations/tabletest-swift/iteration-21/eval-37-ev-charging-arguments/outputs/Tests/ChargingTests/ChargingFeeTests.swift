import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("Charges each started quarter after the free minutes", table: """
        Scenario                     | Charger   | Minutes | Fee (euros)?
        At the five-minute mark      | fast      | 5       | 0
        First minute past five       | fast      | 6       | 3
        End of the first quarter     | fast      | 15      | 3
        Start of the second quarter  | fast      | 16      | 6
        Four-hour fast session       | fast      | 240     | 25
        Cheapest charger             | ac        | 40      | 3
        Priciest charger, mid-length | ultrafast | 40      | 15
        Overnight ultrafast session  | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("Charges the session maximum for an unread meter", table: """
        Scenario          | Charger   | Fee (euros)?
        AC charger        | ac        | 10
        Fast charger      | fast      | 25
        Ultrafast charger | ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    // Kept as @Test(arguments:): the chargers come from the app (`Charger.allCases`), so a new
    // charger is covered without touching this test. A hand-written table would lose that.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
