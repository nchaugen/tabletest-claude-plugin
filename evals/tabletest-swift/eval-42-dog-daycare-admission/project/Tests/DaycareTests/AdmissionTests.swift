import TableTest
import Testing
@testable import Daycare

struct AdmissionTests {

    /// Fixture: Bella, a 20 kg dog. The weight does not take part in the admission.
    @TableTest("Admits a dog that is old enough and vaccinated", table: """
        Scenario                      | Age (weeks) | Vaccinations | Decision?
        A week under the youngest age | 11          | {1, 2}       | tooYoung
        At the youngest age           | 12          | 1            | admitted
        Old enough, never vaccinated  | {12, 52}    | 0            | needsVaccination
        """)
    func admitsADogThatIsOldEnoughAndVaccinated(ageWeeks: Int, vaccinations: Int, decision: Admission.Decision) {
        let dog = Dog(name: "Bella", ageWeeks: ageWeeks, vaccinations: vaccinations, weightKg: 20)

        #expect(Admission.decision(for: dog) == decision)
    }

    /// Fixture: Bella, a vaccinated adult dog.
    @TableTest("Puts a dog in the play group for its weight", table: """
        Weight (kg) | Play Group?
        {2, 9}      | small
        {10, 24}    | medium
        {25, 60}    | large
        """)
    func putsADogInThePlayGroupForItsWeight(weightKg: Int, playGroup: PlayGroup) {
        let dog = Dog(name: "Bella", ageWeeks: 52, vaccinations: 1, weightKg: weightKg)

        #expect(Admission.playGroup(for: dog) == playGroup)
    }

    @TableTest("Charges the daily fee of the play group and the stay", table: """
        Play Group | Stay     | Daily Fee (NOK)?
        small      | full day | 320
        small      | half day | 190
        medium     | full day | 360
        medium     | half day | 210
        large      | full day | 400
        large      | half day | 240
        """)
    func chargesTheDailyFeeOfThePlayGroupAndTheStay(playGroup: PlayGroup, stay: Stay, dailyFee: Int) {
        #expect(Admission.dailyFee(for: playGroup, stay: stay) == dailyFee)
    }
}
