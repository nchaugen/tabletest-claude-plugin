import Foundation
import TableTest
import Testing
@testable import Bowling

struct LanePriceTests {

    /// Prices in euros. Each lane has its own hourly rate, the hours after the third are free,
    /// and a member pays 80 % of the price.
    @TableTest("""
        Scenario                 | Lane     | Hours | Member | Price (EUR)?
        Standard lane            | standard | 2     | false  | 60
        Deluxe lane              | deluxe   | 2     | false  | 90
        Kids lane                | kids     | 1     | false  | 20
        Booking past three hours | standard | 5     | false  | 90
        Member on a deluxe lane  | deluxe   | 2     | true   | 72
        """)
    func pricesABookingByLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
        let booking = Booking(lane: lane, hours: hours, isMember: member)

        #expect(try LanePrice.euros(for: booking) == price)
    }

    @Test func bookingNeedsAtLeastOneHour() {
        let booking = Booking(lane: .standard, hours: 0)

        #expect(throws: BookingError.noHours) {
            try LanePrice.euros(for: booking)
        }
    }
}
