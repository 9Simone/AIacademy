package com.weatherapp.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Classe di configurazione per mappare le proprietà dal file application.yaml
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationProperties {
    
    @JsonProperty("api.weather.open-meteo")
    private String apiWeatherOpenMeteo;
    
    @JsonProperty("api.geocoding.nominatim")
    private String apiGeocodingNominatim;
    
    @JsonProperty("api-params.weather.current-weather")
    private String apiParamsWeatherCurrentWeather;
    
    @JsonProperty("api-params.weather.timezone")
    private String apiParamsWeatherTimezone;
    
    @JsonProperty("api-params.geocoding.format")
    private String apiParamsGeocodingFormat;
    
    @JsonProperty("api-params.geocoding.limit")
    private String apiParamsGeocodingLimit;
    
    @JsonProperty("http.timeout-seconds")
    private Integer httpTimeoutSeconds;
    
    @JsonProperty("http.headers.user-agent")
    private String httpHeadersUserAgent;
    
    @JsonProperty("http.headers.accept")
    private String httpHeadersAccept;
    
    @JsonProperty("messages.errors.city-not-found")
    private String messagesErrorsCityNotFound;
    
    @JsonProperty("messages.errors.weather-data-unavailable")
    private String messagesErrorsWeatherDataUnavailable;
    
    @JsonProperty("messages.errors.api-call-error")
    private String messagesErrorsApiCallError;
    
    @JsonProperty("messages.errors.request-interrupted")
    private String messagesErrorsRequestInterrupted;
    
    @JsonProperty("messages.errors.geocoding-interrupted")
    private String messagesErrorsGeocodingInterrupted;
    
    @JsonProperty("messages.defaults.weather-description")
    private String messagesDefaultsWeatherDescription;
    
    @JsonProperty("app.coordinate-precision")
    private Integer appCoordinatePrecision;
    
    // Metodi con valori di default per evitare NullPointerException
    public String getApiWeatherOpenMeteo() {
        return apiWeatherOpenMeteo != null ? apiWeatherOpenMeteo : "https://api.open-meteo.com/v1/forecast";
    }
    
    public String getApiGeocodingNominatim() {
        return apiGeocodingNominatim != null ? apiGeocodingNominatim : "https://nominatim.openstreetmap.org/search";
    }
    
    public String getApiParamsWeatherCurrentWeather() {
        return apiParamsWeatherCurrentWeather != null ? apiParamsWeatherCurrentWeather : "current_weather=true";
    }
    
    public String getApiParamsWeatherTimezone() {
        return apiParamsWeatherTimezone != null ? apiParamsWeatherTimezone : "timezone=auto";
    }
    
    public String getApiParamsGeocodingFormat() {
        return apiParamsGeocodingFormat != null ? apiParamsGeocodingFormat : "format=json";
    }
    
    public String getApiParamsGeocodingLimit() {
        return apiParamsGeocodingLimit != null ? apiParamsGeocodingLimit : "limit=1";
    }
    
    public Integer getHttpTimeoutSeconds() {
        return httpTimeoutSeconds != null ? httpTimeoutSeconds : 10;
    }
    
    public String getHttpHeadersUserAgent() {
        return httpHeadersUserAgent != null ? httpHeadersUserAgent : "WeatherApp/1.0";
    }
    
    public String getHttpHeadersAccept() {
        return httpHeadersAccept != null ? httpHeadersAccept : "application/json";
    }
    
    public String getMessagesErrorsCityNotFound() {
        return messagesErrorsCityNotFound != null ? messagesErrorsCityNotFound : "Città non trovata: %s";
    }
    
    public String getMessagesErrorsWeatherDataUnavailable() {
        return messagesErrorsWeatherDataUnavailable != null ? messagesErrorsWeatherDataUnavailable : "Dati meteo non disponibili nella risposta";
    }
    
    public String getMessagesErrorsApiCallError() {
        return messagesErrorsApiCallError != null ? messagesErrorsApiCallError : "Errore nella chiamata API: %d";
    }
    
    public String getMessagesErrorsRequestInterrupted() {
        return messagesErrorsRequestInterrupted != null ? messagesErrorsRequestInterrupted : "Richiesta interrotta";
    }
    
    public String getMessagesErrorsGeocodingInterrupted() {
        return messagesErrorsGeocodingInterrupted != null ? messagesErrorsGeocodingInterrupted : "Richiesta geocoding interrotta";
    }
    
    public String getMessagesDefaultsWeatherDescription() {
        return messagesDefaultsWeatherDescription != null ? messagesDefaultsWeatherDescription : "Dati meteo attuali";
    }
    
    public Integer getAppCoordinatePrecision() {
        return appCoordinatePrecision != null ? appCoordinatePrecision : 6;
    }
}
