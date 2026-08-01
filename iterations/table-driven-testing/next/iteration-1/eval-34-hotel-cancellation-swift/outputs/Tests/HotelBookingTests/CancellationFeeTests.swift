import Testing
import HotelBooking

/// Runs `cancellationFee`, capturing either the returned fee or the thrown
/// `CancellationError` as plain values so cases can assert on both without
/// branching between a value assertion and a throw assertion.
private func attemptCancellationFee(
    bookingValue: Int,
    daysBeforeCheckIn: Int
) -> (fee: Int?, error: CancellationError?) {
    do {
        return (try cancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: daysBeforeCheckIn), nil)
    } catch let error as CancellationError {
        return (nil, error)
    } catch {
        return (nil, nil)
    }
}

@Suite("Cancellation fee")
struct CancellationFeeTests {

    // Booking value is held fixed at €1000 for every case, so the days-before-check-in
    // tier alone drives the outcome. €1000 divides evenly by every applicable rate, so
    // expected fees don't depend on an unspecified rounding rule.
    private static let feeByDaysBeforeCheckIn: [(daysBeforeCheckIn: Int, fee: Int?, error: CancellationError?)] = [
        (30, 0, nil),
        (29, 500, nil),
        (7, 500, nil),
        (6, 800, nil),
        (1, 800, nil),
        (0, 1000, nil),
        (-1, nil, .stayAlreadyStarted),
    ]

    @Test(
        "Determines the cancellation fee, or rejection, from days before check-in",
        arguments: feeByDaysBeforeCheckIn
    )
    func cancellationFeeByDaysBeforeCheckIn(daysBeforeCheckIn: Int, fee: Int?, error: CancellationError?) {
        let result = attemptCancellationFee(bookingValue: 1000, daysBeforeCheckIn: daysBeforeCheckIn)
        #expect(result.fee == fee)
        #expect(result.error == error)
    }

    // Cancellation window is held fixed at 15 days before check-in (the 50% tier) for
    // every case, so booking value alone drives the outcome. Booking values divide evenly
    // by 2 so expected fees don't depend on an unspecified rounding rule.
    private static let feeByBookingValue: [(bookingValue: Int, fee: Int)] = [
        (0, 0),
        (100, 50),
        (1000, 500),
    ]

    @Test(
        "Scales the cancellation fee with booking value at a fixed cancellation window",
        arguments: feeByBookingValue
    )
    func cancellationFeeScalesWithBookingValue(bookingValue: Int, fee: Int) {
        let result = attemptCancellationFee(bookingValue: bookingValue, daysBeforeCheckIn: 15)
        #expect(result.fee == fee)
        #expect(result.error == nil)
    }
}
