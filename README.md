# Dinso

Dinso är ett fristående, integrationsfritt demosystem för pension och försäkring. Alla personer, företag och belopp är fiktiva och ändringar är lokala för demon.

Systemet är en gemensam kodbas som byggs som tre kundvarianter: `svenskebanken`, `pensionsbolaget` och `finbanken`. Vid bygge eller start väljs en kund, vilket skapar en egen frontend och ett eget API med kundspecifik konfiguration och demodata.

Frontend är en gemensam Vue-applikation med kundoverlays i `client/customers/`. En overlay anger bland annat kundens konfiguration, tema och översättningar. Kundflaggan väljer rätt overlay vid bygge eller start, så att varje kund får en separat frontendvariant från samma applikation.

Backend består av gemensamma Gradle-moduler och en Spring Boot-app per kund under `server/customers/`. Bygget väljer kundappen, som laddar kundens seedade demoprofiler och kör på en egen API-port. Frontend kopplas alltid till API:t för samma kund.

## Systemkrav

För lokal utveckling krävs:

- Node.js 25 med npm för frontend.
- Java 25 för backend. Gradle Wrapper (`server/gradlew`) ingår i projektet och behöver inte installeras separat.

För att köra hela systemet i containrar krävs Docker med Docker Compose. Då behövs varken Node.js eller Java installerat lokalt.

Systemet kan köras på macOS, Linux och Windows. Docker Compose är det enklaste sättet att köra hela systemet på samtliga plattformar. På Windows fungerar frontend och Docker normalt, men `server/start-customer.sh` kräver en POSIX-shell, till exempel WSL eller Git Bash. Kör annars backend med `server/gradlew.bat` eller direkt med `java -jar` i PowerShell eller Kommandotolken.

## CocoIndex Code (ccc)

`ccc` är ett verktyg för kodagenter som ger semantisk sökning i kodbasen. Det hjälper agenten att hitta funktionalitet utifrån vad den gör, utan att den behöver känna till exakta filnamn eller symboler.

Be agenten att installera och sätta upp verktyget, till exempel: _"Installera, initiera och indexera ccc för det här projektet."_ Agenten installerar då `cocoindex-code`, kör `ccc init` och bygger indexet med `ccc index`. Därefter kan agenten använda `ccc search` för att hitta relevant kod och uppdatera indexet efter större ändringar.

## Köra systemet

Du kan köra systemet lokalt med Node.js och Java, eller starta hela kundvarianten med Docker Compose. Frontend och backend använder följande kundnamn och portar:

| Kund | Frontend | Backend/API |
|---|---:|---:|
| `svenskebanken` | `http://localhost:5173` | `http://localhost:8081` |
| `pensionsbolaget` | `http://localhost:5174` | `http://localhost:8082` |
| `finbanken` | `http://localhost:5175` | `http://localhost:8083` |

### Frontend lokalt

Installera beroenden en gång och starta sedan önskad kund. Dev-skriptet väljer automatiskt korrekt Vite-port och API-URL för kunden.

```sh
cd client
npm ci
npm run dev -- --svenskebanken
```

Byt kundflagga för att starta en annan variant:

```sh
npm run dev -- --pensionsbolaget
npm run dev -- --finbanken
```

Om ingen kundflagga anges startas `svenskebanken`. Frontend förutsätter att motsvarande backend kör på API-porten i tabellen ovan.

Bygg alla kundvarianter med:

```sh
npm run build:all
```

Det validerar kundoverlays och skapar byggena under `client/dist/`.

### Backend lokalt

Backend kräver Java 25. Starta enklast en kund med `start-customer.sh`; skriptet bygger kundens körbara JAR och startar den på rätt API-port:

```sh
cd server
./start-customer.sh --svenskebanken
```

Skriptet har motsvarande flaggor för övriga kunder:

```sh
./start-customer.sh --pensionsbolaget
./start-customer.sh --finbanken
```

Du kan även starta kundappen direkt med Gradle. Ange port för att den ska matcha frontend-konfigurationen:

```sh
cd server
./gradlew :customers:svenskebanken:bootRun --args='--server.port=8081'
./gradlew :customers:pensionsbolaget:bootRun --args='--server.port=8082'
./gradlew :customers:finbanken:bootRun --args='--server.port=8083'
```

Alternativt bygger och kör du JAR-filen själv:

```sh
cd server
./gradlew :customers:svenskebanken:bootJar
java -jar customers/svenskebanken/build/libs/svenskebanken-0.1.0-SNAPSHOT.jar --server.port=8081
```

Byt kundnamn och port i de två sista kommandona för `pensionsbolaget` eller `finbanken`. Använd den körbara JAR-filen, inte en fil som slutar på `-plain.jar`.

### Hela systemet med Docker

Docker Compose bygger och startar både frontend och backend för en kund:

```sh
docker compose --profile svenskebanken up --build
```

Öppna därefter `http://localhost:5173`. Starta PensionsBolaget eller FinBanken genom att ersätta profilnamnet med `pensionsbolaget` respektive `finbanken`.

Starta alla kundvarianter samtidigt med:

```sh
docker compose --profile svenskebanken --profile pensionsbolaget --profile finbanken up --build
```

Frontend exponeras då på port `5173`, `5174` och `5175`, medan API:erna exponeras på `8081`, `8082` och `8083`. Stoppa tjänsterna med `Ctrl+C`; använd `docker compose down` för att ta bort containrarna.

## Verifiering

```sh
cd server
./gradlew :customers:svenskebanken:test :customers:pensionsbolaget:test :customers:finbanken:test

cd ../client
npm run typecheck
npm run build:all
```

## Kundmatris

| Kund | Portaler | Språk | Särskilt scenario |
|---|---|---|---|
| SvenskeBanken | Privat, Företag | sv, en | Bred portfölj och alla företagsroller |
| PensionsBolaget | Privat, Företag | sv, en | Tjänstepension och utbetalning |
| FinBanken | Privat | en | Fond- och riskskyddsfokus; inget företags-API |

## Backend

`server/` har modulära Gradle-moduler för service, databas, API, gemensam Spring-konfiguration och tre kundappar. H2 seedar kundens demoprofiler vid start. `POST /api/auth/login` tar ett profil-id och utfärdar en kortlivad, signerad JWT som kopplas till en persisterad demosession; privata och företagsöversikter kräver `Authorization: Bearer <token>` och företagsmutationer kräver administratörsrollen.

Starta en kund lokalt med exempelvis:

```sh
cd server
./gradlew :customers:svenskebanken:bootRun
```

Compose bygger en separat Spring Boot-app och SPA per kund, med Nginx som frontend-fallback och proxy för `/api`.
