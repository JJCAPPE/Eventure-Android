package com.example.eventure.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Venue implements Serializable {
    @SerializedName("name")
    private String name;
    
    @SerializedName("city")
    private City city;
    
    @SerializedName("state")
    private State state;
    
    @SerializedName("address")
    private Address address;
    
    @SerializedName("location")
    private Location location;

    public String getName() {
        return name;
    }

    public City getCity() {
        return city;
    }

    public State getState() {
        return state;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public static class City implements Serializable {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }
    
    public static class State implements Serializable {
        @SerializedName("name")
        private String name;
        
        @SerializedName("stateCode")
        private String stateCode;

        public String getName() {
            return name;
        }
        
        public String getStateCode() {
            return stateCode;
        }
    }
    
    public static class Address implements Serializable {
        @SerializedName("line1")
        private String line1;

        public String getLine1() {
            return line1;
        }
    }
    
    public static class Location implements Serializable {
        @SerializedName("longitude")
        private String longitude;
        
        @SerializedName("latitude")
        private String latitude;

        public String getLongitude() {
            return longitude;
        }

        public String getLatitude() {
            return latitude;
        }
    }
}
