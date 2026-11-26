import TableTest
import Testing
@testable import ParcelLocker

struct PickupTests {
    /// Rule: the third wrong code in a row blocks the parcel, and a blocked parcel stays blocked.
    ///
    /// The right code is 482913 in every row. Each parcel is inside its 72-hour pickup window
    /// (the window has its own table), so the hours since delivery are held at 24.
    ///
    /// Assumptions:
    /// - `Wrong Codes In A Row` counts the wrong codes typed since the last right one.
    ///   A right code opens the locker whatever that count is, below three.
    /// - Three wrong codes in a row means the parcel is blocked, so the count is 3 for a blocked
    ///   parcel. The same refusal is given for a right and for a wrong code.
    @TableTest("""
        Scenario                        | Pickup Code | Wrong Codes In A Row | Hours Since Delivery | Code Typed       | Result?
        Right code, any earlier misses  | 482913      | {0, 1, 2}            | 24                   | 482913           | opens
        First wrong code                | 482913      | 0                    | 24                   | 482914           | wrongCode(attemptsLeft: 2)
        Second wrong code in a row      | 482913      | 1                    | 24                   | 482914           | wrongCode(attemptsLeft: 1)
        Third wrong code in a row       | 482913      | 2                    | 24                   | 482914           | blocked
        Three wrong codes already typed | 482913      | 3                    | 24                   | {482913, 482914} | blocked
        """)
    func refusesWrongCodesUntilTheThirdBlocksTheParcel(
        pickupCode: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, codeTyped: String, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }

    /// Rule: a parcel can be picked up until 72 hours after delivery; after that it goes back to the depot.
    ///
    /// The pickup window is 72 hours. `Hours Since Delivery` is whole hours, as the parcel reports it,
    /// so 72 is the last value inside the window and 73 the first outside it.
    ///
    /// Assumptions (open questions for the product owner):
    /// - Expiry takes precedence over blocking: a parcel that is blocked and also past the window
    ///   is reported as returned to the depot, as the courier has taken it away.
    /// - Past the window the answer does not depend on the code typed or on earlier wrong codes.
    @TableTest("""
        Scenario                                      | Pickup Code | Wrong Codes In A Row | Hours Since Delivery | Code Typed       | Result?
        Inside the window                             | 482913      | 0                    | {0, 72}              | 482913           | opens
        Just outside the window                       | 482913      | {0, 1, 2}            | 73                   | {482913, 482914} | returnedToDepot
        Outside the window, already three wrong codes | 482913      | 3                    | 73                   | {482913, 482914} | returnedToDepot
        """)
    func returnsAnExpiredParcelToTheDepot(
        pickupCode: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, codeTyped: String, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }
}
