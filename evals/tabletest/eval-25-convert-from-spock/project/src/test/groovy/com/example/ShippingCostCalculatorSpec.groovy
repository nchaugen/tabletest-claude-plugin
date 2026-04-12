package com.example

import spock.lang.Specification
import spock.lang.Unroll

class ShippingCostCalculatorSpec extends Specification {

    def calculator = new ShippingCostCalculator()

    @Unroll
    def "shipping cost for #scenario"() {
        given:
        def zone = new ShippingZone()
        zone.region = region
        zone.speed = speed
        def dims = [length, width, height]
        def opts = new PackageOptions()
        if (fragile) opts.setFragile(true)
        if (insuredValue != null) opts.setInsuredValue(insuredValue)
        if (handling != null) opts.setHandling(handling)

        expect:
        calculator.calculateShippingCost(zone, weight, dims, opts, carrier) == expected

        where:
        scenario                        | region | speed      | weight | length | width | height | fragile | insuredValue | handling | carrier        | expected
        'EU standard light'             | 'EU'   | 'standard' | 0.5    | 20     | 15    | 10     | false   | null         | null     | Carrier.DHL    | 5.00
        'EU standard medium'            | 'EU'   | 'standard' | 3.0    | 30     | 20    | 15     | false   | null         | null     | Carrier.DHL    | 7.50
        'EU standard heavy'             | 'EU'   | 'standard' | 10.0   | 40     | 30    | 20     | false   | null         | null     | Carrier.DHL    | 12.50
        'EU standard very heavy'        | 'EU'   | 'standard' | 25.0   | 50     | 40    | 30     | false   | null         | null     | Carrier.UPS    | 20.00
        'EU express light'              | 'EU'   | 'express'  | 0.5    | 20     | 15    | 10     | false   | null         | null     | Carrier.DHL    | 8.00
        'EU express medium'             | 'EU'   | 'express'  | 3.0    | 30     | 20    | 15     | false   | null         | null     | Carrier.UPS    | 12.00
        'US standard light'             | 'US'   | 'standard' | 0.5    | 20     | 15    | 10     | false   | null         | null     | Carrier.FEDEX  | 7.00
        'US express heavy'              | 'US'   | 'express'  | 10.0   | 40     | 30    | 20     | false   | null         | null     | Carrier.DHL    | 30.00
        'dimensional weight override'   | 'EU'   | 'standard' | 1.0    | 70     | 50    | 10     | false   | null         | null     | Carrier.DHL    | 12.50
        'fragile surcharge'             | 'EU'   | 'standard' | 3.0    | 30     | 20    | 15     | true    | null         | null     | Carrier.DHL    | 8.625
        'insured surcharge'             | 'EU'   | 'standard' | 3.0    | 30     | 20    | 15     | false   | 500          | null     | Carrier.DHL    | 10.50
        'fragile and insured'           | 'EU'   | 'standard' | 3.0    | 30     | 20    | 15     | true    | 200          | null     | Carrier.DHL    | 11.625
        'hazmat handling'               | 'EU'   | 'standard' | 3.0    | 30     | 20    | 15     | false   | null         | 'hazmat' | Carrier.DHL    | 15.50
        'oversize surcharge'            | 'EU'   | 'standard' | 3.0    | 120    | 5     | 5      | false   | null         | null     | Carrier.DHL    | 17.50
        'same rate DHL'                 | 'EU'   | 'express'  | 3.0    | 30     | 20    | 15     | false   | null         | null     | Carrier.DHL    | 12.00
        'same rate UPS'                 | 'EU'   | 'express'  | 3.0    | 30     | 20    | 15     | false   | null         | null     | Carrier.UPS    | 12.00
        'same rate FEDEX'               | 'EU'   | 'express'  | 3.0    | 30     | 20    | 15     | false   | null         | null     | Carrier.FEDEX  | 12.00
    }
}
