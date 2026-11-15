import TableTest
import Testing
@testable import ParcelLocker

struct PickupTests {
    /// The parcel is inside its 72-hour pickup window, so only the code typed and the wrong codes
    /// already given decide the result. Three wrong codes in a row block the parcel, and a blocked
    /// parcel stays blocked for the right code too. A right code resets the count, so it opens
    /// whether none, one or two wrong codes came before it.
    @TableTest("""
        Scenario                        | Pickup Code | Code Typed       | Wrong Codes In A Row | Hours Since Delivery | Result?
        Right code                      | 483920      | 483920           | {0, 1, 2}            | 24                   | opens
        First wrong code                | 483920      | {483921, 039482} | 0                    | 24                   | wrongCode(attemptsLeft: 2)
        Second wrong code in a row      | 483920      | {483921, 039482} | 1                    | 24                   | wrongCode(attemptsLeft: 1)
        Third wrong code in a row       | 483920      | {483921, 039482} | 2                    | 24                   | blocked
        Parcel already blocked          | 483920      | {483920, 483921} | 3                    | 24                   | blocked
        """)
    func refusesWrongCodesAndBlocksTheParcelOnTheThird(
        pickupCode: String, codeTyped: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }

    /// The pickup window is 72 hours from delivery, and the 72nd hour is still inside it.
    /// Open question: a parcel that is both blocked and past the window is read as returned to the
    /// depot, because the courier has taken it away; the rows with three wrong codes show this.
    /// Hours are whole hours since delivery.
    @TableTest("""
        Scenario                   | Hours Since Delivery | Wrong Codes In A Row | Code Typed       | Pickup Code | Result?
        Inside the window          | {0, 72}              | 0                    | 483920           | 483920      | opens
        Past the window            | {73, 1000}           | {0, 1, 2, 3}         | {483920, 483921} | 483920      | returnedToDepot
        """)
    func returnsTheParcelToTheDepotAfterSeventyTwoHours(
        hoursSinceDelivery: Int, wrongCodesInARow: Int, codeTyped: String, pickupCode: String, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }
}
