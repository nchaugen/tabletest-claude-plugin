import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {

    /// Where the box stands when Ann asks to replace her vote. Ben voted no and Cy voted yes in
    /// every situation.
    enum Situation: String {
        case openPoll = "open poll"
        case closedPoll = "closed poll"
        case sealedBallot = "Ann's ballot sealed"
        case notVoted = "Ann has not voted"

        var box: BallotBox {
            var box = BallotBox()
            box.cast(.no, for: "Ben")
            box.cast(.yes, for: "Cy")
            switch self {
            case .openPoll:
                box.cast(.yes, for: "Ann")
            case .closedPoll:
                box.cast(.yes, for: "Ann")
                box.close()
            case .sealedBallot:
                box.cast(.yes, for: "Ann")
                box.seal(for: "Ann")
            case .notVoted:
                break
            }
            return box
        }
    }

    /// Fixture: Ann voted yes, except where she has not voted. She asks to replace her vote with
    /// no. The open poll is the one case where the box changes, so the rows show that the refusals
    /// leave Ann's vote and the tally as they were.
    @TableTest("Replaces a vote only while the poll is open", table: """
        Situation           | Result?                  | Ann's Vote After? | Tally After?
        open poll           | replaced                 | no                | [yes: 1, no: 2]
        closed poll         | refused(pollClosed)      | yes               | [yes: 2, no: 1]
        Ann's ballot sealed | refused(ballotSealed)    | yes               | [yes: 2, no: 1]
        Ann has not voted   | refused(noVoteToReplace) |                   | [yes: 1, no: 1]
        """)
    func replacesAVoteOnlyWhileThePollIsOpen(situation: Situation, result: Replacement, voteAfter: Choice?,
                                             tallyAfter: [Choice: Int]) throws {
        var box = situation.box

        #expect(try box.replaceVote(.no, for: "Ann") == result)
        #expect(box.vote(of: "Ann") == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}
