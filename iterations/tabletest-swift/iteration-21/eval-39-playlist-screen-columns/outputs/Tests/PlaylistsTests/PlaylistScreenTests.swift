import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// Where the screen was opened from.
    ///
    /// The library opens with every song, most recently added first. The playlist "Late-night jazz"
    /// holds jazz, most recently added first; the second one is the same playlist, read by an app
    /// version that could not read one of its rules.
    enum Source: String {
        case library
        case playlist
        case playlistWithAnUnreadRule = "playlist with an unread rule"

        func screen(after change: Change) -> PlaylistScreen {
            switch self {
            case .library:
                .library(change.applied(to: SongQuery(rules: SmartRules(), order: .recentlyAdded)))
            case .playlist:
                Self.screen(of: Self.lateNightJazz(unreadRules: 0), after: change)
            case .playlistWithAnUnreadRule:
                Self.screen(of: Self.lateNightJazz(unreadRules: 1), after: change)
            }
        }

        private static func lateNightJazz(unreadRules: Int) -> SavedPlaylist {
            SavedPlaylist(
                name: "Late-night jazz",
                query: SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded),
                unreadRules: unreadRules)
        }

        private static func screen(of playlist: SavedPlaylist, after change: Change) -> PlaylistScreen {
            PlaylistScreen(source: .playlist(playlist), query: change.applied(to: playlist.query))
        }
    }

    /// What the user does to the rules and order on screen.
    enum Change: String {
        case none = "no change"
        case addsAGenre = "adds a genre rule"
        case addsARating = "adds a rating rule"
        case limitsToUnplayed = "limits to unplayed songs"
        case sortsByArtist = "sorts by artist"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            var changed = query
            switch self {
            case .none: break
            case .addsAGenre: changed.rules.genres.insert(.folk)
            case .addsARating: changed.rules.minRating = 4
            case .limitsToUnplayed: changed.rules.unplayedOnly = true
            case .sortsByArtist: changed.order = .artist
            case .removesEveryRule: changed.rules = SmartRules()
            }
            return changed
        }
    }

    /// The library is never edited and offers to save its rules as a playlist. A playlist offers
    /// actions only once it is edited, and needs a rule to be saved. A playlist with an unread rule is
    /// never updated, so the rule that this app version could not read is not deleted.
    ///
    /// Every rule kind counts as a rule, so the rule-adding rows vary the kind.
    @TableTest("""
        Scenario                                               | Opened From                  | Change                                                            | Edited? | Actions?
        Library as opened                                      | library                      | no change                                                         | false   | []
        Library sorted by artist                               | library                      | sorts by artist                                                   | false   | []
        Rule added to the library                              | library                      | {adds a genre rule, adds a rating rule, limits to unplayed songs} | false   | [saveAsPlaylist]
        Playlist as saved                                      | playlist                     | no change                                                         | false   | []
        Playlist sorted by artist                              | playlist                     | sorts by artist                                                   | true    | [revert, saveAsNew, update]
        Rule added to a playlist                               | playlist                     | {adds a genre rule, adds a rating rule, limits to unplayed songs} | true    | [revert, saveAsNew, update]
        Every rule removed from a playlist                     | playlist                     | removes every rule                                                | true    | [revert]
        Playlist with an unread rule as saved                  | playlist with an unread rule | no change                                                         | false   | []
        Playlist with an unread rule sorted by artist          | playlist with an unread rule | sorts by artist                                                   | true    | [revert, saveAsNew]
        Rule added to a playlist with an unread rule           | playlist with an unread rule | {adds a genre rule, adds a rating rule, limits to unplayed songs} | true    | [revert, saveAsNew]
        Every rule removed from a playlist with an unread rule | playlist with an unread rule | removes every rule                                                | true    | [revert]
        """)
    func offersTheActionsTheScreenRulesGive(
        openedFrom: Source, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let screen = openedFrom.screen(after: change)
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
