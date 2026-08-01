import Testing
@testable import HotelBooking

@Suite("Cancellation fee")
struct CancellationFeeTests {

    /// `bookingValue` is held at 1000 except at the two paid-tier boundaries (7 and 1
    /// days), where a different value confirms the fee scales with booking value rather
    /// than being a fixed amount.
    @Test(
        "Computes the cancellation fee from days before check-in",
        arguments: [
            (daysBeforeCheckIn: 30, bookingValue: 1000, fee: 0),
            (daysBeforeCheckIn: 29, bookingValue: 1000, fee: 500),
            (daysBeforeCheckIn: 7, bookingValue: 200, fee: 100),
            (daysBeforeCheckIn: 6, bookingValue: 1000, fee: 800),
            (daysBeforeCheckIn: 1, bookingValue: 200, fee: 160),
            (daysBeforeCheckIn: 0, bookingValue: 1000, fee: 1000),
        ]
    )
    func cancellationFeeByDaysBeforeCheckIn(daysBeforeCheckIn: Int, bookingValue: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Rejects cancellation once the stay has started")
    func rejectsCancellationOnceStayHasStarted() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 1000, daysBeforeCheckIn: -1)
        }
    }
}
