import TableTest
import Testing
@testable import ParcelLocker

struct PickupTests {
    /// Fixture: the parcel's pickup code is 482916, and it was delivered 24 hours ago (inside
    /// the 72-hour pickup window, which is the subject of the next table).
    ///
    /// A wrong code is any code other than 482916. The locker blocks a parcel on the third wrong
    /// code in a row, so `Wrong Codes In A Row` counts the wrong codes typed before this attempt.
    /// A right code opens the locker whatever the earlier count, and a blocked parcel stays blocked.
    ///
    /// Assumption: a parcel with three wrong codes in a row is already blocked.
    @TableTest("""
        Scenario                   | Wrong Codes In A Row | Typed Code       | Result?
        Right code after misses    | {0, 1, 2}            | 482916           | opens
        First attempt, mistyped    | 0                    | {482917, 842916} | wrongCode(attemptsLeft: 2)
        Second wrong code in a row | 1                    | {482917, 842916} | wrongCode(attemptsLeft: 1)
        Third wrong code in a row  | 2                    | {482917, 842916} | blocked
        Already blocked, any code  | 3                    | {482916, 482917} | blocked
        """)
    func decidesPickupFromTheTypedCode(wrongCodesInARow: Int, typedCode: String, result: PickupResult) throws {
        let parcel = Parcel(pickupCode: "482916", wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: 24)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    /// Fixture: the parcel's pickup code is 482916. The pickup window is 72 hours after delivery;
    /// `Hours Since Delivery` is whole hours, so 72 means 72:00 up to 72:59 (assumed to be inside).
    ///
    /// Once the window has passed the courier takes the parcel back to the depot, so the locker
    /// says so whatever code is typed and whatever the earlier wrong codes, also for a parcel
    /// that was blocked (assumed: the depot message wins over the blocked message).
    @TableTest("""
        Scenario                    | Hours Since Delivery | Wrong Codes In A Row | Typed Code       | Result?
        Last hour of the window     | 72                   | 0                    | 482916           | opens
        First hour after the window | 73                   | {0, 1, 2, 3}         | {482916, 482917} | returnedToDepot
        """)
    func returnsAnExpiredParcelToTheDepot(
        hoursSinceDelivery: Int, wrongCodesInARow: Int, typedCode: String, result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: "482916", wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }
}
