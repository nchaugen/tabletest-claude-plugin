import TableTest
import Testing
@testable import ParcelLocker

struct PickupTests {
    /// The pickup window: a parcel can be picked up for up to 72 hours after delivery, 72 included.
    /// `Hours Since Delivery` counts whole hours, as `Parcel` does.
    ///
    /// Assumptions (the requirements leave these open):
    /// - Past the window the courier has taken the parcel back, so the depot message wins over a block:
    ///   it is given whatever the code and whatever the earlier wrong codes.
    /// - Within the window, the first row says that a right code opens at the last hour.
    @TableTest("""
        Scenario                   | Hours Since Delivery | Pickup Code | Typed Code       | Wrong Codes In A Row | Result?
        Last hour of the window    | 72                   | 493027      | 493027           | 0                    | opens
        First hour past the window | 73                   | 493027      | {493027, 493028} | {0, 1, 2, 3}         | returnedToDepot
        """)
    func sendsAnExpiredParcelBackToTheDepot(
        hoursSinceDelivery: Int, pickupCode: String, typedCode: String, wrongCodesInARow: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    /// Within the pickup window (the parcel was delivered 10 hours ago; the window has its own table).
    ///
    /// Assumptions:
    /// - A parcel is blocked once `Wrong Codes In A Row` has reached 3, so a count above 3 is blocked too.
    /// - A typed code that is not six digits is a wrong code, like any other wrong code.
    /// - A right code opens the locker while fewer than 3 wrong codes are on record, whatever the count.
    @TableTest("""
        Scenario                            | Pickup Code | Typed Code       | Wrong Codes In A Row | Result?
        Right code, attempts left or used   | 493027      | 493027           | {0, 1, 2}            | opens
        Wrong code, no earlier wrong code   | 493027      | 493028           | 0                    | wrongCode(attemptsLeft: 2)
        Wrong code, one earlier wrong code  | 493027      | 493028           | 1                    | wrongCode(attemptsLeft: 1)
        Wrong code, two earlier wrong codes | 493027      | 493028           | 2                    | blocked
        Code with five digits               | 493027      | 49302            | 0                    | wrongCode(attemptsLeft: 2)
        Parcel already blocked              | 493027      | {493027, 493028} | {3, 4}               | blocked
        """)
    func refusesAWrongCodeAndBlocksTheThirdInARow(
        pickupCode: String, typedCode: String, wrongCodesInARow: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: 10)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }
}
