import TableTest
import Testing
@testable import Library

extension LoanReminder.Kind: TableTextDecodable {
    public init?(tableText: String) { self.init(rawValue: tableText) }
}

struct LoanReminderTests {
    /// The decision behind the reminder text. `Days Until Due` is 0 on the due date and negative
    /// once overdue. A reservation by another reader only matters while the loan is due within
    /// 3 days; in every other tier the table varies it to show it is ignored.
    /// Open question: the spec says "within 3 days" and "30 days or more overdue"; read here as
    /// 0...3 and -30 or lower, as the code does.
    @TableTest("""
        Scenario                             | Days Until Due | Reserved      | Reminder?
        First day outside the renewal window | 4              | {true, false} | not due
        Due soon, nobody else waiting        | {0, 3}         | false         | can renew
        Due soon, another reader waiting     | {0, 3}         | true          | must return
        Within the first 29 days past due    | {-1, -29}      | {true, false} | fined
        Thirty days past due                 | -30            | {true, false} | account blocked
        """)
    func choosesTheReminderForALoan(daysUntilDue: Int, reserved: Bool, reminder: LoanReminder.Kind) {
        let loan = Loan(title: "Dune", daysUntilDue: daysUntilDue, isReserved: reserved)
        #expect(LoanReminder.kind(for: loan) == reminder)
    }

    /// Wiring check: the text is built from the decision and quotes the title. The other
    /// sentences are constants, so they are not asserted one by one.
    @Test func writesTheReservedReminderWithTheQuotedTitle() {
        let loan = Loan(title: "Dune", daysUntilDue: 2, isReserved: true)
        #expect(LoanReminder.text(for: loan) == """
            "Dune" is due within 3 days. Another reader has reserved it, so it cannot be renewed: \
            please return it to any branch by the due date.
            """)
    }
}
