# Systemdokumentation för Dinso

## 1. Översikt

Dinso är ett fristående demosystem för pension och försäkring. Systemet demonstrerar privata pensions- och försäkringsflöden samt företagsadministration med deterministiska, fiktiva data.

Dinso är byggt för demonstration och lokal utveckling:

- inga externa integrationer används;
- inga riktiga personer, företag, försäkringar eller belopp används;
- ändringar är lokala för den aktuella demon;
- data kan återställas genom att starta om den berörda API-applikationen;
- inloggning sker genom val av en fördefinierad demo-profil, inte med lösenord.

Systemets viktigaste egenskaper är kundisolering, rollstyrning, tydlig portal- och kontextmarkering samt reversibla demoåtgärder.

## 2. Funktionellt innehåll

Dinso har två portaler:

### Privatportal

Den privata portalen visar en persons:

- översikt;
- försäkringar och fondfördelning;
- händelser och transaktioner;
- dokument;
- utbetalningar.

En privat profil kan uppdatera fondfördelningen när kundens regler tillåter det. Åtgärden är en lokal demoändring och innebär ingen signering eller verklig orderläggning.

### Företagsportal

Företagsportalen visar:

- företagsöversikt;
- pensionsplaner och avtal;
- medarbetare och anställningar;
- ärenden;
- företagsdokument.

En företagsadministratör kan, beroende på kundens regler:

- lägga till en demoanställd;
- ändra lön;
- registrera tjänstledighet;
- avsluta en anställning.

En företagsbetraktare har läsbehörighet men saknar skrivåtgärder.

## 3. Teknisk arkitektur

Dinso består av en Vue-klient och tre separata Spring Boot-applikationer. Varje kundvariant byggs och körs som en egen webbapplikation och en egen API-applikation.

```text
Webbläsare
    |
    v
Vue 3 + TypeScript + Vite
    |
    | /api via Nginx
    v
Kundspecifik Spring Boot-applikation
    |
    +-- API-modul
    +-- tjänstelager
    +-- databaslager
    +-- gemensam Spring-konfiguration
    +-- H2-databas
```

### Klient

Klienten använder:

- Vue 3 för komponent- och sidstruktur;
- TypeScript för typer;
- Vite för utveckling och kundspecifika byggen;
- Pinia för klientens tillstånd;
- Vue Router för portal- och sidrutter;
- kundoverlays för tema, språk och presentation.

Kundoverlays finns under `client/customers/<kund>/`. De kan innehålla konfiguration, lokalisering, SCSS-variabler och kundspecifika presentationsregler. Overlayn styr inte serverns behörighet.

### Server

Servern är uppdelad i Gradle-moduler:

- `service` innehåller domänmodeller, kundidentifierare, kundregler, roller och åtgärdstyper;
- `database` innehåller JPA-entiteter, repositories, sessioner och seedning;
- `api` innehåller REST-kontrollers och autentiseringsflödet;
- `springconfig` innehåller gemensam Spring-konfiguration;
- `customers/<kund>` innehåller den separata Spring Boot-startpunkten för respektive kund.

De tre kundapplikationerna återanvänder gemensamma domän- och databasmoduler men får varsin kundidentifierare som Spring-bean. Kundidentifieraren används för att välja rätt regler och rätt seeddata.

## 4. Kundvarianter

| Kundvariant | Portaler | Språk | Huvudscenario |
|---|---|---|---|
| SvenskeBanken | Privat och företag | Svenska och engelska | Bred portfölj, försäkringar, avgifter och företagsåtgärder |
| PensionsBolaget | Privat och företag | Svenska och engelska | Tjänstepension, utbetalning och begränsade företagsåtgärder |
| FinBanken | Privat | Engelska | Fond- och riskskyddsfokus |

Kundreglerna definieras i respektive `application.properties` och läses av serverns `CustomerRules`. De omfattar bland annat:

- tillgängliga portaler;
- tillgängliga språk och standardspråk;
- maximalt antal fonder i en fördelning;
- maximalt antal månader för tjänstledighet;
- tillåtna orsaker för tjänstledighet;
- visning av avgifter, transaktioner och traditionell bonusränta;
- tillåtna företagsåtgärder.

Klienten får dessa regler via `GET /api/demo/config` och ska endast exponera funktioner som är tillgängliga för aktuell kund och aktuell profil. Servern validerar samma regler oberoende av klientens presentation.

## 5. Demo-profiler och roller

Profilerna ligger i den kundspecifika seedkatalogen. Profilvalet visar namn, portal, roll och demonstrationsscenario.

Privatprofilerna är:

- `*-portfolio` – bred pensions- och försäkringsportfölj;
- `*-payment` – försäkringar och pensionsutbetalningar.

Företagsprofilerna finns för kundvarianter med företagsportal:

- `*-admin` – företagsadministratör för ett företag;
- `*-multi` – administratör som kan välja mellan flera företag;
- `*-viewer` – företagsbetraktare med läsbehörighet.

