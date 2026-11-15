import TableTest
import Testing
@testable import SkiRental

struct BindingTests {

    static let releaseSettings: [(Skier, Double)] = [
        (Skier(heightCm: 140, weightKg: 35, level: .beginner), 2.5),
        (Skier(heightCm: 165, weightKg: 55, level: .intermediate), 4.5),
        (Skier(heightCm: 170, weightKg: 70, level: .intermediate), 5.5),
        (Skier(heightCm: 175, weightKg: 72, level: .expert), 6.5),
        (Skier(heightCm: 178, weightKg: 80, level: .intermediate), 7.0),
        (Skier(heightCm: 180, weightKg: 90, level: .intermediate), 7.0),
        (Skier(heightCm: 185, weightKg: 95, level: .beginner), 6.5),
    ]

    @Test("Sets the release by weight class and level", arguments: releaseSettings)
    func setsTheReleaseByWeightClassAndLevel(skier: Skier, setting: Double) {
        #expect(Binding.releaseSetting(for: skier) == setting)
    }

    @TableTest("Fits the boot soles the rental bindings take", table: """
        Boot Sole  | Fits?
        alpine     | true
        grip walk  | true
        touring    | false
        """)
    func fitsTheBootSolesTheRentalBindingsTake(sole: BootSole, fits: Bool) {
        #expect(Binding.fits(sole) == fits)
    }
}
