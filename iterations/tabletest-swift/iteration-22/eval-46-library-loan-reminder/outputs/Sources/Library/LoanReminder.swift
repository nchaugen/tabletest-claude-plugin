/// A loan of one item, as the circulation system reports it.
public struct Loan: Equatable, Sendable {
    public let title: String
    /// Days until the item is due: 0 on the due date, negative once the loan is overdue.
    public let daysUntilDue: Int
    /// Another reader has reserved the item.
    public let isReserved: Bool

    public init(title: String, daysUntilDue: Int, isReserved: Bool) {
        self.title = title
        self.daysUntilDue = daysUntilDue
        self.isReserved = isReserved
    }
}

public enum LoanReminder {
    /// Which reminder a loan gets. The raw values are what the cells of a table read.
    public enum Kind: String, CaseIterable, Equatable, Sendable {
        case notDue
        case dueSoonRenewable
        case dueSoonReserved
        case overdue
        case accountBlocked
    }

    /// The kind of reminder for one loan. A loan due within 3 days can be renewed unless another
    /// reader has reserved the item. An overdue loan costs a fine, and a loan 30 days or more
    /// overdue blocks the account.
    public static func kind(for loan: Loan) -> Kind {
        switch loan.daysUntilDue {
        case 4...: .notDue
        case 0...3 where loan.isReserved: .dueSoonReserved
        case 0...3: .dueSoonRenewable
        case -29 ... -1: .overdue
        default: .accountBlocked
        }
    }

    /// The reminder for one loan, in the app and in the e-mail.
    public static func text(for loan: Loan) -> String {
        let title = "\"\(loan.title)\""
        switch kind(for: loan) {
        case .notDue:
            return "\(title) is not due yet. Enjoy your reading, and remember that you can see all your loans and their due dates in the app."
        case .dueSoonReserved:
            return "\(title) is due within 3 days. Another reader has reserved it, so it cannot be renewed: please return it to any branch by the due date."
        case .dueSoonRenewable:
            return "\(title) is due within 3 days. You can renew it once in the app, or return it to any branch before the library closes on the due date."
        case .overdue:
            return "\(title) is overdue. A fine of 5 kroner a day applies until you return it, and you can pay the fine in the app or at any branch."
        case .accountBlocked:
            return "\(title) is 30 days or more overdue, so your account is blocked. Return the item and pay the fine at any branch to borrow again."
        }
    }
}
