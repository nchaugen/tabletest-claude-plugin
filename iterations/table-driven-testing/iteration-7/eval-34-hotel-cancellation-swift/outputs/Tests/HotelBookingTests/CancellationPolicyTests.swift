import Testing
@testable import HotelBooking

@Suite("Cancellation fee")
struct CancellationFeeTests {

    // Booking value is held at €1000 throughout, except for the last case,
    // which uses a different booking value to show the fee stays proportional
    // to it at a given tier rather than being tied to the €1000 figure.
    @Test("Charges a fee for cancelling based on days remaining before check-in", arguments: [
        (bookingValue: 1000, daysBeforeCheckIn: 30, fee: 0),
        (bookingValue: 1000, daysBeforeCheckIn: 29, fee: 500),
        (bookingValue: 1000, daysBeforeCheckIn: 7, fee: 500),
        (bookingValue: 1000, daysBeforeCheckIn: 6, fee: 800),
        (bookingValue: 1000, daysBeforeCheckIn: 1, fee: 800),
        (bookingValue: 1000, daysBeforeCheckIn: 0, fee: 1000),
        (bookingValue: 2000, daysBeforeCheckIn: 7, fee: 1000),
    ])
    func cancellationFeeByDaysBeforeCheckIn(bookingValue: Int, daysBeforeCheckIn: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    // The booking value is irrelevant to whether the stay has already started,
    // so it is held at one fixed, valid value across these cases.
    @Test("Rejects cancellation once the stay has already started", arguments: [-1, -7, -30])
    func cancellationAfterStayHasStarted(daysBeforeCheckIn: Int) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 1000, daysBeforeCheckIn: daysBeforeCheckIn)
        }
    }
}
