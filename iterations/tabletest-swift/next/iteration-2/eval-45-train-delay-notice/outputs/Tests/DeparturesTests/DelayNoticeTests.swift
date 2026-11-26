import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// Fixture: a service to Leeds scheduled for 08:15, with the next one at 09:15.
    /// Held constant: a delay under 5 minutes counts as on time, and a delay of 60 minutes or more
    /// earns a refund. A cancelled service gets its notice whatever its delay.
    @TableTest("""
        Scenario                    | Cancelled | Delay (min)   | Notice?
        Just short of a late train  | false     | {0, 4}        | onTime
        Smallest delay worth noting | false     | 5             | delayed(minutes: 5)
        Longest delay without claim | false     | 59            | delayed(minutes: 59)
        Smallest delay with a claim | false     | 60            | delayedWithRefund(minutes: 60)
        Withdrawn before departure  | true      | {0, 30, 90}   | cancelled(next: '09:15')
        """)
    func decidesWhatTheNoticeTellsPassengers(cancelled: Bool, delay: Int, notice: Notice) {
        let departure = Departure(
            scheduled: "08:15", destination: "Leeds", delayMinutes: delay,
            isCancelled: cancelled, nextDeparture: "09:15"
        )
        #expect(DelayNotice.notice(for: departure) == notice)
    }

    /// Wiring check: the screen text names the service and carries the notice's data. The table
    /// above covers the decision, so the other sentences are not copied here.
    @Test func textNamesTheServiceAndTheDelay() {
        let departure = Departure(
            scheduled: "08:15", destination: "Leeds", delayMinutes: 12,
            isCancelled: false, nextDeparture: "09:15"
        )
        #expect(DelayNotice.text(for: departure) == """
            The 08:15 service to Leeds is delayed by about 12 minutes. \
            We are sorry for the inconvenience, and we will update this notice \
            as soon as we know more.
            """)
    }
}
