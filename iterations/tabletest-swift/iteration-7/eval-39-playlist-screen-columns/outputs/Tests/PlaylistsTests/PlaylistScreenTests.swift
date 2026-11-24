import TableTest
import Testing
@testable import Playlists

/// Fixture: the library is shown with no rules, most recently added first. The smart playlist
/// "Late-night jazz" holds jazz, most recently added first. The playlist with an unread rule is the
/// same playlist, saved by a newer app version that added a rule this version cannot read.
/// A change is made to the rules on screen and is not yet saved.
struct PlaylistScreenTests {

    enum OpenedFrom: String {
        case library
        case playlist
        case playlistWithAnUnreadRule = "playlist with an unread rule"

        func screen(after change: Change) -> PlaylistScreen {
            switch self {
            case .library:
                let query = SongQuery(rules: SmartRules(), order: .recentlyAdded)
                return PlaylistScreen(source: .library, query: change.applied(to: query))
            case .playlist:
                return Self.edited(Self.lateNightJazz(unreadRules: 0), by: change)
            case .playlistWithAnUnreadRule:
                return Self.edited(Self.lateNightJazz(unreadRules: 1), by: change)
            }
        }

        private static func lateNightJazz(unreadRules: Int) -> SavedPlaylist {
            SavedPlaylist(
                name: "Late-night jazz",
                query: SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded),
                unreadRules: unreadRules)
        }

        private static func edited(_ playlist: SavedPlaylist, by change: Change) -> PlaylistScreen {
            PlaylistScreen(source: .playlist(playlist), query: change.applied(to: playlist.query))
        }
    }

    enum Change: String {
        case none = "no change"
        case addsAGenreRule = "adds a genre rule"
        case addsARatingRule = "adds a rating rule"
        case addsAnUnplayedOnlyRule = "adds an unplayed-only rule"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            var changed = query
            switch self {
            case .none: break
            case .addsAGenreRule: changed.rules.genres.insert(.rock)
            case .addsARatingRule: changed.rules.minRating = 4
            case .addsAnUnplayedOnlyRule: changed.rules.unplayedOnly = true
            case .changesTheOrder: changed.order = .artist
            case .removesEveryRule: changed.rules = SmartRules()
            }
            return changed
        }
    }

    /// A playlist is edited when its rules or order differ from the saved ones; the library never is.
    /// A playlist needs a rule, so removing every rule offers only to revert. A playlist with an
    /// unread rule is never offered an update.
    @TableTest("""
        Opened From                  | Change                                                              | Edited? | Actions?
        library                      | {no change, removes every rule}                                     | false   | []
        library                      | {adds a genre rule, adds a rating rule, adds an unplayed-only rule} | false   | [saveAsPlaylist]
        library                      | changes the order                                                   | false   | []
        playlist                     | no change                                                           | false   | []
        playlist                     | {adds a genre rule, adds a rating rule, adds an unplayed-only rule} | true    | [revert, saveAsNew, update]
        playlist                     | changes the order                                                   | true    | [revert, saveAsNew, update]
        playlist                     | removes every rule                                                  | true    | [revert]
        playlist with an unread rule | no change                                                           | false   | []
        playlist with an unread rule | {adds a genre rule, adds a rating rule, adds an unplayed-only rule} | true    | [revert, saveAsNew]
        playlist with an unread rule | changes the order                                                   | true    | [revert, saveAsNew]
        playlist with an unread rule | removes every rule                                                  | true    | [revert]
        """)
    func offersTheActionsTheScreenRulesGive(
        openedFrom: OpenedFrom, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let screen = openedFrom.screen(after: change)
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
