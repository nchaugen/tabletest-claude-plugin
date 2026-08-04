import Testing
import HotelBooking

// Assumptions, since the feature description leaves them open:
// - `cancellationFee` is the only public entry point, so the day-before-check-in
//   tier and the percentage-of-value arithmetic are exercised through the same
//   function. The tier table below uses a booking value (1000) chosen so each
//   cell is traceable in one step; a separate table isolates rounding behaviour
//   for booking values the tier percentages don't divide evenly.
// - Fees are rounded to the nearest whole euro, ties rounding away from zero
//   (e.g. 50% of 33 = 16.5 rounds to 17), matching Swift's default `rounded()`.

@Suite("Cancellation fee")
struct CancellationFeeTests {

    @Test("Applies the fee percentage for the day-before-check-in tier", arguments: [
        (daysBeforeCheckIn: 30, bookingValue: 1000, fee: 0),     // free tier, at the boundary
        (daysBeforeCheckIn: 30, bookingValue: 250, fee: 0),      // free tier, regardless of booking value
        (daysBeforeCheckIn: 29, bookingValue: 1000, fee: 500),   // 50% tier, just below the free boundary
        (daysBeforeCheckIn: 7, bookingValue: 1000, fee: 500),    // 50% tier, at the boundary
        (daysBeforeCheckIn: 6, bookingValue: 1000, fee: 800),    // 80% tier, just below the 50% boundary
        (daysBeforeCheckIn: 1, bookingValue: 1000, fee: 800),    // 80% tier, at the boundary
        (daysBeforeCheckIn: 0, bookingValue: 1000, fee: 1000),   // full fee on the day of check-in
    ])
    func feeByDaysBeforeCheckIn(daysBeforeCheckIn: Int, bookingValue: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Rounds the fee to the nearest euro when the percentage does not divide evenly", arguments: [
        (daysBeforeCheckIn: 15, bookingValue: 101, fee: 51),  // 50% tier: 50.5 rounds up to 51
        (daysBeforeCheckIn: 15, bookingValue: 33, fee: 17),   // 50% tier, exact tie: 16.5 rounds up to 17
        (daysBeforeCheckIn: 3, bookingValue: 101, fee: 81),   // 80% tier: 80.8 rounds up to 81
    ])
    func roundsFeeToNearestEuro(daysBeforeCheckIn: Int, bookingValue: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Rejects cancellation once the stay has started", arguments: [
        (daysBeforeCheckIn: -1, bookingValue: 1000),   // just past the day-of-check-in boundary
        (daysBeforeCheckIn: -30, bookingValue: 250),   // well after check-in, regardless of booking value
    ])
    func rejectsCancellationAfterStayStarted(daysBeforeCheckIn: Int, bookingValue: Int) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn)
        }
    }
}
