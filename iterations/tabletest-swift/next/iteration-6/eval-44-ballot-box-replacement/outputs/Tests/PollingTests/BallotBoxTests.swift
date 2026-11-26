import TableTest
import Testing
@testable import Polling

/// Replacing a vote in a `BallotBox`.
///
/// Each table varies one condition of the replacement and holds the other two at the value
/// that allows it: the poll is open, the voter has a vote on file, the ballot is unsealed.
/// The box holds one voter, so the tally is that voter's vote alone.
///
/// Assumptions: a refusal reports the one reason that applies. Where several reasons apply at
/// once (a closed poll and a sealed ballot, say), the rules do not say which is reported, so
/// no row combines them.
struct BallotBoxTests {
    enum Poll: String {
        case open
        case closed

        var isOpen: Bool { self == .open }
    }

    enum Ballot: String {
        case unsealed
        case sealed

        func arrange(_ box: inout BallotBox, for voter: String) {
            switch self {
            case .unsealed: break
            case .sealed: box.seal(for: voter)
            }
        }
    }

    static let voter = "Ada"

    /// Arranges the box, asks it to replace the voter's vote, and checks its answer and what
    /// the box holds afterwards.
    private func replaceVote(
        poll: Poll, voteOnFile: Choice?, ballot: Ballot, newVote: Choice,
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        var box = BallotBox(isOpen: poll.isOpen)
        voteOnFile.cast(into: &box, for: Self.voter)
        ballot.arrange(&box, for: Self.voter)

        #expect(try box.replaceVote(newVote, for: Self.voter) == replacement)
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }

    @TableTest("""
        Scenario                   | Poll   | Vote On File | Ballot   | New Vote | Replacement?        | Vote After? | Tally After?
        Voter changes their mind   | open   | yes          | unsealed | no       | replaced            | no          | [no: 1]
        Request after the deadline | closed | yes          | unsealed | no       | refused(pollClosed) | yes         | [yes: 1]
        """)
    func replacesAVoteOnlyWhileThePollIsOpen(
        poll: Poll, voteOnFile: Choice?, ballot: Ballot, newVote: Choice,
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        try replaceVote(poll: poll, voteOnFile: voteOnFile, ballot: ballot, newVote: newVote,
                        replacement: replacement, voteAfter: voteAfter, tallyAfter: tallyAfter)
    }

    @TableTest("""
        Scenario                   | Poll | Vote On File | Ballot   | New Vote  | Replacement?             | Vote After? | Tally After?
        Voter changes their mind   | open | yes          | unsealed | no        | replaced                 | no          | [no: 1]
        Voter who skipped the poll | open |              | unsealed | {yes, no} | refused(noVoteToReplace) |             | [:]
        """)
    func replacesAVoteOnlyWhereTheVoterHasOne(
        poll: Poll, voteOnFile: Choice?, ballot: Ballot, newVote: Choice,
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        try replaceVote(poll: poll, voteOnFile: voteOnFile, ballot: ballot, newVote: newVote,
                        replacement: replacement, voteAfter: voteAfter, tallyAfter: tallyAfter)
    }

    @TableTest("""
        Scenario                      | Poll | Vote On File | Ballot   | New Vote | Replacement?          | Vote After? | Tally After?
        Voter changes their mind      | open | yes          | unsealed | no       | replaced              | no          | [no: 1]
        Request on a finalised ballot | open | yes          | sealed   | no       | refused(ballotSealed) | yes         | [yes: 1]
        """)
    func replacesAVoteOnlyWhileTheBallotIsUnsealed(
        poll: Poll, voteOnFile: Choice?, ballot: Ballot, newVote: Choice,
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        try replaceVote(poll: poll, voteOnFile: voteOnFile, ballot: ballot, newVote: newVote,
                        replacement: replacement, voteAfter: voteAfter, tallyAfter: tallyAfter)
    }
}

extension Optional where Wrapped == Choice {
    /// Puts the vote on file in the box; no vote leaves the box empty.
    fileprivate func cast(into box: inout BallotBox, for voter: String) {
        switch self {
        case .none: break
        case .some(let choice): box.cast(choice, for: voter)
        }
    }
}
