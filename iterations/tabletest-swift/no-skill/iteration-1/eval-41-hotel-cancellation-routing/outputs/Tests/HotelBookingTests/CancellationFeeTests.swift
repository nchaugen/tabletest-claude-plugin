import Testing
@testable import HotelBooking

// Assumptions:
// - Booking values are whole euros; every value here divides evenly by 2 and 5,
//   so rounding of the 50% / 80% fees is deliberately not specified.
// - "Days before check-in" is whole days; a negative value means the stay has started.
struct CancellationFeeTests {

    @Test("Charges a fee by days before check-in", arguments: [
        (scenario: "first day of free cancellation",       bookingValue: 200, daysBeforeCheckIn: 30, fee: 0),
        (scenario: "last day before free cancellation",    bookingValue: 200, daysBeforeCheckIn: 29, fee: 100),
        (scenario: "last day of half-price cancellation",  bookingValue: 200, daysBeforeCheckIn: 7,  fee: 100),
        (scenario: "first day of 80% cancellation",        bookingValue: 200, daysBeforeCheckIn: 6,  fee: 160),
        (scenario: "last day of 80% cancellation",         bookingValue: 200, daysBeforeCheckIn: 1,  fee: 160),
        (scenario: "day of check-in",                      bookingValue: 200, daysBeforeCheckIn: 0,  fee: 200),
    ])
    func chargesFeeByDaysBeforeCheckIn(
        scenario: String, bookingValue: Int, daysBeforeCheckIn: Int, fee: Int
    ) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Rejects cancellation once the stay has started")
    func rejectsCancellationAfterStayStarted() {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 200, daysBeforeCheckIn: -1)
        }
    }
}
