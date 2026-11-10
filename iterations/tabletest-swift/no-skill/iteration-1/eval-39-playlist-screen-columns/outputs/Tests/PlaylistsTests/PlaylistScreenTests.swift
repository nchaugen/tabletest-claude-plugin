import TableTest
import Testing
@testable import Playlists

/// Reads an action by its case name, so a table can list the actions a screen offers.
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

    /// The library offers to save the rules on screen as a playlist once there is a rule. The
    /// order of the songs is no rule, and the library is never edited.
    @TableTest("Offers to save the library's rules as a playlist", table: """
        Scenario       | Genres | Unplayed only | Order                      | Edited? | Actions?
        No rule        | {}     | false         | {recentlyAdded, artist}    | false   | []
        A genre rule   | {rock} | false         | {recentlyAdded, artist}    | false   | [saveAsPlaylist]
        Unplayed only  | {}     | true          | {recentlyAdded, artist}    | false   | [saveAsPlaylist]
        """)
    func offersActionsOnTheLibrary(
        genres: Set<Genre>, unplayedOnly: Bool, order: SortOrder,
        edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let query = SongQuery(rules: SmartRules(genres: genres, unplayedOnly: unplayedOnly), order: order)
        let screen = PlaylistScreen.library(query)

        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }

    /// Fixture: the smart playlist "Late-night jazz" is saved with the genre jazz, no minimum
    /// rating, and the songs most recently added first. The columns give the rules on screen.
    /// A playlist read with an unread rule is never written back from here, so it offers no update.
    @TableTest("Offers the actions for the rules on a playlist's screen", table: """
        Scenario                          | Unread rules | Genres | Minimum rating | Order         | Edited? | Actions?
        As saved                          | 0            | {jazz} |                | recentlyAdded | false   | []
        A rule added                      | 0            | {jazz} | 4              | recentlyAdded | true    | [revert, saveAsNew, update]
        Another order                     | 0            | {jazz} |                | artist        | true    | [revert, saveAsNew, update]
        Every rule removed                | 0            | {}     |                | recentlyAdded | true    | [revert]
        An unread rule, as saved          | 1            | {jazz} |                | recentlyAdded | false   | []
        An unread rule, a rule added      | 1            | {jazz} | 4              | recentlyAdded | true    | [revert, saveAsNew]
        """)
    func offersActionsOnAPlaylist(
        unreadRules: Int, genres: Set<Genre>, minRating: Int?, order: SortOrder,
        edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let saved = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)
        let playlist = SavedPlaylist(name: "Late-night jazz", query: saved, unreadRules: unreadRules)
        let onScreen = SongQuery(rules: SmartRules(genres: genres, minRating: minRating), order: order)
        let screen = PlaylistScreen(source: .playlist(playlist), query: onScreen)

        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
