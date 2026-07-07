import Testing
import HotelBooking

@Suite("Cancellation fee")
struct CancellationFeeTests {

    struct FeeCase: Sendable, CustomTestStringConvertible {
        let daysBeforeCheckIn: Int
        let bookingValue: Int
        let expectedFee: Int

        var testDescription: String {
            "\(daysBeforeCheckIn) days before check-in, booking \(bookingValue) -> fee \(expectedFee)"
        }
    }

    static let feeCases: [FeeCase] = [
        // 30+ days before check-in: free
        FeeCase(daysBeforeCheckIn: 30, bookingValue: 100, expectedFee: 0),
        FeeCase(daysBeforeCheckIn: 45, bookingValue: 250, expectedFee: 0),

        // 7-29 days before check-in: 50% of booking value
        FeeCase(daysBeforeCheckIn: 29, bookingValue: 100, expectedFee: 50),
        FeeCase(daysBeforeCheckIn: 15, bookingValue: 200, expectedFee: 100),
        FeeCase(daysBeforeCheckIn: 7, bookingValue: 500, expectedFee: 250),

        // 1-6 days before check-in: 80% of booking value
        FeeCase(daysBeforeCheckIn: 6, bookingValue: 100, expectedFee: 80),
        FeeCase(daysBeforeCheckIn: 3, bookingValue: 500, expectedFee: 400),
        FeeCase(daysBeforeCheckIn: 1, bookingValue: 200, expectedFee: 160),

        // Day of check-in: full booking value
        FeeCase(daysBeforeCheckIn: 0, bookingValue: 300, expectedFee: 300),
    ]

    @Test("charges the fee defined by the cancellation window", arguments: feeCases)
    func fee(testCase: FeeCase) throws {
        let fee = try cancellationFee(
            bookingValue: testCase.bookingValue,
            daysBeforeCheckIn: testCase.daysBeforeCheckIn
        )
        #expect(fee == testCase.expectedFee)
    }

    @Test("throws once the stay has already started", arguments: [-1, -2, -30])
    func stayAlreadyStarted(daysBeforeCheckIn: Int) {
        #expect(throws: CancellationError.stayAlreadyStarted) {
            try cancellationFee(bookingValue: 100, daysBeforeCheckIn: daysBeforeCheckIn)
        }
    }
}
