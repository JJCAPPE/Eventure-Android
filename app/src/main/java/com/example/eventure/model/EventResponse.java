package com.example.eventure.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EventResponse {
    @SerializedName("_embedded")
    private EventsEmbedded embedded;
    
    @SerializedName("page")
    private Page page;
    
    public EventsEmbedded getEmbedded() {
        return embedded;
    }
    
    public Page getPage() {
        return page;
    }
    
    public static class EventsEmbedded {
        @SerializedName("events")
        private List<Event> events;
        
        public List<Event> getEvents() {
            return events;
        }
    }
    
    public static class Page {
        @SerializedName("totalElements")
        private int totalElements;
        
        @SerializedName("totalPages")
        private int totalPages;
        
        @SerializedName("size")
        private int size;
        
        @SerializedName("number")
        private int number;
        
        public int getTotalElements() {
            return totalElements;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public int getSize() {
            return size;
        }
        
        public int getNumber() {
            return number;
        }
    }
}
