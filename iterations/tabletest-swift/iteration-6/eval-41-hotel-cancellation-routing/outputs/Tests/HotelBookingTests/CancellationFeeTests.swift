import TableTest
import Testing
@testable import HotelBooking

struct CancellationFeeTests {
    /// Booking values are whole euros, chosen so every fee is a whole euro: rounding is not specified.
    /// Ranges are written as their edges (e.g. 7 and 29), so each tier boundary is exercised from both sides.
    /// Days Before Check-In is whole days; the stay has not started while it is 0 or more.
    @TableTest("""
        Scenario                         | Booking Value (EUR) | Days Before Check-In | Fee (EUR)?
        A month or more ahead            | 200                 | {30, 90}             | 0
        One to four weeks ahead          | 300                 | {7, 29}              | 150
        Within the last week             | 250                 | {1, 6}               | 200
        Day of check-in                  | 180                 | 0                    | 180
        """)
    func chargesTheFeeForTheTimeLeftBeforeCheckIn(bookingValue: Int, daysBeforeCheckIn: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    /// Assumption: a negative number of days before check-in means the stay has started.
    @Test("Rejects a cancellation once the stay has started")
    func rejectsCancellationAfterStayStarted() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 200, daysBeforeCheckIn: -1)
        }
    }
}
