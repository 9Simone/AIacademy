package com.weatherapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.config.ApplicationProperties;
import com.weatherapp.model.WeatherData;
import com.weatherapp.model.OpenMeteoResponse;
import com.weatherapp.model.GeocodingResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

/**
 * Servizio principale per il recupero di dati meteo da API esterne.
 * 
 * Questa classe si occupa di:
 * - Geocoding: conversione nomi città → coordinate geografiche
 * - API calls: recupero dati meteo da Open-Meteo
 * - JSON parsing: trasformazione risposte API in oggetti Java
 * - Error handling: gestione errori di rete e API
 * 
 * Pattern utilizzato: Service Layer con dependency injection implicita
 * 
 * @author Weather App Team
 * @version 1.0.0
 */
public class WeatherService {
    
    // Configurazione centralizzata
    private final ApplicationProperties config;
    
    // Dipendenze iniettate
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Costruttore del servizio.
     * Inizializza le dipendenze necessarie per le chiamate HTTP e parsing JSON.
     */
    public WeatherService(ApplicationProperties config) {
        this.config = config;
        this.httpClient = createHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Crea e configura l'HTTP client.
     * 
     * @return HttpClient configurato con timeout appropriati
     */
    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(config.getHttpTimeoutSeconds()))
            .build();
    }
    
    /**
     * Recupera i dati meteo attuali per una città specificata.
     * 
     * Questo è il metodo principale che orchestra l'intero flusso:
     * 1. Converte il nome della città in coordinate (geocoding)
     * 2. Chiama l'API meteo con le coordinate ottenute
     * 3. Parsing della risposta JSON
     * 4. Restituisce un oggetto WeatherData formattato
     * 
     * @param cityName il nome della città (es: "Roma", "New York", "Tokyo")
     * @return WeatherData contenente temperatura, nome città e descrizione
     * @throws IOException se si verificano errori di rete o parsing
     * @throws IllegalArgumentException se la città non viene trovata
     */
    public WeatherData getWeatherData(String cityName) throws IOException {
        try {
            double[] coordinates = getCoordinatesForCity(cityName);
            
            if (coordinates == null) {
                throw new IllegalArgumentException(String.format(config.getMessagesErrorsCityNotFound(), cityName));
            }
            
            String apiUrl = buildWeatherApiUrl(coordinates);
            HttpResponse<String> response = executeHttpRequest(apiUrl);
            
            return parseWeatherResponse(response, cityName);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(config.getMessagesErrorsRequestInterrupted(), e);
        }
    }
    
    /**
     * Costruisce l'URL per l'API meteo Open-Meteo.
     * 
     * @param coordinates array con [latitudine, longitudine]
     * @return URL completo per la richiesta API
     */
    private String buildWeatherApiUrl(double[] coordinates) {
        return String.format(Locale.US, 
            "%s?latitude=%." + config.getAppCoordinatePrecision() + "f&longitude=%." + config.getAppCoordinatePrecision() + "f&%s&%s", 
            config.getApiWeatherOpenMeteo(), coordinates[0], coordinates[1], 
            config.getApiParamsWeatherCurrentWeather(), 
            config.getApiParamsWeatherTimezone());
    }
    
    /**
     * Esegue una richiesta HTTP all'URL specificato.
     * 
     * @param apiUrl URL completo per la richiesta
     * @return HttpResponse con la risposta del server
     * @throws IOException per errori di rete o interruzione
     * @throws InterruptedException se la richiesta viene interrotta
     */
    private HttpResponse<String> executeHttpRequest(String apiUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .timeout(Duration.ofSeconds(config.getHttpTimeoutSeconds()))
            .header("Accept", config.getHttpHeadersAccept())
            .GET()
            .build();
        
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
    
    /**
     * Parsing della risposta API meteo e creazione dell'oggetto WeatherData.
     * 
     * @param response risposta HTTP dall'API
     * @param cityName nome della città originale
     * @return WeatherData con i dati meteo
     * @throws IOException per errori di parsing o dati mancanti
     */
    private WeatherData parseWeatherResponse(HttpResponse<String> response, String cityName) throws IOException {
        if (response.statusCode() != 200) {
            throw new IOException(String.format(config.getMessagesErrorsApiCallError(), response.statusCode()));
        }
        
        OpenMeteoResponse apiResponse = objectMapper.readValue(response.body(), OpenMeteoResponse.class);
        
        if (apiResponse.getCurrentWeather() == null) {
            throw new IOException(config.getMessagesErrorsWeatherDataUnavailable());
        }
        
        double temperature = apiResponse.getCurrentWeather().getTemperature();
        return new WeatherData(temperature, cityName, config.getMessagesDefaultsWeatherDescription());
    }
    
    /**
     * Converte un nome di città in coordinate geografiche (latitudine, longitudine).
     * 
     * Questo metodo implementa il geocoding utilizzando l'API Nominatim:
     * - URL encode del nome città per gestire caratteri speciali
     * - Chiamata HTTP GET all'endpoint di geocoding
     * - Parsing JSON array per estrarre coordinate del primo risultato
     * 
     * @param cityName il nome della città da geocodificare
     * @return array double[2] con [latitudine, longitudine] o null se non trovato
     * @throws IOException per errori di rete o parsing JSON
     */
    private double[] getCoordinatesForCity(String cityName) throws IOException {
        try {
            String encodedCityName = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
            String apiUrl = buildGeocodingApiUrl(encodedCityName);
            
            HttpRequest request = createGeocodingRequest(apiUrl);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            return parseGeocodingResponse(response);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(config.getMessagesErrorsGeocodingInterrupted(), e);
        }
    }
    
    /**
     * Costruisce l'URL per l'API di geocoding Nominatim.
     * 
     * @param encodedCityName nome città codificato per URL
     * @return URL completo per la richiesta di geocoding
     */
    private String buildGeocodingApiUrl(String encodedCityName) {
        return String.format("%s?%s&q=%s&%s", 
            config.getApiGeocodingNominatim(), 
            config.getApiParamsGeocodingFormat(), 
            encodedCityName, 
            config.getApiParamsGeocodingLimit());
    }
    
    /**
     * Crea la richiesta HTTP per il geocoding.
     * 
     * @param apiUrl URL completo per la richiesta di geocoding
     * @return HttpRequest configurata per Nominatim
     */
    private HttpRequest createGeocodingRequest(String apiUrl) {
        return HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .timeout(Duration.ofSeconds(config.getHttpTimeoutSeconds()))
            .header("User-Agent", config.getHttpHeadersUserAgent())
            .GET()
            .build();
    }
    
    /**
     * Parsing della risposta di geocoding ed estrazione delle coordinate.
     * 
     * @param response risposta HTTP dall'API Nominatim
     * @return array double[2] con [latitudine, longitudine] o null
     * @throws IOException per errori di parsing JSON
     */
    private double[] parseGeocodingResponse(HttpResponse<String> response) throws IOException {
        if (response.statusCode() != 200) {
            return null;
        }
        
        GeocodingResponse[] geocodingResponses = objectMapper.readValue(
            response.body(), GeocodingResponse[].class);
        
        if (geocodingResponses.length == 0) {
            return null;
        }
        
        GeocodingResponse location = geocodingResponses[0];
        return new double[]{
            location.getLatitudeAsDouble(), 
            location.getLongitudeAsDouble()
        };
    }
}
