import TableTest
import Testing
@testable import Playlists

/// Fixture: the smart playlist "Late-night jazz" holds jazz, most recently added first.
struct PlaylistScreenTests {

    static let jazz = SongQuery(rules: SmartRules(genres: [.jazz]), order: .recentlyAdded)
    static let wellRatedJazz = SongQuery(rules: SmartRules(genres: [.jazz], minRating: 4), order: .recentlyAdded)
    static let lateNightJazz = SavedPlaylist(name: "Late-night jazz", query: jazz, unreadRules: 0)
    static let lateNightJazzWithAnUnreadRule = SavedPlaylist(name: "Late-night jazz", query: jazz, unreadRules: 1)

    static let screens: [(screen: String, open: PlaylistScreen, edited: Bool, actions: [PlaylistScreen.Action])] = [
        (screen: "the library, no rule",
         open: .library(SongQuery(rules: SmartRules(), order: .recentlyAdded)), edited: false, actions: []),
        (screen: "the library, a rule",
         open: .library(SongQuery(rules: SmartRules(genres: [.rock]), order: .recentlyAdded)), edited: false,
         actions: [.saveAsPlaylist]),
        (screen: "the library, another order",
         open: .library(SongQuery(rules: SmartRules(), order: .artist)), edited: false, actions: []),
        (screen: "the library, unplayed songs only",
         open: .library(SongQuery(rules: SmartRules(unplayedOnly: true), order: .recentlyAdded)), edited: false,
         actions: [.saveAsPlaylist]),
        (screen: "a playlist as saved", open: .opening(lateNightJazz), edited: false, actions: []),
        (screen: "a playlist, a rule added",
         open: PlaylistScreen(source: .playlist(lateNightJazz), query: wellRatedJazz), edited: true,
         actions: [.revert, .saveAsNew, .update]),
        (screen: "a playlist, another order",
         open: PlaylistScreen(source: .playlist(lateNightJazz),
                              query: SongQuery(rules: SmartRules(genres: [.jazz]), order: .artist)),
         edited: true, actions: [.revert, .saveAsNew, .update]),
        (screen: "a playlist, every rule removed",
         open: PlaylistScreen(source: .playlist(lateNightJazz),
                              query: SongQuery(rules: SmartRules(), order: .recentlyAdded)),
         edited: true, actions: [.revert]),
        (screen: "a playlist with an unread rule, as saved", open: .opening(lateNightJazzWithAnUnreadRule),
         edited: false, actions: []),
        (screen: "a playlist with an unread rule, a rule added",
         open: PlaylistScreen(source: .playlist(lateNightJazzWithAnUnreadRule), query: wellRatedJazz),
         edited: true, actions: [.revert, .saveAsNew]),
    ]

    @Test("Offers the actions the screen rules give", arguments: screens)
    func offersTheActions(screen: String, open: PlaylistScreen, edited: Bool, actions: [PlaylistScreen.Action]) {
        #expect(open.isEdited == edited)
        #expect(open.actions == actions)
    }
}
