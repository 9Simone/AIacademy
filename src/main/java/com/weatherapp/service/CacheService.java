package com.weatherapp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Servizio di cache con scadenza basata sul tempo per memorizzare e recuperare dati da API.
 * 
 * Questa classe fornisce funzionalità di caching con TTL (Time To Live) per ottimizzare
 * le chiamate a servizi esterni riducendo il numero di richieste API.
 * 
 * @param <T> Tipo di dati da memorizzare in cache
 * @author Weather App Team
 * @version 1.0.0
 */
public class CacheService<T> {
    
    private final Map<String, CacheEntry<T>> cache;
    private final long defaultTtlMinutes;
    
    /**
     * Classe interna per rappresentare una voce nella cache con timestamp di scadenza.
     */
    private static class CacheEntry<V> {
        private final V data;
        private final long expiryTimeMillis;
        
        public CacheEntry(V data, long ttlMinutes) {
            this.data = data;
            // Se TTL è 0 o negativo, scade immediatamente
            if (ttlMinutes <= 0) {
                this.expiryTimeMillis = System.currentTimeMillis() - 1;
            } else {
                this.expiryTimeMillis = System.currentTimeMillis() + (ttlMinutes * 60 * 1000);
            }
        }
        
        public V getData() {
            return data;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTimeMillis;
        }
    }
    
    /**
     * Costruttore con TTL di default di 60 minuti.
     */
    public CacheService() {
        this(60);
    }
    
    /**
     * Costruttore con TTL personalizzato.
     * 
     * @param defaultTtlMinutes Tempo di vita default in minuti per le voci della cache
     */
    public CacheService(long defaultTtlMinutes) {
        this.cache = new HashMap<>();
        this.defaultTtlMinutes = defaultTtlMinutes;
    }
    
    /**
     * Memorizza dati nella cache con TTL di default.
     * 
     * @param key Chiave univoca per identificare i dati
     * @param data Dati da memorizzare
     * @throws NullPointerException se la chiave è null
     */
    public void put(String key, T data) {
        put(key, data, defaultTtlMinutes);
    }
    
    /**
     * Memorizza dati nella cache con TTL personalizzato.
     * 
     * @param key Chiave univoca per identificare i dati
     * @param data Dati da memorizzare
     * @param ttlMinutes Tempo di vita in minuti
     * @throws NullPointerException se la chiave è null
     */
    public void put(String key, T data, long ttlMinutes) {
        if (key == null) {
            throw new NullPointerException("La chiave non può essere null");
        }
        cache.put(key, new CacheEntry<>(data, ttlMinutes));
    }
    
    /**
     * Recupera dati dalla cache se non scaduti.
     * 
     * @param key Chiave dei dati da recuperare
     * @return Dati se presenti e non scaduti, altrimenti null
     * @throws NullPointerException se la chiave è null
     */
    public T get(String key) {
        if (key == null) {
            throw new NullPointerException("La chiave non può essere null");
        }
        CacheEntry<T> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.getData();
        }
        
        // Rimuovi voce scaduta dalla cache
        cache.remove(key);
        return null;
    }
    
    /**
     * Recupera dati dalla cache, se non presenti o scaduti li recupera tramite il supplier fornito.
     * 
     * @param key Chiave dei dati da recuperare
     * @param dataSupplier Funzione per recuperare i dati se non in cache
     * @return Dati dalla cache o dal supplier
     * @throws NullPointerException se la chiave o il supplier sono null
     */
    public T getOrFetch(String key, Supplier<T> dataSupplier) {
        return getOrFetch(key, dataSupplier, defaultTtlMinutes);
    }
    
    /**
     * Recupera dati dalla cache, se non presenti o scaduti li recupera tramite il supplier fornito.
     * 
     * @param key Chiave dei dati da recuperare
     * @param dataSupplier Funzione per recuperare i dati se non in cache
     * @param ttlMinutes TTL per i nuovi dati memorizzati
     * @return Dati dalla cache o dal supplier
     * @throws NullPointerException se la chiave o il supplier sono null
     */
    public T getOrFetch(String key, Supplier<T> dataSupplier, long ttlMinutes) {
        if (dataSupplier == null) {
            throw new NullPointerException("Il supplier non può essere null");
        }
        T cachedData = get(key);
        if (cachedData != null) {
            return cachedData;
        }
        
        // Recupera i dati tramite il supplier
        T freshData = dataSupplier.get();
        if (freshData != null) {
            put(key, freshData, ttlMinutes);
        }
        
        return freshData;
    }
    
    /**
     * Verifica se una chiave esiste nella cache e non è scaduta.
     * 
     * @param key Chiave da verificare
     * @return true se la chiave esiste e non è scaduta
     * @throws NullPointerException se la chiave è null
     */
    public boolean containsKey(String key) {
        if (key == null) {
            throw new NullPointerException("La chiave non può essere null");
        }
        CacheEntry<T> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return true;
        }
        
        // Rimuovi voce scaduta dalla cache
        cache.remove(key);
        return false;
    }
    
    /**
     * Rimuove una chiave dalla cache.
     * 
     * @param key Chiave da rimuovere
     * @return Dati rimossi o null se non presenti
     * @throws NullPointerException se la chiave è null
     */
    public T remove(String key) {
        if (key == null) {
            throw new NullPointerException("La chiave non può essere null");
        }
        CacheEntry<T> entry = cache.remove(key);
        return entry != null ? entry.getData() : null;
    }
    
    /**
     * Svuota completamente la cache.
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Rimuove tutte le voci scadute dalla cache.
     * 
     * @return Numero di voci rimosse
     */
    public int cleanupExpired() {
        final int[] removedCount = {0};
        cache.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                removedCount[0]++;
            }
            return expired;
        });
        return removedCount[0];
    }
    
    /**
     * Restituisce il numero di voci attualmente in cache (incluse quelle scadute).
     * 
     * @return Numero di voci totali
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Restituisce il numero di voci valide (non scadute) in cache.
     * 
     * @return Numero di voci valide
     */
    public int validSize() {
        return (int) cache.entrySet().stream()
                .filter(entry -> !entry.getValue().isExpired())
                .count();
    }
    
    /**
     * Verifica se la cache è vuota.
     * 
     * @return true se non ci sono voci in cache
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
