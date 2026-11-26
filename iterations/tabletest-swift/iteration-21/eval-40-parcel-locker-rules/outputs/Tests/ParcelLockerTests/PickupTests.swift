import TableTest
import Testing
@testable import ParcelLocker

struct PickupTests {
    /// Assumptions: a code counts as wrong whenever it differs from the pickup code, so a
    /// malformed code is one more wrong code (not tabled here). `Wrong Codes In A Row` is the
    /// count before this attempt. The pickup window is held open here: the hours cannot change
    /// the outcome, and the rows claim that by taking both ends of the window.
    @TableTest("""
        Scenario            | Pickup Code | Wrong Codes In A Row | Code Typed       | Hours Since Delivery | Result?
        Fresh parcel        | 482913      | 0                    | 482913           | {0, 72}              | opens
        Last chance         | 482913      | 2                    | 482913           | {0, 72}              | opens
        First miss          | 482913      | 0                    | 000000           | {0, 72}              | wrongCode(attemptsLeft: 2)
        Second miss         | 482913      | 1                    | 000000           | {0, 72}              | wrongCode(attemptsLeft: 1)
        Third miss in a row | 482913      | 2                    | 000000           | {0, 72}              | blocked
        After the third     | 482913      | 3                    | {482913, 000000} | {0, 72}              | blocked
        """)
    func countsWrongCodesAndBlocksTheParcelOnTheThird(
        pickupCode: String, wrongCodesInARow: Int, codeTyped: String, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }

    /// The window is 72 whole hours after delivery, and 72 itself is still inside it (assumed:
    /// "up to 72 hours"). No wrong codes have been typed, so the attempts rule stays out of it.
    @TableTest("""
        Scenario                   | Pickup Code | Wrong Codes In A Row | Code Typed       | Hours Since Delivery | Result?
        Inside the window          | 482913      | 0                    | 482913           | {0, 72}              | opens
        First hour past the window | 482913      | 0                    | {482913, 000000} | 73                   | returnedToDepot
        """)
    func takesTheParcelBackToTheDepotAfter72Hours(
        pickupCode: String, wrongCodesInARow: Int, codeTyped: String, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }

    /// Assumed: a parcel past the window is no longer in the locker, so the depot return wins
    /// over a block, and a wrong code past the window does not count as an attempt.
    @TableTest("""
        Scenario                       | Pickup Code | Wrong Codes In A Row | Code Typed       | Hours Since Delivery | Result?
        Blocked when the time ran out  | 482913      | 3                    | {482913, 000000} | 73                   | returnedToDepot
        Would have been the third miss | 482913      | 2                    | 000000           | 73                   | returnedToDepot
        """)
    func reportsTheDepotReturnAheadOfABlock(
        pickupCode: String, wrongCodesInARow: Int, codeTyped: String, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }
}
