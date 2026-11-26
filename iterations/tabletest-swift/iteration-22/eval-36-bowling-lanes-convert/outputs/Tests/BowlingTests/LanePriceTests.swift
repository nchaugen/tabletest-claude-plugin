import Foundation
import TableTest
import Testing
@testable import Bowling

struct LanePriceTests {

    /// Each lane has its own hourly rate, only the first three hours are charged, and a member
    /// pays 80 % of the price. The limit of three hours and the member share are held fixed;
    /// the rows straddle the limit between the third and the fourth hour.
    @TableTest("""
        Scenario                | Lane     | Hours | Member | Price (€)?
        Standard hourly rate    | standard | 2     | false  | 60
        Deluxe hourly rate      | deluxe   | 2     | false  | 90
        Kids hourly rate        | kids     | 1     | false  | 20
        Last hour charged       | standard | 3     | false  | 90
        First hour free         | standard | 4     | false  | 90
        Member on a paid lane   | deluxe   | 2     | true   | 72
        """)
    func pricesABookingFromLaneHoursAndMembership(lane: Lane, hours: Int, isMember: Bool, price: Decimal) throws {
        let booking = Booking(lane: lane, hours: hours, isMember: isMember)

        #expect(try LanePrice.euros(for: booking) == price)
    }

    @Test func rejectsABookingWithoutHours() {
        let booking = Booking(lane: .standard, hours: 0)

        #expect(throws: BookingError.noHours) {
            try LanePrice.euros(for: booking)
        }
    }
}
