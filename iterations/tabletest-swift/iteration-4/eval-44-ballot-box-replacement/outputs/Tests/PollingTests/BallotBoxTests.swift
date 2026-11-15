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

    static let voter = "Ana"

    /// Builds a box where `voter` has `vote` (blank: no vote), other voters hold `otherVotes`,
    /// the voter's ballot is `ballot`, and the poll is `poll`.
    static func box(poll: Poll, vote: Choice?, ballot: Ballot, otherVotes: [Choice]) -> BallotBox {
        var box = BallotBox()
        for (index, choice) in otherVotes.enumerated() {
            box.cast(choice, for: "other-\(index)")
        }
        vote.map { box.cast($0, for: voter) }
        if ballot == .sealed { box.seal(for: voter) }
        if poll == .closed { box.close() }
        return box
    }

    /// Fixture: the poll is open and the voter's ballot is not sealed in every row.
    /// Other voters' votes are listed so the tally shows that only the voter's own vote moves.
    @TableTest("""
        Scenario                      | Current Vote | New Choice | Other Votes | Replacement? | Vote After? | Tally After?
        Only voter switches sides     | yes          | no         | []          | replaced     | no          | [no: 1]
        Voter switches among others   | yes          | no         | [yes, yes]  | replaced     | no          | [yes: 2, no: 1]
        Voter picks the same choice   | yes          | yes        | [no]        | replaced     | yes         | [yes: 1, no: 1]
        Voter switches to the other   | no           | yes        | [no]        | replaced     | yes         | [yes: 1, no: 1]
        """)
    func replacesTheVotersVoteWhileThePollIsOpen(
        currentVote: Choice, newChoice: Choice, otherVotes: [Choice],
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        var box = Self.box(poll: .open, vote: currentVote, ballot: .unsealed, otherVotes: otherVotes)
        #expect(try box.replaceVote(newChoice, for: Self.voter) == replacement)
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }

    /// Each row has exactly one reason to refuse; the rules do not say which reason wins when
    /// several apply, so no row combines them (open question).
    /// A refusal leaves the voter's vote and the tally as they were.
    @TableTest("""
        Scenario                    | Poll   | Current Vote | Ballot   | New Choice | Other Votes | Replacement?             | Vote After? | Tally After?
        Poll closed                 | closed | yes          | unsealed | no         | [no]        | refused(pollClosed)      | yes         | [yes: 1, no: 1]
        Voter has not voted         | open   |              | unsealed | yes        | [no]        | refused(noVoteToReplace) |             | [no: 1]
        Voter's ballot is sealed    | open   | yes          | sealed   | no         | [no]        | refused(ballotSealed)    | yes         | [yes: 1, no: 1]
        """)
    func refusesToReplaceAVoteThatMustStay(
        poll: Poll, currentVote: Choice?, ballot: Ballot, newChoice: Choice, otherVotes: [Choice],
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        var box = Self.box(poll: poll, vote: currentVote, ballot: ballot, otherVotes: otherVotes)
        #expect(try box.replaceVote(newChoice, for: Self.voter) == replacement)
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}
