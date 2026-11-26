import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// Where the screen was opened from.
    enum OpenedFrom: String {
        case library = "the library"
        case playlist = "a playlist"
        case playlistWithUnreadRule = "a playlist with an unread rule"

        static let lateNightJazz = SavedPlaylist(
            name: "Late-night jazz",
            query: SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded),
            unreadRules: 0)

        /// The screen as it opens, before the user changes anything.
        var screen: PlaylistScreen {
            switch self {
            case .library:
                .library(SongQuery(rules: SmartRules(), order: .recentlyAdded))
            case .playlist:
                .opening(Self.lateNightJazz)
            case .playlistWithUnreadRule:
                .opening(SavedPlaylist(name: Self.lateNightJazz.name, query: Self.lateNightJazz.query, unreadRules: 1))
            }
        }

        func shown(after change: Change) -> PlaylistScreen {
            var shown = screen
            shown.query = change.applied(to: shown.query)
            return shown
        }
    }

    /// What the user does to the rules on screen.
    enum Change: String {
        case none = "no change"
        case addsARatingRule = "requires a rating of 4+"
        case limitsToUnplayed = "limits to unplayed"
        case sortsByArtist = "sorts by artist"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            var changed = query
            switch self {
            case .none: break
            case .addsARatingRule: changed.rules.minRating = 4
            case .limitsToUnplayed: changed.rules.unplayedOnly = true
            case .sortsByArtist: changed.order = .artist
            case .removesEveryRule: changed.rules = SmartRules()
            }
            return changed
        }
    }

    /// Fixture: the library has no rule and sorts by most recently added. The playlist "Late-night
    /// jazz" holds jazz, most recently added first. A playlist with an unread rule holds the same
    /// songs, and a newer app version added one rule that this version cannot read.
    @TableTest("""
        Scenario                                 | Opened From                    | Change                  | Edited? | Actions?
        Library as it opens                      | the library                    | no change               | false   | []
        Library given a rule                     | the library                    | requires a rating of 4+ | false   | [saveAsPlaylist]
        Library given an unplayed-only rule      | the library                    | limits to unplayed      | false   | [saveAsPlaylist]
        Library sorted differently               | the library                    | sorts by artist         | false   | []
        Playlist as saved                        | a playlist                     | no change               | false   | []
        Playlist given another rule              | a playlist                     | requires a rating of 4+ | true    | [revert, saveAsNew, update]
        Playlist sorted differently              | a playlist                     | sorts by artist         | true    | [revert, saveAsNew, update]
        Playlist left with no rule               | a playlist                     | removes every rule      | true    | [revert]
        Unread rule, playlist as saved           | a playlist with an unread rule | no change               | false   | []
        Unread rule, playlist given another rule | a playlist with an unread rule | requires a rating of 4+ | true    | [revert, saveAsNew]
        """)
    func offersTheActionsTheScreenRulesGive(
        openedFrom: OpenedFrom, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let screen = openedFrom.shown(after: change)
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
