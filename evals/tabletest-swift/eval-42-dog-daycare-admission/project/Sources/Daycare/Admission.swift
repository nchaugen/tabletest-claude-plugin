/// A dog as the daycare registers it.
public struct Dog: Equatable, Sendable {
    public let name: String
    public let ageWeeks: Int
    public let vaccinations: Int
    public let weightKg: Int

    public init(name: String, ageWeeks: Int, vaccinations: Int, weightKg: Int) {
        self.name = name
        self.ageWeeks = ageWeeks
        self.vaccinations = vaccinations
        self.weightKg = weightKg
    }
}

/// The group a dog plays in, by its weight.
public enum PlayGroup: String, Equatable, Sendable {
    case small
    case medium
    case large
}

/// How long a dog stays on one day.
public enum Stay: String, Equatable, Sendable {
    case fullDay = "full day"
    case halfDay = "half day"
}

public enum Admission {
    public enum Decision: String, Equatable, Sendable {
        case admitted
        case tooYoung
        case needsVaccination
    }

    /// A dog is admitted from 12 weeks with one vaccination. A puppy under 16 weeks needs a
    /// second vaccination.
    public static func decision(for dog: Dog) -> Decision {
        if dog.ageWeeks < 12 { return .tooYoung }
        let needed = dog.ageWeeks < 16 ? 2 : 1
        return dog.vaccinations >= needed ? .admitted : .needsVaccination
    }

    public static func playGroup(for dog: Dog) -> PlayGroup {
        switch dog.weightKg {
        case ..<10: .small
        case 10..<25: .medium
        default: .large
        }
    }

    /// The price of one day, in whole kroner.
    public static func dailyFee(for group: PlayGroup, stay: Stay) -> Int {
        switch (group, stay) {
        case (.small, .fullDay): 320
        case (.small, .halfDay): 190
        case (.medium, .fullDay): 360
        case (.medium, .halfDay): 210
        case (.large, .fullDay): 400
        case (.large, .halfDay): 240
        }
    }
}
