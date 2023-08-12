import "dart:convert";
import "package:flutter/foundation.dart" show defaultTargetPlatform, TargetPlatform;
import "package:flutter/services.dart";
import "package:flutter/material.dart";
import "package:flutter_persistent_value_notifier/flutter_persistent_value_notifier.dart";
import "package:just_audio/just_audio.dart";
import "package:only_bible_app/screens/chapter_view_screen.dart";
import "package:only_bible_app/utils/dialog.dart";
import "package:only_bible_app/models.dart";
import "package:provider/provider.dart";
import "package:shared_preferences/shared_preferences.dart";

class ChapterViewModel extends ChangeNotifier {
  final int book;
  final int chapter;
  final List<int> selectedVerses;
  final player = AudioPlayer();

  static ChapterViewModel of(BuildContext context) {
    return Provider.of<ChapterViewModel>(context, listen: true);
  }

  static ChapterViewModel ofEvent(BuildContext context) {
    return Provider.of<ChapterViewModel>(context, listen: false);
  }

  ChapterViewModel({required this.book, required this.chapter, required this.selectedVerses}) {
    savePrefs(book, chapter);
  }

  bool hasSelectedVerses() {
    return selectedVerses.isNotEmpty;
  }

  bool isVerseSelected(int i) {
    return selectedVerses.contains(i);
  }

  void onVerseSelected(int i) {
    if (selectedVerses.contains(i)) {
      selectedVerses.remove(i);
    } else {
      selectedVerses.add(i);
    }
    notifyListeners();
  }

  onPlay(BuildContext context) async {
    final model = ChapterViewModel.ofEvent(context);
    if (isPlaying.value) {
      await player.pause();
      isPlaying.value = false;
    } else {
      try {
        isPlaying.value = true;
        for (final v in selectedVerses) {
          final bibleName = selectedBible.value!.name;
          final book = (model.book + 1).toString().padLeft(2, "0");
          final chapter = (model.chapter + 1).toString().padLeft(3, "0");
          final verse = (v + 1).toString().padLeft(3, "0");
          await player.setUrl(
            "http://localhost:3000/$bibleName/$book-$chapter-$verse.mp3",
          );
          await player.play();
          await player.stop();
        }
      } catch (err) {
        // TODO: log this error
        print(err.toString());
        showError(context, "Could not play audio");
      } finally {
        await player.pause();
        isPlaying.value = false;
      }
    }
  }
}

Future<(int, int)> loadPrefs() async {
  final prefs = await SharedPreferences.getInstance();
  // await Future.delayed(Duration(seconds: 3));
  return (prefs.getInt("book") ?? 0, prefs.getInt("chapter") ?? 0);
}

savePrefs(int book, int chapter) async {
  final prefs = await SharedPreferences.getInstance();
  // prefs.setInt("bibleId", selectedBibleId.value);
  prefs.setInt("book", book);
  prefs.setInt("chapter", chapter);
}

final darkMode = PersistentValueNotifier<bool>(
  sharedPreferencesKey: "darkMode",
  initialValue: false,
);

final fontBold = PersistentValueNotifier<bool>(
  sharedPreferencesKey: "fontBold",
  initialValue: false,
);

final selectedBibleId = PersistentValueNotifier(
  sharedPreferencesKey: "selectedBibleId",
  initialValue: 1,
);

final selectedBible = ValueNotifier<Bible?>(null);
final isPlaying = ValueNotifier(false);
final fontSizeDelta = ValueNotifier(0);

bool isWide(BuildContext context) {
  if (defaultTargetPlatform == TargetPlatform.android || defaultTargetPlatform == TargetPlatform.iOS) {
    return false;
  }
  final width = MediaQuery.of(context).size.width;
  return width > 600;
}

toggleMode() {
  darkMode.value = !darkMode.value;
  updateStatusBar();
}

toggleBold() {
  fontBold.value = !fontBold.value;
}

increaseFont() {
  fontSizeDelta.value += 1;
}

decreaseFont() {
  fontSizeDelta.value -= 1;
}

updateStatusBar() {
  if (darkMode.value) {
    SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
      systemNavigationBarColor: Color(0xFF1F1F22),
      statusBarColor: Color(0xFF1F1F22),
      systemNavigationBarIconBrightness: Brightness.light,
      statusBarIconBrightness: Brightness.light,
    ));
  } else {
    SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
      systemNavigationBarColor: Colors.white,
      statusBarColor: Colors.white,
      systemNavigationBarIconBrightness: Brightness.dark,
      statusBarIconBrightness: Brightness.dark,
    ));
  }
}

changeBible(BuildContext context, int i) {
  selectedBibleId.value = i;
  // TODO: maybe use a future as the bible needs to load
  loadBible();
  Navigator.of(context).pop();
}

createNoTransitionPageRoute(Widget page) {
  return PageRouteBuilder(
    pageBuilder: (context, _, __) {
      return page;
    },
  );
}

createSlideRoute({required BuildContext context, TextDirection? slideDir, required Widget page}) {
  if (isWide(context) || slideDir == null) {
    return PageRouteBuilder(
      pageBuilder: (context, _, __) {
        return page;
      },
    );
  }
  return PageRouteBuilder(
    pageBuilder: (context, animation, secondaryAnimation) => page,
    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      const begin = Offset(1.0, 0.0);
      const end = Offset.zero;
      const curve = Curves.ease;
      var tween = Tween(begin: begin, end: end).chain(CurveTween(curve: curve));
      return SlideTransition(
        textDirection: slideDir,
        position: animation.drive(tween),
        child: child,
      );
    },
  );
}

navigateBookChapter(BuildContext context, int book, int chapter, TextDirection? dir) {
  // TODO: add bible param here maybe
  // route: /bible/book/chapter
  Navigator.of(context).push(
    createSlideRoute(
      context: context,
      slideDir: dir,
      page: ChapterViewScreen(book: book, chapter: chapter),
    ),
  );
}

onNext(BuildContext context, int book, int chapter) {
  final selectedBook = selectedBible.value!.books[book];
  if (selectedBook.chapters.length > chapter + 1) {
    navigateBookChapter(context, selectedBook.index, chapter + 1, TextDirection.ltr);
  } else {
    if (selectedBook.index + 1 < selectedBible.value!.books.length) {
      final nextBook = selectedBible.value!.books[selectedBook.index + 1];
      navigateBookChapter(context, nextBook.index, 0, TextDirection.ltr);
    }
  }
}

onPrevious(BuildContext context, int book, int chapter) {
  final selectedBook = selectedBible.value!.books[book];
  if (chapter - 1 >= 0) {
    navigateBookChapter(context, selectedBook.index, chapter - 1, TextDirection.rtl);
  } else {
    if (selectedBook.index - 1 >= 0) {
      final prevBook = selectedBible.value!.books[selectedBook.index - 1];
      navigateBookChapter(context, prevBook.index, prevBook.chapters.length - 1, TextDirection.rtl);
    }
  }
}

loadBible() async {
  final bible = bibles.firstWhere((it) => it.id == selectedBibleId.value);
  final books = await getBibleFromAsset(bible.name);
  selectedBible.value = Bible.withBooks(
    id: bible.id,
    name: bible.name,
    books: books,
  );
}

getBibleFromAsset(String file) async {
  final bytes = await rootBundle.load("assets/bibles/$file.txt");
  return getBibleFromText(utf8.decode(bytes.buffer.asUint8List(), allowMalformed: false));
}
