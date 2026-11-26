import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    /// The first five minutes of a session cost nothing; after that each started quarter of an hour
    /// costs the charger's rate, up to the charger's session maximum.
    @TableTest("Charges each started quarter after the free minutes", table: """
        Scenario                        | Charger   | Minutes | Fee (euros)?
        At the end of the grace period  | fast      | 5       | 0
        Just past the grace period      | fast      | 6       | 3
        At the end of the first quarter | fast      | 15      | 3
        Into the second quarter         | fast      | 16      | 6
        Long session on a fast charger  | fast      | 240     | 25
        Slowest charger rate            | ac        | 40      | 3
        Fastest charger rate            | ultrafast | 40      | 15
        Day-long ultrafast session      | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("An unread meter costs the session maximum", table: """
        Scenario        | Charger   | Fee (euros)?
        Slowest charger | ac        | 10
        Middle charger  | fast      | 25
        Fastest charger | ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    // Kept as @Test(arguments:) on purpose: the cases come from Charger.allCases, so a charger
    // added to the app is covered here without a change to the test. A table would list them by hand.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
