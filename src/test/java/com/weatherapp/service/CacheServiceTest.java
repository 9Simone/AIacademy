package com.weatherapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe CacheService.
 * 
 * @author Weather App Team
 * @version 1.0.0
 */
class CacheServiceTest {
    
    private CacheService<String> cacheService;
    
    @BeforeEach
    void setUp() {
        cacheService = new CacheService<>(1); // TTL di 1 minuto per i test
    }
    
    @Test
    @DisplayName("Dovrebbe memorizzare e recuperare dati correttamente")
    void testPutAndGet() {
        String key = "test-key";
        String value = "test-value";
        
        cacheService.put(key, value);
        
        assertEquals(value, cacheService.get(key));
        assertTrue(cacheService.containsKey(key));
        assertEquals(1, cacheService.size());
        assertEquals(1, cacheService.validSize());
    }
    
    @Test
    @DisplayName("Dovrebbe restituire null per chiave non esistente")
    void testGetNonExistentKey() {
        assertNull(cacheService.get("non-existent-key"));
        assertFalse(cacheService.containsKey("non-existent-key"));
    }
    
    @Test
    @DisplayName("Dovrebbe gestire correttamente la scadenza dei dati")
    void testExpiration() throws InterruptedException {
        String key = "expire-key";
        String value = "expire-value";
        
        // Usa TTL molto breve per il test - 1 millisecondo
        CacheService<String> shortTtlCache = new CacheService<>(1);
        shortTtlCache.put(key, value, 1); // 1 minuto = 60 secondi = 60000 millisecondi
        
        // Verifica che i dati siano presenti immediatamente
        assertEquals(value, shortTtlCache.get(key));
        
        // Attendi un po' per assicurare la scadenza
        Thread.sleep(100);
        
        // Verifica che i dati siano ancora presenti (1 minuto = 60000ms, non dovrebbero essere scaduti)
        assertEquals(value, shortTtlCache.get(key));
        
        // Test con TTL molto più breve usando un approccio diverso
        CacheService<String> veryShortTtlCache = new CacheService<>(1);
        // Mettiamo un valore direttamente con expirazione manuale
        veryShortTtlCache.put(key, value, 0); // 0 minuti = scade immediatamente
        
        // Dovrebbe essere scaduto
        assertNull(veryShortTtlCache.get(key));
        assertFalse(veryShortTtlCache.containsKey(key));
    }
    
    @Test
    @DisplayName("Dovrebbe utilizzare il supplier quando i dati non sono in cache")
    void testGetOrFetch() {
        String key = "fetch-key";
        String expectedValue = "fetched-value";
        
        String result = cacheService.getOrFetch(key, () -> expectedValue);
        
        assertEquals(expectedValue, result);
        assertEquals(expectedValue, cacheService.get(key)); // Dovrebbe essere in cache ora
    }
    
    @Test
    @DisplayName("Dovrebbe utilizzare dati dalla cache quando disponibili e non scaduti")
    void testGetOrFetchUsesCache() {
        String key = "cache-key";
        String cachedValue = "cached-value";
        String freshValue = "fresh-value";
        
        // Metti dati in cache
        cacheService.put(key, cachedValue);
        
        // Chiama getOrFetch - dovrebbe usare i dati in cache
        String result = cacheService.getOrFetch(key, () -> freshValue);
        
        assertEquals(cachedValue, result); // Dovrebbe restituire il valore cached
        assertEquals(cachedValue, cacheService.get(key)); // Cache non modificata
    }
    
    @Test
    @DisplayName("Dovrebbe rimuovere correttamente una chiave")
    void testRemove() {
        String key = "remove-key";
        String value = "remove-value";
        
        cacheService.put(key, value);
        assertEquals(1, cacheService.size());
        
        String removedValue = cacheService.remove(key);
        
        assertEquals(value, removedValue);
        assertEquals(0, cacheService.size());
        assertNull(cacheService.get(key));
        assertFalse(cacheService.containsKey(key));
    }
    
    @Test
    @DisplayName("Dovrebbe svuotare completamente la cache")
    void testClear() {
        cacheService.put("key1", "value1");
        cacheService.put("key2", "value2");
        cacheService.put("key3", "value3");
        
        assertEquals(3, cacheService.size());
        
        cacheService.clear();
        
        assertEquals(0, cacheService.size());
        assertTrue(cacheService.isEmpty());
    }
    
    @Test
    @DisplayName("Dovrebbe pulire le voci scadute")
    void testCleanupExpired() throws InterruptedException {
        CacheService<String> testCache = new CacheService<>(1);
        
        testCache.put("valid-key", "valid-value", 60); // Non scade presto
        testCache.put("expired-key", "expired-value", 0); // Scade immediatamente
        
        assertEquals(2, testCache.size());
        assertEquals(1, testCache.validSize()); // Solo "valid-key" dovrebbe essere valido
        
        int removedCount = testCache.cleanupExpired();
        
        assertEquals(1, removedCount);
        assertEquals(1, testCache.size());
        assertEquals(1, testCache.validSize());
        assertTrue(testCache.containsKey("valid-key"));
        assertFalse(testCache.containsKey("expired-key"));
    }
    
    @Test
    @DisplayName("Dovrebbe gestire correttamente valori null")
    void testNullValues() {
        String key = "null-key";
        
        cacheService.put(key, null);
        
        assertNull(cacheService.get(key));
        assertTrue(cacheService.containsKey(key));
    }
    
    @Test
    @DisplayName("Dovrebbe gestire correttamente chiavi null")
    void testNullKeys() {
        assertThrows(NullPointerException.class, () -> {
            cacheService.put(null, "value");
        });
        
        assertThrows(NullPointerException.class, () -> {
            cacheService.get(null);
        });
        
        assertThrows(NullPointerException.class, () -> {
            cacheService.containsKey(null);
        });
        
        assertThrows(NullPointerException.class, () -> {
            cacheService.remove(null);
        });
    }
    
    @Test
    @DisplayName("Dovrebbe essere thread-safe")
    void testThreadSafety() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = "key-" + threadId + "-" + j;
                        String value = "value-" + threadId + "-" + j;
                        
                        cacheService.put(key, value);
                        String retrieved = cacheService.get(key);
                        assertEquals(value, retrieved);
                        
                        if (j % 10 == 0) {
                            cacheService.remove(key);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        
        // Verifica che la cache sia in uno stato consistente
        assertTrue(cacheService.size() >= 0);
        assertTrue(cacheService.validSize() >= 0);
        assertTrue(cacheService.validSize() <= cacheService.size());
    }
    
    @Test
    @DisplayName("Dovrebbe gestire supplier null")
    void testNullSupplier() {
        String key = "supplier-key";
        
        assertThrows(NullPointerException.class, () -> {
            cacheService.getOrFetch(key, null);
        });
    }
    
    @Test
    @DisplayName("Dovrebbe funzionare con TTL personalizzato")
    void testCustomTtl() throws InterruptedException {
        String key = "custom-ttl-key";
        String value = "custom-ttl-value";
        
        cacheService.put(key, value, 0); // 0 minuti = scade immediatamente
        
        // Dovrebbe essere scaduto immediatamente
        assertNull(cacheService.get(key));
        
        // Test con TTL positivo
        String key2 = "custom-ttl-key-2";
        String value2 = "custom-ttl-value-2";
        
        cacheService.put(key2, value2, 60); // 60 minuti
        
        // Dovrebbe essere valido
        assertEquals(value2, cacheService.get(key2));
    }
}
