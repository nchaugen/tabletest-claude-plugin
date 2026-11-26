import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// Where the screen came from.
    enum Screen: String {
        case library = "library"
        case playlist = "playlist"
        case playlistWithAnUnreadRule = "playlist with an unread rule"

        static let jazz = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)

        /// The screen as it opens: the library with no rule, or "Late-night jazz" (jazz, most
        /// recently added first), which holds an unread rule in the last case.
        func opened() -> PlaylistScreen {
            switch self {
            case .library:
                .library(SongQuery(rules: SmartRules(), order: .recentlyAdded))
            case .playlist:
                .opening(SavedPlaylist(name: "Late-night jazz", query: Self.jazz, unreadRules: 0))
            case .playlistWithAnUnreadRule:
                .opening(SavedPlaylist(name: "Late-night jazz", query: Self.jazz, unreadRules: 1))
            }
        }
    }

    /// What the user does to the rules or the order on screen.
    enum Change: String {
        case none = "no change"
        case addsAGenreRule = "adds a genre rule"
        case addsARatingRule = "adds a rating rule"
        case addsAnUnplayedOnlyRule = "adds an unplayed-only rule"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to screen: PlaylistScreen) -> PlaylistScreen {
            var screen = screen
            switch self {
            case .none: break
            case .addsAGenreRule: screen.query.rules.genres.insert(.folk)
            case .addsARatingRule: screen.query.rules.minRating = 4
            case .addsAnUnplayedOnlyRule: screen.query.rules.unplayedOnly = true
            case .changesTheOrder: screen.query.order = .artist
            case .removesEveryRule: screen.query.rules = SmartRules()
            }
            return screen
        }
    }

    /// The edited playlist "Late-night jazz" holds a genre rule. A playlist read with an unread
    /// rule is never written back with `update`.
    @TableTest("""
        Scenario                               | Screen                                   | Change                                                                                 | Edited? | Actions?
        Library without a rule                 | library                                  | {no change, changes the order}                                                         | false   | []
        Library with a rule                    | library                                  | {adds a genre rule, adds a rating rule, adds an unplayed-only rule}                    | false   | [saveAsPlaylist]
        Playlist as opened                     | {playlist, playlist with an unread rule} | no change                                                                              | false   | []
        Playlist edited, rules kept            | playlist                                 | {adds a genre rule, adds a rating rule, adds an unplayed-only rule, changes the order} | true    | [revert, saveAsNew, update]
        Edited playlist holding an unread rule | playlist with an unread rule             | {adds a genre rule, adds a rating rule, adds an unplayed-only rule, changes the order} | true    | [revert, saveAsNew]
        Every rule removed from a playlist     | {playlist, playlist with an unread rule} | removes every rule                                                                     | true    | [revert]
        """)
    func offersTheActionsTheScreenRulesGive(screen: Screen, change: Change, edited: Bool, actions: [PlaylistScreen.Action]) {
        let result = change.applied(to: screen.opened())
        #expect(result.isEdited == edited)
        #expect(result.actions == actions)
    }
}
