import Testing
@testable import HotelBooking

struct CancellationFeeCase {
    let scenario: String
    let bookingValue: Int
    let daysBeforeCheckIn: Int
    let expectedFee: Int
}

@Test("Cancellation fee by days before check-in", arguments: [
    CancellationFeeCase(scenario: "many days before check-in", bookingValue: 200, daysBeforeCheckIn: 60, expectedFee: 0),
    CancellationFeeCase(scenario: "at the free-cancellation threshold", bookingValue: 200, daysBeforeCheckIn: 30, expectedFee: 0),
    CancellationFeeCase(scenario: "just inside the half-price window", bookingValue: 200, daysBeforeCheckIn: 29, expectedFee: 100),
    CancellationFeeCase(scenario: "mid-way through the half-price window", bookingValue: 340, daysBeforeCheckIn: 15, expectedFee: 170),
    CancellationFeeCase(scenario: "at the bottom of the half-price window", bookingValue: 200, daysBeforeCheckIn: 7, expectedFee: 100),
    CancellationFeeCase(scenario: "at the top of the eighty-percent window", bookingValue: 200, daysBeforeCheckIn: 6, expectedFee: 160),
    CancellationFeeCase(scenario: "mid-way through the eighty-percent window", bookingValue: 150, daysBeforeCheckIn: 3, expectedFee: 120),
    CancellationFeeCase(scenario: "at the bottom of the eighty-percent window", bookingValue: 200, daysBeforeCheckIn: 1, expectedFee: 160),
    CancellationFeeCase(scenario: "cancelling on the day of check-in", bookingValue: 200, daysBeforeCheckIn: 0, expectedFee: 200),
])
func cancellationFeeByDaysBeforeCheckIn(_ testCase: CancellationFeeCase) throws {
    let fee = try cancellationFee(bookingValue: testCase.bookingValue, daysBeforeCheckIn: testCase.daysBeforeCheckIn)
    #expect(fee == testCase.expectedFee)
}

struct StayAlreadyStartedCase {
    let scenario: String
    let daysBeforeCheckIn: Int
}

@Test("Cancelling once the stay has started throws stayAlreadyStarted", arguments: [
    StayAlreadyStartedCase(scenario: "the day after check-in", daysBeforeCheckIn: -1),
    StayAlreadyStartedCase(scenario: "well after the stay has started", daysBeforeCheckIn: -10),
])
func cancellationAfterStayStarted(_ testCase: StayAlreadyStartedCase) {
    #expect(throws: CancellationError.stayAlreadyStarted) {
        try cancellationFee(bookingValue: 200, daysBeforeCheckIn: testCase.daysBeforeCheckIn)
    }
}
