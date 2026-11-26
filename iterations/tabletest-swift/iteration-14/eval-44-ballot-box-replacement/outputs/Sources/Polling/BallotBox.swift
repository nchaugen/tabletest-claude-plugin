/// The answers of the poll.
public enum Choice: String, Hashable, Sendable {
    case yes
    case no
}

/// Why the box refuses to replace a vote.
public enum Refusal: String, Codable, Equatable, Sendable {
    case pollClosed
    case noVoteToReplace
    case ballotSealed
}

/// What the box did with a request to replace a vote.
public enum Replacement: Codable, Equatable, Sendable {
    case replaced
    case refused(Refusal)
}

struct NotImplemented: Error {}

/// The votes of one poll, one vote for each voter.
public struct BallotBox: Sendable {
    public private(set) var isOpen: Bool
    private var votes: [String: Choice]
    private var sealedVoters: Set<String>

    public init(isOpen: Bool = true) {
        self.isOpen = isOpen
        self.votes = [:]
        self.sealedVoters = []
    }

    public mutating func cast(_ choice: Choice, for voter: String) {
        votes[voter] = choice
    }

    /// Seals the voter's ballot: their vote is final.
    public mutating func seal(for voter: String) {
        sealedVoters.insert(voter)
    }

    public mutating func close() {
        isOpen = false
    }

    public func vote(of voter: String) -> Choice? {
        votes[voter]
    }

    /// The count of votes for each choice. A choice with no vote is not in the tally.
    public var tally: [Choice: Int] {
        votes.values.reduce(into: [:]) { counts, choice in counts[choice, default: 0] += 1 }
    }

    /// Replaces the voter's vote with `choice`, or refuses to.
    public mutating func replaceVote(_ choice: Choice, for voter: String) throws -> Replacement {
        throw NotImplemented()
    }
}
