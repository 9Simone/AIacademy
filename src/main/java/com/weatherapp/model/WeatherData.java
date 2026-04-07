package com.weatherapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherData {
    @JsonProperty("temperature")
    private double temperature;
    
    @JsonProperty("cityName")
    private String cityName;
    
    @JsonProperty("description")
    private String description;
    
    public WeatherData(double temperature, String cityName, String description) {
        this.temperature = temperature;
        this.cityName = cityName;
        this.description = description;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public String getCityName() {
        return cityName;
    }
    
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return String.format("Tempo a %s: %.1f°C - %s", cityName, temperature, description);
    }
}
