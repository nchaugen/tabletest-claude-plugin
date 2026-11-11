import TableTest
import Testing
@testable import ParcelLocker

// Assumptions where the rules leave room (change the rows if any is wrong):
// - `wrongCodesInARow` is the count BEFORE the code now typed. A wrong code at 2 is the third
//   in a row, so it blocks. A parcel arriving with 3 is already blocked.
// - A typed code that is not exactly the six-digit pickup code (too short, too long, not digits,
//   empty) is a wrong code like any other.
// - "Up to 72 hours" is inclusive: 72 whole hours still opens, 73 is past the window.
// - A blocked parcel stays blocked for any code, right or wrong.
// - When a parcel is both blocked and past the window, the depot message wins: the parcel is
//   no longer in the locker, so there is nothing for support to unblock.

struct PickupTests {

    /// Within the 72-hour window. The hours since delivery are varied over the values the
    /// attempt rules ignore: the delivery hour and the last hour of the window.
    @TableTest("Counts wrong codes and blocks the parcel on the third", table: """
        Scenario                    | Pickup code | Typed code                  | Wrong codes in a row | Hours since delivery | Result?
        Right code on a fresh parcel | 482915      | 482915                      | 0                    | {0, 72}              | opens
        Right code after two misses  | 482915      | 482915                      | 2                    | {0, 72}              | opens
        First wrong code             | 482915      | 482916                      | 0                    | {0, 72}              | wrongCode(attemptsLeft: 2)
        Second wrong code            | 482915      | 482916                      | 1                    | {0, 72}              | wrongCode(attemptsLeft: 1)
        Third wrong code in a row    | 482915      | 482916                      | 2                    | {0, 72}              | blocked
        Not a six-digit code         | 482915      | {48291, 4829155, 48291a, ''} | 0                    | {0, 72}              | wrongCode(attemptsLeft: 2)
        Already blocked              | 482915      | {482915, 482916}            | 3                    | {0, 72}              | blocked
        """)
    func countsWrongCodesAndBlocksOnTheThird(
        pickupCode: String, typedCode: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }

    /// Whole hours since delivery. The window is 72 hours; the last two rows show that
    /// expiry takes precedence over the attempt rules.
    @TableTest("Sends the parcel back to the depot after 72 hours whatever code is typed", table: """
        Scenario                          | Pickup code | Typed code               | Wrong codes in a row | Hours since delivery | Result?
        At the 72-hour limit              | 482915      | 482915                   | 0                    | 72                   | opens
        Just past the limit               | 482915      | {482915, 482916, 48291}  | 0                    | 73                   | returnedToDepot
        Wrong code that would block       | 482915      | 482916                   | 2                    | 73                   | returnedToDepot
        Blocked parcel                    | 482915      | 482915                   | 3                    | 73                   | returnedToDepot
        """)
    func sendsTheParcelBackToTheDepotAfterTheWindow(
        pickupCode: String, typedCode: String, wrongCodesInARow: Int, hoursSinceDelivery: Int, result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode, wrongCodesInARow: wrongCodesInARow, hoursSinceDelivery: hoursSinceDelivery)
        #expect(try Locker.pickUp(parcel, code: typedCode) == result)
    }
}

/// Lets a table cell spell a result as Swift does: `opens`, `wrongCode(attemptsLeft: 2)`.
extension PickupResult: TableTextDecodable {
    public init?(tableText: String) {
        switch tableText {
        case "opens": self = .opens
        case "blocked": self = .blocked
        case "returnedToDepot": self = .returnedToDepot
        default:
            let prefix = "wrongCode(attemptsLeft: "
            guard tableText.hasPrefix(prefix), tableText.hasSuffix(")"),
                  let attemptsLeft = Int(tableText.dropFirst(prefix.count).dropLast())
            else { return nil }
            self = .wrongCode(attemptsLeft: attemptsLeft)
        }
    }
}
