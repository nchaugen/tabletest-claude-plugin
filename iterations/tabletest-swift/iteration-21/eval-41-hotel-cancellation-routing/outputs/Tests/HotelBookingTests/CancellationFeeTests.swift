import TableTest
import Testing
@testable import HotelBooking

struct CancellationFeeTests {
    /// Booking values and fees are whole euros. Booking values are chosen so every percentage
    /// comes out exact, because the rounding rule for fractional euros is not specified.
    /// Each tier is stated by its own first and last day, so both sides of every boundary
    /// (30/29, 7/6, 1/0) are covered by neighbouring rows.
    @TableTest("""
        Scenario                    | Booking Value (EUR) | Days Before Check-in | Fee (EUR)?
        A month or more ahead       | 300                 | {30, 60}             | 0
        Between one and four weeks  | 250                 | {7, 29}              | 125
        In the final week           | 150                 | {1, 6}               | 120
        Arrival day                 | 180                 | 0                    | 180
        """)
    func chargesFeeByDaysBeforeCheckIn(bookingValue: Int, daysBeforeCheckIn: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    /// A negative number of days means the stay has started. One day past check-in is the
    /// first rejected value; further past it rejects for no new reason.
    @Test("Rejects cancellation once the stay has started")
    func rejectsCancellationOnceStayHasStarted() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 200, daysBeforeCheckIn: -1)
        }
    }
}
