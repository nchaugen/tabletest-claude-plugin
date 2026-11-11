import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("Charges each started quarter after the free minutes", table: """
        Scenario                    | Charger   | Minutes | Fee (EUR)?
        Within the free minutes     | fast      | 5       | 0
        First minute after free     | fast      | 6       | 3
        End of the first quarter    | fast      | 15      | 3
        Start of the second quarter | fast      | 16      | 6
        Long fast session           | fast      | 240     | 25
        Slow charger                | ac        | 40      | 3
        Ultrafast charger           | ultrafast | 40      | 15
        Long ultrafast session      | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("Charges the session maximum for an unread meter", table: """
        Charger   | Fee (EUR)?
        ac        | 10
        fast      | 25
        ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    // Kept as @Test(arguments:): the chargers come from the app, so a new Charger case is covered automatically.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
