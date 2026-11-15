import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// Fixture: the 08:15 service to Oslo, with the next departure to Oslo at 09:00.
    /// The notice changes at a delay of 5 minutes (delayed) and 60 minutes (refund); a cancellation
    /// overrides any delay, so the cancelled row varies the delay across values it ignores.
    @TableTest("""
        Scenario                        | Delay Minutes | Cancelled | Text?
        Just under the delay threshold  | {0, 4}        | false     | The 08:15 service to Oslo is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        At the delay threshold          | 5             | false     | The 08:15 service to Oslo is delayed by about 5 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        Just under the refund threshold | 59            | false     | The 08:15 service to Oslo is delayed by about 59 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        At the refund threshold         | 60            | false     | The 08:15 service to Oslo is delayed by about 60 minutes. You can claim a refund of half your fare: apply on our website within 30 days of travel, and keep your ticket until the claim is settled.
        Cancelled, whatever the delay   | {0, 90}       | true      | The 08:15 service to Oslo is cancelled. Your ticket is valid on the next departure at 09:00, or you can claim a full refund of your fare on our website within 30 days of travel.
        """)
    func tellsPassengersTheStatusOfADeparture(delayMinutes: Int, cancelled: Bool, text: String) {
        let departure = Departure(
            scheduled: "08:15", destination: "Oslo", delayMinutes: delayMinutes,
            isCancelled: cancelled, nextDeparture: "09:00"
        )
        #expect(DelayNotice.text(for: departure) == text)
    }

    /// Fixture: the departure is on time, so the notice is the on-time wording.
    @TableTest("""
        Scenario                       | Scheduled | Destination         | Text?
        Morning departure              | 08:15     | Oslo                | The 08:15 service to Oslo is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        Evening departure, longer name | 21:40     | Lillehammer Station | The 21:40 service to Lillehammer Station is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        """)
    func namesTheServiceFromScheduledTimeAndDestination(scheduled: String, destination: String, text: String) {
        let departure = Departure(
            scheduled: scheduled, destination: destination, delayMinutes: 0,
            isCancelled: false, nextDeparture: "09:00"
        )
        #expect(DelayNotice.text(for: departure) == text)
    }
}
