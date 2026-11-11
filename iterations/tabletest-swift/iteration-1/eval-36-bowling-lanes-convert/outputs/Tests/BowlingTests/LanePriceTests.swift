import Foundation
import TableTest
import Testing
@testable import Bowling

struct LanePriceTests {

    /// Prices are in euros. Members pay 80 % of the price.
    @TableTest("""
        Scenario                | Lane     | Hours | Member | Price (EUR)?
        Standard lane           | standard | 2     | false  | 60
        Deluxe lane             | deluxe   | 2     | false  | 90
        Kids lane               | kids     | 1     | false  | 20
        Booking past third hour | standard | 5     | false  | 90
        Member booking          | deluxe   | 2     | true   | 72
        """)
    func pricesABookingFromLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
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
