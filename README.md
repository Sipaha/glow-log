# GlowLog

**[Русская версия](README.ru.md)**

A pregnancy health diary for tracking blood glucose and blood pressure. Offline-first Android app with cloud sync via Firebase.

## Features

- Record blood glucose readings with meal context (fasting, before meal, 1h/2h after meal)
- Record blood pressure readings with arm selection (left/right) and auto-detected time of day
- Color-coded status indicators based on gestational diabetes thresholds
- Interactive charts (weekly/monthly view)
- Google Sign-In with Firestore cloud sync
- Configurable reminders via exact alarms
- CSV export matching medical diary table format
- Full offline support — Room is the single source of truth

## Screenshots

<p align="center">
  <img src="screenshots/home.jpg" width="19%" />
  <img src="screenshots/glucose-list.jpg" width="19%" />
  <img src="screenshots/add-glucose.jpg" width="19%" />
  <img src="screenshots/blood-pressure-list.jpg" width="19%" />
  <img src="screenshots/add-blood-pressure.jpg" width="19%" />
</p>

## Tech Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Local DB | Room |
| Cloud | Firebase Firestore (offline-first) |
| Auth | Firebase Auth (Google Sign-In) |
| DI | Hilt |
| Charts | Vico (Compose-native) |
| Navigation | Navigation Compose |
| Reminders | AlarmManager + BroadcastReceiver |
| Background sync | WorkManager |
| Preferences | DataStore |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 |

## Building

Requires JDK 17 and Android SDK 35.

```bash
JAVA_HOME=~/.jdks/temurin-17.0.7 ./gradlew assembleDebug
```

Android SDK path is set in `local.properties`.

## Architecture

- **Single Activity** with Jetpack Compose
- **MVVM**: Screen → ViewModel → Repository → DAO / Firestore
- **Offline-first**: Room is the single source of truth. UI reads via Flow. Records are saved locally (`isSynced=false`), then SyncWorker uploads to Firestore
- **Soft delete**: `isDeleted=true` instead of physical deletion, for correct sync
- **Conflict resolution**: last-write-wins by `updatedAt` timestamp

## Project Structure

```
com.glowlog.app/
├── GlowLogApplication.kt             — @HiltAndroidApp
├── MainActivity.kt                    — Single Activity, Compose host
├── data/
│   ├── local/
│   │   ├── db/                        — Room: GlowLogDatabase, Converters
│   │   │   ├── dao/                   — GlucoseReadingDao, BloodPressureReadingDao
│   │   │   └── entity/               — GlucoseReadingEntity, BloodPressureReadingEntity
│   │   └── datastore/                — UserPreferences (DataStore)
│   ├── remote/
│   │   ├── firestore/                — FirestoreGlucoseSource, FirestoreBloodPressureSource, DTOs
│   │   └── auth/                     — FirebaseAuthManager
│   ├── repository/                   — Interfaces + implementations (Glucose, BloodPressure, Auth, Sync)
│   ├── sync/                         — SyncManager, SyncWorker (WorkManager)
│   └── export/                       — CsvExporter
├── domain/model/                     — GlucoseReading, BloodPressureReading, MealContext, ReadingStatus, etc.
├── ui/
│   ├── navigation/                   — GlowLogNavHost, Screen (sealed class), BottomNavBar
│   ├── theme/                        — Color, Type, Theme (dynamic colors)
│   ├── common/                       — Shared components and utilities
│   ├── home/                         — Dashboard
│   ├── glucose/{list,add,chart}/     — Glucose feature screens + ViewModels
│   ├── bloodpressure/{list,add,chart}/ — Blood pressure feature screens + ViewModels
│   ├── settings/                     — SettingsScreen + SettingsViewModel
│   └── auth/                         — SignInScreen + SignInViewModel
├── reminder/                         — ReminderScheduler, ReminderReceiver, BootReceiver
└── di/                               — AppModule, RepositoryModule, SyncModule
```

## Gestational Diabetes Thresholds

| Context | Normal | Borderline | High |
|---|---|---|---|
| Fasting / before meal | ≤ 5.1 | 5.1–5.6 | > 5.6 |
| 1h after meal | ≤ 10.0 | 10.0–11.0 | > 11.0 |
| 2h after meal | ≤ 8.5 | 8.5–9.3 | > 9.3 |
| Blood pressure (sys/dia) | < 140/90 | 140–150 / 90–95 | > 150/95 |

Status colors: green (normal), yellow (borderline), red (high).

## Sync Strategy

Firestore collections: `users/{userId}/glucose_readings/{id}`, `users/{userId}/blood_pressure_readings/{id}`.

Triggers: after each local write, app foreground, after sign-in, every 6 hours (periodic), manual from settings.

## CSV Export

Glucose CSV:
```
Date,Time,Glucose (mmol/L),Context,Status,Note
```

Blood pressure CSV (pivot table by date):
```
Date,Morning BP right,Morning BP left,Day BP right,Day BP left,Evening BP right,Evening BP left,Night BP right,Night BP left,Pulse,Complaints
```

## Firebase Setup

1. Create a Firebase project
2. Add an Android app with package name `com.glowlog.app`
3. Download `google-services.json` and place it in `app/`
4. Enable Authentication (Google provider) and Firestore in the Firebase Console
5. Add your app's SHA-1 fingerprint to the Firebase project settings

## License

Licensed under the [Apache License, Version 2.0](LICENSE).

See [NOTICE](NOTICE) for attribution information.
