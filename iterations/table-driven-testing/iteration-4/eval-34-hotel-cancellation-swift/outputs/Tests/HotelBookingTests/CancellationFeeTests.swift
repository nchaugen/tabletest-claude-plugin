import Testing
import HotelBooking

@Suite("Cancellation fee")
struct CancellationFeeTests {

    // Booking value fixed at 1000 so every tier's percentage is an exact,
    // easy-to-verify integer (0%, 50%, 80%, 100%).
    @Test("Fee tier by days before check-in", arguments: [
        (daysBeforeCheckIn: 60, expectedFee: 0),
        (daysBeforeCheckIn: 30, expectedFee: 0),
        (daysBeforeCheckIn: 29, expectedFee: 500),
        (daysBeforeCheckIn: 15, expectedFee: 500),
        (daysBeforeCheckIn: 7, expectedFee: 500),
        (daysBeforeCheckIn: 6, expectedFee: 800),
        (daysBeforeCheckIn: 3, expectedFee: 800),
        (daysBeforeCheckIn: 1, expectedFee: 800),
        (daysBeforeCheckIn: 0, expectedFee: 1000),
    ])
    func feeTierByDaysBeforeCheckIn(daysBeforeCheckIn: Int, expectedFee: Int) throws {
        #expect(try cancellationFee(bookingValue: 1000, daysBeforeCheckIn: daysBeforeCheckIn) == expectedFee)
    }

    // Days fixed within each tier; booking values chosen to divide evenly by
    // the tier's percentage, since the rules don't specify a rounding rule
    // for fractional amounts.
    @Test("Fee scales with booking value", arguments: [
        (daysBeforeCheckIn: 15, bookingValue: 100, expectedFee: 50),
        (daysBeforeCheckIn: 15, bookingValue: 730, expectedFee: 365),
        (daysBeforeCheckIn: 3, bookingValue: 250, expectedFee: 200),
        (daysBeforeCheckIn: 3, bookingValue: 1250, expectedFee: 1000),
        (daysBeforeCheckIn: 0, bookingValue: 475, expectedFee: 475),
    ])
    func feeScalesWithBookingValue(daysBeforeCheckIn: Int, bookingValue: Int, expectedFee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == expectedFee)
    }

    @Test("Cancellation after the stay has started", arguments: [-1, -2, -10])
    func cancellationAfterStayStarted(daysBeforeCheckIn: Int) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 1000, daysBeforeCheckIn: daysBeforeCheckIn)
        }
    }
}
