import TableTest
import Testing
@testable import Library

struct LoanReminderTests {
    /// Fixture: one loan titled "Dune". The days until due are counted from the due date (0 on the
    /// due date, negative once past it). Each tier is covered by its own first and last day.
    ///
    /// Assumption: 4 days or more is the top tier, which has no upper limit, so 365 stands for its far end.
    /// The reservation only matters in the last days before the due date (see the next test).
    @TableTest("""
        Scenario                      | Title | Days Until Due | Reserved By Another Reader | Reminder?
        Days to spare                 | Dune  | {4, 365}       | {true, false}              | '"Dune" is not due yet. Enjoy your reading, and remember that you can see all your loans and their due dates in the app.'
        First month past the due date | Dune  | {-1, -29}      | {true, false}              | '"Dune" is overdue. A fine of 5 kroner a day applies until you return it, and you can pay the fine in the app or at any branch.'
        Second month onward           | Dune  | {-30, -90}     | {true, false}              | '"Dune" is 30 days or more overdue, so your account is blocked. Return the item and pay the fine at any branch to borrow again.'
        """)
    func remindsAboutALoanOutsideTheLastDays(title: String, daysUntilDue: Int, reserved: Bool, reminder: String) {
        #expect(LoanReminder.text(for: Loan(title: title, daysUntilDue: daysUntilDue, isReserved: reserved)) == reminder)
    }

    /// Fixture: one loan titled "Dune". Within 3 days of the due date (the due date itself included)
    /// the reminder depends on whether another reader is waiting for the item.
    @TableTest("""
        Scenario                           | Title | Days Until Due | Reserved By Another Reader | Reminder?
        Last days, nobody else wants it    | Dune  | {0, 3}         | false                      | '"Dune" is due within 3 days. You can renew it once in the app, or return it to any branch before the library closes on the due date.'
        Last days, wanted by someone else  | Dune  | {0, 3}         | true                       | '"Dune" is due within 3 days. Another reader has reserved it, so it cannot be renewed: please return it to any branch by the due date.'
        """)
    func remindsAboutALoanInTheLastDays(title: String, daysUntilDue: Int, reserved: Bool, reminder: String) {
        #expect(LoanReminder.text(for: Loan(title: title, daysUntilDue: daysUntilDue, isReserved: reserved)) == reminder)
    }
}
