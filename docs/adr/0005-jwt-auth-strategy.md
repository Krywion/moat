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
  (odporność na XSS). Własny `BearerTokenResolver` czyta token z ciasteczka
  `auth_token`.
- **Tylko access token** (24h), bez refresh — YAGNI.
- **CSRF: SameSite=Strict** + wyłączony Spring CSRF (aplikacja stateless).
  Przeglądarka nie wysyła ciasteczka w żądaniach cross-site. W dev frontend
  i backend są tym samym *site* (port nie wpływa na SameSite).
- **Logout po stronie serwera** — HttpOnly cookie nie da się usunąć z JS,
  więc `POST /auth/logout` kasuje ciasteczko.

Endpointy auth:

| Metoda | Ścieżka | Opis |
|--------|---------|------|
| `POST` | `/auth/register` | Rejestracja (publiczny) |
| `POST` | `/auth/login` | Logowanie, ustawia cookie (publiczny) |
| `POST` | `/auth/logout` | Kasuje cookie (chroniony) |
| `GET` | `/auth/me` | Bieżący użytkownik z JWT (chroniony) |

## Alternatywy rozważane

- **Keycloak** — odrzucony jako nadmiarowy dla skali projektu.
- **Token w localStorage** — odrzucony z powodu podatności na XSS.
- **Sesje serwerowe** — odrzucone; stan po stronie serwera niepotrzebny.

## Implementacja

- Konfiguracja bezpieczeństwa: `backend/src/main/java/com/moat/auth/SecurityConfig.java`
- Wystawianie i walidacja JWT: `JwtService.java`, `JwtConfig.java`
- Ciasteczko: `AuthCookieFactory.java`
- Odczyt tokenu z cookie: `CookieBearerTokenResolver.java`
- REST: `AuthController.java`, `AuthService.java`
- Encja użytkownika: `backend/src/main/java/com/moat/user/User.java` (rola `USER` / `ADMIN`)
- Frontend: `fetch` z `credentials: 'include'` w `frontend/src/api/client.ts`

## Konsekwencje

- **+** Prostota; brak zewnętrznego IdP; token niewidoczny dla JS.
- **+** Walidacja tokenu deklaratywna (Resource Server); rola z claima, bez
  zapytania do bazy na każdym żądaniu.
- **−** SameSite=Strict wymaga, by frontend i backend były tym samym site;
  przy wdrożeniu na różnych domenach trzeba przejść na inną strategię CSRF.
- **−** Brak refresh tokena — po wygaśnięciu konieczne ponowne logowanie.
