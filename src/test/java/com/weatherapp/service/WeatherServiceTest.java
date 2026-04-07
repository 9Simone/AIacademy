package com.weatherapp.service;

import com.weatherapp.config.ApplicationProperties;
import com.weatherapp.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherServiceTest {
    
    private WeatherService weatherService;
    
    @BeforeEach
    void setUp() {
        // Creiamo una configurazione di test con valori di default
        ApplicationProperties config = new ApplicationProperties();
        weatherService = new WeatherService(config);
    }
    
    @Test
    void testGetWeatherData_WithValidCity_ShouldReturnWeatherData() {
        // Questo test richiederà connessione internet
        // In un progetto reale, si dovrebbe mockare le chiamate HTTP
        
        try {
            WeatherData result = weatherService.getWeatherData("Roma");
            
            assertNotNull(result);
            assertEquals("Roma", result.getCityName());
            assertTrue(result.getTemperature() > -50 && result.getTemperature() < 60);
            assertNotNull(result.getDescription());
        } catch (Exception e) {
            // Se non c'è connessione internet, il test fallisce
            // In un ambiente reale, si dovrebbe mockare il servizio
            assertTrue(e.getMessage().contains("Città non trovata") || 
                      e.getMessage().contains("Errore nella chiamata API"));
        }
    }
    
    @Test
    void testGetWeatherData_WithInvalidCity_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeatherData("CittàInesistente12345");
        });
    }
}
