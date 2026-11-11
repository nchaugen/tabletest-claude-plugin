import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// Where the screen was opened from.
    enum Screen: String {
        case library = "the library"
        case playlist = "a playlist"
        case playlistWithUnreadRule = "a playlist with an unread rule"

        static let jazz = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)

        func opened() -> PlaylistScreen {
            switch self {
            case .library:
                .library(SongQuery(rules: SmartRules(), order: .recentlyAdded))
            case .playlist:
                .opening(SavedPlaylist(name: "Late-night jazz", query: Self.jazz, unreadRules: 0))
            case .playlistWithUnreadRule:
                .opening(SavedPlaylist(name: "Late-night jazz", query: Self.jazz, unreadRules: 1))
            }
        }
    }

    /// What the user does to the rules on screen.
    enum Change: String {
        case none = "no change"
        case addsAGenreRule = "adds a genre rule"
        case addsARatingRule = "adds a minimum rating rule"
        case addsAnUnplayedRule = "adds an unplayed-only rule"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            var query = query
            switch self {
            case .none: break
            case .addsAGenreRule: query.rules.genres.insert(.rock)
            case .addsARatingRule: query.rules.minRating = 4
            case .addsAnUnplayedRule: query.rules.unplayedOnly = true
            case .changesTheOrder: query.order = .artist
            case .removesEveryRule: query.rules = SmartRules()
            }
            return query
        }
    }

    /// Fixture: the library opens with no rules, most recently added first. The smart playlist
    /// "Late-night jazz" holds jazz, most recently added first. "An unread rule" means the
    /// playlist file holds a rule this app version could not read.
    @TableTest("""
        Screen                         | Change                                                                      | Edited? | Actions?
        the library                    | {no change, changes the order, removes every rule}                          | false   | []
        the library                    | {adds a genre rule, adds a minimum rating rule, adds an unplayed-only rule} | false   | [saveAsPlaylist]
        a playlist                     | no change                                                                   | false   | []
        a playlist                     | {adds a genre rule, adds a minimum rating rule, adds an unplayed-only rule} | true    | [revert, saveAsNew, update]
        a playlist                     | changes the order                                                           | true    | [revert, saveAsNew, update]
        a playlist                     | removes every rule                                                          | true    | [revert]
        a playlist with an unread rule | no change                                                                   | false   | []
        a playlist with an unread rule | {adds a genre rule, adds a minimum rating rule, adds an unplayed-only rule} | true    | [revert, saveAsNew]
        a playlist with an unread rule | changes the order                                                           | true    | [revert, saveAsNew]
        a playlist with an unread rule | removes every rule                                                          | true    | [revert]
        """)
    func offersTheActionsTheRulesOnScreenAllow(
        screen: Screen, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        var opened = screen.opened()
        opened.query = change.applied(to: opened.query)
        #expect(opened.isEdited == edited)
        #expect(opened.actions == actions)
    }
}
