package com.weatherapp;

import com.weatherapp.controller.WeatherController;

/**
 * Entry point principale dell'applicazione Weather App.
 * 
 * Questa classe si occupa di:
 * - Inizializzare il controller principale
 * - Avviare il flusso di interazione con l'utente
 * - Gestire eventuali errori di startup
 * 
 * @author Weather App Team
 * @version 1.0.0
 */
public class Main {
    
    /**
     * Metodo main - punto di ingresso dell'applicazione.
     * 
     * @param args argomenti da linea di comando (non utilizzati in questa versione)
     */
    public static void main(String[] args) {
        try {
            // Inizializzazione del controller che gestisce l'interazione utente
            WeatherController controller = new WeatherController();
            
            // Avvio del loop principale per l'input dell'utente
            controller.start();
            
        } catch (Exception e) {
            // Gestione errori critici durante l'avvio
            System.err.println("Errore fatale durante l'avvio dell'applicazione:");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
