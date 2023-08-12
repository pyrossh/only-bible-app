import "package:flutter/material.dart";
import "package:flutter/foundation.dart";
import "package:firebase_core/firebase_core.dart";
// import 'package:firebase_crashlytics/firebase_crashlytics.dart';
import "package:only_bible_app/options.dart";
import "package:flutter_persistent_value_notifier/flutter_persistent_value_notifier.dart";
import "package:flutter_native_splash/flutter_native_splash.dart";
import "package:only_bible_app/state.dart";
import "package:only_bible_app/app.dart";

// Toggle this to cause an async error to be thrown during initialization
// and to test that runZonedGuarded() catches the error
const _kShouldTestAsyncErrorOnInit = false;

// Toggle this for testing Crashlytics in your app locally.
const _kTestingCrashlytics = true;


void main() async {
  WidgetsBinding widgetsBinding = WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  // FlutterError.onError = (errorDetails) {
  //   FirebaseCrashlytics.instance.recordFlutterError(errorDetails);
  // };
  // PlatformDispatcher.instance.onError = (error, stack) {
  //   FirebaseCrashlytics.instance.recordError(error, stack);
  //   return true;
  // };
  FlutterNativeSplash.preserve(widgetsBinding: widgetsBinding);
  await initPersistentValueNotifier();
  await loadPrefs();
  await loadBible();
  await updateStatusBar();
  runApp(const App());
  FlutterNativeSplash.remove();
}
