import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// Fixture: the departure is the 08:15 service to Bergen. The wording of the notice depends on
    /// the delay, on whether the departure is cancelled, and on the next departure (cancellations only).
    /// Assumption: a negative delay (running early) counts as on time.
    @TableTest("""
        Scenario                           | Delay (min)        | Cancelled | Next Departure          | Text?
        Under the delay threshold          | {-2, 0, 4}         | false     | {'09:15', '10:00'}      | The 08:15 service to Bergen is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        At the delay threshold             | 5                  | false     | '09:15'                 | The 08:15 service to Bergen is delayed by about 5 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        Just under the refund threshold    | 59                 | false     | '09:15'                 | The 08:15 service to Bergen is delayed by about 59 minutes. We are sorry for the inconvenience, and we will update this notice as soon as we know more.
        At the refund threshold            | 60                 | false     | '09:15'                 | The 08:15 service to Bergen is delayed by about 60 minutes. You can claim a refund of half your fare: apply on our website within 30 days of travel, and keep your ticket until the claim is settled.
        Cancelled, whatever the delay was  | {0, 30, 90}        | true      | '09:15'                 | The 08:15 service to Bergen is cancelled. Your ticket is valid on the next departure at 09:15, or you can claim a full refund of your fare on our website within 30 days of travel.
        Cancelled, later next departure    | 0                  | true      | '10:00'                 | The 08:15 service to Bergen is cancelled. Your ticket is valid on the next departure at 10:00, or you can claim a full refund of your fare on our website within 30 days of travel.
        """)
    func wordsTheNoticeFromTheDeparturesStatus(delayMinutes: Int, cancelled: Bool, nextDeparture: String, text: String) {
        let departure = Departure(
            scheduled: "08:15", destination: "Bergen",
            delayMinutes: delayMinutes, isCancelled: cancelled, nextDeparture: nextDeparture
        )
        #expect(DelayNotice.text(for: departure) == text)
    }

    /// Fixture: the departure is on time, so only the service description varies.
    @TableTest("""
        Scenario                | Scheduled | Destination | Text?
        Morning service         | 08:15     | Bergen      | The 08:15 service to Bergen is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        Late evening service    | 23:40     | Oslo S      | The 23:40 service to Oslo S is on time. Please have your ticket ready for inspection, and mind the gap between the train and the platform when you board.
        """)
    func namesTheServiceFromScheduledTimeAndDestination(scheduled: String, destination: String, text: String) {
        let departure = Departure(
            scheduled: scheduled, destination: destination,
            delayMinutes: 0, isCancelled: false, nextDeparture: "09:15"
        )
        #expect(DelayNotice.text(for: departure) == text)
    }
}
