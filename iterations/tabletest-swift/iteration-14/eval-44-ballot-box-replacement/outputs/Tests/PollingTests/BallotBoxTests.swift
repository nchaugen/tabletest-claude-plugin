import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {
    static let voter = "ada"

    enum PollStatus: String {
        case open
        case closed

        func apply(to box: inout BallotBox) {
            if self == .closed { box.close() }
        }
    }

    enum BallotState: String {
        case unsealed
        case sealed

        func apply(to box: inout BallotBox, for voter: String) {
            if self == .sealed { box.seal(for: voter) }
        }
    }

    /// Replaces the voter's vote with the new choice, or refuses to.
    ///
    /// Fixture: the voter is "ada"; every other voter has a vote of their own, listed in
    /// `Other Votes`, so the tally shows what a replacement or a refusal does to it.
    ///
    /// Assumptions (the rules do not settle them):
    /// - A refusal for more than one reason at once (closed poll and sealed ballot, or no vote
    ///   and sealed ballot) is not specified, so no row combines reasons.
    /// - Replacing a vote with the same choice is not specified, so no row does it.
    /// - A refusal is returned as `Replacement.refused`, not thrown; `throws` only covers the stub.
    @TableTest("""
        Scenario                          | Poll   | Voter's Vote | Ballot   | Other Votes | New Choice | Replacement?             | Vote After? | Tally After?
        Change of mind among other voters | open   | yes          | unsealed | [yes, no]   | no         | replaced                 | no          | [yes: 1, no: 2]
        Change of mind by the only voter  | open   | yes          | unsealed | []          | no         | replaced                 | no          | [no: 1]
        After the deadline                | closed | yes          | unsealed | [yes]       | no         | refused(pollClosed)      | yes         | [yes: 2]
        Voter yet to vote                 | open   |              | unsealed | [yes]       | no         | refused(noVoteToReplace) |             | [yes: 1]
        Voter who signed off              | open   | yes          | sealed   | [yes]       | no         | refused(ballotSealed)    | yes         | [yes: 2]
        """)
    func replacesAVoteOnlyInAnOpenPollForAVoterWithAnUnsealedVote(
        poll: PollStatus,
        vote: Choice?,
        ballot: BallotState,
        otherVotes: [Choice],
        newChoice: Choice,
        replacement: Replacement,
        voteAfter: Choice?,
        tallyAfter: [Choice: Int]
    ) throws {
        var box = BallotBox()
        otherVotes.cast(into: &box)
        vote.cast(into: &box, for: Self.voter)
        ballot.apply(to: &box, for: Self.voter)
        poll.apply(to: &box)

        let result = try box.replaceVote(newChoice, for: Self.voter)

        #expect(result == replacement)
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}

extension Array where Element == Choice {
    /// Each choice is the vote of a different voter other than the one under test.
    fileprivate func cast(into box: inout BallotBox) {
        for (index, choice) in enumerated() {
            box.cast(choice, for: "other-\(index)")
        }
    }
}

extension Optional where Wrapped == Choice {
    /// A missing choice means the voter has not voted.
    fileprivate func cast(into box: inout BallotBox, for voter: String) {
        if let choice = self { box.cast(choice, for: voter) }
    }
}
