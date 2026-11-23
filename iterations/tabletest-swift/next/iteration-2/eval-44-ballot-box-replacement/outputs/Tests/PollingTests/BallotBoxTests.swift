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

    static let voter = "avery"

    /// A box in which `voter` holds `voteOnFile` (when there is one) and every other voter holds
    /// one of `otherVotes`. The poll is closed and the ballot is sealed last, after all votes are cast.
    private static func box(poll: Poll, voteOnFile: Choice?, ballot: Ballot, otherVotes: [Choice]) -> BallotBox {
        var box = BallotBox()
        for (index, choice) in otherVotes.enumerated() {
            box.cast(choice, for: "other-\(index)")
        }
        if let voteOnFile { box.cast(voteOnFile, for: voter) }
        if ballot == .sealed { box.seal(for: voter) }
        if poll == .closed { box.close() }
        return box
    }

    /// Fixture: the poll is open and the voter's ballot is not sealed. The other voters' votes
    /// are in the box before and after, and none of them is touched by the replacement.
    @TableTest("""
        Scenario                           | Vote On File | Replacement Vote | Other Votes | Vote After? | Tally After?
        Only voter, yes changed to no      | yes          | no               | []          | no          | [no: 1]
        Only voter, no changed to yes      | no           | yes              | []          | yes         | [yes: 1]
        Replacement equals the vote        | yes          | yes              | []          | yes         | [yes: 1]
        Among other voters                 | yes          | no               | [yes, no]   | no          | [yes: 1, no: 2]
        """)
    func replacesTheVoteWhileThePollIsOpen(
        voteOnFile: Choice, replacementVote: Choice, otherVotes: [Choice], voteAfter: Choice, tallyAfter: [Choice: Int]
    ) throws {
        var box = Self.box(poll: .open, voteOnFile: voteOnFile, ballot: .unsealed, otherVotes: otherVotes)

        let replacement = try box.replaceVote(replacementVote, for: Self.voter)

        #expect(replacement == .replaced)
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }

    /// Fixture: each row meets exactly one reason for refusing, so the rows do not say which
    /// refusal wins when several apply (for example a closed poll and a sealed ballot).
    /// A voter with no vote has an unsealed ballot. The replacement vote is either choice:
    /// the refusal does not depend on it.
    @TableTest("""
        Scenario                    | Poll   | Vote On File | Ballot   | Replacement Vote | Other Votes | Refusal?        | Vote After? | Tally After?
        Poll closed                 | closed | yes          | unsealed | {yes, no}        | [no]        | pollClosed      | yes         | [yes: 1, no: 1]
        Voter has not voted         | open   |              | unsealed | {yes, no}        | [no]        | noVoteToReplace |             | [no: 1]
        Ballot sealed               | open   | yes          | sealed   | {yes, no}        | [no]        | ballotSealed    | yes         | [yes: 1, no: 1]
        """)
    func refusesAReplacementAndChangesNothing(
        poll: Poll, voteOnFile: Choice?, ballot: Ballot, replacementVote: Choice, otherVotes: [Choice],
        refusal: Refusal, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        var box = Self.box(poll: poll, voteOnFile: voteOnFile, ballot: ballot, otherVotes: otherVotes)

        let replacement = try box.replaceVote(replacementVote, for: Self.voter)

        #expect(replacement == .refused(refusal))
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}
