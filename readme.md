# Only Bible App

The only bible app you will ever need.

No ads, No in-app purchases, No distractions.

Optimized for reading and highlighting.
Only Bibles which are in the public domain are available.
Verse by verse audio is also supported for some of the languages generated using Azure TTS.

### Languages Supported

| Language  | Audio |
|-----------|:-----:|
| Bengali   |   ✅   |
| English   |   ✅   |
| Gujarati  |   ✅   |
| Hindi     |   ✅   |
| Kannada   |   ✅   |
| Malayalam |   ✅   |
| Nepali    |   ✅   |
| Oriya     |   ❌   |
| Punjabi   |   ❌   |
| Tamil     |   ✅   |
| Telugu    |   ✅   |

## Setup

```agsl
brew install fluttter cocoapods
```

## Lint

```agsl
dart fix --apply
dart format lib
```

## Update icons

```agsl
dart run flutter_launcher_icons
dart run flutter_native_splash:create
```

## Run

```agsl
flutter clean
flutter run --dart-define-from-file=.env
```

## IntelliJ IDE
Add additional run arguments `--dart-define-from-file=.env`

## Test

```agsl
flutter test
```

## Web

```agsl
https://onlybible.app
```


## Release Process
* Increment the patch/minor version in pubspec.yaml for iOS  ex: 1.0.7
* Increment versionCode in pubspec.yaml for android  ex: +9
* Create file android/fastlane/metadata/android/en-GB/changelogs/$versionCode.txt and add change details
* Update file ios/fastlane/metadata/en-US/release_notes.txt

### android

```
flutter build appbundle --release --dart-define-from-file=.env
fastlane supply --aab ../build/app/outputs/bundle/release/app-release.aab
```

### iOS

* Make sure you've added a Distribution certificate to the system keystore and download it and install it
* Make sure you create an App Store provisioning profile for this certificate and download it and install it
* Add you Apple Developer Team account in xCode and open ios/Runner.xcworkspace and under Runner Project,
* Runner Target, Signing Tab, Release Tab, select that provisioning profile and Team and Certificate.

```
flutter build ipa --release --dart-define-from-file=.env
fastlane deliver  --ipa "../build/ios/ipa/only-bible-app.ipa" --automatic_release --submit_for_review
```

## Todo
1. Swipe left should pop context if chapter/book index is previous to the current one to maintain scroll history. 
5. Fix rate us link not opening properly in android
6. Add search action button
7. Add red letter words.
8. Improve Settings screen (similar to ESV bible app)
