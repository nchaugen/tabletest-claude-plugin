import TableTest
import Testing
@testable import HotelBooking

// Assumptions:
// - Booking values are whole euros; every value below makes the percentage a whole euro,
//   so rounding is not specified here.
// - `daysBeforeCheckIn` counts whole days; 0 is the day of check-in, negative means the stay has started.
struct CancellationFeeTests {

    @Test("Charges a fee that rises as check-in approaches", arguments: [
        (scenario: "first day of the free window",           bookingValue: 400, daysBeforeCheckIn: 30, feeEur: 0),
        (scenario: "last day before the free window",        bookingValue: 400, daysBeforeCheckIn: 29, feeEur: 200),
        (scenario: "first day of the one-week window",       bookingValue: 250, daysBeforeCheckIn: 7,  feeEur: 125),
        (scenario: "last day before the one-week window",    bookingValue: 250, daysBeforeCheckIn: 6,  feeEur: 200),
        (scenario: "final day before check-in",              bookingValue: 300, daysBeforeCheckIn: 1,  feeEur: 240),
        (scenario: "day of check-in",                        bookingValue: 300, daysBeforeCheckIn: 0,  feeEur: 300),
    ])
    func chargesFeeByDaysBeforeCheckIn(
        scenario: String, bookingValue: Int, daysBeforeCheckIn: Int, feeEur: Int
    ) throws {
        let fee = try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn)
        #expect(fee == feeEur)
    }

    @Test("Rejects cancellation once the stay has started", arguments: [
        (scenario: "day after check-in", bookingValue: 300, daysBeforeCheckIn: -1),
    ])
    func rejectsCancellationAfterCheckIn(
        scenario: String, bookingValue: Int, daysBeforeCheckIn: Int
    ) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn)
        }
    }
}
