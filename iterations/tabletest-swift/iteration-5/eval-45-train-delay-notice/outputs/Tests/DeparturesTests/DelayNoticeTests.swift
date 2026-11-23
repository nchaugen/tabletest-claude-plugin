import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// The platform-screen text for one departure. A delay under 5 minutes counts as on time, a
    /// delay of 60 minutes or more adds a refund offer, and a cancellation overrides any delay.
    /// Whether the next departure is shown is not part of the rule, so a departure that runs
    /// keeps the same text whatever the next departure is.
    @TableTest("""
        Scenario                          | Scheduled | Destination | Delay (min) | Cancelled | Next Departure   | Text?
        Delay under the on-time limit     | 08:15     | Bergen      | {0, 4}      | false     | {09:15, 10:15}   | The 08:15 service to Bergen is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        Delay at the on-time limit        | 08:15     | Bergen      | 5           | false     | 09:15            | The 08:15 service to Bergen is delayed by about 5 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        Delay just under the refund limit | 12:40     | Trondheim   | 59          | false     | 13:40            | The 12:40 service to Trondheim is delayed by about 59 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        Delay at the refund limit         | 17:05     | Oslo        | 60          | false     | 18:05            | The 17:05 service to Oslo is delayed by about 60 minutes. You can claim a refund of half your fare: apply on our website within 30 days of travel, and keep your ticket until the claim is settled.
        Cancelled departure               | 21:30     | Stavanger   | {0, 30, 90} | true      | 22:30            | The 21:30 service to Stavanger is cancelled. Your ticket is valid on the next departure at 22:30, or you can claim a full refund of your fare on our website within 30 days of travel.
        """)
    func tellsPassengersWhatToExpectFromADeparture(
        scheduled: String,
        destination: String,
        delayMinutes: Int,
        isCancelled: Bool,
        nextDeparture: String,
        text: String
    ) {
        let departure = Departure(
            scheduled: scheduled,
            destination: destination,
            delayMinutes: delayMinutes,
            isCancelled: isCancelled,
            nextDeparture: nextDeparture
        )
        #expect(DelayNotice.text(for: departure) == text)
    }
}
