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

    static let voter = "ada"

    /// The voter asking to replace their vote is Ada. The other voters in the box are bystanders:
    /// their votes must survive every request. Where two rules could both refuse (a closed poll and
    /// a sealed ballot, say), which reason the box gives is not specified, so no row combines them.
    /// Open question: whether the box refuses a replacement that repeats the voter's present choice;
    /// it is assumed to accept it as a replacement.
    @TableTest("""
        Scenario                      | Poll   | Ballot   | Votes Before         | New Choice | Replacement?                | Vote After? | Tally After?
        Mind changed during the poll  | open   | unsealed | [ada: yes, bea: no]  | no         | replaced                    | no          | [no: 2]
        Same choice repeated          | open   | unsealed | [ada: yes, bea: no]  | yes        | replaced                    | yes         | [yes: 1, no: 1]
        Request after the deadline    | closed | unsealed | [ada: yes, bea: no]  | no         | refused(pollClosed)         | yes         | [yes: 1, no: 1]
        Voter missing from the box    | open   | unsealed | [bea: no]            | yes        | refused(noVoteToReplace)    |             | [no: 1]
        Request after finalising      | open   | sealed   | [ada: yes, bea: no]  | no         | refused(ballotSealed)       | yes         | [yes: 1, no: 1]
        """)
    func replacesAVoteOnlyWhileTheVoterMayChangeIt(
        poll: Poll, ballot: Ballot, votesBefore: [String: Choice], newChoice: Choice,
        replacement: Replacement, voteAfter: Choice?, tallyAfter: [Choice: Int]
    ) throws {
        var box = votesBefore.ballotBox()
        ballot.apply(to: &box, for: Self.voter)
        poll.apply(to: &box)

        #expect(try box.replaceVote(newChoice, for: Self.voter) == replacement)
        #expect(box.vote(of: Self.voter) == voteAfter)
        #expect(box.tally == tallyAfter)
    }
}

extension Dictionary where Key == String, Value == Choice {
    fileprivate func ballotBox() -> BallotBox {
        var box = BallotBox()
        for (voter, choice) in self { box.cast(choice, for: voter) }
        return box
    }
}
