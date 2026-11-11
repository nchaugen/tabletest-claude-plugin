import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("""
        Scenario                          | Charger   | Minutes | Euros?
        Inside the free minutes           | fast      | 5       | 0
        First minute past the free time   | fast      | 6       | 3
        End of the first started quarter  | fast      | 15      | 3
        Second quarter started            | fast      | 16      | 6
        Long session on a fast charger    | fast      | 240     | 25
        Slow charger                      | ac        | 40      | 3
        Ultrafast charger                 | ultrafast | 40      | 15
        Long session on an ultrafast one  | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarterAfterTheFreeMinutes(charger: Charger, minutes: Int, euros: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == euros)
    }

    @TableTest("""
        Charger   | Euros?
        ac        | 10
        fast      | 25
        ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, euros: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == euros)
    }

    // Kept as @Test(arguments:): the chargers come from Charger.allCases, so a charger added
    // to the app is covered without touching this test. A hand-written table would lose that.
    @Test("Every charger is free for five minutes", arguments: Charger.allCases)
    func isFreeForFiveMinutes(charger: Charger) {
        #expect(ChargingFee.euros(for: charger, minutes: 5) == 0)
    }
}
