package com.weatherapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.weatherapp.config.ApplicationProperties;
import com.weatherapp.model.WeatherData;
import com.weatherapp.service.WeatherService;
import lombok.NoArgsConstructor;
import java.io.InputStream;
import java.util.Scanner;

@NoArgsConstructor
public class WeatherController {
    private WeatherService weatherService;
    private Scanner scanner;
    
    public WeatherController(ApplicationProperties config) {
        this.weatherService = new WeatherService(config);
        this.scanner = new Scanner(System.in);
    }
    
    private ApplicationProperties loadConfiguration() throws Exception {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        InputStream inputStream = WeatherController.class.getResourceAsStream("/application.yaml");
        
        if (inputStream == null) {
            throw new RuntimeException("File application.yaml non trovato nel classpath");
        }
        
        return yamlMapper.readValue(inputStream, ApplicationProperties.class);
    }
    
    private void initializeServices() {
        try {
            ApplicationProperties config = loadConfiguration();
            this.weatherService = new WeatherService(config);
            this.scanner = new Scanner(System.in);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'inizializzazione dei servizi", e);
        }
    }
    
    public void start() {
        // Inizializza i servizi se non sono già stati inizializzati
        if (weatherService == null || scanner == null) {
            initializeServices();
        }
        
        System.out.println("=== App Meteo ===");
        System.out.println("Inserisci 'esci' per terminare il programma");
        
        while (true) {
            System.out.print("\nInserisci il nome di una città: ");
            String cityName = scanner.nextLine().trim();
            
            if (cityName.equalsIgnoreCase("esci")) {
                System.out.println("Arrivederci!");
                break;
            }
            
            if (cityName.isEmpty()) {
                System.out.println("Per favore, inserisci un nome di città valido.");
                continue;
            }
            
            try {
                WeatherData weather = weatherService.getWeatherData(cityName);
                System.out.println(weather);
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
}
