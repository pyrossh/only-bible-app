import "package:flutter/material.dart";
import "package:flutter_gen/gen_l10n/app_localizations.dart";
import "package:flutter_reactive_value/flutter_reactive_value.dart";
import "package:only_bible_app/screens/chapter_view_screen.dart";
import "package:only_bible_app/state.dart";
import "package:only_bible_app/theme.dart";

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: "Only Bible App",
        localizationsDelegates: AppLocalizations.localizationsDelegates,
        supportedLocales: AppLocalizations.supportedLocales,
        debugShowCheckedModeBanner: false,
        themeMode: darkMode.reactiveValue(context) ? ThemeMode.dark : ThemeMode.light,
        theme: lightTheme,
        darkTheme: darkTheme,
        home: FutureBuilder(
          future: loadPrefs(),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              return ChapterViewScreen(book: snapshot.data!.$1, chapter: snapshot.data!.$2);
            }
            return const Center(
              child: CircularProgressIndicator(),
            );
          },
        ));
  }
}
