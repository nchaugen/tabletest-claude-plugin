import TableTest
@testable import Playlists

extension SmartRules: TableValueDecodable {
    /// Starts from no rule, so a cell names only the rules it sets.
    public init(tableValue: TableValue) throws {
        self = try tableValue.readingFields { cell in
            SmartRules(genres: try cell.value("genres") ?? [],
                       minRating: try cell.value("minRating"),
                       unplayedOnly: try cell.value("unplayedOnly") ?? false)
        }
    }
}
