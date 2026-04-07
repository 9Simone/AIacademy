package com.weatherapp.service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Esempio di utilizzo del CacheService per memorizzare e recuperare dati da API con scadenza.
 * 
 * Questa classe dimostra come utilizzare il CacheService per ottimizzare le chiamate API
 * riducendo il numero di richieste e migliorando le performance.
 * 
 * @author Weather App Team
 * @version 1.0.0
 */
public class ApiCacheExample {
    
    private final CacheService<String> weatherCache;
    private final CacheService<String> forecastCache;
    
    public ApiCacheExample() {
        // Cache per dati meteo correnti con TTL di 30 minuti
        this.weatherCache = new CacheService<>(30);
        
        // Cache per previsioni con TTL di 2 ore
        this.forecastCache = new CacheService<>(120);
    }
    
    /**
     * Simula una chiamata API per ottenere dati meteo.
     * 
     * @param city Nome della città
     * @return Dati meteo in formato JSON
     */
    private String fetchWeatherFromApi(String city) {
        System.out.println("🌐 Chiamata API per meteo di: " + city);
        
        // Simula latenza di rete
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simula risposta API
        return String.format("""
            {
                "city": "%s",
                "temperature": 22.5,
                "humidity": 65,
                "description": "Parzialmente nuvoloso",
                "timestamp": "%s"
            }
            """, city, LocalDateTime.now());
    }
    
    /**
     * Ottiene dati meteo utilizzando la cache.
     * 
     * @param city Nome della città
     * @return Dati meteo
     */
    public String getWeatherData(String city) {
        String cacheKey = "weather_" + city.toLowerCase().replace(" ", "_");
        
        return weatherCache.getOrFetch(cacheKey, () -> fetchWeatherFromApi(city));
    }
    
    /**
     * Ottiene dati meteo con TTL personalizzato.
     * 
     * @param city Nome della città
     * @param customTtlMinutes TTL personalizzato in minuti
     * @return Dati meteo
     */
    public String getWeatherDataWithCustomTtl(String city, long customTtlMinutes) {
        String cacheKey = "weather_custom_" + city.toLowerCase().replace(" ", "_");
        
        return weatherCache.getOrFetch(cacheKey, () -> fetchWeatherFromApi(city), customTtlMinutes);
    }
    
    /**
     * Simula una chiamata API per ottenere previsioni.
     * 
     * @param city Nome della città
     * @return Dati previsionali in formato JSON
     */
    private String fetchForecastFromApi(String city) {
        System.out.println("🌐 Chiamata API per previsioni di: " + city);
        
        // Simula latenza di rete maggiore per le previsioni
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simula risposta API
        return String.format("""
            {
                "city": "%s",
                "forecast": [
                    {"day": "Domani", "temp_max": 24, "temp_min": 18},
                    {"day": "Dopodomani", "temp_max": 26, "temp_min": 19},
                    {"day": "Tra 3 giorni", "temp_max": 23, "temp_min": 17}
                ],
                "timestamp": "%s"
            }
            """, city, LocalDateTime.now());
    }
    
    /**
     * Ottiene previsioni meteo utilizzando la cache.
     * 
     * @param city Nome della città
     * @return Dati previsionali
     */
    public String getForecastData(String city) {
        String cacheKey = "forecast_" + city.toLowerCase().replace(" ", "_");
        
        return forecastCache.getOrFetch(cacheKey, () -> fetchForecastFromApi(city));
    }
    
    /**
     * Forza l'aggiornamento dei dati meteo rimuovendoli dalla cache.
     * 
     * @param city Nome della città
     * @return Nuovi dati meteo
     */
    public String refreshWeatherData(String city) {
        String cacheKey = "weather_" + city.toLowerCase().replace(" ", "_");
        weatherCache.remove(cacheKey);
        
        return getWeatherData(city);
    }
    
    /**
     * Metodo dimostrativo che mostra l'utilizzo del cache service.
     */
    public void demonstrateUsage() {
        System.out.println("=== Demo CacheService per API ===\n");
        
        String city = "Roma";
        
        // Prima chiamata - dovrebbe fare la richiesta API
        System.out.println("1. Prima richiesta (deve chiamare l'API):");
        long startTime = System.currentTimeMillis();
        String weather1 = getWeatherData(city);
        long endTime = System.currentTimeMillis();
        System.out.println("Risposta: " + weather1);
        System.out.println("Tempo impiegato: " + (endTime - startTime) + " ms\n");
        
        // Seconda chiamata - dovrebbe usare la cache
        System.out.println("2. Seconda richiesta (dovrebbe usare la cache):");
        startTime = System.currentTimeMillis();
        String weather2 = getWeatherData(city);
        endTime = System.currentTimeMillis();
        System.out.println("Risposta: " + weather2);
        System.out.println("Tempo impiegato: " + (endTime - startTime) + " ms\n");
        
        // Statistiche della cache
        System.out.println("3. Statistiche cache meteo:");
        System.out.println("- Dimensione totale: " + weatherCache.size());
        System.out.println("- Voci valide: " + weatherCache.validSize());
        System.out.println("- Contiene 'Roma': " + weatherCache.containsKey("weather_" + city.toLowerCase().replace(" ", "_")) + "\n");
        
        // Forza aggiornamento
        System.out.println("4. Aggiornamento forzato:");
        String refreshedWeather = refreshWeatherData(city);
        System.out.println("Dati aggiornati: " + refreshedWeather + "\n");
        
        // Pulizia cache scadute
        System.out.println("5. Pulizia voci scadute:");
        int removed = weatherCache.cleanupExpired();
        System.out.println("Voci rimosse: " + removed);
        System.out.println("Dimensione cache dopo pulizia: " + weatherCache.size());
    }
    
    /**
     * Esempio di utilizzo asincrono con CompletableFuture.
     * 
     * @param city Nome della città
     * @return CompletableFuture con i dati meteo
     */
    public CompletableFuture<String> getWeatherDataAsync(String city) {
        return CompletableFuture.supplyAsync(() -> getWeatherData(city));
    }
    
    /**
     * Metodo main per eseguire la demo.
     * 
     * @param args Argomenti da linea di comando
     */
    public static void main(String[] args) {
        ApiCacheExample example = new ApiCacheExample();
        example.demonstrateUsage();
    }
}