Profilernas externa id:n är kundbundna. Samma profil-id hos en annan kund ger inte åtkomst till den aktuella kundens data.

## 6. Autentisering och auktorisering

Autentiseringsflödet är avsett för demo och bygger på profil-id:

1. Klienten hämtar tillgängliga profiler från `GET /api/demo/profiles`.
2. Besökaren väljer en profil.
3. Klienten skickar profil-id till `POST /api/auth/login`.
4. Servern verifierar att profilen finns hos aktuell kund.
5. Servern skapar en kortlivad, signerad JWT.
6. Servern sparar en aktiv demosession i databasen.
7. Klienten skickar JWT:n som `Authorization: Bearer <token>`.

En session är giltig endast om:

- tokenens signatur och giltighetstid kan verifieras;
- sessionen fortfarande är aktiv;
- sessionen hör till en giltig profil;
- profilens kund, portal och roll matchar den begärda resursen.

Utloggning via `POST /api/auth/logout` invaliderar sessionen. En tidigare token kan då inte längre användas trots att tokenens ursprungliga giltighetstid inte har löpt ut.

Åtkomstkontrollerna sker i API:t:

- privata rutter kräver en aktiv privat profil;
- företagsrutter kräver en aktiv företagsprofil;
- företagsmutationer kräver rollen `COMPANY_ADMIN`;
- företagsdata begränsas till företag som profilen har en auktorisation för;
- privata data begränsas till den inloggade profilen;
- kundregler valideras på serversidan vid skrivåtgärder.

## 7. REST-API

Alla API-rutter ligger under prefixet `/api`.

### Demo och hälsa

| Metod | Rutt | Funktion |
|---|---|---|
| `GET` | `/api/demo/profiles` | Hämtar profiler för aktuell kund |
| `GET` | `/api/demo/config` | Hämtar kundens aktiva regler |
| `GET` | `/api/demo/health` | Kontrollerar att kundens API är redo |

### Autentisering

| Metod | Rutt | Funktion |
|---|---|---|
| `POST` | `/api/auth/login` | Skapar demosession från profil-id |
| `POST` | `/api/auth/logout` | Invaliderar aktuell demosession |

### Privatportal

| Metod | Rutt | Funktion |
|---|---|---|
| `GET` | `/api/private/overview` | Hämtar privat översikt |
| `GET` | `/api/private/insurances/{insuranceId}` | Hämtar försäkringsdetaljer |
| `GET` | `/api/private/transactions` | Hämtar händelser och transaktioner |
| `GET` | `/api/private/documents` | Hämtar dokument |
| `GET` | `/api/private/payments` | Hämtar utbetalningar |
| `PUT` | `/api/private/insurances/{insuranceId}/fund-allocation` | Uppdaterar fondfördelning enligt kundregler |

### Företagsportal

Företagsrutterna exponeras endast i kundapplikationer som har företagsprofilen aktiverad.

| Metod | Rutt | Funktion |
|---|---|---|
| `GET` | `/api/company/companies` | Hämtar företag som profilen får se |
| `GET` | `/api/company/overview` | Hämtar företagsöversikt |
| `GET` | `/api/company/plans` | Hämtar pensionsplaner |
| `GET` | `/api/company/employments` | Hämtar och söker medarbetare |
| `GET` | `/api/company/employments/{employmentId}` | Hämtar en anställning |
| `POST` | `/api/company/employees` | Lägger till demoanställd |
| `PUT` | `/api/company/employments/{employmentId}/salary` | Ändrar lön |
| `PUT` | `/api/company/employments/{employmentId}/leave` | Registrerar tjänstledighet |
| `PUT` | `/api/company/employments/{employmentId}/end` | Avslutar anställning |

Skrivande företagsrutter kräver både rätt portal och administratörsroll. Alla skrivåtgärder kontrollerar dessutom att vald plan, anställning och företag hör ihop och att profilen har behörighet till företaget.

## 8. Datamodell och seedning

H2 används som lokal, temporär databas. Schemat skapas vid applikationsstart med `create-drop`, vilket innebär att databasen inte är avsedd för beständig drift.

Centrala entiteter är:

- personer;
- demo-profiler;
- företag;
- företagsauktorisationer;
- pensionsplaner;
- anställningar;
- försäkringar;
- fondinnehav;
- transaktioner;
- dokument;
- utbetalningar;
- demosessioner;
- demo-händelser.

Vid start:

1. rätt kundidentifierare injiceras av kundapplikationen;
2. kundens demo-profiler skapas;
3. kundens privata data, företag, planer och anställningar seedas;
4. seedlogiken kopplar profiler till rätt personer och företag;
5. servern börjar acceptera API-anrop.

Seedningen är deterministisk och begränsad till den aktuella kunden. Data från en kundvariant läses inte in i en annan kundvariants databas.

Skrivåtgärder sparas under den aktuella demosessionens kundkontext. Företagsåtgärder skapar även en post i demo-händelselogg med åtgärdstyp, tidpunkt och en kort sammanfattning.

## 9. Kundspecifika regler

### SvenskeBanken

