import TableTest
import Testing
@testable import Playlists

/// Fixture: the smart playlist "Late-night jazz" holds jazz, most recently added first.
struct PlaylistScreenTests {

    /// The rules the user has set on the library screen.
    enum LibraryRules: String {
        case none = "no rule"
        case aGenre = "a genre"
        case unplayedOnly = "unplayed songs only"

        var smartRules: SmartRules {
            switch self {
            case .none: SmartRules()
            case .aGenre: SmartRules(genres: [.rock])
            case .unplayedOnly: SmartRules(unplayedOnly: true)
            }
        }
    }

    /// How the saved playlist reads from the library file.
    enum SavedAs: String {
        case read = "read in full"
        case withUnreadRule = "with an unread rule"

        var playlist: SavedPlaylist {
            SavedPlaylist(name: "Late-night jazz", query: PlaylistScreenTests.jazz,
                          unreadRules: self == .withUnreadRule ? 1 : 0)
        }
    }

    /// What the user does to the playlist's rules after opening it.
    enum Change: String {
        case none = "no change"
        case addsARule = "adds a rule"
        case changesTheOrder = "changes the order"
        case removesEveryRule = "removes every rule"

        func applied(to query: SongQuery) -> SongQuery {
            switch self {
            case .none: query
            case .addsARule: SongQuery(rules: SmartRules(genres: [.jazz], minRating: 4), order: query.order)
            case .changesTheOrder: SongQuery(rules: query.rules, order: .artist)
            case .removesEveryRule: SongQuery(rules: SmartRules(), order: query.order)
            }
        }
    }

    static let jazz = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)

    /// The library is never edited, so only the rules on screen decide; the sort order is ignored.
    @TableTest("""
        Rules on Screen     | Sort Order              | Actions?
        no rule             | {recentlyAdded, artist} | []
        a genre             | {recentlyAdded, artist} | [saveAsPlaylist]
        unplayed songs only | {recentlyAdded, artist} | [saveAsPlaylist]
        """)
    func offersToSaveTheLibraryRulesAsAPlaylist(rules: LibraryRules, order: SortOrder, actions: [PlaylistScreen.Action]) {
        let screen = PlaylistScreen.library(SongQuery(rules: rules.smartRules, order: order))
        #expect(screen.actions == actions)
    }

    /// A playlist needs a rule. A playlist with an unread rule is never written back from here.
    @TableTest("""
        Playlist                            | Change                           | Edited? | Actions?
        {read in full, with an unread rule} | no change                        | false   | []
        read in full                        | {adds a rule, changes the order} | true    | [revert, saveAsNew, update]
        with an unread rule                 | {adds a rule, changes the order} | true    | [revert, saveAsNew]
        {read in full, with an unread rule} | removes every rule               | true    | [revert]
        """)
    func offersTheActionsAnEditedPlaylistAllows(
        savedAs: SavedAs, change: Change, edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let playlist = savedAs.playlist
        let screen = PlaylistScreen(source: .playlist(playlist), query: change.applied(to: playlist.query))
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}
