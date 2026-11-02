# SortFile
Przenosi pliki XML z katalogu `Home/` do `Dev/`. Pliki JAR trafiają do `Dev/` (parzysta lokalna godzina ostatniej modyfikacji) lub `Test/` (nieparzysta).

## Wymagania
Java 17+ oraz Maven 3.9+.

## Budowanie i uruchomienie
```bash
mvn clean package
java -jar target/sort-file-1.1.0.jar
# Jednorazowy przebieg:
java -jar target/sort-file-1.1.0.jar --once
```

Uruchamiaj w katalogu zawierającym `Home/`. Program utworzy `Dev/` i `Test/`, jeśli nie istnieją. Rejestr zdarzeń: `Home/count.txt`.

**Bezpieczeństwo:** Przy istniejącym pliku docelowym nie nadpisuje i nie usuwa pliku źródłowego. Skanowanie odbywa się co sekundę.

## Testy
```bash
mvn clean package
bash scripts/smoke-test.sh
```
