import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// The notice text is what passengers read on the platform screen and in the app, so the
    /// wording is compared in full. The next departure is only read for a cancelled service, and
    /// the delay is ignored for a cancelled service. The rows that claim this vary those values.
    /// A delay under 5 minutes reads as on time, and 60 minutes or more offers a refund. The
    /// edges of each band are rows, because the minutes appear in the text.
    @TableTest("""
        Scenario                     | Scheduled | Destination | Delay (minutes)        | Cancelled | Next Departure     | Text?
        Not delayed enough to notice | 09:15     | Bergen      | {0, 4}                 | false     | {'09:45', '10:15'} | The 09:15 service to Bergen is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        First minute of a delay      | 09:15     | Bergen      | 5                      | false     | {'09:45', '10:15'} | The 09:15 service to Bergen is delayed by about 5 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        Last minute before refund    | 14:40     | Trondheim   | 59                     | false     | {'15:40', '16:40'} | The 14:40 service to Trondheim is delayed by about 59 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        First minute of refund       | 14:40     | Trondheim   | 60                     | false     | {'15:40', '16:40'} | The 14:40 service to Trondheim is delayed by about 60 minutes. You can claim a refund of half your fare: apply on our website within 30 days of travel, and keep your ticket until the claim is settled.
        Long delay                   | 22:05     | Oslo        | 185                    | false     | {'23:05', '00:05'} | The 22:05 service to Oslo is delayed by about 185 minutes. You can claim a refund of half your fare: apply on our website within 30 days of travel, and keep your ticket until the claim is settled.
        Cancelled service            | 07:30     | Stavanger   | {0, 4, 5, 59, 60, 185} | true      | 08:30              | The 07:30 service to Stavanger is cancelled. Your ticket is valid on the next departure at 08:30, or you can claim a full refund of your fare on our website within 30 days of travel.
        """)
    func tellsPassengersTheStatusOfADeparture(
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
