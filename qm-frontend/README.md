# UC20 — React Frontend for the Quantity Measurement System

This replaces the static HTML/CSS/JS frontend from UC19 with a React app.
The Spring Boot backend's logic, endpoints, DTOs, and security rules are
**unchanged** except for one file (see "Required Backend Change" below) —
that change was unavoidable and is explained in detail there.

---

## 1. Required Backend Change (1 file — please read why)

Your existing `OAuth2AuthenticationSuccessHandler` writes the JWT as raw
JSON directly onto the browser response after Google login completes.
That works when you test it by hand, but a React SPA can never receive
it: the Google login redirect is a full-page browser navigation that
never runs through your React code, so there is no JavaScript in the page
to read that JSON.

This is not a style choice — it is the standard, unavoidable pattern for
combining Spring OAuth2 with any SPA (React, Vue, Angular, all of them
hit this same wall). The fix keeps every line of token-generation logic
identical and only changes **where the response is sent**: instead of
writing JSON, the handler redirects the browser to a frontend route
(`/oauth2/redirect`) with the token and user claims as query parameters.
React reads them once, stores the JWT in `localStorage` exactly as
before, and redirects to `/dashboard`.

Replace the full contents of
`src/main/java/com/apps/quantitymeasurement/security/OAuth2AuthenticationSuccessHandler.java`
in your backend project with this:

```java
package com.apps.quantitymeasurement.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * UC20 CHANGE — and the only backend file touched for the React migration.
 *
 * Previously this handler wrote the JWT as raw JSON directly onto the
 * OAuth2 callback response. That works for a Postman/manual test, but it
 * is not reachable by a single-page app: the browser's full-page redirect
 * to Google, and back to this handler, never passes through React code,
 * so the SPA has no way to read a JSON body rendered on a bare page.
 *
 * The fix is the standard pattern for SPA + Spring OAuth2: redirect the
 * browser to a frontend route with the token (and the claims needed to
 * populate the UI) as query parameters. The frontend route
 * (/oauth2/redirect) reads them once and stores the JWT exactly as before.
 *
 * Nothing about how the token is generated, signed, or validated changed —
 * only where the response is sent.
 */
@Component
public class OAuth2AuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private static final Logger logger =
            Logger.getLogger(OAuth2AuthenticationSuccessHandler.class.getName());

    private final JwtService jwtService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email   = oAuth2User.getAttribute("email");
        String name    = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        String token = jwtService.generateToken(email, name, picture);

        logger.info("Issued JWT for authenticated user: " + email);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", token)
                .queryParam("email", email)
                .queryParam("name", name)
                .queryParam("picture", picture)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
```

Then add one line to `application.properties`:
```properties
app.frontend.url=http://localhost:3000
```

**No other backend file changes.** Every REST endpoint, DTO, service, and
security rule from UC19 is reused exactly as-is.

### Optional (not required): show real dates in History

`QuantityMeasurementEntity` already has `createdAt`, but
`QuantityMeasurementDTO` never exposes it, so the History page's Date
column currently shows "—". If you'd like real timestamps, add:
```java
public java.time.LocalDateTime createdAt; // in QuantityMeasurementDTO

// inside QuantityMeasurementDTO.from(...)
dto.createdAt = entity.getCreatedAt();
```
The frontend already reads `row.createdAt` and displays it if present —
this is a pure addition, nothing to break.

**Dashboard → Recent Activity already shows real dates/times without this
patch.** It no longer re-fetches from the History API; it keeps its own
small local log (`src/utils/activityLog.js`, `src/context/ActivityContext.jsx`),
written the instant a Compare/Convert/Add/Subtract/Divide succeeds in this
browser. That's what makes it update instantly and always show an accurate
date/time — it doesn't depend on the backend patch above. The full History
page still reads from the backend, so its Date column will keep showing
"—" until you apply the optional patch.

---

## 2. Folder structure (updated for the minimal-UI redesign)

```
quantity-measurement-frontend/
├── index.html                    ← pre-hydration theme script lives here
├── package.json
├── vite.config.js
├── .env
└── src/
    ├── main.jsx                  ← wraps app in ThemeProvider + ToastProvider
    ├── App.jsx                   ← renders <ToastContainer /> at root
    ├── index.css                 ← neutral palette, light/dark CSS variables
    ├── context/
    │   ├── AuthContext.jsx
    │   ├── ThemeContext.jsx      ← light/dark, persisted to localStorage
    │   ├── ToastContext.jsx      ← global toast queue
    │   └── ActivityContext.jsx   ← NEW: instant, timestamped Recent Activity
    ├── hooks/
    │   ├── useAuth.js
    │   ├── useTheme.js
    │   ├── useToast.js
    │   ├── useClipboard.js       ← powers the Copy Result button
    │   └── useActivity.js        ← NEW
    ├── services/
    │   ├── api.js
    │   ├── authService.js
    │   └── quantityService.js
    ├── utils/
    │   ├── units.js
    │   ├── validation.js          all client-side validation rules
    │   ├── datetime.js            ← NEW: shared date/time formatting
    │   └── activityLog.js         ← NEW: localStorage-backed activity log
    ├── components/
    │   ├── layout/                Navbar (profile dropdown + theme toggle), Footer
    │   ├── common/
    │   │   ├── ProtectedRoute.jsx
    │   │   ├── Loader.jsx
    │   │   ├── Skeleton.jsx
    │   │   ├── EmptyState.jsx
    │   │   └── ToastContainer.jsx
    │   ├── dashboard/
    │   │   ├── MeasurementCard.jsx  ← the dashboard's primary, centered focus
    │   │   └── RecentActivity.jsx   ← reads ActivityContext, shows date + time
    │   └── history/                HistoryTable (skeleton + empty state)
    └── pages/
        ├── Home.jsx / Home.css
        ├── Login.jsx / Login.css
        ├── OAuthRedirect.jsx
        ├── Dashboard.jsx / Dashboard.css   ← redesigned: no Welcome card
        ├── History.jsx / History.css
        ├── About.jsx / About.css
        └── NotFound.jsx / NotFound.css
```

