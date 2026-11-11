import TableTest
import Testing
@testable import ParcelLocker

struct PickupTests {
    /// The parcel was delivered 12 hours ago, well inside the 72-hour pickup window, so only the
    /// code and the wrong-code count decide the result.
    ///
    /// Assumptions:
    /// - A code that is not the six-digit pickup code (too short, too long) is a wrong code.
    /// - The third wrong code in a row blocks the parcel, so the counter before it is 2.
    /// - A counter of 3 means the parcel is already blocked.
    @TableTest("Checks the typed code and blocks the parcel on the third wrong code", table: """
        Scenario                          | Pickup Code | Code Typed                | Wrong Codes In A Row | Hours Since Delivery | Result?
        First attempt                     | 482913      | 482913                    | 0                    | 12                   | opens
        Right code after two wrong codes  | 482913      | 482913                    | 2                    | 12                   | opens
        First wrong code                  | 482913      | {482914, 48291, 4829130}  | 0                    | 12                   | wrongCode(attemptsLeft: 2)
        Second wrong code in a row        | 482913      | 482914                    | 1                    | 12                   | wrongCode(attemptsLeft: 1)
        Third wrong code in a row         | 482913      | 482914                    | 2                    | 12                   | blocked
        Parcel already blocked            | 482913      | {482913, 482914}          | 3                    | 12                   | blocked
        """)
    func checksTheTypedCode(
        pickupCode: String, codeTyped: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }

    /// The pickup window is 72 hours from delivery, counted in whole hours: 72 is still inside it.
    ///
    /// Assumption: an expired parcel is returned to the depot even if it is also blocked,
    /// because the locker says so "whatever code is typed".
    @TableTest("Returns a parcel to the depot after the 72-hour pickup window", table: """
        Scenario                    | Pickup Code | Code Typed       | Wrong Codes In A Row | Hours Since Delivery | Result?
        Just delivered              | 482913      | 482913           | 0                    | 0                    | opens
        Last hour of the window     | 482913      | 482913           | 0                    | 72                   | opens
        First hour past the window  | 482913      | {482913, 482914} | {0, 1, 2, 3}         | 73                   | returnedToDepot
        """)
    func returnsAnExpiredParcelToTheDepot(
        pickupCode: String, codeTyped: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }
}
