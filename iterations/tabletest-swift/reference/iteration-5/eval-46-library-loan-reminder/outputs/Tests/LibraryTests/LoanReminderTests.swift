import TableTest
import Testing
@testable import Library

/// Fixture: a loan of "Kristin Lavransdatter". The title changes no reminder.
struct LoanReminderTests {

    static func loan(daysUntilDue: Int, isReserved: Bool) -> Loan {
        Loan(title: "Kristin Lavransdatter", daysUntilDue: daysUntilDue, isReserved: isReserved)
    }

    @TableTest("Gives a loan the reminder its due date calls for", table: """
        Scenario                   | Days Until Due | Reserved      | Reminder?
        Four days left             | 4              | {false, true} | notDueYet
        Three days left            | 3              | false         | dueSoon
        Three days left, reserved  | 3              | true          | dueSoonReserved
        On the due date            | 0              | false         | dueSoon
        On the due date, reserved  | 0              | true          | dueSoonReserved
        One day overdue            | -1             | {false, true} | overdue
        Last day before the block  | -29            | {false, true} | overdue
        Thirty days overdue        | -30            | {false, true} | blocked
        """)
    func givesALoanTheReminderItsDueDateCallsFor(daysUntilDue: Int, isReserved: Bool, reminder: Reminder) {
        #expect(LoanReminder.reminder(for: Self.loan(daysUntilDue: daysUntilDue, isReserved: isReserved)) == reminder)
    }

    // The wording is reviewed, not tested. One check shows that the text is the rendering of
    // the decided reminder for this loan.

    @Test("Renders the reminder the loan gets")
    func rendersTheReminderTheLoanGets() {
        #expect(LoanReminder.text(for: Self.loan(daysUntilDue: 2, isReserved: true)) == """
            "Kristin Lavransdatter" is due within 3 days. Another reader has reserved it, so it \
            cannot be renewed: please return it to any branch by the due date.
            """)
    }
}
