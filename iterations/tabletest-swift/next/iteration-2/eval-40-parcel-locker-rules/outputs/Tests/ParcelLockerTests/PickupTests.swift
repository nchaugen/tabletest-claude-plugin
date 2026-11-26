import TableTest
import Testing
@testable import ParcelLocker

/// Pickup of a parcel with its six-digit code.
///
/// Assumptions where the rules are silent:
/// - `wrongCodesInARow` counts the wrong codes *before* this attempt, so the third wrong code
///   is typed when the count is 2, and a count of 3 is a blocked parcel.
/// - The pickup window is 72 whole hours after delivery, inclusive: 72 still opens, 73 is too late.
/// - A parcel past the window goes back to the depot even when it is also blocked.
/// - Only well-formed six-digit codes are typed. Malformed input is not specified here.
struct PickupTests {
    /// Within the window (`Hours Since Delivery` 0 to 72) the hours do not change the answer.
    /// A parcel is blocked after three wrong codes in a row, so the third wrong code in a row
    /// shows no attempts left.
    @TableTest("""
        Scenario                    | Pickup Code | Typed Code | Wrong Codes In A Row | Hours Since Delivery | Result?
        Right code, no earlier miss | 482915      | 482915     | 0                    | {0, 72}              | opens
        Right code after two misses | 482915      | 482915     | 2                    | {0, 72}              | opens
        First wrong code            | 482915      | 482916     | 0                    | {0, 72}              | wrongCode(attemptsLeft: 2)
        Second wrong code in a row  | 482915      | 482916     | 1                    | {0, 72}              | wrongCode(attemptsLeft: 1)
        Third wrong code in a row   | 482915      | 482916     | 2                    | {0, 72}              | blocked
        """)
    func refusesAWrongCodeAndCountsTheAttemptsLeft(
        pickupCode: String, typedCode: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    /// A parcel with three wrong codes in a row is blocked, and the recipient must call support.
    @TableTest("""
        Scenario                    | Pickup Code | Typed Code     | Wrong Codes In A Row | Hours Since Delivery | Result?
        Blocked parcel, any code    | 482915      | {482915, 100000} | 3                  | {0, 72}              | blocked
        """)
    func keepsABlockedParcelBlocked(
        pickupCode: String, typedCode: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    /// Past the window the courier has taken the parcel back, whatever code is typed and
    /// whether or not the parcel was blocked. The last hour inside the window is in the first table.
    @TableTest("""
        Scenario                    | Pickup Code | Typed Code       | Wrong Codes In A Row | Hours Since Delivery | Max Pickup Hours (Policy) | Result?
        One hour past the window    | 482915      | {482915, 482916} | {0, 2, 3}            | 73                   | 72                        | returnedToDepot
        """)
    func returnsAnExpiredParcelToTheDepot(
        pickupCode: String, typedCode: String, wrongCodesInARow: Int, hoursSinceDelivery: Int,
        maxPickupHours: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }
}
