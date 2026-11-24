import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// A delay under 5 minutes counts as on time, and a delay of 60 minutes or more earns a refund.
    /// A cancellation takes precedence over any delay. The next departure only matters for a
    /// cancelled service, so the other rows give it a value that must not leak into the text.
    ///
    /// Assumption: an early departure (negative delay) counts as on time.
    @TableTest("""
        Scenario                      | Scheduled | Destination | Delay (min) | Cancelled | Next Departure | Text?
        Early or less than 5 late     | 09:15     | Oslo        | {-2, 0, 4}  | false     | 10:15          | The 09:15 service to Oslo is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        First minute of delay notice  | 09:15     | Oslo        | 5           | false     | 10:15          | The 09:15 service to Oslo is delayed by about 5 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        Last minute before refund     | 14:40     | Bergen      | 59          | false     | 15:40          | The 14:40 service to Bergen is delayed by about 59 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        First minute with refund      | 14:40     | Bergen      | 60          | false     | 15:40          | The 14:40 service to Bergen is delayed by about 60 minutes. You can claim a refund of half your fare: apply on our website within 30 days of travel, and keep your ticket until the claim is settled.
        Long delay                    | 18:05     | Trondheim   | 125         | false     | 19:05          | The 18:05 service to Trondheim is delayed by about 125 minutes. You can claim a refund of half your fare: apply on our website within 30 days of travel, and keep your ticket until the claim is settled.
        Cancelled, whatever the delay | 07:30     | Stavanger   | {0, 10, 90} | true      | 08:30          | The 07:30 service to Stavanger is cancelled. Your ticket is valid on the next departure at 08:30, or you can claim a full refund of your fare on our website within 30 days of travel.
        """)
    func tellsPassengersTheStateOfTheirDeparture(
        scheduled: String,
        destination: String,
        delayMinutes: Int,
        cancelled: Bool,
        nextDeparture: String,
        text: String
    ) {
        let departure = Departure(
            scheduled: scheduled,
            destination: destination,
            delayMinutes: delayMinutes,
            isCancelled: cancelled,
            nextDeparture: nextDeparture
        )
        #expect(DelayNotice.text(for: departure) == text)
    }
}
