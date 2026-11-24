import TableTest
import Testing
@testable import HotelBooking

struct CancellationFeeTests {
    /// Assumptions:
    /// - Days before check-in are whole days; 0 is the day of check-in.
    /// - The booking value is held at 250 euros so every percentage yields a whole-euro fee.
    ///   Rounding of fractional euros is unspecified and deliberately not pinned here.
    /// - Value sets use the first and last day of a tier, so each tier boundary is straddled
    ///   by the neighbouring rows (30|29, 7|6, 1|0). The free tier has no upper edge.
    @TableTest("Charges a cancellation fee by days before check-in", table: """
        Scenario                          | Booking Value (EUR) | Days Before Check-in | Fee (EUR)?
        Month or more ahead               | 250                 | 30                   | 0
        Between a week and a month ahead  | 250                 | {7, 29}              | 125
        Between a day and a week ahead    | 250                 | {1, 6}               | 200
        On the day of check-in            | 250                 | 0                    | 250
        """)
    func chargesFeeByDaysBeforeCheckIn(bookingValue: Int, daysBeforeCheckIn: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    /// Rejection is a separate concern from the fee ladder. The booking value does not
    /// influence it, so it varies across the arguments. A stay that started one day ago
    /// is the first rejected value; values further past it reject for no new reason.
    @Test("Rejects a cancellation once the stay has started, whatever the booking value",
          arguments: [0, 250, 1_000])
    func rejectsCancellationAfterStayStarted(bookingValue: Int) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: -1)
        }
    }
}
