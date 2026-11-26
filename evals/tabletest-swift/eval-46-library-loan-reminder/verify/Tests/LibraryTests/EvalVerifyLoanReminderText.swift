import Testing
@testable import Library

/// The eval's own check that `text(for:)` still gives the text the scaffold gave, on both sides of
/// each line. The runner adds this file after the agent has finished. Not part of the answer.
struct EvalVerifyLoanReminderText {
    static let enjoy = "Enjoy your reading, and remember that you can see all your loans and their due dates in the app."
    static let reserved = "Another reader has reserved it, so it cannot be renewed: please return it to any branch by the due date."
    static let renew = "You can renew it once in the app, or return it to any branch before the library closes on the due date."
    static let fine = "A fine of 5 kroner a day applies until you return it, and you can pay the fine in the app or at any branch."
    static let blocked = "so your account is blocked. Return the item and pay the fine at any branch to borrow again."

    static func text(_ days: Int, reserved: Bool = false) -> String {
        LoanReminder.text(for: Loan(title: "Kristin Lavransdatter", daysUntilDue: days, isReserved: reserved))
    }

    @Test func keepsTheTextOnBothSidesOfEachLine() {
        #expect(Self.text(4) == "\"Kristin Lavransdatter\" is not due yet. \(Self.enjoy)")
        #expect(Self.text(4, reserved: true) == "\"Kristin Lavransdatter\" is not due yet. \(Self.enjoy)")
        #expect(Self.text(3) == "\"Kristin Lavransdatter\" is due within 3 days. \(Self.renew)")
        #expect(Self.text(3, reserved: true) == "\"Kristin Lavransdatter\" is due within 3 days. \(Self.reserved)")
        #expect(Self.text(0) == "\"Kristin Lavransdatter\" is due within 3 days. \(Self.renew)")
        #expect(Self.text(0, reserved: true) == "\"Kristin Lavransdatter\" is due within 3 days. \(Self.reserved)")
        #expect(Self.text(-1) == "\"Kristin Lavransdatter\" is overdue. \(Self.fine)")
        #expect(Self.text(-1, reserved: true) == "\"Kristin Lavransdatter\" is overdue. \(Self.fine)")
        #expect(Self.text(-29) == "\"Kristin Lavransdatter\" is overdue. \(Self.fine)")
        #expect(Self.text(-30) == "\"Kristin Lavransdatter\" is 30 days or more overdue, \(Self.blocked)")
        #expect(Self.text(-45, reserved: true) == "\"Kristin Lavransdatter\" is 30 days or more overdue, \(Self.blocked)")
    }
}
