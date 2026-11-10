import TableTest
import Testing
@testable import Playlists

/// Fixture: the smart playlist "Late-night jazz" holds jazz, most recently added first.
struct PlaylistScreenTests {

    static let jazz = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)

    static func lateNightJazz(unreadRules: Int) -> SavedPlaylist {
        SavedPlaylist(name: "Late-night jazz", query: jazz, unreadRules: unreadRules)
    }

    @TableTest("Offers Save as playlist on the library once a rule shows", table: """
        Rules                | Order                   | Actions?
        [:]                  | {recentlyAdded, artist} | []
        [genres: {rock}]     | {recentlyAdded, artist} | [saveAsPlaylist]
        [minRating: 4]       | recentlyAdded           | [saveAsPlaylist]
        [unplayedOnly: true] | recentlyAdded           | [saveAsPlaylist]
        """)
    func offersSaveAsPlaylist(rules: SmartRules, order: SortOrder, actions: [PlaylistScreen.Action]) {
        let screen = PlaylistScreen.library(SongQuery(rules: rules, order: order))

        #expect(!screen.isEdited)
        #expect(screen.actions == actions)
    }

    /// What the user did to the rules of "Late-night jazz".
    enum Change: String {
        case none
        case addsARule = "adds a rule"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        var shown: SongQuery {
            switch self {
            case .none: PlaylistScreenTests.jazz
            case .addsARule: SongQuery(rules: SmartRules(genres: [.jazz], minRating: 4), order: .recentlyAdded)
            case .changesTheOrder: SongQuery(rules: SmartRules(genres: [.jazz]), order: .artist)
            case .removesEveryRule: SongQuery(rules: SmartRules(), order: .recentlyAdded)
            }
        }
    }

    /// A playlist read with an unread rule is never written back from here: the write would
    /// delete the rule a newer app version added.
    @TableTest("Offers Revert, Save as new and Update once a playlist is edited", table: """
        Unread rules | Change             | Edited? | Actions?
        0            | none               | false   | []
        0            | adds a rule        | true    | [revert, saveAsNew, update]
        0            | changes the order  | true    | [revert, saveAsNew, update]
        0            | removes every rule | true    | [revert]
        {1, 2}       | none               | false   | []
        {1, 2}       | adds a rule        | true    | [revert, saveAsNew]
        {1, 2}       | changes the order  | true    | [revert, saveAsNew]
        {1, 2}       | removes every rule | true    | [revert]
        """)
    func offersTheEditActions(unreadRules: Int, change: Change, edited: Bool, actions: [PlaylistScreen.Action]) {
        let screen = PlaylistScreen(source: .playlist(Self.lateNightJazz(unreadRules: unreadRules)), query: change.shown)

        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
