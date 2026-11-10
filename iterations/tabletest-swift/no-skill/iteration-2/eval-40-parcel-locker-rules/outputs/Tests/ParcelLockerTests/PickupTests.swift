import TableTest
import Testing
@testable import ParcelLocker

// Assumptions where the rules are silent (change the rows if any is wrong):
// - A parcel is blocked once `wrongCodesInARow` has reached 3. The locker never sees a larger count
//   in practice, so only 3 is exercised.
// - "Up to 72 hours" includes hour 72: `hoursSinceDelivery` is whole hours, 72 still opens, 73 does not.
// - The depot rule wins over blocking: a parcel past 72 hours is `returnedToDepot` even if blocked,
//   because it is no longer in the locker.
// - A typed code that is not exactly the six-digit pickup code (e.g. five digits) is just a wrong code.
// - Right-code attempts are not counted; the count of wrong codes in a row is supplied by the parcel.

/// Cells for the expected result: `opens`, `blocked`, `returnedToDepot` or `wrongCode <attempts left>`.
extension PickupResult: TableTextDecodable {
    public init?(tableText: String) {
        let words = tableText.split(separator: " ").map(String.init)
        switch (words.first, words.count) {
        case ("opens", 1): self = .opens
        case ("blocked", 1): self = .blocked
        case ("returnedToDepot", 1): self = .returnedToDepot
        case ("wrongCode", 2):
            guard let attemptsLeft = Int(words[1]) else { return nil }
            self = .wrongCode(attemptsLeft: attemptsLeft)
        default: return nil
        }
    }
}

struct PickupTests {
    // The pickup code is 493817 and the parcel has been in the locker 10 hours in every row
    // (inside the 72-hour window), so only the code and the failed attempts decide the result.
    @TableTest("""
        Scenario                        | Wrong codes in a row | Typed code         | Result?
        First wrong code                | 0                    | {000000, 493818}   | wrongCode 2
        Second wrong code in a row      | 1                    | {000000, 493818}   | wrongCode 1
        Third wrong code in a row       | 2                    | {000000, 493818}   | blocked
        Code one digit short            | 0                    | 49381              | wrongCode 2
        Right code at the first attempt | 0                    | 493817             | opens
        Right code at the last attempt  | 2                    | 493817             | opens
        """)
    func refusesAWrongCodeAndCountsTheAttemptsLeft(wrongCodesInARow: Int, typedCode: String, result: PickupResult) throws {
        let parcel = Parcel(pickupCode: "493817", wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: 10)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    // Pickup code 493817, 10 hours since delivery.
    @TableTest("""
        Scenario       | Wrong codes in a row | Typed code        | Result?
        Blocked parcel | 3                    | {493817, 000000} | blocked
        """)
    func keepsABlockedParcelBlockedWhateverCodeIsTyped(wrongCodesInARow: Int, typedCode: String, result: PickupResult) throws {
        let parcel = Parcel(pickupCode: "493817", wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: 10)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    // Pickup code 493817, no wrong codes so far.
    @TableTest("""
        Scenario                   | Hours since delivery | Typed code | Result?
        Right code inside window   | {0, 72}              | 493817     | opens
        Wrong code inside window   | {0, 72}              | 000000     | wrongCode 2
        Right code past the window | 73                   | 493817     | returnedToDepot
        Wrong code past the window | 73                   | 000000     | returnedToDepot
        """)
    func returnsAParcelToTheDepotAfter72HoursWhateverCodeIsTyped(hoursSinceDelivery: Int, typedCode: String, result: PickupResult) throws {
        let parcel = Parcel(pickupCode: "493817", wrongCodesInARow: 0, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    // The one interaction between the two rules: which of them wins (see assumptions above).
    // Pickup code 493817.
    @TableTest("""
        Scenario                     | Wrong codes in a row | Hours since delivery | Typed code       | Result?
        Blocked and past the window  | 3                    | 73                   | {493817, 000000} | returnedToDepot
        """)
    func letsTheDepotRuleOverrideABlockedParcel(wrongCodesInARow: Int, hoursSinceDelivery: Int, typedCode: String, result: PickupResult) throws {
        let parcel = Parcel(pickupCode: "493817", wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }
}
