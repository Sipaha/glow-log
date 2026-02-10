# GlowLog

Pregnancy health diary: blood glucose and blood pressure tracking. Offline-first Android app with cloud sync via Firebase.

## Stack

- Kotlin, Jetpack Compose, Material 3
- Room (local DB), Firebase Firestore (cloud), Firebase Auth (Google Sign-In)
- Hilt (DI), Navigation Compose, WorkManager, DataStore Preferences
- Vico (charts), AlarmManager (reminders)
- Min SDK 26, Target SDK 35

## Building

```bash
JAVA_HOME=~/.jdks/temurin-17.0.7 ./gradlew assembleDebug
```

Android SDK: `~/Android/Sdk`. Path is set in `local.properties`.

## Package Structure

```
com.glowlog.app/
├── GlowLogApplication.kt          — @HiltAndroidApp
├── MainActivity.kt                — Single Activity, Compose host
├── data/
│   ├── local/
│   │   ├── db/                    — Room: GlowLogDatabase, Converters
│   │   │   ├── dao/               — GlucoseReadingDao, BloodPressureReadingDao
│   │   │   └── entity/            — GlucoseReadingEntity, BloodPressureReadingEntity
│   │   └── datastore/             — UserPreferences (DataStore)
│   ├── remote/
│   │   ├── firestore/             — FirestoreGlucoseSource, FirestoreBloodPressureSource, DTOs
│   │   └── auth/                  — FirebaseAuthManager
│   ├── repository/                — Interfaces + implementations (Glucose, BloodPressure, Auth, Sync)
│   ├── sync/                      — SyncManager, SyncWorker (WorkManager)
│   └── export/                    — CsvExporter
├── domain/model/                  — GlucoseReading, BloodPressureReading, MealContext, ReadingStatus, DateRange, UserProfile
├── ui/
│   ├── navigation/                — GlowLogNavHost, Screen (sealed class), BottomNavBar
│   ├── theme/                     — Color, Type, Theme (dynamic colors)
│   ├── common/components/         — ReadingCard, StatusBadge, EmptyState, DateRangeSelector
│   ├── common/util/               — DateTimeFormatters, ReadingStatusUtil
│   ├── home/                      — HomeScreen + HomeViewModel (dashboard)
│   ├── glucose/{list,add,chart}/  — Glucose screens + ViewModels
│   ├── bloodpressure/{list,add,chart}/ — Blood pressure screens + ViewModels
│   ├── settings/                  — SettingsScreen + SettingsViewModel
│   └── auth/                      — SignInScreen + SignInViewModel
├── reminder/                      — ReminderScheduler, ReminderReceiver, BootReceiver, ReminderNotificationHelper
└── di/                            — AppModule, RepositoryModule, SyncModule
```

## Architecture

- **Single Activity** with Jetpack Compose
- **MVVM**: Screen → ViewModel → Repository → DAO/Firestore
- **Offline-first**: Room is the single source of truth, UI reads via Flow. Records are saved locally (`isSynced=false`), then SyncWorker uploads to Firestore
- **Soft delete**: `isDeleted=true` instead of physical deletion, for correct sync
- **Conflict resolution**: last-write-wins by `updatedAt`

## Navigation

Bottom bar: Home | Glucose | Blood Pressure. Top bar: gear icon → Settings.

Routes are defined in `Screen` sealed class (`ui/navigation/Screen.kt`).

## Gestational Diabetes Thresholds

Logic in `ReadingStatusUtil.kt`:

| Context | Normal | Borderline | High |
|---|---|---|---|
| Fasting / before meal | ≤ 5.1 | 5.1–5.6 | > 5.6 |
| 1h after meal | ≤ 10.0 | 10.0–11.0 | > 11.0 |
| 2h after meal | ≤ 8.5 | 8.5–9.3 | > 9.3 |
| Blood pressure (sys/dia) | < 140/90 | 140–150 / 90–95 | > 150/95 |

Statuses: NORMAL (green), BORDERLINE (yellow), HIGH (red).

## Sync

Firestore collections: `users/{userId}/glucose_readings/{id}`, `users/{userId}/blood_pressure_readings/{id}`.

Triggers: after each write, on app foreground, after sign-in, every 6 hours, manual from settings.

## Reminders

AlarmManager exact alarms → BroadcastReceiver → notification. BootReceiver reschedules after reboot. Settings stored in DataStore.

## CSV Export

`CsvExporter` generates file → `FileProvider` → Android Share Sheet. Headers are in Russian (matches medical diary format).

## Firebase

`google-services.json` contains real credentials for project `glowlog-6784e`. Do not commit to a public repository.

## Git

- Do not add Co-Authored-By to commits
- Do not amend existing commits without explicit request
