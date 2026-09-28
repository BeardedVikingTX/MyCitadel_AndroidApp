# MyCitadel for Android

> **Your digital fortress. In your pocket.**

The native Android client for [MyCitadel](https://mycitadel.lol) — the privacy-first social platform built on zero-knowledge architecture. Every piece of personal data is encrypted before it leaves your device. No trackers. No ad networks. No compromise.

[![Platform: Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Language: Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![UI: Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](http://makeapullrequest.com)

---

## 📱 What Is This?

This is the **native Android application** for MyCitadel. It is a first-class client — not a WebView wrapper — built with Jetpack Compose and Material 3. It talks to the same zero-knowledge API that powers the web platform at [mycitadel.lol](https://mycitadel.lol).

**Nothing you enter is ever stored in plaintext on our servers.** Everything sensitive is encrypted client-side, then re-encrypted with a per-user key derived from a master key that never leaves the server's isolated key directory.

---

## ✨ Features

### Current

- 🏰 **Native Citadel UI** — the full MyCitadel visual identity, built in Compose
- 🎨 **Custom fonts** — self-hosted Cinzel, Orbitron, JetBrains Mono, Inter
- ⚡ **Glitched animated headers** — GPU-accelerated RGB-split effect
- 🌑 **Dark theme only** — designed for the platform, not as an afterthought
- 📖 **Marketing pages** — Home, About, Contact, Security & Privacy, Terms

### In Progress

- 🔐 **Registration & login** — against the same zero-knowledge API
- 🛡️ **Two-factor authentication** — TOTP with QR code enrollment
- 📡 **Feed** — connection-gated timeline of posts
- 👥 **Connections** — request, accept, block, sever
- 💬 **Comments & reactions** — with full notification support
- 🔔 **Push notifications** — Firebase Cloud Messaging
- ⭐ **Premium subscription** — Stripe Checkout in-app

### Planned

- 📸 **Profile editor** — avatar, banner, wallpaper, theme customization
- 🔒 **End-to-end encrypted messaging** (future)
- 🌐 **Bilingual support** (future)

---

## 🏗️ Architecture

### Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Navigation** | Navigation Compose |
| **Networking** | Retrofit + OkHttp + kotlinx.serialization |
| **Local storage** | DataStore (encrypted preferences) |
| **Push** | Firebase Cloud Messaging (planned) |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 35 (Android 15) |

### Project Structure
```agsl
app/src/main/java/lol/mycitadel/app/
├── MainActivity.kt Entry point
├── ui/
│ ├── components/ Reusable Compose components
│ │ ├── Panel.kt CitadelPanel — bordered card with glow
│ │ └── GlitchText.kt GPU-accelerated glitch title
│ ├── screens/ Feature screens
│ │ └── HomeScreen.kt Landing page
│ └── theme/ Design system
│ ├── Color.kt Full Citadel palette
│ ├── Theme.kt Material 3 theme wiring
│ └── Type.kt Font families + typography
└── res/
├── font/ Self-hosted TTF fonts
└── values/ strings.xml, colors.xml
```


### Design System

The Android app shares the exact color palette and typography with the web platform — same hex values, same font files, same visual language. The brand is identical across every surface.

| Token | Value | Usage |
|-------|-------|-------|
| `Void` | `#05070A` | Page background |
| `Abyss` | `#0A0E14` | Dark surface |
| `Slab` | `#10151D` | Raised cards |
| `Cyan` | `#00E5FF` | Primary accent |
| `Gold` | `#D4AF37` | Premium accent |
| `Rune` | `#A855F7` | Violet accent |
| `Blood` | `#EF4444` | Errors |

---

## 🚀 Building & Running

### Prerequisites

- **Android Studio** — Ladybug (2024.2) or newer
- **JDK 17** — bundled with Android Studio
- **Android SDK 35** — install via SDK Manager
- **A physical device or emulator** — min API 26

### Build

```bash
git clone https://github.com/BeardedVikingTX/MyCitadel_AndroidApp.git
cd MyCitadel_AndroidApp

# Open in Android Studio, then:
# 1. Wait for Gradle sync to complete
# 2. Connect a device (USB debugging enabled) OR start an emulator
# 3. Click the green Run ▶ button
```

### Build a Signed Release APK
```agsl
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`  

🔒 Security
-----------

This app talks to the same hardened API as the web platform, and inherits every one of its guarantees:

| Property | Implementation |
| --- | --- |
| **Transport** | TLS 1.2+ only, cleartext traffic disabled in `AndroidManifest.xml` |
| **Password hashing** | Argon2id (256 MiB memory, 4 iterations, 2 threads) --- server-side |
| **PII encryption** | Envelope encryption with per-user keys, server-side at rest |
| **Session cookie** | `Secure`, `HttpOnly`, `SameSite=Strict` |
| **CSRF** | Double-submit token on every unsafe request |
| **API auth** | Session cookie + CSRF header + client header |
| **No tracking** | No analytics SDKs, no advertising IDs, no fingerprinting |

The full threat model is documented at [mycitadel.lol/security](https://mycitadel.lol/security).

### For Security Researchers

If you find a vulnerability in this app, please email **security@mycitadel.lol** before disclosing publicly. We aim to acknowledge within 72 hours and remediate critical findings within 7 days.

See the full bug bounty program at [mycitadel.lol/security#bug-bounty](https://mycitadel.lol/security#bug-bounty).

* * * * *

🤝 Contributing
---------------

We welcome contributions! Please:

1.  Fork the repository

2.  Create a feature branch (`git checkout -b feature/amazing-thing`)

3.  Commit your changes (`git commit -m 'Add amazing thing'`)

4.  Push to the branch (`git push origin feature/amazing-thing`)

5.  Open a Pull Request

For significant changes, please open an issue first to discuss what you'd like to change.

### Code Style

-   **Kotlin** --- follow the [official Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html)

-   **Compose** --- prefer small, focused composables; hoist state; avoid side effects in composition

-   **Naming** --- clear and descriptive; no abbreviations

* * * * *

📜 License
----------

The source code in this repository is published under the **MIT License** --- see the [LICENSE](https://license/) file for details.

**Trademark Notice:** The names **"MyCitadel"**, **"mycitadel.lol"**, the runic mark **ᛗ**, and all related branding are trademarks of the operator of MyCitadel. They are **not** licensed under the MIT License and may not be used to promote forks, competing services, or any product that implies endorsement or affiliation. See [Terms of Service § Intellectual Property](https://mycitadel.lol/terms#ip) for details.

* * * * *

🔗 Links
--------
* **Website** | [https://mycitadel.lol](https://mycitadel.lol/) 
* **API Reference** | [https://api.mycitadel.lol](https://api.mycitadel.lol/) 
* **API Repository** | [API_MyCitadel](https://github.com/BeardedVikingTX/API_MyCitadel) 
* **Web Repository** | [MyCitadel](https://github.com/BeardedVikingTX/MyCitadel) 
* **Security** | <https://mycitadel.lol/security> 
* **HackerOne** | (coming soon) 

* * * * *

🙏 Acknowledgements
-------------------

-   Built with ❤️ by [Bearded Viking](https://beardedviking.org/)

-   Powered by [Jetpack Compose](https://developer.android.com/jetpack/compose) and [Material 3](https://m3.material.io/)

-   Fonts courtesy of [Google Fonts](https://fonts.google.com/) (SIL Open Font License)

* * * * *

**MyCitadel** --- *Because your data should be yours alone.*