import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {
    enum Poll: String {
        case open
        case closed

        /// A box holding `votes`, with the ballot of `voter` sealed or not, and the poll in this state.
        func box(holding votes: [String: Choice], ballot: Ballot, of voter: String) -> BallotBox {
            var box = BallotBox()
            for (name, choice) in votes {
                box.cast(choice, for: name)
            }
            if ballot == .sealed {
                box.seal(for: voter)
            }
            if self == .closed {
                box.close()
            }
            return box
        }
    }

    enum Ballot: String {
        case sealed
        case unsealed
    }

    /// The voter replacing their vote is alice; bob's vote is there to show it is left alone.
    ///
    /// Assumptions the rules leave open, so no row depends on them:
    /// - when several refusal reasons apply at once, which one is reported;
    /// - whether a sealed ballot without a vote is refused as sealed or as no vote;
    /// - whether replacing a vote with the same choice counts as replaced.
    /// In the refused rows the replacement choice does not matter, so it takes both values.
    @TableTest("""
        Scenario                          | Poll   | Voter's Ballot | Votes Before          | Replacement Vote | Replacement?             | Vote After? | Tally After?
        Change of mind while voting       | open   | unsealed       | [alice: yes, bob: no] | no               | replaced                 | no          | [no: 2]
        Only voter for the choice         | open   | unsealed       | [alice: yes]          | no               | replaced                 | no          | [no: 1]
        Change of mind after the deadline | closed | unsealed       | [alice: yes, bob: no] | {yes, no}        | refused(pollClosed)      | yes         | [yes: 1, no: 1]
        Voter who abstained so far        | open   | unsealed       | [bob: no]             | {yes, no}        | refused(noVoteToReplace) |             | [no: 1]
        Change of mind after confirming   | open   | sealed         | [alice: yes, bob: no] | {yes, no}        | refused(ballotSealed)    | yes         | [yes: 1, no: 1]
        """)
    func replacesAVoteOnlyWhileThePollIsOpenAndTheBallotIsUnsealed(
        poll: Poll,
        ballot: Ballot,
        votesBefore: [String: Choice],
        replacementVote: Choice,
        replacement: Replacement,
        voteAfter: Choice?,
        tallyAfter: [Choice: Int]
    ) throws {
        var box = poll.box(holding: votesBefore, ballot: ballot, of: "alice")

        let result = try box.replaceVote(replacementVote, for: "alice")

        #expect(result == replacement)
        #expect(box.vote(of: "alice") == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}
