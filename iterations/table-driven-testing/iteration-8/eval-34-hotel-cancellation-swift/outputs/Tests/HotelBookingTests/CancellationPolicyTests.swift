import Testing
@testable import HotelBooking

@Suite("Cancellation fee")
struct CancellationFeeTests {

    // Cancellation fee is a percentage of the booking value determined by the day-before-check-in
    // tier the cancellation falls into. Booking values vary per case so each expected fee is
    // traceable as bookingValue * tier rate for that row.
    @Test(
        "Charges a percentage of the booking value based on days before check-in",
        arguments: [
            (daysBeforeCheckIn: 30, bookingValue: 1000, fee: 0),      // free tier begins
            (daysBeforeCheckIn: 29, bookingValue: 1000, fee: 500),    // 50% tier ends
            (daysBeforeCheckIn: 7, bookingValue: 2000, fee: 1000),    // 50% tier begins
            (daysBeforeCheckIn: 6, bookingValue: 2000, fee: 1600),    // 80% tier ends
            (daysBeforeCheckIn: 1, bookingValue: 500, fee: 400),      // 80% tier begins
            (daysBeforeCheckIn: 0, bookingValue: 750, fee: 750),      // full value on day of check-in
        ]
    )
    func feeByDaysBeforeCheckIn(daysBeforeCheckIn: Int, bookingValue: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    // bookingValue does not affect whether cancellation is rejected once the stay has started,
    // so it is held at one representative value here — the fee-by-tier rule above owns bookingValue.
    @Test("Rejects cancellation once the stay has started")
    func rejectsCancellationAfterStayStarted() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 1000, daysBeforeCheckIn: -1)
        }
    }
}