---

## 3. Git — branching from UC19

```bash
# from inside your existing repo, on the uc19 branch
git checkout uc19
git pull origin uc19
git checkout -b uc20

# apply the one backend file change described above, then:
git add src/main/java/com/apps/quantitymeasurement/security/OAuth2AuthenticationSuccessHandler.java
git add src/main/resources/application.properties

# add the new frontend project as a sibling folder (or its own repo — your call)
git add quantity-measurement-frontend/

# remove the old static frontend UC19 shipped, now fully replaced by React
git rm src/main/resources/static/index.html
git rm src/main/resources/static/script.js
git rm src/main/resources/static/style.css
git rm src/main/resources/static/history.html
git rm src/main/resources/static/history.js

git commit -m "UC20: replace static HTML/CSS/JS frontend with React SPA

- Add full React (Vite) frontend: routing, Context API auth, Axios
  services with JWT interceptor, and a measurement module covering
  compare/convert/add/subtract/divide across all four unit types.
- Update OAuth2AuthenticationSuccessHandler to redirect to the SPA with
  the token as a query param instead of writing raw JSON, since a
  full-page OAuth redirect can't otherwise reach React code.
- Remove the now-unused static/ frontend files.
- No changes to REST controllers, services, DTOs, or JWT/security rules."

git push origin uc20
```

---

## 4. Running it

**Backend** (unchanged):
```bash
cd Quantity-Measurement-App
./mvnw spring-boot:run
# runs on http://localhost:8080
```

**Frontend**:
```bash
cd quantity-measurement-frontend
npm install
npm run dev
# runs on http://localhost:3000 (pinned in vite.config.js
# to match the CORS whitelist already in SecurityConfig)
```

Open `http://localhost:3000`, click **Get Started** → **Continue with
Google** → you'll land on `/dashboard` signed in.

---

## 5. What UC20 adds, and why

- **Full React rewrite** of the UC19 static frontend: functional
  components + hooks throughout, React Router for the six pages, Context
  API for auth state, no class components anywhere.
- **`services/api.js`** centralizes the Axios instance; a request
  interceptor attaches `Authorization: Bearer <token>` to every call, and
  a response interceptor clears the session and redirects to `/login` on
  a 401 — so an expired token never leaves the user stuck on a broken
  screen.
- **`MeasurementCard`** is the single component implementing all UC20
  operation rules: Temperature hides Add/Subtract/Divide; Compare hides
  the target/result unit; Convert hides the second quantity and shows a
  Target Unit picker; Add/Subtract show a live Result Unit dropdown that
  calls `/add-with-target-unit` or `/subtract-with-target-unit` on
  change; Divide returns a plain ratio with no unit.
- **History** fans out across the five `/history/operation/{op}`
  endpoints (there's no "get everything" endpoint on the backend) and
  merges the results client-side for search/filter/pagination.
- Integrates with UC19 **exactly as it was written** — the only backend
  touch is the OAuth redirect target, explained above.

---

## 6. The minimal-UI redesign (on top of everything above)

The functionality and backend integration above are unchanged. What changed
is the visual design and a handful of UX features:

- **Design direction**: neutral grays, a single accent color, no
  gradients, no heavy motion — GitHub/Linear/Notion-style restraint
  instead of a marketing-site look.
- **Light/dark theme** — toggle in the Navbar, persisted to
  `localStorage` under the key `qm_theme`, restored instantly on refresh
  via a small inline script in `index.html` (so there's no flash of the
  wrong theme while React boots). Every color in the app is a CSS
  variable switched by `[data-theme]`, so no component has its own
  theme-branching logic.
- **Toast notifications** (`ToastContext` / `useToast`) replace every
  inline error banner and `alert()`. API errors, copy failures, and
  successful copies all surface as a toast in the bottom-right corner,
  auto-dismissing after ~3 seconds or on click.
- **Copy Result** — every successful operation (Compare, Convert, Add,
  Subtract, Divide) gets a Copy Result button next to the result. It uses
  `navigator.clipboard.writeText`, flips to "✓ Copied" for 2 seconds, and
  fires a success toast. Implemented as a reusable `useClipboard` hook so
  it's not duplicated per-operation.
- **Full client-side validation** (`utils/validation.js`) — required
  fields, numeric parsing, and explicit NaN/Infinity rejection, all
  checked **before** any API call. The submit button is disabled until
  the form is valid, duplicate submissions are blocked while a request is
  in flight, and every invalid field gets an inline message plus a red
  border — never a browser `alert()`.
- **Skeleton loaders** — the History table and the new Dashboard "Recent
  Activity" card both show shimmer placeholders while loading instead of
  a blocking spinner, and respect `prefers-reduced-motion`.
- **Empty states** — a shared `EmptyState` component gives History and
  Recent Activity a proper "No history found" / "No activity yet" screen
  instead of a blank table.
- **Recent Activity Preview** — new Dashboard widget showing your last 5
  operations, with a "View all" link into History.
- **Accessibility** — every form control has a real `<label htmlFor>`,
  invalid fields carry `aria-invalid` + `aria-describedby`, toasts are in
  an `aria-live="polite"` region, and focus rings are visible everywhere
  (`:focus-visible`) rather than suppressed.

Nothing about routing, auth, Axios interceptors, or the measurement
operations themselves changed in this pass — only how they look and how
errors/validation are surfaced.
