import TableTest
import Testing
@testable import Polling

struct BallotBoxTests {
    /// The poll state a row starts in.
    enum Poll: String {
        case open
        case closed

        func apply(to box: inout BallotBox) {
            if self == .closed { box.close() }
        }
    }

    /// The situation of Ana's ballot a row starts in, written in the words of a voter.
    struct Ballot: TableTextDecodable {
        let vote: Choice?
        let isSealed: Bool

        init?(tableText: String) {
            switch tableText {
            case "no vote": (vote, isSealed) = (nil, false)
            case "voted yes": (vote, isSealed) = (.yes, false)
            case "voted no": (vote, isSealed) = (.no, false)
            case "voted yes, sealed": (vote, isSealed) = (.yes, true)
            default: return nil
            }
        }

        func place(in box: inout BallotBox, voter: String) {
            guard let vote else { return }
            box.cast(vote, for: voter)
            if isSealed { box.seal(for: voter) }
        }
    }

    /// Fixture: Ana is the voter who asks to replace her vote. Bea has voted yes and nothing
    /// changes that, so the tally shows whether a request touched anyone's vote but Ana's.
    ///
    /// Assumptions the rules leave open: when several refusal reasons apply at once (a closed
    /// poll and a sealed ballot, say) the rules do not say which is reported, so no row
    /// combines them; and replacing a vote with the voter's current choice is not tabled,
    /// because the rules do not say whether that counts as a replacement.
    @TableTest("Replaces an unsealed vote only while the poll is open", table: """
        Scenario                          | Poll   | Ana's Ballot      | Replace With | Replacement?             | Ana's Vote? | Tally?
        Yes voter, ballot still unsealed  | open   | voted yes         | no           | replaced                 | no          | [yes: 1, no: 1]
        No voter, ballot still unsealed   | open   | voted no          | yes          | replaced                 | yes         | [yes: 2]
        Change of mind after the deadline | closed | voted yes         | no           | refused(pollClosed)      | yes         | [yes: 2]
        Voter who never voted             | open   | no vote           | {yes, no}    | refused(noVoteToReplace) |             | [yes: 1]
        Voter who finalised their ballot  | open   | voted yes, sealed | no           | refused(ballotSealed)    | yes         | [yes: 2]
        """)
    func replacesAVote(poll: Poll, ballot: Ballot, choice: Choice, replacement: Replacement, vote: Choice?, tally: [Choice: Int]) throws {
        var box = BallotBox()
        ballot.place(in: &box, voter: "ana")
        box.cast(.yes, for: "bea")
        poll.apply(to: &box)

        let result = try box.replaceVote(choice, for: "ana")

        #expect(result == replacement)
        #expect(box.vote(of: "ana") == vote)
        #expect(box.tally == tally)
    }
}
