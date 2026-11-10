import Foundation
import TableTest
import Testing
@testable import Bowling

struct LanePriceTests {

    @TableTest("Prices a booking by its lane and hours", table: """
        Scenario                         | Lane     | Hours | Member | Price?
        A standard lane costs 30 an hour | standard | 2     | false  | 60
        A deluxe lane costs 45 an hour   | deluxe   | 2     | false  | 90
        A kids lane costs 20 an hour     | kids     | 1     | false  | 20
        Hours after the third are free   | standard | 5     | false  | 90
        A member pays 80 %               | deluxe   | 2     | true   | 72
        """)
    func pricesABooking(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
        #expect(try LanePrice.euros(for: Booking(lane: lane, hours: hours, isMember: member)) == price)
    }

    @Test func bookingNeedsAtLeastOneHour() {
        let booking = Booking(lane: .standard, hours: 0)

        #expect(throws: BookingError.noHours) {
            try LanePrice.euros(for: booking)
        }
    }
}
