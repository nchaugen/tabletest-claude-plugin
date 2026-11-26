import TableTest
import Testing
@testable import Playlists

/// Fixture: the library starts with no rule, most recently added first. The smart playlist
/// "Late-night jazz" holds jazz, most recently added first. Each row opens a screen, makes one
/// change to the rules or the order on it, and reads what the screen offers. The library has no
/// rule to remove, so "removes every rule" has no library row.
struct PlaylistScreenTests {

    /// Where the screen was opened from.
    enum OpenedFrom: String {
        case library = "library"
        case playlist = "playlist"
        case playlistWithUnreadRule = "playlist with an unread rule"

        func screen() -> PlaylistScreen {
            switch self {
            case .library:
                .library(SongQuery(rules: SmartRules(), order: .recentlyAdded))
            case .playlist:
                .opening(Self.lateNightJazz(unreadRules: 0))
            case .playlistWithUnreadRule:
                .opening(Self.lateNightJazz(unreadRules: 1))
            }
        }

        private static func lateNightJazz(unreadRules: Int) -> SavedPlaylist {
            SavedPlaylist(
                name: "Late-night jazz",
                query: SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded),
                unreadRules: unreadRules)
        }
    }

    /// What the user does to the rules and the order on screen.
    enum Change: String {
        case none = "no change"
        case addsAGenreRule = "adds a genre rule"
        case addsARatingRule = "adds a rating rule"
        case addsAnUnplayedOnlyRule = "adds an unplayed-only rule"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to screen: PlaylistScreen) -> PlaylistScreen {
            var changed = screen
            switch self {
            case .none: break
            case .addsAGenreRule: changed.query.rules.genres.insert(.rock)
            case .addsARatingRule: changed.query.rules.minRating = 4
            case .addsAnUnplayedOnlyRule: changed.query.rules.unplayedOnly = true
            case .changesTheOrder: changed.query.order = .artist
            case .removesEveryRule: changed.query.rules = SmartRules()
            }
            return changed
        }
    }

    @TableTest("""
        Scenario                        | Opened From                  | Change                                                                                 | Edited? | Actions?
        Library, no rule chosen         | library                      | {no change, changes the order}                                                         | false   | []
        Library, a rule chosen          | library                      | {adds a genre rule, adds a rating rule, adds an unplayed-only rule}                    | false   | [saveAsPlaylist]
        Playlist as saved               | playlist                     | no change                                                                              | false   | []
        Playlist, a rule remains        | playlist                     | {adds a genre rule, adds a rating rule, adds an unplayed-only rule, changes the order} | true    | [revert, saveAsNew, update]
        Playlist, every rule removed    | playlist                     | removes every rule                                                                     | true    | [revert]
        Unread rule, as saved           | playlist with an unread rule | no change                                                                              | false   | []
        Unread rule, a rule remains     | playlist with an unread rule | {adds a genre rule, adds a rating rule, adds an unplayed-only rule, changes the order} | true    | [revert, saveAsNew]
        Unread rule, every rule removed | playlist with an unread rule | removes every rule                                                                     | true    | [revert]
        """)
    func offersTheActionsTheScreenRulesGive(
        openedFrom: OpenedFrom, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let screen = change.applied(to: openedFrom.screen())
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
