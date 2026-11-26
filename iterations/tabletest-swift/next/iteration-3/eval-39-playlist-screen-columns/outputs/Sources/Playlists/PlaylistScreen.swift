public enum Genre: String, Codable, CaseIterable, Sendable {
    case jazz
    case rock
    case folk
    case classical
}

public enum SortOrder: String, Codable, CaseIterable, Sendable {
    case recentlyAdded
    case artist
}

/// The rules that choose the songs of a smart playlist. A new rule set chooses every song.
public struct SmartRules: Equatable, Sendable {
    public var genres: Set<Genre> = []
    public var minRating: Int? = nil
    public var unplayedOnly: Bool = false

    public init(genres: Set<Genre> = [], minRating: Int? = nil, unplayedOnly: Bool = false) {
        self.genres = genres
        self.minRating = minRating
        self.unplayedOnly = unplayedOnly
    }

    public var hasRules: Bool {
        !genres.isEmpty || minRating != nil || unplayedOnly
    }
}

/// The songs on screen: the rules that choose them and the order that sorts them.
public struct SongQuery: Equatable, Sendable {
    public var rules: SmartRules
    public var order: SortOrder

    public init(rules: SmartRules, order: SortOrder) {
        self.rules = rules
        self.order = order
    }
}

/// A smart playlist as this app version read it from the library file.
public struct SavedPlaylist: Equatable, Sendable {
    public let name: String
    public let query: SongQuery
    /// Rules the file holds that this app version could not read.
    public let unreadRules: Int

    public init(name: String, query: SongQuery, unreadRules: Int) {
        self.name = name
        self.query = query
        self.unreadRules = unreadRules
    }
}

/// The song list on screen: the whole library or a smart playlist, and the rules the user sees
/// now. A change to the rules stays on screen until the user updates the playlist.
public struct PlaylistScreen: Equatable, Sendable {
    public enum Source: Equatable, Sendable {
        case library
        case playlist(SavedPlaylist)
    }

    /// What the screen offers for the rules on it, in the order it shows them.
    public enum Action: Equatable, CaseIterable, Sendable {
        case saveAsPlaylist
        case revert
        case saveAsNew
        case update
    }

    public let source: Source
    public var query: SongQuery

    public init(source: Source, query: SongQuery) {
        self.source = source
        self.query = query
    }

    public static func library(_ query: SongQuery) -> PlaylistScreen {
        PlaylistScreen(source: .library, query: query)
    }

    public static func opening(_ playlist: SavedPlaylist) -> PlaylistScreen {
        PlaylistScreen(source: .playlist(playlist), query: playlist.query)
    }

    /// The query on screen differs from the one the playlist stores. The library is never edited.
    public var isEdited: Bool {
        guard case .playlist(let playlist) = source else { return false }
        return query != playlist.query
    }

    /// A playlist needs a rule. A playlist read with an unread rule is never written back from
    /// here: the write would delete the rule a newer app version added.
    public var actions: [Action] {
        switch source {
        case .library:
            return query.rules.hasRules ? [.saveAsPlaylist] : []
        case .playlist(let playlist):
            guard isEdited else { return [] }
            guard query.rules.hasRules else { return [.revert] }
            return playlist.unreadRules > 0 ? [.revert, .saveAsNew] : [.revert, .saveAsNew, .update]
        }
    }
}
