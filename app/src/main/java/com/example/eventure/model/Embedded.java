package com.example.eventure.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Embedded implements Serializable {
    @SerializedName("venues")
    private List<Venue> venues;

    public List<Venue> getVenues() {
        return venues;
    }
}
