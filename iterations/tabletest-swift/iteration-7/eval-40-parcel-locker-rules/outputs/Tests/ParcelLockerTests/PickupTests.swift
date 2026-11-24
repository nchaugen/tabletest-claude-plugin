import TableTest
import Testing
@testable import ParcelLocker

struct PickupTests {
    /// Parcel still inside the 72-hour pickup window (held fixed: the window is 72 hours; hours
    /// since delivery are whole hours, and 72 is still inside it). The outcome follows the typed
    /// code and the wrong codes already typed in a row. The hours since delivery do not matter
    /// inside the window, so each row runs at both ends of it.
    ///
    /// Assumptions: a wrong code typed with two wrong codes already in a row is the third, so it
    /// blocks. A parcel with three wrong codes in a row is blocked for every code. Any wrong code
    /// counts the same, whether a near miss or too short.
    @TableTest("""
        Scenario                      | Pickup Code | Typed Code           | Wrong Codes In A Row | Hours Since Delivery | Result?
        Right code, any earlier count | 482915      | 482915               | {0, 1, 2}            | {0, 72}              | opens
        First wrong code              | 482915      | {482916, 48291}      | 0                    | {0, 72}              | wrongCode(attemptsLeft: 2)
        Second wrong code in a row    | 482915      | {482916, 48291}      | 1                    | {0, 72}              | wrongCode(attemptsLeft: 1)
        Third wrong code in a row     | 482915      | {482916, 48291}      | 2                    | {0, 72}              | blocked
        Parcel already blocked        | 482915      | {482915, 482916}     | 3                    | {0, 72}              | blocked
        """)
    func checksTheTypedCodeWhileTheParcelIsInTheLocker(
        pickupCode: String,
        typedCode: String,
        wrongCodesInARow: Int,
        hoursSinceDelivery: Int,
        result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode,
            wrongCodesInARow: wrongCodesInARow,
            hoursSinceDelivery: hoursSinceDelivery
        )
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    /// Held fixed: the pickup window is 72 hours after delivery, counted in whole hours, so hour 72
    /// is the last hour inside it. Past the window the parcel is gone from the locker, so the
    /// outcome ignores the typed code and the wrong codes already typed, a blocked parcel included.
    ///
    /// Assumption: the depot return takes precedence over a block, since the parcel is no longer
    /// in the locker and support is not the next step.
    @TableTest("""
        Scenario                    | Pickup Code | Typed Code       | Wrong Codes In A Row | Hours Since Delivery | Result?
        Inside the window           | 482915      | 482915           | 0                    | {0, 72}              | opens
        First hour past the window  | 482915      | {482915, 482916} | {0, 1, 2, 3}         | 73                   | returnedToDepot
        """)
    func returnsAnExpiredParcelToTheDepotWhateverCodeIsTyped(
        pickupCode: String,
        typedCode: String,
        wrongCodesInARow: Int,
        hoursSinceDelivery: Int,
        result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode,
            wrongCodesInARow: wrongCodesInARow,
            hoursSinceDelivery: hoursSinceDelivery
        )
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }
}
