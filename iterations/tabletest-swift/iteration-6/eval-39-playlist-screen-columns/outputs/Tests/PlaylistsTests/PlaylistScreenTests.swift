import TableTest
import Testing
@testable import Playlists

struct PlaylistScreenTests {

    /// What the user does to the rules of a saved playlist on screen.
    enum Change: String {
        case none = "as saved"
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

    /// The library holds no stored query, so what is on screen can only be saved as a playlist.
    /// A rule is anything that narrows the songs; the order does not count as one.
    @TableTest("Offers saving a library query that has a rule", table: """
        Scenario            | Genres | Minimum Rating | Unplayed Only | Order                   | Actions?
        No rule             | {}     |                | false         | {recentlyAdded, artist} | []
        Genre only          | {rock} |                | false         | {recentlyAdded, artist} | [saveAsPlaylist]
        Rating only         | {}     | 4              | false         | {recentlyAdded, artist} | [saveAsPlaylist]
        Unplayed songs only | {}     |                | true          | {recentlyAdded, artist} | [saveAsPlaylist]
        """)
    func offersSavingALibraryQuery(
        genres: Set<Genre>, minimumRating: Int?, unplayedOnly: Bool, order: SortOrder, actions: [PlaylistScreen.Action]
    ) {
        let rules = SmartRules(genres: genres, minRating: minimumRating, unplayedOnly: unplayedOnly)
        #expect(PlaylistScreen.library(SongQuery(rules: rules, order: order)).actions == actions)
    }

    @Test("Never counts a library query as edited")
    func neverEditsTheLibrary() {
        let screen = PlaylistScreen.library(SongQuery(rules: SmartRules(genres: [.rock]), order: .artist))
        #expect(!screen.isEdited)
    }

    /// Fixture: the saved playlist "Late-night jazz" holds jazz, most recently added first.
    /// An unread rule is one the library file holds that this app version could not read.
    @TableTest("Offers actions for a playlist changed on screen", table: """
        Scenario                   | Unread Rules | Change             | Edited? | Actions?
        Nothing changed            | {0, 1}       | as saved           | false   | []
        Rule added                 | 0            | adds a rule        | true    | [revert, saveAsNew, update]
        Rule added, unread rule    | 1            | adds a rule        | true    | [revert, saveAsNew]
        Order changed              | 0            | changes the order  | true    | [revert, saveAsNew, update]
        Order changed, unread rule | 1            | changes the order  | true    | [revert, saveAsNew]
        Every rule removed         | {0, 1}       | removes every rule | true    | [revert]
        """)
    func offersActionsForAChangedPlaylist(
        unreadRules: Int, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let saved = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)
        let playlist = SavedPlaylist(name: "Late-night jazz", query: saved, unreadRules: unreadRules)
        let screen = PlaylistScreen(source: .playlist(playlist), query: change.applied(to: saved))
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
