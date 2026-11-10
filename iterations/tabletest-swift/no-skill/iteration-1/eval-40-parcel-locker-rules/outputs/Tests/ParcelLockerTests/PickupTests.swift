import TableTest
import Testing
@testable import ParcelLocker

// Assumptions where the rules leave room (change the rows if the product decides otherwise):
// - `wrongCodesInARow` is the count *before* this attempt. A parcel with 3 is already blocked.
// - A code that is not the parcel's code counts as wrong, whatever its shape
//   (too short, too long, not digits). There is no separate "malformed" outcome.
// - "Up to 72 hours" is read on the whole hours the parcel reports: 72 is still
//   inside the window, 73 is outside.
// - Once the window has closed the courier has taken the parcel back, so the depot
//   message wins over blocking: an expired parcel is never reported as blocked or wrong.
// - A right code does not need to reset anything here; `pickUp` only reports the outcome.

struct PickupTests {

    @TableTest("""
        Scenario                       | Pickup Code | Entered Code                    | Wrong Codes In A Row | Hours Since Delivery | Result?
        Right code, no earlier misses  | 123456      | 123456                          | 0                    | {0, 72}              | opens
        Right code after misses        | 123456      | 123456                          | {1, 2}               | {0, 72}              | opens
        First wrong code               | 123456      | 654321                          | 0                    | {0, 72}              | wrongCode(attemptsLeft: 2)
        Second wrong code in a row     | 123456      | 654321                          | 1                    | {0, 72}              | wrongCode(attemptsLeft: 1)
        Third wrong code in a row      | 123456      | 654321                          | 2                    | {0, 72}              | blocked
        Not six digits or not digits   | 123456      | {12345, 1234567, 12345a, ''}    | 0                    | 24                   | wrongCode(attemptsLeft: 2)
        Already blocked                | 123456      | {123456, 654321}                | 3                    | {0, 72}              | blocked
        """)
    func answersTheCodeByAttemptsUsed(
        pickupCode: String,
        enteredCode: String,
        wrongCodesInARow: Int,
        hoursSinceDelivery: Int,
        result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode,
            wrongCodesInARow: wrongCodesInARow,
            hoursSinceDelivery: hoursSinceDelivery
        )

        #expect(try Locker.pickUp(parcel, code: enteredCode) == result)
    }

    // The 72-hour window, for a parcel that is not blocked (Wrong Codes In A Row is 0 throughout).
    @TableTest("""
        Scenario                    | Pickup Code | Entered Code               | Hours Since Delivery | Result?
        Last hour of the window     | 123456      | 123456                     | 72                   | opens
        Wrong code in the window    | 123456      | 654321                     | 72                   | wrongCode(attemptsLeft: 2)
        First hour after the window | 123456      | {123456, 654321, 12345}    | 73                   | returnedToDepot
        """)
    func returnsTheParcelToTheDepotAfter72Hours(
        pickupCode: String,
        enteredCode: String,
        hoursSinceDelivery: Int,
        result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode,
            wrongCodesInARow: 0,
            hoursSinceDelivery: hoursSinceDelivery
        )

        #expect(try Locker.pickUp(parcel, code: enteredCode) == result)
    }

    // Precedence: the depot message beats the attempt counter. Only these rows show it;
    // the two tables above show each rule alone.
    @TableTest("""
        Scenario                          | Pickup Code | Entered Code     | Wrong Codes In A Row | Hours Since Delivery | Result?
        Would have been the third miss    | 123456      | 654321           | 2                    | 73                   | returnedToDepot
        Blocked before the window closed  | 123456      | {123456, 654321} | 3                    | 73                   | returnedToDepot
        """)
    func prefersTheDepotMessageOverTheAttemptCounter(
        pickupCode: String,
        enteredCode: String,
        wrongCodesInARow: Int,
        hoursSinceDelivery: Int,
        result: PickupResult
    ) throws {
        let parcel = Parcel(
            pickupCode: pickupCode,
            wrongCodesInARow: wrongCodesInARow,
            hoursSinceDelivery: hoursSinceDelivery
        )

        #expect(try Locker.pickUp(parcel, code: enteredCode) == result)
    }
}

/// Reads `opens`, `blocked`, `returnedToDepot` and `wrongCode(attemptsLeft: 2)` in table cells.
extension PickupResult: TableTextDecodable {
    public init?(tableText: String) {
        let wrongCodePrefix = "wrongCode(attemptsLeft: "
        switch tableText {
        case "opens": self = .opens
        case "blocked": self = .blocked
        case "returnedToDepot": self = .returnedToDepot
        case _ where tableText.hasPrefix(wrongCodePrefix) && tableText.hasSuffix(")"):
            let digits = tableText.dropFirst(wrongCodePrefix.count).dropLast()
            guard let attemptsLeft = Int(digits) else { return nil }
            self = .wrongCode(attemptsLeft: attemptsLeft)
        default: return nil
        }
    }
}
