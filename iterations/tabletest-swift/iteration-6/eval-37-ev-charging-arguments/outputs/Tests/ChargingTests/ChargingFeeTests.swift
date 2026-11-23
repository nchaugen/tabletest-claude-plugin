import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    /// The first five minutes are free. After that each started quarter of an hour is charged at the
    /// charger's rate, up to the charger's session maximum.
    @TableTest("""
        Scenario                     | Charger   | Minutes | Fee (EUR)?
        Last free minute             | fast      | 5       | 0
        First charged minute         | fast      | 6       | 3
        End of the first quarter     | fast      | 15      | 3
        Second quarter begins        | fast      | 16      | 6
        Long session hits the cap    | fast      | 240     | 25
        Slow charger, one rate       | ac        | 40      | 3
        Fastest charger, higher rate | ultrafast | 40      | 15
        Very long session hits cap   | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("""
        Scenario | Charger   | Fee (EUR)?
        Slowest  | ac        | 10
        Middle   | fast      | 25
        Fastest  | ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    // Kept as @Test(arguments:): the chargers come from the app, so a new Charger case
    // is covered here without touching the test.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
