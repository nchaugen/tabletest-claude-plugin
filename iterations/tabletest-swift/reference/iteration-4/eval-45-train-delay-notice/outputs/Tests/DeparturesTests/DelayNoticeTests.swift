import TableTest
import Testing
@testable import Departures

/// Fixture: the 14:05 service to Lillehammer. The next departure to Lillehammer is at 15:05.
struct DelayNoticeTests {

    static func departure(delayMinutes: Int, isCancelled: Bool) -> Departure {
        Departure(scheduled: "14:05", destination: "Lillehammer", delayMinutes: delayMinutes,
                  isCancelled: isCancelled, nextDeparture: "15:05")
    }

    @TableTest("Gives a departure the notice its delay calls for", table: """
        Scenario                    | Delay (min) | Cancelled | Notice?
        No delay                    | 0           | false     | onTime
        Just under five minutes     | 4           | false     | onTime
        At five minutes             | 5           | false     | delayed(minutes: 5)
        Just under the hour         | 59          | false     | delayed(minutes: 59)
        At the hour                 | 60          | false     | delayedWithRefund(minutes: 60)
        Cancelled, early or late    | {0, 75}     | true      | 'cancelled(next: "15:05")'
        """)
    func givesADepartureTheNoticeItsDelayCallsFor(delayMinutes: Int, isCancelled: Bool, notice: Notice) {
        #expect(DelayNotice.notice(for: Self.departure(delayMinutes: delayMinutes, isCancelled: isCancelled)) == notice)
    }

    // The wording is reviewed, not tested. One check shows that the text is the rendering of
    // the decided notice for this departure.

    @Test("Renders the notice the departure gets")
    func rendersTheNoticeTheDepartureGets() {
        #expect(DelayNotice.text(for: Self.departure(delayMinutes: 75, isCancelled: false)) == """
            The 14:05 service to Lillehammer is delayed by about 75 minutes. You can claim a refund \
            of half your fare: apply on our website within 30 days of travel, and keep your ticket \
            until the claim is settled.
            """)
    }
}
