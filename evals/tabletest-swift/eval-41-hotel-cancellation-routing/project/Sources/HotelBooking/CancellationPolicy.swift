public enum CancellationError: Error, Equatable {
    case stayAlreadyStarted
}

struct NotImplemented: Error {}

/// Fee charged for cancelling a booking, given the booking value in euros
/// and the number of whole days remaining before check-in.
/// A negative `daysBeforeCheckIn` means the stay has already started.
public func cancellationFee(bookingValue: Int, daysBeforeCheckIn: Int) throws -> Int {
    throw NotImplemented()
}
