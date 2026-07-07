import Testing
@testable import HotelBooking

@Suite("Cancellation fee by days before check-in")
struct CancellationFeeTests {

    @Test("Cancellation fee scales with days before check-in", arguments: [
        (daysBeforeCheckIn: 45, bookingValue: 300, fee: 0),    // well within the free-cancellation window
        (daysBeforeCheckIn: 30, bookingValue: 250, fee: 0),    // at the free-cancellation boundary
        (daysBeforeCheckIn: 29, bookingValue: 200, fee: 100),  // just inside the half-price window
        (daysBeforeCheckIn: 15, bookingValue: 300, fee: 150),  // mid-way through the half-price window
        (daysBeforeCheckIn: 7, bookingValue: 400, fee: 200),   // at the half-price boundary
        (daysBeforeCheckIn: 6, bookingValue: 250, fee: 200),   // just inside the high-fee window
        (daysBeforeCheckIn: 3, bookingValue: 500, fee: 400),   // mid-way through the high-fee window
        (daysBeforeCheckIn: 1, bookingValue: 150, fee: 120),   // the day before check-in
        (daysBeforeCheckIn: 0, bookingValue: 180, fee: 180),   // cancelling on the day of check-in
    ])
    func feeByDaysBeforeCheckIn(daysBeforeCheckIn: Int, bookingValue: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Cancelling once the stay has started is rejected", arguments: [
        -1,  // check-in was yesterday
        -5,  // a few days into the stay
        -30, // long after the stay began
    ])
    func cancellationAfterStayStarted(daysBeforeCheckIn: Int) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 200, daysBeforeCheckIn: daysBeforeCheckIn)
        }
    }
}
