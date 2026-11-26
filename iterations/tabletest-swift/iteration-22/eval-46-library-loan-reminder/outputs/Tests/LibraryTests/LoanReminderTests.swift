import TableTest
import Testing
@testable import Library

struct LoanReminderTests {
    /// Which reminder a loan gets, from the days until it is due and whether another reader has
    /// reserved it. Due soon is 0 to 3 days; overdue is 1 to 29 days late. The reservation only
    /// matters for a loan due soon, so every other row varies it to show that it does not.
    @TableTest("""
        Scenario                      | Days Until Due | Reserved By Another Reader | Reminder?
        More than 3 days left         | {4, 365}       | {true, false}              | notDue
        Due soon, free to renew       | {0, 3}         | false                      | dueSoonRenewable
        Due soon, reserved            | {0, 3}         | true                       | dueSoonReserved
        Overdue, within the fine band | {-1, -29}      | {true, false}              | overdue
        Overdue for a month or more   | {-30, -400}    | {true, false}              | accountBlocked
        """)
    func choosesTheReminderForALoan(daysUntilDue: Int, reserved: Bool, reminder: LoanReminder.Kind) {
        let loan = Loan(title: "Dune", daysUntilDue: daysUntilDue, isReserved: reserved)
        #expect(LoanReminder.kind(for: loan) == reminder)
    }

    /// Wiring check: the text goes through the decision above and quotes the title. The wording of
    /// each reminder is not asserted line by line, as a copy would only check that two copies agree.
    @Test func wordsTheReminderForTheLoanKindAndQuotesTheTitle() {
        let loan = Loan(title: "Dune", daysUntilDue: -1, isReserved: false)
        #expect(LoanReminder.text(for: loan) == """
            "Dune" is overdue. A fine of 5 kroner a day applies until you return it, \
            and you can pay the fine in the app or at any branch.
            """)
    }
}
