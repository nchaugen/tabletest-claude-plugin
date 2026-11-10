import TableTest
import Testing
@testable import HotelBooking

/// Fixture: a booking worth 200 euros.
struct CancellationFeeTests {

    @TableTest("Charges more the closer the cancellation is to check-in", table: """
        Days before check-in | Fee?
        30                   | 0
        29                   | 100
        7                    | 100
        6                    | 160
        1                    | 160
        0                    | 200
        """)
    func chargesByDaysBeforeCheckIn(daysBeforeCheckIn: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: 200, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Refuses a cancellation once the stay has started")
    func refusesOnceTheStayHasStarted() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 200, daysBeforeCheckIn: -1)
        }
    }
}
