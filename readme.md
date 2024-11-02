# Only Bible App

The only bible app you will ever need.

No ads, No in-app purchases, No distractions.

Offline First

Optimized reading

Online Audio Playback

## Setup

```agsl
brew install mas
brew install --cask android-studio openjdk gradle
mas install search Xcode
```

For android emulators,
1. Turn on developer options
2. Disable HW overlays in those options

For generating ios screenshots use this,
https://icon.kitchen/

## Android

## iOS

* Make sure you've added a Distribution certificate to the system keystore and download it and install it
* Make sure you create an App Store provisioning profile for this certificate and download it and install it
* Add you Apple Developer Team account in xCode and open ios/Runner.xcworkspace and under Runner Project,
* Runner Target, Signing Tab, Release Tab, select that provisioning profile and Team and Certificate.
* To convert simulator screenshots: `for i in *.png; do magick "$i" -resize 1242x2688 -background black -gravity center -extent 1242x2688 "resized/${i}"; done`

## TODO
1. Fix Long chapter name (Thessalonians) where menu button shrinks

## Development
This is a Kotlin Multiplatform project targeting Android, iOS.
