import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {
    enum PollState: String {
        case open
        case closed

        func applied(to box: inout BallotBox) {
            if self == .closed { box.close() }
        }
    }

    enum Ballot: String {
        case unsealed
        case sealed

        func applied(to box: inout BallotBox, for voter: String) {
            if self == .sealed { box.seal(for: voter) }
        }
    }

    enum VoteBefore: String {
        case none = "no vote"
        case yes
        case no

        func applied(to box: inout BallotBox, for voter: String) {
            switch self {
            case .none: break
            case .yes: box.cast(.yes, for: voter)
            case .no: box.cast(.no, for: voter)
            }
        }
    }

    /// Fixture: the voter is Ada. Ben has also voted yes and is never the one replacing, so the tally
    /// shows whether a refusal or a replacement touches anyone else.
    ///
    /// Assumptions where the rules leave it open:
    /// - A refusal leaves the box exactly as it was, so a refused row shows the vote and tally before.
    /// - Which refusal wins when several apply is not specified, so each row has at most one reason.
    /// - Replacing a vote with the same choice is a replacement, not a refusal.
    @TableTest("""
        Scenario                          | Poll   | Vote Before | Ballot   | New Vote | Replacement?            | Vote After? | Tally After?
        Change of mind during the poll    | open   | yes         | unsealed | no       | replaced                | no          | [yes: 1, no: 1]
        Same answer again during the poll | open   | yes         | unsealed | yes      | replaced                | yes         | [yes: 2]
        Change after the deadline         | closed | yes         | unsealed | no       | refused(pollClosed)     | yes         | [yes: 2]
        Change by someone who abstained   | open   | no vote     | unsealed | no       | refused(noVoteToReplace) |            | [yes: 1]
        Change after confirmation         | open   | yes         | sealed   | no       | refused(ballotSealed)   | yes         | [yes: 2]
        """)
    func replacesAVoteOnlyWhileTheBoxAllowsIt(
        poll: PollState, voteBefore: VoteBefore, ballot: Ballot, newVote: Choice,
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        var box = BallotBox()
        box.cast(.yes, for: "Ben")
        voteBefore.applied(to: &box, for: "Ada")
        ballot.applied(to: &box, for: "Ada")
        poll.applied(to: &box)

        let result = try box.replaceVote(newVote, for: "Ada")

        #expect(result == replacement)
        #expect(box.vote(of: "Ada") == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}
