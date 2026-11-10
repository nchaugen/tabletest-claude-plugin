import TableTest
import Testing
@testable import HotelBooking

// Assumptions:
// - `daysBeforeCheckIn` is whole days; 0 is the day of check-in, negative means the stay has started.
// - Booking values are chosen so every percentage yields a whole euro amount (no rounding rule is specified).
// - The fee ladder is exercised with the classification and the percentage fused: the tier is not observable
//   from `cancellationFee`, so band and amount are verified together.
struct CancellationFeeTests {

    @Test("Charges a share of the booking value by days before check-in", arguments: [
        (scenario: "start of the free window",  bookingValue: 200, daysBeforeCheckIn: 30, fee: 0),
        (scenario: "first day charged 50%",     bookingValue: 200, daysBeforeCheckIn: 29, fee: 100),
        (scenario: "last day charged 50%",      bookingValue: 200, daysBeforeCheckIn: 7 , fee: 100),
        (scenario: "first day charged 80%",     bookingValue: 200, daysBeforeCheckIn: 6 , fee: 160),
        (scenario: "last day charged 80%",      bookingValue: 200, daysBeforeCheckIn: 1 , fee: 160),
        (scenario: "check-in day",              bookingValue: 200, daysBeforeCheckIn: 0 , fee: 200),
    ])
    func chargesFeeByDaysBeforeCheckIn(
        scenario: String, bookingValue: Int, daysBeforeCheckIn: Int, fee: Int
    ) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Rejects cancellation once the stay has started", arguments: [
        (scenario: "day after check-in", bookingValue: 200, daysBeforeCheckIn: -1),
    ])
    func rejectsCancellationAfterStayStarted(
        scenario: String, bookingValue: Int, daysBeforeCheckIn: Int
    ) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn)
        }
    }
}
