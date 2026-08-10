package payroll;

public final class WeeklyPayCalculator {

    private WeeklyPayCalculator() {
    }

    /**
     * Weekly pay for one employee. A null hour count means no hours of that kind were worked.
     */
    public static int calculateWeeklyPay(
            Integer weekdayHours,
            Integer sundayHours,
            Integer holidayHours,
            int hourlyRate) {
        throw new UnsupportedOperationException("Weekly pay calculation not implemented yet");
    }
}
