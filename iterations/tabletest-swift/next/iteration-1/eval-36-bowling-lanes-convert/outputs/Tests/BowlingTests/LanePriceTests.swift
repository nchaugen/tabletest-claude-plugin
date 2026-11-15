import Foundation
import TableTest
import Testing
@testable import Bowling

struct LanePriceTests {

    /// Prices are in euros. Each lane has its own hourly rate, the hours after the third are
    /// free, and a member pays a discounted share of the price.
    @TableTest("""
        Scenario                     | Lane     | Hours | Member | Price (EUR)?
        Standard lane, two hours     | standard | 2     | false  | 60
        Deluxe lane, two hours       | deluxe   | 2     | false  | 90
        Kids lane, one hour          | kids     | 1     | false  | 20
        Standard lane, five hours    | standard | 5     | false  | 90
        Deluxe lane booked by member | deluxe   | 2     | true   | 72
        """)
    func pricesABookingFromLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
        #expect(try LanePrice.euros(for: Booking(lane: lane, hours: hours, isMember: member)) == price)
    }

    @Test func rejectsABookingWithoutHours() {
        let booking = Booking(lane: .standard, hours: 0)

        #expect(throws: BookingError.noHours) {
            try LanePrice.euros(for: booking)
        }
    }
}
