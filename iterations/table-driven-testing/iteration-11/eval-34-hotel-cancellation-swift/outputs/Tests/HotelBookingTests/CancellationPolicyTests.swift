import Testing
@testable import HotelBooking

// Booking values are chosen to divide evenly by each tier's percentage,
// since the rounding behaviour for fractional fees is not specified.

@Test("Charges a cancellation fee by how many days remain before check-in", arguments: [
    (daysBeforeCheckIn: 30, bookingValue: 500, fee: 0),    // free threshold
    (daysBeforeCheckIn: 29, bookingValue: 200, fee: 100),  // top of the 50% band
    (daysBeforeCheckIn: 7,  bookingValue: 340, fee: 170),  // bottom of the 50% band
    (daysBeforeCheckIn: 6,  bookingValue: 250, fee: 200),  // top of the 80% band
    (daysBeforeCheckIn: 1,  bookingValue: 500, fee: 400),  // bottom of the 80% band
    (daysBeforeCheckIn: 0,  bookingValue: 180, fee: 180),  // day of check-in
])
func cancellationFeeByDaysBeforeCheckIn(daysBeforeCheckIn: Int, bookingValue: Int, fee: Int) throws {
    #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
}

@Test("Rejects cancellation once the stay has started, regardless of booking value", arguments: [
    (daysBeforeCheckIn: -1, bookingValue: 120),
    (daysBeforeCheckIn: -1, bookingValue: 900),
])
func cancellationRejectedAfterStayStarted(daysBeforeCheckIn: Int, bookingValue: Int) {
    #expect(throws: CancellationError.stayAlreadyStarted) {
        try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn)
    }
}
