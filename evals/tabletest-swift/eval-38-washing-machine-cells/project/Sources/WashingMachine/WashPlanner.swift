/// What the laundry is made of.
public enum Fabric: Sendable {
    case cotton
    case synthetics
    case wool
}

/// The program the user sets on the machine. A new program washes at 40 °C and starts now.
public struct WashProgram: Equatable, Sendable {
    public var fabric: Fabric
    public var temperature: Int = 40
    public var prewash: Bool = false
    public var extraRinse: Bool = false
    /// Hours until the wash starts; nil starts it now.
    public var delayStartHours: Int? = nil

    public init(fabric: Fabric, temperature: Int = 40, prewash: Bool = false, extraRinse: Bool = false,
                delayStartHours: Int? = nil) {
        self.fabric = fabric
        self.temperature = temperature
        self.prewash = prewash
        self.extraRinse = extraRinse
        self.delayStartHours = delayStartHours
    }
}

/// What the machine does with a program.
public enum WashOutcome: Equatable, Sendable {
    case runs(minutes: Int)
    case scheduled(inHours: Int, minutes: Int)
    case tooHot(limit: Int)
}

public enum WashPlanner {
    /// What the machine does with the program.
    ///
    /// - The machine refuses a temperature above the fabric's limit: cotton 90 °C, synthetics
    ///   60 °C, wool 40 °C.
    /// - A wash takes 120 minutes for cotton, 90 for synthetics and 50 for wool, at 40 °C. Each
    ///   10 °C above 40 adds 10 minutes.
    /// - A prewash adds 20 minutes, and an extra rinse adds 15.
    /// - A delayed start schedules the wash; the wash then takes the same time.
    public static func outcome(of program: WashProgram) -> WashOutcome {
        let limit = temperatureLimit(of: program.fabric)
        guard program.temperature <= limit else { return .tooHot(limit: limit) }
        let minutes = baseMinutes(of: program.fabric)
            + max(0, program.temperature - 40) / 10 * 10
            + (program.prewash ? 20 : 0)
            + (program.extraRinse ? 15 : 0)
        guard let delay = program.delayStartHours else { return .runs(minutes: minutes) }
        return .scheduled(inHours: delay, minutes: minutes)
    }

    private static func temperatureLimit(of fabric: Fabric) -> Int {
        switch fabric {
        case .cotton: 90
        case .synthetics: 60
        case .wool: 40
        }
    }

    private static func baseMinutes(of fabric: Fabric) -> Int {
        switch fabric {
        case .cotton: 120
        case .synthetics: 90
        case .wool: 50
        }
    }
}
