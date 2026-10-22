# Fastify Spotify Link Player Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a local Fastify app that plays Spotify playlist or album embeds from a URL pasted by the user.

**Architecture:** Fastify serves a static vanilla JavaScript page from `fastify-app/public/`. The browser accepts only standard Spotify playlist and album URLs, converts them to canonical Spotify Embed URLs, and delegates playback to Spotify's iframe.

**Tech Stack:** Node.js 24, npm 11, Fastify 5.12.7, `@fastify/static` 10.1.7, HTML, CSS, browser JavaScript, Spotify Embeds.

**Spec:** `docs/superpowers/specs/2026-10-21-fastify-spotify-player-design.md`

## Global Constraints

- Keep every project file under `fastify-app/`.
- Bind the server to `127.0.0.1`; default to port `3000`, with `PORT` as an override.
- Accept only HTTPS links hosted on `open.spotify.com` for playlist and album resources.
- Construct embed URLs under `https://open.spotify.com/embed/`; never insert the submitted URL as HTML.
- Do not use OAuth, Spotify credentials, tokens, persistent storage, or a database.
- Do not add a test suite in this scope; the approved spec calls for dependency and syntax checks.
- Preserve all existing projects and working-tree changes outside `fastify-app/`.

## Review Focus

The approved spec excludes an automated test suite. During implementation review, check these input classes against the validator and error-state behavior without adding test files:

1. Empty or malformed input must show an accessible error and leave the current player unchanged.
2. A lookalike host such as `open.spotify.com.attacker.example` must be rejected.
3. Unsupported resource types such as tracks or artists must be rejected.
4. Extra path segments, dot-segment normalization, backslashes, or encoded separators must be rejected.
5. Query strings and fragments on a valid playlist or album link must not affect the canonical embed URL.

---

### Task 1: Create the isolated Fastify project

**Files:**
- Create: `fastify-app/package.json`
- Create: `fastify-app/.gitignore`
- Create: `fastify-app/server.js`
- Create: `fastify-app/public/index.html` (starter page, completed in Task 2)
- Generate: `fastify-app/package-lock.json`

**Interfaces:**
- Produces: `npm start` launches the Fastify server on `127.0.0.1:3000` by default.
- Produces: `@fastify/static` serves files from the `public/` directory at the root URL.

- [x] **Step 1: Define the npm package** in `fastify-app/package.json` with name `fastify-spotify-player`, version `1.0.0`, `private: true`, `type: "module"`, script `"start": "node server.js"`, and dependencies `fastify: ^5.12.7` and `@fastify/static: ^10.1.7`.
- [x] **Step 2: Ignore installed dependencies** by adding `node_modules/` to `fastify-app/.gitignore`.
- [x] **Step 3: Add the Fastify entry point** in `fastify-app/server.js`; register `@fastify/static` with the absolute `public/` path relative to `import.meta.url`, then listen on host `127.0.0.1` using `Number(process.env.PORT || 3000)`.
- [x] **Step 4: Add a starter `public/index.html`** with a page title and a message that the player UI is being set up; Task 2 replaces the message with the final form.
- [x] **Step 5: Install dependencies** from `fastify-app/` with `npm install` to create the lockfile and local `node_modules/`.
- [x] **Step 6: Check dependency resolution and server syntax** with `npm ls --depth=0` and `node --check server.js`; both commands should exit successfully.

### Task 2: Add Spotify link validation and the embed player

**Files:**
- Modify: `fastify-app/public/index.html`
- Create: `fastify-app/public/app.js`
- Create: `fastify-app/public/styles.css`

**Interfaces:**
- Consumes: Static page served from Task 1.
- Produces: `buildSpotifyEmbedUrl(input)` returns a canonical embed URL string for a valid playlist/album URL, or `null` for invalid input.

- [x] **Step 1: Build the accessible page structure** in `index.html`: a labeled text input with `inputmode="url"`, submit button, `aria-live="polite"` status element, and empty player container; load `styles.css` and `app.js` as local assets. Use text input so malformed links reach the app's accessible error handler.
- [x] **Step 2: Implement `buildSpotifyEmbedUrl(input)`** in `app.js`; first match the original trimmed string against a strict HTTPS URL shape for `/playlist/{id}` or `/album/{id}` so URL normalization cannot hide extra path segments, then verify protocol, hostname, and exact parsed pathname with `new URL`; return `https://open.spotify.com/embed/{type}/{id}` and ignore query strings/fragments.
- [x] **Step 3: Implement form submission** so invalid input updates the accessible status and preserves the current iframe; valid input clears the status and creates/replaces an iframe using DOM properties, with a descriptive title, width `100%`, height `352`, lazy loading, and Spotify's documented media permissions.
- [x] **Step 4: Add responsive styles** in `styles.css` for the form, status message, and player container; keep the layout usable on narrow screens.
- [x] **Step 5: Check browser JavaScript syntax** with `node --check public/app.js` from `fastify-app/`.
- [x] **Step 6: Review the five input classes listed above** against the URL validator and submit handler; do not add automated test files, in line with the approved spec.

### Task 3: Document setup and limitations

**Files:**
- Create: `fastify-app/README.md`

**Interfaces:**
- Consumes: The package scripts and user flow from Tasks 1–2.
- Produces: Project-specific instructions for installation, startup, valid links, and Spotify playback limitations.

- [x] **Step 1: Document setup** with `npm install`, `npm start`, the local URL `http://127.0.0.1:3000`, and the optional `PORT` environment variable.
- [x] **Step 2: Document supported links and limitations**: playlist and album links from `open.spotify.com`; playback, sign-in prompts, and regional availability are controlled by Spotify; no account connection or private library access is provided.
- [x] **Step 3: Review the final project status** with `git status --short` and confirm all new app files are under `fastify-app/` and pre-existing changes remain untouched.
