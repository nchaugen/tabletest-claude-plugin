/// A charger at the station.
public enum Charger: String, CaseIterable, Sendable {
    case ac
    case fast
    case ultrafast
}

public enum ChargingFee {
    /// The fee in euros for one charging session. The first five minutes are free; after that,
    /// each started quarter of an hour costs the charger's rate, up to its session maximum.
    public static func euros(for charger: Charger, minutes: Int) -> Int {
        guard minutes > 5 else { return 0 }
        let startedQuarters = (minutes + 14) / 15
        return min(startedQuarters * quarterRate(of: charger), sessionMaximum(of: charger))
    }

    /// A session the meter could not read costs the session maximum.
    public static func unreadMeterEuros(for charger: Charger) -> Int {
        sessionMaximum(of: charger)
    }

    private static func quarterRate(of charger: Charger) -> Int {
        switch charger {
        case .ac: 1
        case .fast: 3
        case .ultrafast: 5
        }
    }

    private static func sessionMaximum(of charger: Charger) -> Int {
        switch charger {
        case .ac: 10
        case .fast: 25
        case .ultrafast: 40
        }
    }
}
