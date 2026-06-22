# ADR 0005 — Uwierzytelnianie: JWT w HttpOnly cookie (Resource Server)

- **Status:** zaakceptowany
- **Data:** 2026-06-22

## Kontekst

Aplikacja potrzebuje uwierzytelniania z rolą w bazie (ROADMAP sekcja 5).
Roadmapa wskazuje JWT zamiast Keycloaka oraz komunikację REST z frontendem Vue.

## Decyzja

- **JWT zamiast Keycloaka** — brak zewnętrznego IdP; aplikacja sama wystawia
  i waliduje tokeny (HMAC-SHA256, biblioteka Nimbus via Spring Security
  OAuth2 Resource Server). Mniej infrastruktury dla projektu tej skali.
- **HttpOnly cookie** zamiast tokenu w body — token poza zasięgiem JS
  (odporność na XSS). Własny `BearerTokenResolver` czyta token z ciasteczka.
- **Tylko access token** (24h), bez refresh — YAGNI.
- **CSRF: SameSite=Strict** + wyłączony Spring CSRF (aplikacja stateless).
  Przeglądarka nie wysyła ciasteczka w żądaniach cross-site. W dev frontend
  i backend są tym samym *site* (port nie wpływa na SameSite).
- **Logout po stronie serwera** — HttpOnly cookie nie da się usunąć z JS,
  więc `POST /auth/logout` kasuje ciasteczko.

## Konsekwencje

- **+** Prostota; brak zewnętrznego IdP; token niewidoczny dla JS.
- **+** Walidacja tokenu deklaratywna (Resource Server); rola z claima, bez
  zapytania do bazy na każdym żądaniu.
- **−** SameSite=Strict wymaga, by frontend i backend były tym samym site;
  przy wdrożeniu na różnych domenach trzeba przejść na inną strategię CSRF.
- **−** Brak refresh tokena — po wygaśnięciu konieczne ponowne logowanie.
- Odrzucone: Keycloak (nadmiarowy), token w localStorage (podatność XSS),
  sesje serwerowe (stan po stronie serwera).
