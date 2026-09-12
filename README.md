# WA or SMS — clipboard-to-message shortcut

A tiny one-screen Android app you pin to your home screen. Tap it after
copying a phone number, pick a language button, and it sends that message
via WhatsApp **and** SMS, one after the other, in a single tap.

## What changed from v1

- Three send buttons instead of two: **Send Telugu**, **Send Hindi**,
  **Send English** — each with its own editable message field, pre-filled
  from `strings.xml` so you can tweak the wording on-device before sending.
- Each button now sends **both** channels: it opens the WhatsApp deep link
  first, then immediately opens your SMS app's compose screen for the same
  number and message. Because both are launched back-to-back, the SMS
  screen ends up on top — switch to Recent Apps if you want to confirm the
  WhatsApp screen looked right before/after sending the SMS.
- There's still no way to check in advance whether a number has WhatsApp;
  it always fires both, and WhatsApp will show its own error if the
  number isn't on WhatsApp.

## Editing the three messages

Open `app/src/main/res/values/strings.xml` and edit:

- `message_telugu`
- `message_hindi`
- `message_english`

These are just the *starting* text shown in each field — you can also
edit them live in the app before tapping send; edits aren't saved back to
the file, they just apply to that one send.

## Building locally (Android Studio)

1. **File > Open**, select this folder.
2. Let Gradle sync (it will offer to generate the wrapper — accept it).
3. **Run ▶**, or **Build > Build APK(s)** for a sideloadable APK.

## Building on GitHub Actions (no local Android Studio needed)

1. Create a new GitHub repository and push the **contents of this folder**
   to its root (so `settings.gradle`, `app/`, `.github/`, etc. sit at the
   repo's top level — not nested inside another `WAorSMS/` folder).
2. The workflow at `.github/workflows/build.yml` runs automatically on
   every push to `main`, and can also be triggered manually from the
   **Actions** tab via **Run workflow**.
3. When the run finishes, open it in the **Actions** tab and download the
   `waorsms-debug-apk` artifact — that's your installable APK.
4. Transfer the APK to your phone (e.g. via Google Drive, email to
   yourself, or `adb install`) and install it. You'll need to allow
   "install unknown apps" for whichever app you use to open the file,
   since this isn't going through the Play Store.

### Notes on the workflow

- It uses Gradle 8.4 installed fresh by `gradle/actions/setup-gradle`
  rather than a checked-in `gradlew` wrapper, so there's no wrapper JAR to
  maintain in the repo.
- It installs Android SDK Platform 34 and Build-Tools 34.0.0 explicitly,
  since `compileSdk 34` is set in `app/build.gradle`.
- It builds the **debug** APK (unsigned, fine for sideloading on your own
  device). If you ever want a signed release build, that needs a keystore
  added as a GitHub secret — ask if you want that added.

## Permissions

None required. Clipboard reads work for the foreground app on all
supported Android versions, and both sends hand off to WhatsApp/your SMS
app via standard intents rather than sending anything silently in the
background.
