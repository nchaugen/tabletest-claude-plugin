import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {
    enum Poll: String {
        case open
        case closed
    }

    enum Ballot: String {
        case unsealed
        case sealed
    }

    static let voter = "ana"
    static let otherVoter = "bea"

    /// A box holding the other voter's vote for no, plus the voter's vote when they have one.
    private static func box(poll: Poll, voteBefore: Choice?, ballot: Ballot) -> BallotBox {
        var box = BallotBox()
        box.cast(.no, for: otherVoter)
        if let voteBefore { box.cast(voteBefore, for: voter) }
        if ballot == .sealed { box.seal(for: voter) }
        if poll == .closed { box.close() }
        return box
    }

    /// Fixture: a second voter has voted no, so the tally shows that other votes stay as they were.
    /// Each refusal row has only its own reason to refuse. Which refusal wins when several apply
    /// is not specified, so no row combines them.
    @TableTest("""
        Scenario                   | Poll   | Vote Before | Ballot   | New Vote | Replacement?                | Vote After? | Tally After?
        Open poll, yes to no       | open   | yes         | unsealed | no       | replaced                    | no          | [no: 2]
        Open poll, no to yes       | open   | no          | unsealed | yes      | replaced                    | yes         | [yes: 1, no: 1]
        Open poll, same vote again | open   | yes         | unsealed | yes      | replaced                    | yes         | [yes: 1, no: 1]
        Poll closed after voting   | closed | yes         | unsealed | no       | refused(pollClosed)         | yes         | [yes: 1, no: 1]
        Voter has not voted        | open   |             | unsealed | yes      | refused(noVoteToReplace)    |             | [no: 1]
        Voter sealed their ballot  | open   | yes         | sealed   | no       | refused(ballotSealed)       | yes         | [yes: 1, no: 1]
        """)
    func replacesAVoteOrRefusesWithoutChangingAnything(
        poll: Poll, voteBefore: Choice?, ballot: Ballot, newVote: Choice,
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        var box = Self.box(poll: poll, voteBefore: voteBefore, ballot: ballot)

        let result = try box.replaceVote(newVote, for: Self.voter)

        #expect(result == replacement)
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}
