package com.example.eventure.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Image implements Serializable {
    @SerializedName("ratio")
    private String ratio;
    
    @SerializedName("url")
    private String url;
    
    @SerializedName("width")
    private int width;
    
    @SerializedName("height")
    private int height;

    public String getRatio() {
        return ratio;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
