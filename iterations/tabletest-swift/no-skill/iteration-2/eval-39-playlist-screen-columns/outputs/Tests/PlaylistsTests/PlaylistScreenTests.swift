import TableTest
import Testing
@testable import Playlists

/// Lets a table cell name an action by its case, as in `[revert, saveAsNew]`.
extension PlaylistScreen.Action: TableTextDecodable {
    public init?(tableText: String) {
        switch tableText {
        case "saveAsPlaylist": self = .saveAsPlaylist
        case "revert": self = .revert
        case "saveAsNew": self = .saveAsNew
        case "update": self = .update
        default: return nil
        }
    }
}

struct PlaylistScreenTests {

    /// The library screen shows the rules in the columns, in the order in the column.
    /// The sort order does not change what is offered, so every row runs with both orders.
    @TableTest("Offers saving the library rules as a playlist", table: """
        Scenario         | Genres | Minimum rating | Unplayed only | Order                   | Actions?
        No rule          | {}     |                | false         | {recentlyAdded, artist} | []
        A genre rule     | {rock} |                | false         | {recentlyAdded, artist} | [saveAsPlaylist]
        A minimum rating | {}     | 4              | false         | {recentlyAdded, artist} | [saveAsPlaylist]
        An unplayed-only | {}     |                | true          | {recentlyAdded, artist} | [saveAsPlaylist]
        """)
    func offersSavingTheLibraryRules(
        genres: Set<Genre>, minRating: Int?, unplayedOnly: Bool, order: SortOrder,
        actions: [PlaylistScreen.Action]
    ) {
        let rules = SmartRules(genres: genres, minRating: minRating, unplayedOnly: unplayedOnly)
        #expect(PlaylistScreen.library(SongQuery(rules: rules, order: order)).actions == actions)
    }

    @Test("Never marks the library as edited")
    func neverMarksTheLibraryAsEdited() {
        let rules = SmartRules(genres: [.rock])
        #expect(!PlaylistScreen.library(SongQuery(rules: rules, order: .artist)).isEdited)
    }

    /// Fixture: the smart playlist "Late-night jazz" holds jazz, most recently added first.
    /// The columns give the rules and the order on screen, and the unread rules the playlist
    /// was read with.
    @TableTest("Offers the actions for the edits to a playlist", table: """
        Scenario                  | Unread rules | Genres | Minimum rating | Order         | Edited? | Actions?
        As saved                  | 0            | {jazz} |                | recentlyAdded | false   | []
        A rule added              | 0            | {jazz} | 4              | recentlyAdded | true    | [revert, saveAsNew, update]
        Another order             | 0            | {jazz} |                | artist        | true    | [revert, saveAsNew, update]
        Every rule removed        | 0            | {}     |                | recentlyAdded | true    | [revert]
        Unread rule, as saved     | 1            | {jazz} |                | recentlyAdded | false   | []
        Unread rule, a rule added | 1            | {jazz} | 4              | recentlyAdded | true    | [revert, saveAsNew]
        """)
    func offersTheActionsForTheEdits(
        unreadRules: Int, genres: Set<Genre>, minRating: Int?, order: SortOrder,
        edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let saved = SavedPlaylist(
            name: "Late-night jazz",
            query: SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded),
            unreadRules: unreadRules)
        let onScreen = SongQuery(rules: SmartRules(genres: genres, minRating: minRating), order: order)
        let screen = PlaylistScreen(source: .playlist(saved), query: onScreen)

        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
