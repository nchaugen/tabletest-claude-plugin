import TableTest
import Testing
@testable import Departures

struct DelayNoticeTests {
    /// The decision behind the passenger text. The 5-minute and 60-minute limits are written into
    /// `DelayNotice.notice(for:)` and are not exposed as constants, so the rows straddle them.
    /// Fixture: the 14:05 service to Leeds; the next departure to the same destination leaves 15:05.
    /// A cancellation is announced the same way whatever the delay was.
    @TableTest("""
        Scenario                        | Delay (min) | Cancelled | Notice?
        Below the announcement limit    | {0, 4}      | false     | onTime
        At the announcement limit       | 5           | false     | delayed(minutes: 5)
        Below the refund limit          | 59          | false     | delayed(minutes: 59)
        At the refund limit             | 60          | false     | delayedWithRefund(minutes: 60)
        Service withdrawn               | {0, 5, 90}  | true      | cancelled(next: 15:05)
        """)
    func decidesWhichNoticeADepartureGets(delayMinutes: Int, cancelled: Bool, notice: Notice) {
        let departure = Departure(
            scheduled: "14:05", destination: "Leeds", delayMinutes: delayMinutes,
            isCancelled: cancelled, nextDeparture: "15:05"
        )
        #expect(DelayNotice.notice(for: departure) == notice)
    }

    /// Wiring check: the screen text names the service from the departure and carries the
    /// wording of the notice that was decided. The wording of each notice is not asserted again here.
    @Test func textNamesTheServiceAndStatesTheDecidedNotice() {
        let departure = Departure(
            scheduled: "14:05", destination: "Leeds", delayMinutes: 75,
            isCancelled: false, nextDeparture: "15:05"
        )
        #expect(DelayNotice.text(for: departure) == DelayNotice.text(
            of: .delayedWithRefund(minutes: 75), service: "The 14:05 service to Leeds"
        ))
    }
}
