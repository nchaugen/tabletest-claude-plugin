package com.example;

import java.math.BigDecimal;

public class PackageOptions {
    private boolean fragile;
    private BigDecimal insuredValue;
    private String handling;

    public boolean isFragile() { return fragile; }
    public void setFragile(boolean fragile) { this.fragile = fragile; }

    public BigDecimal getInsuredValue() { return insuredValue; }
    public void setInsuredValue(BigDecimal insuredValue) { this.insuredValue = insuredValue; }

    public String getHandling() { return handling; }
    public void setHandling(String handling) { this.handling = handling; }
}
