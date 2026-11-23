import TableTest
import Testing
@testable import HotelBooking

struct CancellationFeeTests {
    /// Booking values are in euros and chosen so every fee is a whole number of euros;
    /// rounding of fractional fees is not specified and not tested.
    /// Each tier row spans the tier's own first and last day, so every tier boundary
    /// (30/29, 7/6, 1/0) is straddled by neighbouring rows. The free tier has no upper edge,
    /// so a far-off check-in stands in for it. The last row shows the fee follows the booking value.
    @TableTest("""
        Scenario                      | Booking Value (€) | Days Before Check-in | Fee (€)?
        A month or more ahead         | 200               | {30, 365}            | 0
        Between a week and a month    | 200               | {7, 29}              | 100
        Between a day and a week      | 200               | {1, 6}               | 160
        On the day of check-in        | 200               | 0                    | 200
        Different booking value       | 450               | 7                    | 225
        """)
    func chargesTheCancellationFeeForTheDaysBeforeCheckIn(bookingValue: Int, daysBeforeCheckIn: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    /// Days before check-in is negative once the stay has started.
    /// One row suffices: a stay further along is rejected for the same reason.
    @Test("Rejects cancellation once the stay has started")
    func rejectsCancellationOnceTheStayHasStarted() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 200, daysBeforeCheckIn: -1)
        }
    }
}
