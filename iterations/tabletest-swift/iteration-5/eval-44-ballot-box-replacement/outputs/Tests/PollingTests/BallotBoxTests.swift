import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {
    /// Fixture: Ben has voted yes and his ballot is not sealed. Ana is the voter who asks to replace her vote.
    ///
    /// Assumptions: a refusal is returned as `refused(reason)`, not thrown. The rules give no precedence
    /// between refusal reasons, so each refused row has exactly one reason to refuse. The replacement
    /// choice does not affect a refusal, so refused rows range over both choices.
    @TableTest("""
        Scenario                        | Poll Open | Ana's Vote | Ana's Ballot Sealed | Replacement Vote | Replacement?             | Ana's Vote After? | Tally After?
        Open poll, unsealed ballot      | true      | yes        | false               | no               | replaced                 | no                | [yes: 1, no: 1]
        Same choice cast again          | true      | yes        | false               | yes              | replaced                 | yes               | [yes: 2]
        Closed poll                     | false     | yes        | false               | {yes, no}        | refused(pollClosed)      | yes               | [yes: 2]
        Voter without a vote in the box | true      |            | false               | {yes, no}        | refused(noVoteToReplace) |                   | [yes: 1]
        Sealed ballot                   | true      | yes        | true                | {yes, no}        | refused(ballotSealed)    | yes               | [yes: 2]
        """)
    func replacesAVoteOnlyWhileThePollIsOpenAndTheBallotIsUnsealed(
        pollOpen: Bool,
        vote: Choice?,
        sealed: Bool,
        replacementVote: Choice,
        replacement: Replacement,
        voteAfter: Choice?,
        tallyAfter: [Choice: Int]
    ) throws {
        var box = Self.boxWithBen(pollOpen: pollOpen, anasVote: vote, anasBallotSealed: sealed)

        let result = try box.replaceVote(replacementVote, for: "ana")

        #expect(result == replacement)
        #expect(box.vote(of: "ana") == voteAfter)
        #expect(box.tally == tallyAfter)
    }

    private static func boxWithBen(pollOpen: Bool, anasVote: Choice?, anasBallotSealed: Bool) -> BallotBox {
        var box = BallotBox()
        box.cast(.yes, for: "ben")
        if let anasVote { box.cast(anasVote, for: "ana") }
        if anasBallotSealed { box.seal(for: "ana") }
        if !pollOpen { box.close() }
        return box
    }
}
