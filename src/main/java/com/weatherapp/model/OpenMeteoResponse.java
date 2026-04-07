package com.weatherapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenMeteoResponse {
    @JsonProperty("latitude")
    private double latitude;
    
    @JsonProperty("longitude")
    private double longitude;
    
    @JsonProperty("generationtime_ms")
    private double generationtimeMs;
    
    @JsonProperty("utc_offset_seconds")
    private int utcOffsetSeconds;
    
    @JsonProperty("timezone")
    private String timezone;
    
    @JsonProperty("timezone_abbreviation")
    private String timezoneAbbreviation;
    
    @JsonProperty("elevation")
    private double elevation;
    
    @JsonProperty("current_weather_units")
    private CurrentWeatherUnits currentWeatherUnits;
    
    @JsonProperty("current_weather")
    private CurrentWeather currentWeather;
    
    // Getters e setters
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public double getGenerationtimeMs() {
        return generationtimeMs;
    }
    
    public void setGenerationtimeMs(double generationtimeMs) {
        this.generationtimeMs = generationtimeMs;
    }
    
    public int getUtcOffsetSeconds() {
        return utcOffsetSeconds;
    }
    
    public void setUtcOffsetSeconds(int utcOffsetSeconds) {
        this.utcOffsetSeconds = utcOffsetSeconds;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getTimezoneAbbreviation() {
        return timezoneAbbreviation;
    }
    
    public void setTimezoneAbbreviation(String timezoneAbbreviation) {
        this.timezoneAbbreviation = timezoneAbbreviation;
    }
    
    public double getElevation() {
        return elevation;
    }
    
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }
    
    public CurrentWeatherUnits getCurrentWeatherUnits() {
        return currentWeatherUnits;
    }
    
    public void setCurrentWeatherUnits(CurrentWeatherUnits currentWeatherUnits) {
        this.currentWeatherUnits = currentWeatherUnits;
    }
    
    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }
    
    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }
    
    public static class CurrentWeatherUnits {
        @JsonProperty("time")
        private String time;
        
        @JsonProperty("interval")
        private String interval;
        
        @JsonProperty("temperature")
        private String temperature;
        
        @JsonProperty("windspeed")
        private String windspeed;
        
        @JsonProperty("winddirection")
        private String winddirection;
        
        @JsonProperty("is_day")
        private String isDay;
        
        @JsonProperty("weathercode")
        private String weathercode;
        
        // Getters e setters
        public String getTime() {
            return time;
        }
        
        public void setTime(String time) {
            this.time = time;
        }
        
        public String getInterval() {
            return interval;
        }
        
        public void setInterval(String interval) {
            this.interval = interval;
        }
        
        public String getTemperature() {
            return temperature;
        }
        
        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }
        
        public String getWindspeed() {
            return windspeed;
        }
        
        public void setWindspeed(String windspeed) {
            this.windspeed = windspeed;
        }
        
        public String getWinddirection() {
            return winddirection;
        }
        
        public void setWinddirection(String winddirection) {
            this.winddirection = winddirection;
        }
        
        public String getIsDay() {
            return isDay;
        }
        
        public void setIsDay(String isDay) {
            this.isDay = isDay;
        }
        
        public String getWeathercode() {
            return weathercode;
        }
        
        public void setWeathercode(String weathercode) {
            this.weathercode = weathercode;
        }
    }
    
    public static class CurrentWeather {
        @JsonProperty("time")
        private String time;
        
        @JsonProperty("interval")
        private int interval;
        
        @JsonProperty("temperature")
        private double temperature;
        
        @JsonProperty("windspeed")
        private double windSpeed;
        
        @JsonProperty("winddirection")
        private int windDirection;
        
        @JsonProperty("is_day")
        private int isDay;
        
        @JsonProperty("weathercode")
        private int weatherCode;
        
        // Getters e setters
        public String getTime() {
            return time;
        }
        
        public void setTime(String time) {
            this.time = time;
        }
        
        public int getInterval() {
            return interval;
        }
        
        public void setInterval(int interval) {
            this.interval = interval;
        }
        
        public double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
        
        public double getWindSpeed() {
            return windSpeed;
        }
        
        public void setWindSpeed(double windSpeed) {
            this.windSpeed = windSpeed;
        }
        
        public int getWindDirection() {
            return windDirection;
        }
        
        public void setWindDirection(int windDirection) {
            this.windDirection = windDirection;
        }
        
        public int getIsDay() {
            return isDay;
        }
        
        public void setIsDay(int isDay) {
            this.isDay = isDay;
        }
        
        public int getWeatherCode() {
            return weatherCode;
        }
        
        public void setWeatherCode(int weatherCode) {
            this.weatherCode = weatherCode;
        }
    }
}
