import TableTest
import Testing
@testable import ParcelLocker

struct PickupTests {
    /// What the locker does with a code typed within the 72 hours after delivery.
    ///
    /// `Wrong Codes In A Row` is the count before this attempt. Hours Since Delivery is a value set
    /// of the first and last hour of the window: this rule claims the hour does not matter inside it.
    ///
    /// Assumptions where the rules are silent: a code that is not exactly the six digits of the
    /// pickup code (too short, too long, letters) counts as a wrong code, and a parcel that
    /// already has three wrong codes in a row is blocked.
    @TableTest("""
        Scenario                     | Pickup Code | Code Typed               | Wrong Codes In A Row | Hours Since Delivery | Result?
        Pickup code after mistakes   | 482913      | 482913                   | {0, 1, 2}            | {0, 72}              | opens
        First mistake                | 482913      | 000000                   | 0                    | {0, 72}              | wrongCode(attemptsLeft: 2)
        Second mistake in a row      | 482913      | 000000                   | 1                    | {0, 72}              | wrongCode(attemptsLeft: 1)
        Third mistake in a row       | 482913      | 000000                   | 2                    | {0, 72}              | blocked
        Typing after three mistakes  | 482913      | {482913, 000000}         | 3                    | {0, 72}              | blocked
        Entry that is not six digits | 482913      | {48291, 4829130, abcdef} | 0                    | {0, 72}              | wrongCode(attemptsLeft: 2)
        """)
    func countsWrongCodesAndBlocksTheParcel(
        pickupCode: String,
        codeTyped: String,
        wrongCodesInARow: Int,
        hoursSinceDelivery: Int,
        result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode,
            wrongCodesInARow: wrongCodesInARow,
            hoursSinceDelivery: hoursSinceDelivery
        )
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }

    /// What the locker does once the pickup window may have run out, whatever code is typed.
    ///
    /// The window is 72 hours after delivery, and the 72nd hour is still inside it. The locker has no
    /// setting for the window, so `Pickup Window (Hours)` documents the limit the rows straddle.
    ///
    /// Assumption where the rules are silent: the window outranks the block, so an expired parcel
    /// that also has three wrong codes in a row is returned to the depot.
    @TableTest("""
        Scenario                             | Pickup Code | Code Typed       | Wrong Codes In A Row | Hours Since Delivery | Pickup Window (Hours) | Result?
        Last hour of the window              | 482913      | 482913           | 0                    | 72                   | 72                    | opens
        First hour after the window          | 482913      | {482913, 000000} | 0                    | 73                   | 72                    | returnedToDepot
        Past the window with attempts left   | 482913      | {482913, 000000} | {1, 2}               | 73                   | 72                    | returnedToDepot
        Past the window after three mistakes | 482913      | {482913, 000000} | 3                    | 73                   | 72                    | returnedToDepot
        """)
    func returnsAnExpiredParcelToTheDepot(
        pickupCode: String,
        codeTyped: String,
        wrongCodesInARow: Int,
        hoursSinceDelivery: Int,
        pickupWindowHours: Int,
        result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode,
            wrongCodesInARow: wrongCodesInARow,
            hoursSinceDelivery: hoursSinceDelivery
        )
        #expect(try Locker.pickUp(parcel, code: codeTyped) == result)
    }
}
