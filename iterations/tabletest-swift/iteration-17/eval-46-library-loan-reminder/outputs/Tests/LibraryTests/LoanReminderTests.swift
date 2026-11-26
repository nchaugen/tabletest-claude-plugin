import TableTest
import Testing
@testable import Library

struct LoanReminderTests {
    /// Every loan is for the same title. The wording of each reminder is checked once, in
    /// `wordsTheReminderForTheKindOfLoan`; this table covers which reminder a loan gets. Whether
    /// another reader has reserved the item matters only for a loan due within 3 days.
    @TableTest("""
        Scenario                          | Days Until Due | Reserved By Another Reader | Reminder?
        More than 3 days left             | {4, 60}        | {true, false}              | notDue
        Due within 3 days, nobody waiting | {0, 3}         | false                      | renewable
        Due within 3 days, someone waits  | {0, 3}         | true                       | notRenewable
        Past the due date, below 30 days  | {-1, -29}      | {true, false}              | overdue
        Past the due date by 30 days      | {-30, -90}     | {true, false}              | blocked
        """)
    func choosesTheReminderForALoan(daysUntilDue: Int, reservedByAnotherReader: Bool, reminder: LoanReminder.Kind) {
        let loan = Loan(title: "Dune", daysUntilDue: daysUntilDue, isReserved: reservedByAnotherReader)

        #expect(LoanReminder.kind(for: loan) == reminder)
    }

    /// The wiring check: the text is built from the chosen reminder, and carries the title in quotes.
    @Test func wordsTheReminderForTheKindOfLoan() {
        let loan = Loan(title: "Dune", daysUntilDue: -30, isReserved: false)

        #expect(LoanReminder.text(for: loan) == """
            "Dune" is 30 days or more overdue, so your account is blocked. \
            Return the item and pay the fine at any branch to borrow again.
            """)
    }
}
