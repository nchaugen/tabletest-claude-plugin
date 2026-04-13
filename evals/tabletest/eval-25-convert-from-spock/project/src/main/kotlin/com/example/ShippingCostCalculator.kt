package com.example

import java.math.BigDecimal
import java.math.RoundingMode

class ShippingCostCalculator {

    companion object {
        private val DIMENSIONAL_DIVISOR = BigDecimal("5000")
        private const val OVERSIZE_THRESHOLD = 100
        private val OVERSIZE_FEE = BigDecimal("10.00")
        private val FRAGILE_MULTIPLIER = BigDecimal("1.15")
        private val INSURANCE_RATE = BigDecimal("0.006")
        private val INSURANCE_MINIMUM = BigDecimal("3.00")
        private val HAZMAT_FEE = BigDecimal("8.00")
    }

    fun calculateShippingCost(
        zone: ShippingZone, weight: Double,
        dimensions: List<Int>, options: PackageOptions, carrier: Carrier
    ): BigDecimal {
        val effectiveWeight = effectiveWeight(weight, dimensions)
        var base = baseRate(zone.region, zone.speed, effectiveWeight)

        if (isOversize(dimensions)) {
            base = base.add(OVERSIZE_FEE)
        }

        if (options.handling != null && options.handling == "hazmat") {
            base = base.add(HAZMAT_FEE)
        }

        if (options.isFragile) {
            base = base.multiply(FRAGILE_MULTIPLIER)
        }

        if (options.insuredValue != null) {
            val premium = options.insuredValue!!.multiply(INSURANCE_RATE)
            base = base.add(premium.max(INSURANCE_MINIMUM))
        }

        return base.setScale(3, RoundingMode.HALF_UP).stripTrailingZeros()
    }

    private fun effectiveWeight(actualWeight: Double, dimensions: List<Int>): BigDecimal {
        val volume = dimensions[0].toLong() * dimensions[1] * dimensions[2]
        val dimensional = BigDecimal(volume).divide(DIMENSIONAL_DIVISOR, 3, RoundingMode.HALF_UP)
        val actual = BigDecimal.valueOf(actualWeight)
        return if (dimensional > actual) dimensional else actual
    }

    private fun isOversize(dimensions: List<Int>): Boolean {
        return dimensions.any { it > OVERSIZE_THRESHOLD }
    }

    private fun baseRate(region: String, speed: String, weight: BigDecimal): BigDecimal {
        val w = weight.toDouble()
        if (region == "EU" && speed == "standard") {
            if (w <= 1) return BigDecimal("5.00")
            if (w <= 5) return BigDecimal("7.50")
            if (w <= 15) return BigDecimal("12.50")
            return BigDecimal("20.00")
        }
        if (region == "EU" && speed == "express") {
            if (w <= 1) return BigDecimal("8.00")
            if (w <= 5) return BigDecimal("12.00")
            if (w <= 15) return BigDecimal("20.00")
            return BigDecimal("32.00")
        }
        if (region == "US" && speed == "standard") {
            if (w <= 1) return BigDecimal("7.00")
            if (w <= 5) return BigDecimal("10.50")
            if (w <= 15) return BigDecimal("17.50")
            return BigDecimal("28.00")
        }
        if (region == "US" && speed == "express") {
            if (w <= 1) return BigDecimal("11.00")
            if (w <= 5) return BigDecimal("16.50")
            if (w <= 15) return BigDecimal("30.00")
            return BigDecimal("45.00")
        }
        throw IllegalArgumentException("Unknown region/speed: $region/$speed")
    }
}
