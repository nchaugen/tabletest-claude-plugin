import TableTest
import Testing
@testable import HotelBooking

struct CancellationFeeTests {
    /// Booking value is in euros, and the days are whole days before check-in.
    ///
    /// Assumptions:
    /// - Booking values are chosen so every fee is a whole number of euros; rounding of
    ///   fractional fees is unspecified and deliberately not pinned here.
    /// - The fee is not observable separately from the percentage applied, so the tier and the
    ///   amount are verified together. Each cell is still one step from its row (e.g. 50% of 240).
    /// - "30 or more days" has no upper bound, so the free tier is stated from its first day (30)
    ///   up to a distant date (90).
    @TableTest("""
        Scenario                   | Days Before Check-in | Booking Value | Fee?
        Month or more ahead        | {30, 90}             | 500           | 0
        Between a week and a month | {7, 29}              | 240           | 120
        Within the last week       | {1, 6}               | 250           | 200
        Arriving today             | 0                    | 180           | 180
        """)
    func chargesACancellationFeeByDaysBeforeCheckIn(daysBeforeCheckIn: Int, bookingValue: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Rejects a cancellation once the stay has started")
    func rejectsCancellationAfterCheckIn() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 200, daysBeforeCheckIn: -1)
        }
    }
}
