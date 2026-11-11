import TableTest
import Testing
@testable import HotelBooking

// Assumptions:
// - Fees are whole euros; booking values below are multiples of 10 so every
//   percentage is exact and no rounding rule is implied.
// - `daysBeforeCheckIn` is the number of whole days until check-in:
//   0 = day of check-in, negative = stay already started.
// - Ladder boundaries (30/29, 7/6, 1/0) are straddled once; the tier tops and
//   bottoms are the rows' own edge values.

struct CancellationFeeTests {

    @Test("Charges a share of the booking value by days before check-in", arguments: [
        (scenario: "first day of free cancellation",   bookingValue: 400, daysBeforeCheckIn: 30, fee: 0),
        (scenario: "last day before free cancellation", bookingValue: 400, daysBeforeCheckIn: 29, fee: 200),
        (scenario: "first day of half-price window",    bookingValue: 400, daysBeforeCheckIn: 7,  fee: 200),
        (scenario: "last day before half-price window", bookingValue: 250, daysBeforeCheckIn: 6,  fee: 200),
        (scenario: "one day before check-in",           bookingValue: 250, daysBeforeCheckIn: 1,  fee: 200),
        (scenario: "day of check-in",                   bookingValue: 250, daysBeforeCheckIn: 0,  fee: 250),
    ])
    func chargesShareOfBookingValue(scenario: String, bookingValue: Int, daysBeforeCheckIn: Int, fee: Int) throws {
        #expect(try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn) == fee)
    }

    @Test("Rejects cancellation once the stay has started", arguments: [
        (scenario: "day after check-in", bookingValue: 400, daysBeforeCheckIn: -1),
    ])
    func rejectsCancellationAfterCheckIn(scenario: String, bookingValue: Int, daysBeforeCheckIn: Int) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn)
        }
    }
}
