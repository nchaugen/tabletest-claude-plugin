import TableTest
import Testing
@testable import HotelBooking

/// Assumptions (the rules leave these open):
/// - Amounts are whole euros, and `daysBeforeCheckIn` is whole days remaining; the tiers are
///   drawn in whole days, so no finer unit is needed.
/// - Booking values are chosen so that 50% and 80% are whole euros; rounding is not specified.
/// - Zero days (day of check-in) is still cancellable; only a negative value means the stay
///   has started, as documented on `cancellationFee`.
struct CancellationFeeTests {

    /// Tier edges are stated as value sets: 30+ free, 7...29 half, 1...6 four-fifths, 0 full.
    /// The upper end of the free tier is unbounded, so 90 stands for "well ahead".
    @TableTest("""
        Scenario                    | Days Before Check-in | Booking Value (EUR) | Fee (EUR)?
        Thirty or more days ahead   | {30, 90}             | 200                 | 0
        One to four weeks ahead     | {7, 29}              | 200                 | 100
        Within the final week       | {1, 6}               | 200                 | 160
        On the day of check-in      | 0                    | 200                 | 200
        Half fee on another value   | 15                   | 150                 | 75
        Four-fifths on another value | 3                   | 150                 | 120
        """)
    func chargesTheFeeForTheDaysRemaining(daysBeforeCheckIn: Int, bookingValue: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    /// Rejection is a separate concern from the fee ladder: one day past check-in is the first
    /// rejected value, and values further past it reject for no new reason.
    @Test("Rejects cancelling once the stay has started")
    func rejectsCancellationOnceTheStayHasStarted() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 200, daysBeforeCheckIn: -1)
        }
    }
}
