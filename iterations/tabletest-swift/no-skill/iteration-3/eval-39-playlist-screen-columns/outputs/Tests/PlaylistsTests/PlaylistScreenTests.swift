import TableTest
import Testing
@testable import Playlists

/// A blank Saved Query is the whole library: nothing is saved, so nothing can be edited.
/// Otherwise the screen opened the smart playlist "Late-night jazz", which stores the Saved Query.
/// A query cell names the rules it sets and leaves the other rules unset, which chooses every song.
struct PlaylistScreenTests {

    @TableTest("Offers the actions its rules allow for the query on screen", table: """
        Scenario                         | Saved Query                            | Unread Rules | Query On Screen                                      | Edited? | Actions?
        Library without a rule           |                                        |              | [order: recentlyAdded]                               | false   | []
        Library with a genre rule        |                                        |              | [genres: {rock}, order: recentlyAdded]               | false   | [saveAsPlaylist]
        Library with only another order  |                                        |              | [order: artist]                                      | false   | []
        Library with an unplayed rule    |                                        |              | [unplayedOnly: true, order: recentlyAdded]           | false   | [saveAsPlaylist]
        Playlist as saved                | [genres: {jazz}, order: recentlyAdded] | 0            | [genres: {jazz}, order: recentlyAdded]               | false   | []
        Playlist with a rule added       | [genres: {jazz}, order: recentlyAdded] | 0            | [genres: {jazz}, minRating: 4, order: recentlyAdded] | true    | [revert, saveAsNew, update]
        Playlist with another order      | [genres: {jazz}, order: recentlyAdded] | 0            | [genres: {jazz}, order: artist]                      | true    | [revert, saveAsNew, update]
        Playlist with every rule removed | [genres: {jazz}, order: recentlyAdded] | 0            | [order: recentlyAdded]                               | true    | [revert]
        Unread rule, playlist as saved   | [genres: {jazz}, order: recentlyAdded] | 1            | [genres: {jazz}, order: recentlyAdded]               | false   | []
        Unread rule, a rule added        | [genres: {jazz}, order: recentlyAdded] | 1            | [genres: {jazz}, minRating: 4, order: recentlyAdded] | true    | [revert, saveAsNew]
        """)
    func offersTheActions(
        savedQuery: SongQuery?, unreadRules: Int?, queryOnScreen: SongQuery,
        edited: Bool, actions: [PlaylistScreen.Action]
    ) {
        let screen = PlaylistScreen.showing(queryOnScreen, openedFrom: savedQuery, unreadRules: unreadRules)
        #expect(screen.isEdited == edited)
        #expect(screen.actions == actions)
    }
}

// MARK: - Fixtures and cell conversions

extension PlaylistScreen {
    /// The library when nothing is saved, otherwise the playlist saved with the query and unread rules.
    fileprivate static func showing(_ query: SongQuery, openedFrom saved: SongQuery?, unreadRules: Int?) -> PlaylistScreen {
        guard let saved else { return .library(query) }
        let playlist = SavedPlaylist(name: "Late-night jazz", query: saved, unreadRules: unreadRules ?? 0)
        return PlaylistScreen(source: .playlist(playlist), query: query)
    }
}

extension SongQuery: TableValueDecodable {
    public init(tableValue: TableValue) throws {
        self = try tableValue.readingFields { fields in
            SongQuery(
                rules: SmartRules(
                    genres: try fields.value("genres") ?? [],
                    minRating: try fields.value("minRating"),
                    unplayedOnly: try fields.value("unplayedOnly") ?? false
                ),
                order: try fields.required("order")
            )
        }
    }
}

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
