import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {
    enum Poll: String {
        case open
        case closed

        func apply(to box: inout BallotBox) {
            switch self {
            case .open: break
            case .closed: box.close()
            }
        }
    }

    /// The state of voter ana's ballot before she tries to replace her vote.
    enum Ballot: String {
        case noVote = "no vote"
        case unsealed = "vote for yes"
        case sealed = "sealed vote for yes"

        func apply(to box: inout BallotBox) {
            switch self {
            case .noVote: break
            case .unsealed: box.cast(.yes, for: "ana")
            case .sealed:
                box.cast(.yes, for: "ana")
                box.seal(for: "ana")
            }
        }
    }

    /// Fixture: voter ben has a vote for no in every row, so the tally shows that other voters are untouched.
    /// ana is the voter who replaces her vote; her ballot before is the `Ballot` column.
    /// Assumption: the rules do not say which refusal wins when several apply at once (for example a
    /// closed poll and a sealed ballot), so each row has exactly one reason to refuse.
    @TableTest("""
        Scenario                      | Poll   | Ballot              | New Choice | Replacement?              | Vote After? | Tally After?
        Open poll, changed mind       | open   | vote for yes        | no         | replaced                  | no          | [no: 2]
        Open poll, same choice again  | open   | vote for yes        | yes        | replaced                  | yes         | [yes: 1, no: 1]
        Poll closed after voting      | closed | vote for yes        | no         | refused(pollClosed)       | yes         | [yes: 1, no: 1]
        Voter has not voted           | open   | no vote             | yes        | refused(noVoteToReplace)  |             | [no: 1]
        Voter has sealed the ballot   | open   | sealed vote for yes | no         | refused(ballotSealed)     | yes         | [yes: 1, no: 1]
        """)
    func replacesOrRefusesAVoteReplacement(
        poll: Poll, ballot: Ballot, newChoice: Choice,
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        var box = BallotBox()
        box.cast(.no, for: "ben")
        ballot.apply(to: &box)
        poll.apply(to: &box)

        let result = try box.replaceVote(newChoice, for: "ana")

        #expect(result == replacement)
        #expect(box.vote(of: "ana") == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}
