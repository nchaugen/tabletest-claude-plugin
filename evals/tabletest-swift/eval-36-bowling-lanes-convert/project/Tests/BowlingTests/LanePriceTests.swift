import Foundation
import TableTest
import Testing
@testable import Bowling

struct LanePriceTests {

    @Test func standardLaneCostsThirtyEurosAnHour() throws {
        let booking = Booking(lane: .standard, hours: 2)

        #expect(try LanePrice.euros(for: booking) == 60)
    }

    @Test func deluxeLaneCostsFortyFiveEurosAnHour() throws {
        let booking = Booking(lane: .deluxe, hours: 2)

        #expect(try LanePrice.euros(for: booking) == 90)
    }

    @Test func kidsLaneCostsTwentyEurosAnHour() throws {
        let booking = Booking(lane: .kids, hours: 1)

        #expect(try LanePrice.euros(for: booking) == 20)
    }

    @Test func hoursAfterTheThirdAreFree() throws {
        let booking = Booking(lane: .standard, hours: 5)

        #expect(try LanePrice.euros(for: booking) == 90)
    }

    @Test func memberPaysEightyPercent() throws {
        let booking = Booking(lane: .deluxe, hours: 2, isMember: true)

        #expect(try LanePrice.euros(for: booking) == 72)
    }

    @Test func bookingNeedsAtLeastOneHour() {
        let booking = Booking(lane: .standard, hours: 0)

        #expect(throws: BookingError.noHours) {
            try LanePrice.euros(for: booking)
        }
    }
}
