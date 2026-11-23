import TableTest
import Testing
@testable import ParcelLocker

/// Fixture: the parcel's six-digit pickup code is 123456 in every table.
///
/// Assumptions where the rules are silent:
/// - The pickup window is 72 whole hours: 72 is still inside, 73 is past it.
/// - A code that is not six digits is a wrong code like any other.
/// - An expired parcel is in the depot, so it is reported as returned even when it was also blocked.
struct PickupTests {
    /// Within the pickup window, so the hours since delivery make no difference to the answer.
    @TableTest("""
        Scenario                      | Pickup Code | Code Typed                | Hours Since Delivery | Wrong Codes In A Row | Result?
        Right code                    | 123456      | 123456                    | {0, 72}              | {0, 1, 2}            | opens
        First wrong code              | 123456      | {654321, 123457, 12345}   | {0, 72}              | 0                    | wrongCode(attemptsLeft: 2)
        Second wrong code in a row    | 123456      | {654321, 123457, 12345}   | {0, 72}              | 1                    | wrongCode(attemptsLeft: 1)
        Third wrong code in a row     | 123456      | {654321, 123457, 12345}   | {0, 72}              | 2                    | blocked
        """)
    func answersATypedCodeFromTheWrongCodesSoFar(
        pickupCode: String, codeTyped: String, hoursSinceDelivery: Int, wrongCodesInARow: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }

    /// Within the pickup window. Three wrong codes in a row is the blocked state.
    @TableTest("""
        Scenario       | Pickup Code | Code Typed       | Hours Since Delivery | Wrong Codes In A Row | Result?
        Blocked parcel | 123456      | {123456, 654321} | {0, 72}              | 3                    | blocked
        """)
    func keepsABlockedParcelBlocked(
        pickupCode: String, codeTyped: String, hoursSinceDelivery: Int, wrongCodesInARow: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }

    /// The pickup window is 72 hours from delivery.
    @TableTest("""
        Scenario                 | Pickup Code | Hours Since Delivery | Wrong Codes In A Row | Code Typed       | Result?
        Last hour of the window  | 123456      | 72                   | 0                    | 123456           | opens
        First hour past it       | 123456      | 73                   | {0, 1, 2, 3}         | {123456, 654321} | returnedToDepot
        """)
    func returnsAnExpiredParcelToTheDepot(
        pickupCode: String, hoursSinceDelivery: Int, wrongCodesInARow: Int, codeTyped: String, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }
}
