import TableTest
import Testing
@testable import Library

struct LoanReminderTests {

    @TableTest("Words the reminder a loan gets", table: """
        Scenario                   | Days Until Due | Reserved | Text?
        Four days left             | 4              | false    | '"Kristin Lavransdatter" is not due yet. Enjoy your reading, and remember that you can see all your loans and their due dates in the app.'
        Three days left            | 3              | false    | '"Kristin Lavransdatter" is due within 3 days. You can renew it once in the app, or return it to any branch before the library closes on the due date.'
        Three days left, reserved  | 3              | true     | '"Kristin Lavransdatter" is due within 3 days. Another reader has reserved it, so it cannot be renewed: please return it to any branch by the due date.'
        On the due date            | 0              | false    | '"Kristin Lavransdatter" is due within 3 days. You can renew it once in the app, or return it to any branch before the library closes on the due date.'
        One day overdue            | -1             | false    | '"Kristin Lavransdatter" is overdue. A fine of 5 kroner a day applies until you return it, and you can pay the fine in the app or at any branch.'
        Last day before the block  | -29            | false    | '"Kristin Lavransdatter" is overdue. A fine of 5 kroner a day applies until you return it, and you can pay the fine in the app or at any branch.'
        Thirty days overdue        | -30            | false    | '"Kristin Lavransdatter" is 30 days or more overdue, so your account is blocked. Return the item and pay the fine at any branch to borrow again.'
        """)
    func wordsTheReminderALoanGets(daysUntilDue: Int, isReserved: Bool, text: String) {
        #expect(LoanReminder.text(for: Loan(title: "Kristin Lavransdatter", daysUntilDue: daysUntilDue, isReserved: isReserved)) == text)
    }
}
