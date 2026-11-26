import TableTest
import Testing
@testable import Library

struct LoanReminderTests {
    /// The decision behind the reminder text. Days Until Due is 0 on the due date and negative once
    /// the loan is overdue. The renewal window is the last 3 days before the due date, and an
    /// account is blocked from 30 days overdue; the code holds both limits inline, so the
    /// boundary rows are what pin them. Only inside the renewal window does a reservation matter.
    @TableTest("""
        Scenario                                 | Days Until Due | Reserved By Another Reader | Reminder?
        Before the renewal window opens          | {4, 60}        | {true, false}              | notDue
        Inside the renewal window, free to renew | {0, 3}         | false                      | renewable
        Inside the renewal window, item on hold  | {0, 3}         | true                       | reservedDueSoon
        After the due date, before the block     | {-29, -1}      | {true, false}              | overdue
        At the block threshold and beyond        | {-30, -90}     | {true, false}              | accountBlocked
        """)
    func choosesTheReminderForALoan(daysUntilDue: Int, reserved: Bool, reminder: LoanReminder.Kind) {
        let loan = Loan(title: "Peer Gynt", daysUntilDue: daysUntilDue, isReserved: reserved)
        #expect(LoanReminder.kind(for: loan) == reminder)
    }

    /// Wiring check: the text is the wording of the decided reminder, with the title in quotes.
    /// The sentences themselves are not asserted one by one; a copy would only check itself.
    @Test func wordsTheReminderWithTheQuotedTitle() {
        let loan = Loan(title: "Peer Gynt", daysUntilDue: 2, isReserved: false)
        #expect(LoanReminder.text(for: loan) == """
            "Peer Gynt" is due within 3 days. \
            You can renew it once in the app, or return it to any branch before the library closes on the due date.
            """)
    }
}
