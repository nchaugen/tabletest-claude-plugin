import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// What the user does to the rules on a playlist's screen.
    enum Change: String {
        case none = "no change"
        case addsARule = "adds a rule"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            switch self {
            case .none: query
            case .addsARule: SongQuery(rules: SmartRules(genres: query.rules.genres, minRating: 4), order: query.order)
            case .changesTheOrder: SongQuery(rules: query.rules, order: .artist)
            case .removesEveryRule: SongQuery(rules: SmartRules(), order: query.order)
            }
        }
    }

    /// Fixture: the playlist "Late-night jazz" holds jazz, most recently added first.
    static let jazz = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)

    /// The library screen has no playlist to compare with, so only the rules on screen decide.
    @TableTest("Offers saving the library search as a playlist once it has a rule", table: """
        Scenario                   | Genres | Minimum Rating | Unplayed Only | Sort Order              | Actions?
        Whole library              | {}     |                | false         | {recentlyAdded, artist} | []
        Narrowed by genre          | {rock} |                | false         | recentlyAdded           | [saveAsPlaylist]
        Narrowed by rating         | {}     | 4              | false         | recentlyAdded           | [saveAsPlaylist]
        Narrowed to unplayed songs | {}     |                | true          | recentlyAdded           | [saveAsPlaylist]
        """)
    func offersSavingTheLibrarySearch(
        genres: Set<Genre>, minimumRating: Int?, unplayedOnly: Bool, sortOrder: SortOrder,
        actions: [PlaylistScreen.Action]
    ) {
        let rules = SmartRules(genres: genres, minRating: minimumRating, unplayedOnly: unplayedOnly)
        let screen = PlaylistScreen.library(SongQuery(rules: rules, order: sortOrder))
        #expect(screen.actions == actions)
    }

    /// Fixture: the playlist "Late-night jazz" holds jazz, most recently added first. The user
    /// changes the rules on screen without updating the playlist. An unread rule is a rule in
    /// the library file that this app version could not read.
    @TableTest("Offers revert, save and update once a playlist is edited", table: """
        Scenario                     | Unread Rules | Change             | Edited? | Actions?
        Playlist as saved            | {0, 1}       | no change          | false   | []
        Rule added, none unread      | 0            | adds a rule        | true    | [revert, saveAsNew, update]
        Rule added, one unread       | 1            | adds a rule        | true    | [revert, saveAsNew]
        Order changed                | 0            | changes the order  | true    | [revert, saveAsNew, update]
        Playlist left without a rule | {0, 1}       | removes every rule | true    | [revert]
        """)
    func offersActionsForAnEditedPlaylist(
        unreadRules: Int, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let playlist = SavedPlaylist(name: "Late-night jazz", query: Self.jazz, unreadRules: unreadRules)
        let screen = PlaylistScreen(source: .playlist(playlist), query: change.applied(to: playlist.query))
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
