package com.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ShippingCostCalculator {

    private static final BigDecimal DIMENSIONAL_DIVISOR = new BigDecimal("5000");
    private static final int OVERSIZE_THRESHOLD = 100;
    private static final BigDecimal OVERSIZE_FEE = new BigDecimal("10.00");
    private static final BigDecimal FRAGILE_MULTIPLIER = new BigDecimal("1.15");
    private static final BigDecimal INSURANCE_RATE = new BigDecimal("0.006");
    private static final BigDecimal INSURANCE_MINIMUM = new BigDecimal("3.00");
    private static final BigDecimal HAZMAT_FEE = new BigDecimal("8.00");

    public BigDecimal calculateShippingCost(ShippingZone zone, double weight,
            List<Integer> dimensions, PackageOptions options, Carrier carrier) {

        BigDecimal effectiveWeight = effectiveWeight(weight, dimensions);
        BigDecimal base = baseRate(zone.getRegion(), zone.getSpeed(), effectiveWeight);

        if (isOversize(dimensions)) {
            base = base.add(OVERSIZE_FEE);
        }

        if (options.getHandling() != null && options.getHandling().equals("hazmat")) {
            base = base.add(HAZMAT_FEE);
        }

        if (options.isFragile()) {
            base = base.multiply(FRAGILE_MULTIPLIER);
        }

        if (options.getInsuredValue() != null) {
            BigDecimal premium = options.getInsuredValue().multiply(INSURANCE_RATE);
            base = base.add(premium.max(INSURANCE_MINIMUM));
        }

        return base.setScale(3, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    private BigDecimal effectiveWeight(double actualWeight, List<Integer> dimensions) {
        long volume = (long) dimensions.get(0) * dimensions.get(1) * dimensions.get(2);
        BigDecimal dimensional = new BigDecimal(volume).divide(DIMENSIONAL_DIVISOR, 3, RoundingMode.HALF_UP);
        BigDecimal actual = BigDecimal.valueOf(actualWeight);
        return dimensional.compareTo(actual) > 0 ? dimensional : actual;
    }

    private boolean isOversize(List<Integer> dimensions) {
        return dimensions.stream().anyMatch(d -> d > OVERSIZE_THRESHOLD);
    }

    private BigDecimal baseRate(String region, String speed, BigDecimal weight) {
        double w = weight.doubleValue();
        if (region.equals("EU") && speed.equals("standard")) {
            if (w <= 1) return new BigDecimal("5.00");
            if (w <= 5) return new BigDecimal("7.50");
            if (w <= 15) return new BigDecimal("12.50");
            return new BigDecimal("20.00");
        }
        if (region.equals("EU") && speed.equals("express")) {
            if (w <= 1) return new BigDecimal("8.00");
            if (w <= 5) return new BigDecimal("12.00");
            if (w <= 15) return new BigDecimal("20.00");
            return new BigDecimal("32.00");
        }
        if (region.equals("US") && speed.equals("standard")) {
            if (w <= 1) return new BigDecimal("7.00");
            if (w <= 5) return new BigDecimal("10.50");
            if (w <= 15) return new BigDecimal("17.50");
            return new BigDecimal("28.00");
        }
        if (region.equals("US") && speed.equals("express")) {
            if (w <= 1) return new BigDecimal("11.00");
            if (w <= 5) return new BigDecimal("16.50");
            if (w <= 15) return new BigDecimal("30.00");
            return new BigDecimal("45.00");
        }
        throw new IllegalArgumentException("Unknown region/speed: " + region + "/" + speed);
    }
}
