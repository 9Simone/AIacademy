# Weather App - App Meteo in Java con Maven

> **Nota per sviluppatori**: Questa guida è pensata per programmatori che vogliono capire, modificare o estendere questo progetto meteo.

## 🎯 **Obiettivo del Progetto**

Applicazione console Java che recupera dati meteo in tempo reale da API esterne, convertendo nomi di città in coordinate geografiche e mostrando temperature attuali.

## 🏗️ **Architettura del Progetto**

```
weather-app/
├── pom.xml                    # Configurazione Maven e dipendenze
├── src/
│   ├── main/
│   │   ├── java/com/weatherapp/
│   │   │   ├── Main.java              # Entry point applicazione
│   │   │   ├── controller/
│   │   │   │   └── WeatherController.java  # Gestione I/O utente
│   │   │   ├── service/
│   │   │   │   └── WeatherService.java   # Logica business e API calls
│   │   │   └── model/               # Data models con Jackson
│   │   │       ├── WeatherData.java
│   │   │       ├── OpenMeteoResponse.java
│   │   │       └── GeocodingResponse.java
│   │   └── resources/              # Properties files
│   └── test/                     # Test unitari
└── target/                        # Build artifacts
```

## 🔧 **Tecnologie Utilizzate**

### **Core Framework**
- **Java 17**: Linguaggio principale con features moderne
- **Maven 3.6+**: Build automation e dependency management
- **Jackson 2.15.2**: JSON parsing e serialization

### **HTTP Client**
- **Java 11 HttpClient**: API HTTP moderna e non-blocking
- **Timeout management**: 10 secondi per tutte le richieste

### **API Esterne**
- **Open-Meteo**: Dati meteo gratuiti senza API key
  ```
  GET https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true&timezone=auto
  ```
- **Nominatim (OpenStreetMap)**: Geocoding gratuito
  ```
  GET https://nominatim.openstreetmap.org/search?format=json&q={city}&limit=1
  ```

## 💻 **Come Eseguire il Progetto**

### **Prerequisiti**
```bash
# Verifica Java 17+
java -version

# Verifica Maven 3.6+
mvn --version
```

### **Build ed Esecuzione**
```bash
# Compilazione completa
mvn clean package -DskipTests

# Esecuzione diretta (consigliata)
java -jar target/weather-app-1.0.0.jar

# Esecuzione con Maven (per sviluppo)
mvn exec:java -Dexec.mainClass="com.weatherapp.Main"
```

## 🔍 **Analisi del Codice**

### **1. WeatherController.java - Gestione I/O**
```java
// Pattern: Simple console interaction
// Responsabilità: Input utente, loop principale, gestione errori UI

public void start() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
        System.out.print("Inserisci il nome di una città: ");
        String cityName = scanner.nextLine().trim();
        
        if (cityName.equalsIgnoreCase("esci")) break;
        
        try {
            WeatherData weather = weatherService.getWeatherData(cityName);
            System.out.println(weather); // Usa toString() di WeatherData
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
```

### **2. WeatherService.java - Logica Business**
```java
// Pattern: Service layer con dependency injection
// Responsabilità: API calls, error handling, data transformation

public WeatherData getWeatherData(String cityName) throws IOException {
    // 1. Geocoding: city name → coordinates
    double[] coordinates = getCoordinatesForCity(cityName);
    
    // 2. Weather API: coordinates → weather data
    String apiUrl = String.format(Locale.US, 
        "%s?latitude=%.6f&longitude=%.6f&current_weather=true&timezone=auto", 
        OPEN_METEO_API, coordinates[0], coordinates[1]);
    
    // 3. HTTP request con timeout e headers
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl))
        .timeout(Duration.ofSeconds(10))
        .header("Accept", "application/json")
        .GET()
        .build();
    
    // 4. JSON parsing con Jackson
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    OpenMeteoResponse apiResponse = objectMapper.readValue(response.body(), OpenMeteoResponse.class);
    
    // 5. Data transformation
    return new WeatherData(
        apiResponse.getCurrentWeather().getTemperature(),
        cityName,
        "Dati meteo attuali"
    );
}
```

### **3. Model Layer - Jackson JSON Mapping**
```java
// Pattern: POJO con annotazioni Jackson per deserialization automatica

@JsonProperty("current_weather")
private CurrentWeather currentWeather;

// La risposta JSON completa viene mappata automaticamente:
// {
//   "latitude": 41.875,
//   "longitude": 12.5,
//   "current_weather": {
//     "temperature": 18.9,
//     "windspeed": 12.1,
//     "winddirection": 10,
//     "is_day": 1,
//     "weathercode": 3
//   }
// }
```

## 🚀 **Esempi Pratici**

### **Esempio 1: Aggiungere una nuova città preferita**
```java
// In WeatherController.java
private List<String> favoriteCities = Arrays.asList("Roma", "Milano", "New York");

public void showFavorites() {
    System.out.println("Città preferite:");
    favoriteCities.forEach(city -> System.out.println("- " + city));
}

public void searchFavoriteCity(int index) {
    if (index < favoriteCities.size()) {
        String city = favoriteCities.get(index);
        WeatherData weather = weatherService.getWeatherData(city);
        System.out.println(weather);
    }
}
```

