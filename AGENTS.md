# AGENTS.md

Instruktioner för AI-kodagenter i Balder Projektplattformen.

## Projektöversikt

Dinso är ett fristående demosystem för pension och försäkring. Systemet har en gemensam kodbas som byggs som separata kundvarianter för `svenskebanken`, `pensionsbolaget` och `finbanken`.

`client/` innehåller en gemensam Vue-frontend. Kundoverlays i `client/customers/` tillför kundspecifik konfiguration, tema och översättningar vid bygge eller start.

`server/` innehåller gemensamma Gradle-moduler och en Spring Boot-app per kund i `server/customers/`. Den valda kundappen laddar kundspecifik seedad demodata och kör som ett separat API. Frontend och API ska alltid använda samma kundvariant.

## Agentbeteende

### Subagenter

När en subagent används ska huvudagenten uttryckligen avgränsa uppdraget enligt skill `subagent-context-research`. 

### Gör inte utan explicit begäran

**Kodanalys** — Använd inte bugbot, security review eller liknande analys/review på eget initiativ. Gör det endast när användaren ber om det.

**Verifiering i browser** — Använd inte browser tools för att verifiera implementering om användaren inte explicit ber om det.

**Systemdokumentation** — Skapa eller uppdatera inte filer gällande systemdokumentation eller skapa inte filer för sammanfattningar av beslut, krav eller arkitektur som en del av implementationen. Gör endast detta när användaren ber uttryckligen om dokumentation, eller skill `close` aktiveras.

### Flytta, byta namn och kopiera filer

Föredra att flytta eller byt namn på filer och mappar med shell-kommandon — **generera inte om hela filer** bara för att ändra sökväg.

### Avslutning efter uppgifter

Efter flerstegsuppgifter: skriv en **extremt kort** avslutning — max 2 meningar eller punktlista med 3–5 punkter. Ingen bakgrund, ingen motivation, inga fil-listor om de inte är nödvändiga.

**Bra:**

> Lagt till `/api/db/status` och `DbStatusCard`. Backend ansluter mot Supabase. Klart att testa via startsidan.

**Undvik:** långa sammanfattningar, arkitekturförklaringar, "varför"-stycken och upprepning av vad användaren redan bad om.