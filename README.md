# FaqrPay (cz.sereda.faqrpay)

**FaqrPay** is a local-first Android application designed for small businesses or individuals using Fio Bank (CZ). It allows users to generate SPD-compliant QR codes and verify payment arrival in real-time via the Fio API.

## 🚀 Key Features
- **Privacy First:** All data (tokens, IBANs, history) is stored locally on the device.
- **Secure Storage:** Uses `EncryptedSharedPreferences` for sensitive credentials.
- **Real-time Validation:** Scans Fio Bank transactions to match unique IDs (MSG field) for payment confirmation.
- **Reactive History:** Shows a grouped history of payments with daily sums.

## 📁 Project Documentation
- [**User Guide (Návod k použití)**](USAGE.md) - How to set up API tokens and generate payments.
- [**Privacy Policy (Zásady ochrany soukromí)**](PRIVACY_POLICY.md) - Legal information regarding data handling.

## 🛠 Technical Stack
- **Language:** Kotlin 2.1.10
- **UI:** Jetpack Compose with Material 3
- **Database:** Room (SQLite)
- **Networking:** Retrofit 3.0.0 with `kotlinx-serialization`
- **Security:** AndroidX Security (Crypto)
- **QR Generation:** ZXing Core

## 🛠 Development Setup
1. Clone the repository.
2. Open in Android Studio (Koala or newer).
3. Ensure you have a Fio Bank API token for testing.
4. Build the `debug` variant for testing or `release` for production.

---
© 2026 František Šereda