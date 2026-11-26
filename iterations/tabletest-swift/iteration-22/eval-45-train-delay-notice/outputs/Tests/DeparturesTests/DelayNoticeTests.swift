import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// The decision behind the text of a notice: what the passengers of one departure are told.
    /// Thresholds held in the code, not passed in: delays under 5 minutes count as on time, and
    /// 60 minutes or more earns a refund. A cancellation takes precedence over any delay.
    /// Fixture: every departure is the 09:30 service to Oslo.
    @TableTest("""
        Scenario                         | Delay (min) | Cancelled | Next Departure | Notice?
        Within the on-time allowance     | {0, 4}      | false     | 10:15          | onTime
        First delay worth announcing     | 5           | false     | 10:15          | delayed(minutes: 5)
        Last delay before compensation   | 59          | false     | 10:15          | delayed(minutes: 59)
        Compensation threshold reached   | 60          | false     | 10:15          | delayedWithRefund(minutes: 60)
        Train not running, any delay     | {0, 30, 90} | true      | 10:15          | cancelled(next: 10:15)
        """)
    func decidesTheNoticeOfADeparture(delay: Int, cancelled: Bool, nextDeparture: String, notice: Notice) {
        let departure = Departure(scheduled: "09:30", destination: "Oslo", delayMinutes: delay,
                                  isCancelled: cancelled, nextDeparture: nextDeparture)
        #expect(DelayNotice.notice(for: departure) == notice)
    }

    /// Wiring check: the text for a departure names its service and carries the delay of its notice.
    /// The constant sentences are not asserted one by one.
    @Test func textNamesTheServiceAndTheDelay() {
        let departure = Departure(scheduled: "09:30", destination: "Oslo", delayMinutes: 12,
                                  isCancelled: false, nextDeparture: "10:15")
        #expect(DelayNotice.text(for: departure) == """
            The 09:30 service to Oslo is delayed by about 12 minutes. \
            We are sorry for the inconvenience, and we will update this notice as soon as we know more.
            """)
    }
}
