import TableTest
import Testing
@testable import Library

struct LoanReminderTests {
    /// Each value set spans its tier from the first to the last day the tier holds, so every
    /// boundary (4/3, 0/-1, -29/-30) is straddled by neighbouring rows. Whether another reader has
    /// reserved the item only matters inside the renewal window; elsewhere it varies to show that.
    /// The whole reminder text is compared, because the wording is what the reader receives.
    @TableTest("""
        Scenario                           | Title | Days Until Due | Reserved      | Reminder?
        Four days or more ahead            | Dune  | {4, 365}       | {true, false} | '"Dune" is not due yet. Enjoy your reading, and remember that you can see all your loans and their due dates in the app.'
        Within 3 days, nobody waiting      | Dune  | {0, 3}         | false         | '"Dune" is due within 3 days. You can renew it once in the app, or return it to any branch before the library closes on the due date.'
        Within 3 days, with a waiting list | Dune  | {0, 3}         | true          | '"Dune" is due within 3 days. Another reader has reserved it, so it cannot be renewed: please return it to any branch by the due date.'
        Under 30 days past the due date    | Dune  | {-29, -1}      | {true, false} | '"Dune" is overdue. A fine of 5 kroner a day applies until you return it, and you can pay the fine in the app or at any branch.'
        A month or more past the due date  | Dune  | {-30, -100}    | {true, false} | '"Dune" is 30 days or more overdue, so your account is blocked. Return the item and pay the fine at any branch to borrow again.'
        """)
    func choosesTheReminderFromTheDueDateAndReservation(
        title: String, daysUntilDue: Int, reserved: Bool, reminder: String
    ) {
        let loan = Loan(title: title, daysUntilDue: daysUntilDue, isReserved: reserved)
        #expect(LoanReminder.text(for: loan) == reminder)
    }

    /// Only the opening of the reminder is compared: the wording after the title belongs to the
    /// table above. The due date and reservation vary across the values this rule ignores.
    @TableTest("""
        Scenario                   | Title            | Days Until Due  | Reserved      | Opening?
        Plain title                | Dune             | {5, 2, -3, -40} | {true, false} | '"Dune" is'
        Empty title                | ''               | {5, 2, -3, -40} | {true, false} | '"" is'
        Title containing quotes    | Dune "Messiah"   | {5, 2, -3, -40} | {true, false} | '"Dune "Messiah"" is'
        Title containing a divider | 'Dune | Messiah' | {5, 2, -3, -40} | {true, false} | '"Dune | Messiah" is'
        """)
    func putsTheTitleInQuotesAtTheStartOfTheReminder(
        title: String, daysUntilDue: Int, reserved: Bool, opening: String
    ) {
        let loan = Loan(title: title, daysUntilDue: daysUntilDue, isReserved: reserved)
        #expect(LoanReminder.text(for: loan).hasPrefix(opening))
    }
}
