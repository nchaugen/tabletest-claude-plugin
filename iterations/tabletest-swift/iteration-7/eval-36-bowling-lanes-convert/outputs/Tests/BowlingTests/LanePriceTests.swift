import Foundation
import TableTest
import Testing
@testable import Bowling

struct LanePriceTests {

    /// Prices are in euros. Each lane has its own hourly rate, and a member pays 80 % of the price.
    @TableTest("""
        Scenario                  | Lane     | Hours | Member | Price?
        Standard lane, two hours  | standard | 2     | false  | 60
        Deluxe lane, two hours    | deluxe   | 2     | false  | 90
        Kids lane, one hour       | kids     | 1     | false  | 20
        Standard lane, five hours | standard | 5     | false  | 90
        Member on deluxe lane     | deluxe   | 2     | true   | 72
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