### **Esempio 2: Sistema di Cache con TTL**
```java
// CacheService con scadenza automatica per ottimizzare chiamate API
CacheService<String> weatherCache = new CacheService<>(30); // 30 minuti TTL

// Recupera dati da cache o API se non presenti/ scaduti
WeatherData weather = weatherCache.getOrFetch(
    "weather_roma", 
    () -> weatherApi.getWeather("Roma")
);

// Metodi principali:
// put(key, data, ttlMinutes) - Memorizza con TTL personalizzato
// get(key) - Recupera (null se scaduto)
// cleanupExpired() - Rimuove voci scadute
// validSize() - Numero voci non scadute
```

### **Esempio 3: Estendere con più dati meteo**
```java
// In OpenMeteoResponse.java
@JsonProperty("hourly")
private HourlyData hourly;

// Aggiungi a WeatherData.java
private double humidity;
private double pressure;
private String weatherDescription;

// Mappa il weather code (es: 3 = "Partly cloudy")
public String getWeatherDescription(int code) {
    return switch (code) {
        case 0 -> "Clear sky";
        case 3 -> "Partly cloudy";
        case 45 -> "Fog";
        default -> "Unknown";
    };
}
```

## 🧪 **Testing e Debug**

### **Test Unitari**
```bash
# Esegui tutti i test
mvn test

# Test specifici
mvn test -Dtest=WeatherServiceTest
mvn test -Dtest=CacheServiceTest

# Demo CacheService
java -cp target/classes com.weatherapp.service.ApiCacheExample

# Salta i test (build veloce)
mvn clean package -DskipTests
```

### **Debug Tips**
```java
// Logging per debug (rimuovi in produzione)
System.err.println("DEBUG: API URL: " + apiUrl);
System.err.println("DEBUG: Response: " + response.body());

// Test con coordinate specifiche
WeatherData weather = service.getWeatherData("Roma");
assert weather.getTemperature() > -50 && weather.getTemperature() < 60;
```

## ⚙️ **Configurazione e Customizzazione**

### **Modificare API Endpoints**
```java
// In WeatherService.java
private static final String OPEN_METEO_API = "https://api.your-weather-provider.com/v1/forecast";
private static final String GEOCODING_API = "https://your-geocoding-provider.com/search";
```

### **Aggiungere API Key**
```java
// In application.properties (src/main/resources)
weather.api.key=your_api_key_here
geocoding.api.key=your_geocoding_key

// In WeatherService.java
private String loadApiKey() {
    try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
        Properties props = new Properties();
        props.load(input);
        return props.getProperty("weather.api.key");
    }
}
```

## 🔄 **Possibili Estensioni**

### **1. Interfaccia Web**
- Spring Boot per REST API
- React/Vue frontend per UI moderna
- WebSocket per aggiornamenti real-time

### **2. Database Integration**
- H2/PostgreSQL per storico dati
- CacheService integrato per performance ottimizzate
- Scheduled tasks per aggiornamenti automatici

### **3. Features Avanzate**
- Previsioni 7 giorni
- Alert meteo per condizioni estreme
- Geolocalizzazione automatica
- Supporto multilingua

## 🐛 **Troubleshooting Comune**

### **Problema: Coordinate errate**
```
Errore: Latitude must be in range of -90 to 90
Causa: Locale formatting con virgola invece di punto
Soluzione: Usare Locale.US in String.format()
```

### **Problema: JSON parsing error**
```
Errore: Unrecognized field "xyz"
Causa: Model non corrisponde alla risposta API
Soluzione: Aggiornare modello con @JsonProperty corretti
```

### **Problema: HTTP timeout**
```
Errore: java.net.SocketTimeoutException
Causa: Network lenta o API non raggiungibile
Soluzione: Aumentare timeout o aggiungere retry logic
```

## 📝 **Best Practices per Sviluppatori**

1. **Error Handling**: Sempre wrappare API calls in try-catch
2. **Resource Management**: Usare try-with-resources per HTTP client
3. **Immutability**: Considerare record Java 16+ per i model
4. **Configuration**: Externalizzare API endpoints e keys
5. **Testing**: Mock HTTP client per test unitari isolati
6. **Logging**: Usare SLF4J invece di System.out.println
7. **Caching**: Usa CacheService con TTL appropriati per ottimizzare chiamate API

## 🚀 **Quick Start per Nuovi Sviluppatori**

```bash
# 1. Clone e setup
git clone <repository>
cd weather-app
mvn clean package

# 2. Prima esecuzione
java -jar target/weather-app-1.0.0.jar

# 3. Sviluppo iterativo
# Modifica codice → mvn compile → test → package → run
```

---

**Questo progetto è un'ottima base per imparare**: Java moderno, Maven, HTTP client, JSON parsing, API integration, architettura a strati, e caching con TTL per performance ottimizzate. 🎯

**CacheService Features**: Sistema di cache generico con scadenza automatica, thread-safe, e supplier pattern per ottimizzare chiamate API. Vedi `ApiCacheExample` per demo pratica.
