import TableTest
import Testing
@testable import Charging

struct ChargingFeeTests {

    @TableTest("Charges each started quarter after the free minutes", table: """
        Scenario                          | Charger   | Minutes | Fee?
        Last free minute                  | fast      | 5       | 0
        First chargeable minute           | fast      | 6       | 3
        End of the first quarter          | fast      | 15      | 3
        Start of the second quarter       | fast      | 16      | 6
        Long session capped at maximum    | fast      | 240     | 25
        Slow charger                      | ac        | 40      | 3
        Ultrafast charger                 | ultrafast | 40      | 15
        Ultrafast long session at maximum | ultrafast | 600     | 40
        """)
    func chargesEachStartedQuarter(charger: Charger, minutes: Int, fee: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == fee)
    }

    @TableTest("Charges the session maximum for an unread meter", table: """
        Charger   | Fee?
        ac        | 10
        fast      | 25
        ultrafast | 40
        """)
    func chargesTheSessionMaximumForAnUnreadMeter(charger: Charger, fee: Int) {
        #expect(ChargingFee.unreadMeterEuros(for: charger) == fee)
    }

    @TableTest("Charges nothing for five minutes on any charger", table: """
        Scenario    | Charger                | Minutes | Fee?
        Free period | {ac, fast, ultrafast}  | 5       | 0
        """)
    func isFreeForFiveMinutes(charger: Charger, minutes: Int, fee: Int) {
        #expect(ChargingFee.euros(for: charger, minutes: minutes) == fee)
    }
}
