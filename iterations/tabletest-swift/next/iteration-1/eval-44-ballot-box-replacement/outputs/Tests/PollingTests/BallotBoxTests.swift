import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {
    enum Poll: String {
        case open
        case closed

        func apply(to box: inout BallotBox) {
            if self == .closed { box.close() }
        }
    }

    enum Ballot: String {
        case unsealed
        case sealed

        func apply(to box: inout BallotBox, for voter: String) {
            if self == .sealed { box.seal(for: voter) }
        }
    }

    static let voter = "ana"

    /// Replacing a vote, and the refusals that leave the box as it was.
    ///
    /// Fixture: the box holds one more vote, from another voter, for `yes`, so the tally shows
    /// whether a replacement or a refusal touched anything beyond the voter's own vote.
    ///
    /// Assumptions, as the requirements do not settle them:
    /// - Each refused row has only its own refusal reason. Which reason wins when several apply
    ///   (a closed poll and a sealed ballot, say) is an open question and is not specified here.
    /// - A refusal does not depend on the choice offered, so the refused rows vary it.
    /// - Replacing a vote with the same choice is a replacement, not a refusal.
    @TableTest("""
        Scenario                     | Poll   | Ballot   | Vote Before | Replacement | Result?                  | Vote After? | Tally After?
        Open poll, vote changes      | open   | unsealed | yes         | no          | replaced                 | no          | [yes: 1, no: 1]
        Open poll, vote changes back | open   | unsealed | no          | yes         | replaced                 | yes         | [yes: 2]
        Open poll, same choice again | open   | unsealed | yes         | yes         | replaced                 | yes         | [yes: 2]
        Poll closed after voting     | closed | unsealed | yes         | {yes, no}   | refused(pollClosed)      | yes         | [yes: 2]
        Voter has not voted          | open   | unsealed |             | {yes, no}   | refused(noVoteToReplace) |             | [yes: 1]
        Voter's ballot sealed        | open   | sealed   | yes         | {yes, no}   | refused(ballotSealed)    | yes         | [yes: 2]
        """)
    func replacesAVoteUnlessTheBoxRefuses(
        poll: Poll,
        ballot: Ballot,
        voteBefore: Choice?,
        replacement: Choice,
        result: Replacement,
        voteAfter: Choice?,
        tallyAfter: [Choice: Int]
    ) throws {
        var box = BallotBox()
        box.cast(.yes, for: "bo")
        voteBefore.cast(into: &box, for: Self.voter)
        ballot.apply(to: &box, for: Self.voter)
        poll.apply(to: &box)

        let outcome = try box.replaceVote(replacement, for: Self.voter)

        #expect(outcome == result)
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}

extension Optional where Wrapped == Choice {
    fileprivate func cast(into box: inout BallotBox, for voter: String) {
        if let choice = self { box.cast(choice, for: voter) }
    }
}
