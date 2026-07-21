package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    companion object {
        private val calculator = ShippingCostCalculator()
    }

    @TableTest("""
        Scenario                          | Zone           | Weight | Dimensions        | Fragile? | Insured Value? | Handling?  | Carrier     | Cost?
        EU standard light                  | EU standard    | 0.5    | [20, 15, 10]    |          |                |            | DHL         | 5.00
        EU standard medium                 | EU standard    | 3.0    | [30, 20, 15]    |          |                |            | DHL         | 7.50
        EU standard heavy                  | EU standard    | 10.0   | [40, 30, 20]    |          |                |            | DHL         | 12.50
        EU standard very heavy             | EU standard    | 25.0   | [50, 40, 30]    |          |                |            | UPS         | 20.00
        EU express light                   | EU express     | 0.5    | [20, 15, 10]    |          |                |            | DHL         | 8.00
        EU express medium                  | EU express     | 3.0    | [30, 20, 15]    |          |                |            | UPS         | 12.00
        US standard light                  | US standard    | 0.5    | [20, 15, 10]    |          |                |            | FEDEX       | 7.00
        US express heavy                   | US express     | 10.0   | [40, 30, 20]    |          |                |            | DHL         | 30.00
        Dimensional weight override        | EU standard    | 1.0    | [70, 50, 10]    |          |                |            | DHL         | 12.50
        Fragile surcharge                  | EU standard    | 3.0    | [30, 20, 15]    | true     |                |            | DHL         | 8.625
        Insured surcharge                  | EU standard    | 3.0    | [30, 20, 15]    |          | 500            |            | DHL         | 10.50
        Fragile and insured                | EU standard    | 3.0    | [30, 20, 15]    | true     | 200            |            | DHL         | 11.625
        Hazmat handling                    | EU standard    | 3.0    | [30, 20, 15]    |          |                | hazmat     | DHL         | 15.50
        Oversize surcharge                 | EU standard    | 3.0    | [120, 5, 5]     |          |                |            | DHL         | 17.50
        Same rate DHL                      | EU express     | 3.0    | [30, 20, 15]    |          |                |            | DHL         | 12.00
        Same rate UPS                      | EU express     | 3.0    | [30, 20, 15]    |          |                |            | UPS         | 12.00
        Same rate FEDEX                    | EU express     | 3.0    | [30, 20, 15]    |          |                |            | FEDEX       | 12.00
    """)
    fun calculateShippingCost(zone: ShippingZone, weight: Double, dimensions: List<Int>,
                              fragile: Boolean?, insuredValue: BigDecimal?, handling: String?,
                              carrier: Carrier, expected: BigDecimal) {
        val opts = PackageOptions()
        if (fragile == true) opts.isFragile = true
        if (insuredValue != null) opts.insuredValue = insuredValue
        if (handling != null) opts.handling = handling
        val actual = calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier)
        assertEquals(0, expected.compareTo(actual))
    }
}

@TypeConverter
fun parseShippingZone(input: String?): ShippingZone {
    if (input == null || input.isBlank()) return ShippingZone()
    val parts = input.trim().split("\\s+".toRegex())
    require(parts.size == 2) { "Expected 'region speed', got: $input" }
    return ShippingZone(parts[0], parts[1])
}

@TypeConverter
fun parseListInt(input: String?): List<Int> {
    if (input == null || input.isBlank()) return emptyList()
    val inner = input.trim().removePrefix("[").removeSuffix("]")
    return inner.split(",").mapNotNull { it.trim().toIntOrNull() }
}
