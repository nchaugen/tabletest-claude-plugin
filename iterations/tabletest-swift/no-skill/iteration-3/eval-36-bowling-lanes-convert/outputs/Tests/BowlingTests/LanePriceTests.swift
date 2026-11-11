import Foundation
import TableTest
import Testing
@testable import Bowling

// Lane is not Codable or CaseIterable, so the table reads it by its case name through this
// test-only conversion.
extension Lane: TableTextDecodable {
    public init?(tableText: String) {
        switch tableText {
        case "standard": self = .standard
        case "deluxe": self = .deluxe
        case "kids": self = .kids
        default: return nil
        }
    }
}

struct LanePriceTests {

    @TableTest("""
        Scenario                   | Lane     | Hours | Member | Price?
        Standard hourly rate       | standard | 2     | false  | 60
        Deluxe hourly rate         | deluxe   | 2     | false  | 90
        Kids hourly rate           | kids     | 1     | false  | 20
        Hours after the third      | standard | 5     | false  | 90
        Member discount            | deluxe   | 2     | true   | 72
        """)
    func chargesTheLaneRateForTheBookedHours(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
        let booking = Booking(lane: lane, hours: hours, isMember: member)

        #expect(try LanePrice.euros(for: booking) == price)
    }

    @Test func rejectsABookingWithoutHours() {
        let booking = Booking(lane: .standard, hours: 0)

        #expect(throws: BookingError.noHours) {
            try LanePrice.euros(for: booking)
        }
    }
}
