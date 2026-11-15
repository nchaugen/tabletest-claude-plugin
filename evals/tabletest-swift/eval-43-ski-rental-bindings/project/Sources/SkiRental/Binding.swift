/// How well a skier skis, as the rental desk asks it.
public enum Level: Equatable, Sendable {
    case beginner
    case intermediate
    case expert
}

/// A skier as the rental desk records them.
public struct Skier: Equatable, Sendable {
    public let heightCm: Int
    public let weightKg: Int
    public let level: Level

    public init(heightCm: Int, weightKg: Int, level: Level) {
        self.heightCm = heightCm
        self.weightKg = weightKg
        self.level = level
    }
}

/// The sole of a ski boot.
public enum BootSole: String, Equatable, Sendable {
    case alpine
    case gripWalk = "grip walk"
    case touring
}

public enum Binding {
    /// The release setting for a skier: the setting of their weight class (under 50 kg, 50–64 kg,
    /// 65–79 kg, 80 kg and over), lowered by half a step for a beginner and raised by one step for
    /// an expert.
    public static func releaseSetting(for skier: Skier) -> Double {
        weightClassSetting(skier.weightKg) + levelAdjustment(skier.level)
    }

    /// The rental bindings take an alpine or a grip-walk sole.
    public static func fits(_ sole: BootSole) -> Bool {
        sole != .touring
    }

    private static func weightClassSetting(_ weightKg: Int) -> Double {
        if weightKg < 50 { return 3.0 }
        if weightKg < 65 { return 4.5 }
        if weightKg <= 80 { return 5.5 }
        return 7.0
    }

    private static func levelAdjustment(_ level: Level) -> Double {
        switch level {
        case .beginner: -0.5
        case .intermediate: 0
        case .expert: 1.0
        }
    }
}