- privat- och företagsportal;
- svenska och engelska;
- högst tio fonder i fondfördelningen;
- tjänstledighet på grund av föräldraledighet eller studier i högst 18 månader;
- avgifter och transaktioner visas;
- alla fyra företagsåtgärder är tillåtna.

### PensionsBolaget

- privat- och företagsportal;
- svenska och engelska;
- högst fem fonder i fondfördelningen;
- endast föräldraledighet i högst tolv månader;
- traditionell bonusränta visas;
- avgifter och transaktioner visas inte;
- nyanställning, löneändring och tjänstledighet är tillåtna;
- avslut av anställning är inte tillåtet.

### FinBanken

- endast privatportal;
- endast engelska;
- högst tio fonder i fondfördelningen;
- inga tjänstledighetsregler eller företagsåtgärder;
- transaktioner visas;
- avgifter och traditionell bonusränta visas inte;
- företags-API och företagsnavigation exponeras inte.

## 10. Klientrutter och navigering

Privata huvudrutter:

- `/privat`
- `/privat/forsakringar`
- `/privat/handelser`
- `/privat/dokument`
- `/privat/utbetalningar`

Företagsrutter:

- `/foretag`
- `/foretag/medarbetare`
- `/foretag/medarbetare/lagg-till`
- `/foretag/avtal`
- `/foretag/arenden`
- `/foretag/dokument`

Klienten använder route metadata för att ange portal, sida och eventuella rollkrav. Metadata används för navigering och visning, men ersätter inte API:ts auktorisering.

## 11. Drift och lokalkörning

### Docker Compose

Starta SvenskeBanken-varianten:

```sh
docker compose --profile svenskebanken up --build
```

Starta alla kundvarianter:

```sh
docker compose --profile svenskebanken --profile pensionsbolaget --profile finbanken up --build
```

Standardportar:

| Kundvariant | Webb | API |
|---|---:|---:|
| SvenskeBanken | `5173` | `8081` |
| PensionsBolaget | `5174` | `8082` |
| FinBanken | `5175` | `8083` |

Webbcontainern kör Nginx. Nginx serverar SPA-klienten med fallback för klientrutter och proxar `/api` till rätt Spring Boot-container.

### Lokal klientutveckling

```sh
cd client
npm install
VITE_USE_API=true VITE_API_URL=http://localhost:8080 npm run dev -- --mode svenskebanken
```

### Lokal serverutveckling

```sh
cd server
./gradlew :customers:svenskebanken:bootRun
```

Motsvarande kundmodul används för de andra två kundvarianterna.

## 12. Återställning

Knappen **Återställ demo** återställer endast klientens lokala vyval. Den återställer inte data som redan sparats i API:ets H2-databas.

För att återställa serverdata startas den aktuella kundens API om. Eftersom databasen är en H2-databas med `create-drop` skapas seeddata på nytt vid nästa applikationsstart.

## 13. Verifiering och kvalitetssäkring

Serverns integrationstester verifierar bland annat:

- att seeddata är kundisolerad;
- att JWT-sessioner skapas och kan invalideras;
- att portal- och rollskydd fungerar;
- att FinBanken inte exponerar företags-API;
- att fondgränser och tjänstledighetsregler följs;
- att fondfördelning valideras och sparas.

Kör serververifiering med:

```sh
cd server
./gradlew :customers:svenskebanken:test :customers:pensionsbolaget:test :customers:finbanken:test
```

Kör klientverifiering med:

```sh
cd client
npm run typecheck
npm run build:all
```

Manuell kontroll bör omfatta tangentbordsnavigation, synlig fokusmarkering, formuläretiketter, statusmeddelanden, responsivitet vid 320 CSS-pixlar samt att kundspecifika portaler, språk och skrivknappar visas korrekt.

## 14. Begränsningar och säkerhetsförutsättningar

Dinso ska betraktas som ett demonstrationssystem och inte som ett produktionssystem. Följande begränsningar gäller:

- JWT-hemligheter och H2-konfiguration är lokala demo-inställningar;
- inga riktiga identiteter eller ekonomiska värden får läggas in;
- sessioner och data är inte avsedda för långvarig lagring;
- externa integrationer, signeringar och verkliga transaktioner saknas;
- kundisolering demonstreras genom separata applikationer och databaser;
- klientens dolda knappar är inte en säkerhetsgräns; all behörighet måste därför fortsatt kontrolleras i API:t.

## 15. Viktiga källfiler

- `README.md` – start, bygg, verifiering och kundöversikt;
- `PRODUCT.md` – produktens syfte och principer;
- `compose.yml` – containerprofiler, portar och kundkopplingar;
- `client/src/router.ts` – klientrutter och portalmetadata;
- `client/src/i18n.ts` – lokalisering och enum-översättningar;
- `server/api` – REST-API och autentisering;
- `server/database` – entiteter, repositories, seedning och datatjänster;
- `server/service` – gemensamma domänbegrepp och kundregler;
- `server/customers` – separata kundapplikationer och deras konfiguration.
