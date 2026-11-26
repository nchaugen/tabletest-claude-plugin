import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// Fixture: the 10:30 service to Leeds, with the next departure to Leeds at 10:45.
    /// Thresholds held fixed by the rule: a delay under 5 minutes counts as on time, and a delay of
    /// 60 minutes or more gives a refund. A cancellation takes precedence over any delay.
    @TableTest("""
        Scenario                      | Delay (min) | Cancelled | Next Departure | Notice?
        Just under the delay limit    | 4           | false     | 10:45          | onTime
        At the delay limit            | 5           | false     | 10:45          | delayed(minutes: 5)
        Last minute before refund     | 59          | false     | 10:45          | delayed(minutes: 59)
        At the refund limit           | 60          | false     | 10:45          | delayedWithRefund(minutes: 60)
        Cancelled, whatever the delay | {0, 30, 90} | true      | 10:45          | cancelled(next: 10:45)
        """)
    func decidesWhichNoticeADepartureGets(delayMinutes: Int, cancelled: Bool, nextDeparture: String, notice: Notice) {
        let departure = Departure(
            scheduled: "10:30", destination: "Leeds", delayMinutes: delayMinutes,
            isCancelled: cancelled, nextDeparture: nextDeparture
        )
        #expect(DelayNotice.notice(for: departure) == notice)
    }

    /// Wiring check: the screen text names the service and carries the delay from the departure.
    /// The fixed sentences of the other notices are not asserted one by one.
    @Test func textNamesTheServiceAndTheDelay() {
        let departure = Departure(
            scheduled: "10:30", destination: "Leeds", delayMinutes: 20,
            isCancelled: false, nextDeparture: "10:45"
        )
        #expect(DelayNotice.text(for: departure) ==
            "The 10:30 service to Leeds is delayed by about 20 minutes. " +
            "We are sorry for the inconvenience, and we will update this notice as soon as we know more.")
    }
}
