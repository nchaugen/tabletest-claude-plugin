import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// Where the screen was opened from.
    enum Screen: String {
        case library = "the library"
        case playlist = "a playlist"
        case playlistWithUnreadRule = "a playlist with an unread rule"

        var opened: PlaylistScreen {
            switch self {
            case .library: .library(SongQuery(rules: SmartRules(), order: .recentlyAdded))
            case .playlist: .opening(PlaylistScreenTests.lateNightJazz(unreadRules: 0))
            case .playlistWithUnreadRule: .opening(PlaylistScreenTests.lateNightJazz(unreadRules: 1))
            }
        }
    }

    /// What the user does to the rules or the order on screen.
    enum Change: String {
        case none = "no change"
        case addsARatingRule = "adds a rating rule"
        case limitsToUnplayedSongs = "limits to unplayed songs"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            var changed = query
            switch self {
            case .none: break
            case .addsARatingRule: changed.rules.minRating = 4
            case .limitsToUnplayedSongs: changed.rules.unplayedOnly = true
            case .changesTheOrder: changed.order = .artist
            case .removesEveryRule: changed.rules = SmartRules()
            }
            return changed
        }
    }

    static func lateNightJazz(unreadRules: Int) -> SavedPlaylist {
        SavedPlaylist(
            name: "Late-night jazz",
            query: SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded),
            unreadRules: unreadRules)
    }

    /// Fixture: the library opens with no rule, most recently added first. A playlist opens as saved:
    /// "Late-night jazz" holds jazz, most recently added first. The library has no rule to remove,
    /// so it is not crossed with that change. Every change is made to the screen after it opens.
    @TableTest("""
        Screen                         | Change                                         | Edited? | Actions?
        the library                    | no change                                      | false   | []
        the library                    | {adds a rating rule, limits to unplayed songs} | false   | [saveAsPlaylist]
        the library                    | changes the order                              | false   | []
        a playlist                     | no change                                      | false   | []
        a playlist                     | {adds a rating rule, changes the order}        | true    | [revert, saveAsNew, update]
        a playlist                     | removes every rule                             | true    | [revert]
        a playlist with an unread rule | no change                                      | false   | []
        a playlist with an unread rule | {adds a rating rule, changes the order}        | true    | [revert, saveAsNew]
        a playlist with an unread rule | removes every rule                             | true    | [revert]
        """)
    func offersTheActionsTheScreenRulesGive(
        screen: Screen, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        var opened = screen.opened
        opened.query = change.applied(to: opened.query)

        #expect(opened.isEdited == edited)
        #expect(opened.actions == actions)
    }
}
