package com.weatherapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GeocodingResponse {
    @JsonProperty("place_id")
    private long placeId;
    
    @JsonProperty("licence")
    private String licence;
    
    @JsonProperty("osm_type")
    private String osmType;
    
    @JsonProperty("osm_id")
    private long osmId;
    
    @JsonProperty("lat")
    private String latitude;
    
    @JsonProperty("lon")
    private String longitude;
    
    @JsonProperty("class")
    private String className;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("place_rank")
    private int placeRank;
    
    @JsonProperty("importance")
    private double importance;
    
    @JsonProperty("addresstype")
    private String addressType;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("boundingbox")
    private List<String> boundingBox;
    
    // Getters e setters
    public long getPlaceId() {
        return placeId;
    }
    
    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }
    
    public String getLicence() {
        return licence;
    }
    
    public void setLicence(String licence) {
        this.licence = licence;
    }
    
    public String getOsmlType() {
        return osmType;
    }
    
    public void setOsmlType(String osmType) {
        this.osmType = osmType;
    }
    
    public long getOsmlId() {
        return osmId;
    }
    
    public void setOsmlId(long osmId) {
        this.osmId = osmId;
    }
    
    public String getLatitude() {
        return latitude;
    }
    
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
    public String getLongitude() {
        return longitude;
    }
    
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getPlaceRank() {
        return placeRank;
    }
    
    public void setPlaceRank(int placeRank) {
        this.placeRank = placeRank;
    }
    
    public double getImportance() {
        return importance;
    }
    
    public void setImportance(double importance) {
        this.importance = importance;
    }
    
    public String getAddressType() {
        return addressType;
    }
    
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public List<String> getBoundingBox() {
        return boundingBox;
    }
    
    public void setBoundingBox(List<String> boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    public double getLatitudeAsDouble() {
        return Double.parseDouble(latitude);
    }
    
    public double getLongitudeAsDouble() {
        return Double.parseDouble(longitude);
    }
}
