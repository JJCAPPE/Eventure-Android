package com.example.eventure.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PriceRange implements Serializable {
    @SerializedName("type")
    private String type;
    
    @SerializedName("currency")
    private String currency;
    
    @SerializedName("min")
    private double min;
    
    @SerializedName("max")
    private double max;

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
    
    public String getPriceRangeDisplay() {
        return currency + " " + min + " - " + currency + " " + max;
    }
}
