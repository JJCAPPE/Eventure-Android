package com.example.eventure.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Dates implements Serializable {
    @SerializedName("start")
    private Start start;
    
    @SerializedName("status")
    private Status status;

    public Start getStart() {
        return start;
    }

    public Status getStatus() {
        return status;
    }
    
    public static class Start implements Serializable {
        @SerializedName("localDate")
        private String localDate;
        
        @SerializedName("localTime")
        private String localTime;
        
        @SerializedName("dateTime")
        private String dateTime;

        public String getLocalDate() {
            return localDate;
        }

        public String getLocalTime() {
            return localTime;
        }

        public String getDateTime() {
            return dateTime;
        }
    }
    
    public static class Status implements Serializable {
        @SerializedName("code")
        private String code;

        public String getCode() {
            return code;
        }
    }
}
