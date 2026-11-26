import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// Where the screen came from.
    enum Screen: String {
        case library = "library"
        case playlist = "playlist"
        case playlistWithUnreadRule = "playlist with an unread rule"

        private static let jazz = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)

        func edited(by change: Change) -> PlaylistScreen {
            var screen = opened
            screen.query = change.applied(to: screen.query)
            return screen
        }

        private var opened: PlaylistScreen {
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

    /// What the user does to the rules and the order on screen.
    enum Change: String {
        case none = "no change"
        case addsAGenreRule = "adds a genre rule"
        case addsARatingRule = "adds a rating rule"
        case addsTheUnplayedOnlyRule = "adds the unplayed-only rule"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            var query = query
            switch self {
            case .none: break
            case .addsAGenreRule: query.rules.genres.insert(.rock)
            case .addsARatingRule: query.rules.minRating = 4
            case .addsTheUnplayedOnlyRule: query.rules.unplayedOnly = true
            case .changesTheOrder: query.order = .artist
            case .removesEveryRule: query.rules = SmartRules()
            }
            return query
        }
    }

    /// Fixture: the library opens with every song, no rule, most recently added first. The smart
    /// playlist "Late-night jazz" holds jazz, most recently added first; its unread rule is one
    /// that a newer app version wrote and this version could not read. Actions come in the order
    /// the screen shows them.
    @TableTest("Offers the actions the screen rules give", table: """
        Scenario                             | Screen                                   | Change                                                                                  | Edited? | Actions?
        Library, no rule to keep             | library                                  | {no change, changes the order}                                                          | false   | []
        Library, rule to keep                | library                                  | {adds a genre rule, adds a rating rule, adds the unplayed-only rule}                    | false   | [saveAsPlaylist]
        Playlist as saved                    | {playlist, playlist with an unread rule} | no change                                                                               | false   | []
        Playlist edited, rules remain        | playlist                                 | {adds a genre rule, adds a rating rule, adds the unplayed-only rule, changes the order} | true    | [revert, saveAsNew, update]
        Playlist edited, unread rule at risk | playlist with an unread rule             | {adds a genre rule, adds a rating rule, adds the unplayed-only rule, changes the order} | true    | [revert, saveAsNew]
        Playlist edited, no rules remain     | {playlist, playlist with an unread rule} | removes every rule                                                                      | true    | [revert]
        """)
    func offersTheActions(screen: Screen, change: Change, edited: Bool, actions: [PlaylistScreen.Action]) {
        let result = screen.edited(by: change)
        #expect(result.isEdited == edited)
        #expect(result.actions == actions)
    }
}
