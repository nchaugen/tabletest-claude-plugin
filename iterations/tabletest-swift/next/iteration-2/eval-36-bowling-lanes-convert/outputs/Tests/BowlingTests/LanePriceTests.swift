import Foundation
import TableTest
import Testing
@testable import Bowling

struct LanePriceTests {

    @TableTest("""
        Scenario                     | Lane     | Hours | Member | Price (EUR)?
        Standard lane, within limit  | standard | 2     | false  | 60
        Deluxe lane, within limit    | deluxe   | 2     | false  | 90
        Kids lane, single hour       | kids     | 1     | false  | 20
        Standard lane, past 3 hours  | standard | 5     | false  | 90
        Member on a deluxe lane      | deluxe   | 2     | true   | 72
        """)
    func pricesABookingFromLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
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
