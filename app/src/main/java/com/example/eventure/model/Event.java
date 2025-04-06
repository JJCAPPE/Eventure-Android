package com.example.eventure.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("url")
    private String url;
    
    @SerializedName("images")
    private List<Image> images;
    
    @SerializedName("dates")
    private Dates dates;
    
    @SerializedName("_embedded")
    private Embedded embedded;
    
    @SerializedName("priceRanges")
    private List<PriceRange> priceRanges;
    
    @SerializedName("info")
    private String info;
    
    @SerializedName("pleaseNote")
    private String pleaseNote;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<Image> getImages() {
        return images;
    }
    
    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            for (Image image : images) {
                if (image.getRatio().equals("16_9") && image.getWidth() > 500) {
                    return image.getUrl();
                }
            }
            return images.get(0).getUrl();
        }
        return null;
    }

    public Dates getDates() {
        return dates;
    }

    public Embedded getEmbedded() {
        return embedded;
    }
    
    public List<PriceRange> getPriceRanges() {
        return priceRanges;
    }
    
    public String getInfo() {
        return info;
    }
    
    public String getPleaseNote() {
        return pleaseNote;
    }
    
    public String getVenueName() {
        if (embedded != null && 
            embedded.getVenues() != null && 
            !embedded.getVenues().isEmpty()) {
            return embedded.getVenues().get(0).getName();
        }
        return "Unknown Venue";
    }
    
    public String getCity() {
        if (embedded != null && 
            embedded.getVenues() != null && 
            !embedded.getVenues().isEmpty() &&
            embedded.getVenues().get(0).getCity() != null) {
            return embedded.getVenues().get(0).getCity().getName();
        }
        return "Unknown City";
    }
    
    public String getState() {
        if (embedded != null && 
            embedded.getVenues() != null && 
            !embedded.getVenues().isEmpty() &&
            embedded.getVenues().get(0).getState() != null) {
            return embedded.getVenues().get(0).getState().getName();
        }
        return "";
    }
    
    public String getFormattedLocation() {
        String city = getCity();
        String state = getState();
        
        if (!city.equals("Unknown City")) {
            if (state != null && !state.isEmpty()) {
                return city + ", " + state;
            }
            return city;
        }
        return "Unknown Location";
    }
}
