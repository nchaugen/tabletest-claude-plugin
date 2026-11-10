import TableTest
import Testing
@testable import ParcelLocker

/// Fixture: the parcel's pickup code is 482915.
struct PickupTests {

    static func parcel(wrongCodesInARow: Int, hoursSinceDelivery: Int) -> Parcel {
        Parcel(pickupCode: "482915", wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
    }

    /// Within the 72 hours after delivery.
    @TableTest("Opens for the pickup code and blocks at the third wrong code", table: """
        Scenario                             | Wrong codes in a row | Code             | Result?
        The pickup code                      | 0                    | 482915           | opens
        A first wrong code                   | 0                    | 111111           | wrongCode(attemptsLeft: 2)
        A second wrong code                  | 1                    | 111111           | wrongCode(attemptsLeft: 1)
        A third wrong code                   | 2                    | 111111           | blocked
        The pickup code after two wrong ones | 2                    | 482915           | opens
        Any code after three wrong ones      | 3                    | {482915, 111111} | blocked
        """)
    func opensForThePickupCode(wrongCodesInARow: Int, code: String, result: PickupResult) throws {
        let parcel = Self.parcel(wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: 10)

        #expect(try Locker.pickUp(parcel, code: code) == result)
    }

    /// The rules do not say which wins for a blocked parcel past 72 hours. Here the courier
    /// takes it back, as for any parcel past its time.
    @TableTest("Returns the parcel to the depot after 72 hours", table: """
        Scenario                         | Hours since delivery | Wrong codes in a row | Code             | Result?
        The last hour to pick it up      | 72                   | 0                    | 482915           | opens
        The first hour past its time     | 73                   | 0                    | {482915, 111111} | returnedToDepot
        Three wrong codes, past its time | 73                   | 3                    | 482915           | returnedToDepot
        """)
    func returnsTheParcelAfter72Hours(hoursSinceDelivery: Int, wrongCodesInARow: Int, code: String,
                                      result: PickupResult) throws {
        let parcel = Self.parcel(wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)

        #expect(try Locker.pickUp(parcel, code: code) == result)
    }
}
