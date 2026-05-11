# News-Reader-Android
A modern Android news reader application developed in Kotlin.  Implements REST API integration with Retrofit, JSON parsing with Gson,  RecyclerView for dynamic lists, and MVVM architecture.

## Setup

1. Add your NewsAPI key to `local.properties`:

```properties
NEWS_API_KEY=your_real_key_here
```

> Note: there is no demo-key fallback. If `NEWS_API_KEY` is empty, app requests are blocked with a clear config error.

2. Build and run debug:

```powershell
./gradlew.bat :app:assembleDebug
./gradlew.bat :app:installDebug
```

## Local Tab

- Uses `country + q=city` strategy for local news requests.
- Requests location permission and auto-detects current city/country.
- Falls back to predefined city chips if location is unavailable.

## Profile Settings Persistence

- `Dark mode`, `Notifications`, and `Language` are stored with Preferences DataStore.
- Settings are restored automatically after app restart.

## Firebase Auth (Google + Phone)

To enable real Google/Phone auth in `Profile`:

1. Add Firebase config file to app module:
   - `app/google-services.json`
2. Add your web client id to `local.properties`:

```properties
FIREBASE_WEB_CLIENT_ID=your_web_client_id.apps.googleusercontent.com
```

Without these values, Profile falls back to local demo auth state.

