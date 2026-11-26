import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// Where the screen was opened from. Each opening has a query it starts with.
    enum Opening: String {
        case library = "library"
        case playlist = "playlist"
        case playlistWithUnreadRule = "playlist with an unread rule"

        private static let allSongs = SongQuery(rules: SmartRules(), order: .recentlyAdded)
        private static let jazz = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)

        /// The screen after the user has made `change` to the query it opened with.
        func screen(after change: Change) -> PlaylistScreen {
            switch self {
            case .library:
                return .library(change.applied(to: Self.allSongs))
            case .playlist:
                let saved = SavedPlaylist(name: "Late-night jazz", query: Self.jazz, unreadRules: 0)
                return PlaylistScreen(source: .playlist(saved), query: change.applied(to: Self.jazz))
            case .playlistWithUnreadRule:
                let saved = SavedPlaylist(name: "Late-night jazz", query: Self.jazz, unreadRules: 1)
                return PlaylistScreen(source: .playlist(saved), query: change.applied(to: Self.jazz))
            }
        }
    }

    /// What the user does to the rules and order on screen.
    enum Change: String {
        case none = "no change"
        case addsRatingRule = "adds a rating rule"
        case restrictsToUnplayed = "restricts to unplayed songs"
        case changesOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            var changed = query
            switch self {
            case .none: break
            case .addsRatingRule: changed.rules.minRating = 4
            case .restrictsToUnplayed: changed.rules.unplayedOnly = true
            case .changesOrder: changed.order = .artist
            case .removesEveryRule: changed.rules = SmartRules()
            }
            return changed
        }
    }

    /// Fixture: the library holds every song, most recently added first, with no rules. The smart
    /// playlist "Late-night jazz" holds jazz, most recently added first. A playlist with an unread
    /// rule holds a rule that this app version could not read.
    @TableTest("""
        Scenario                          | Opened From                  | Change                      | Edited? | Actions?
        Browsing the whole library        | library                      | no change                   | false   | []
        Narrowing the library by rating   | library                      | adds a rating rule          | false   | [saveAsPlaylist]
        Narrowing the library to unplayed | library                      | restricts to unplayed songs | false   | [saveAsPlaylist]
        Re-sorting the library            | library                      | changes the order           | false   | []
        Playlist as saved                 | playlist                     | no change                   | false   | []
        Playlist narrowed further         | playlist                     | adds a rating rule          | true    | [revert, saveAsNew, update]
        Playlist re-sorted                | playlist                     | changes the order           | true    | [revert, saveAsNew, update]
        Playlist stripped of its rules    | playlist                     | removes every rule          | true    | [revert]
        Newer-version playlist as saved   | playlist with an unread rule | no change                   | false   | []
        Newer-version playlist narrowed   | playlist with an unread rule | adds a rating rule          | true    | [revert, saveAsNew]
        """)
    func offersTheActionsTheRulesOnScreenGive(
        openedFrom: Opening, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let screen = openedFrom.screen(after: change)
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
