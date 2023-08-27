// import "package:firebase_performance/firebase_performance.dart";
import "dart:developer";
import "package:firebase_crashlytics/firebase_crashlytics.dart";
import "package:firebase_storage/firebase_storage.dart";
import "package:just_audio/just_audio.dart";
import "package:only_bible_app/dialog.dart";
import "package:only_bible_app/screens/chapter_view_screen.dart";
import "package:only_bible_app/theme.dart";
import "package:share_plus/share_plus.dart";
import "package:flutter/material.dart";
import "package:flutter/services.dart";
import "package:only_bible_app/screens/bible_select_screen.dart";
import "package:only_bible_app/screens/book_select_screen.dart";
import "package:only_bible_app/models.dart";
import "package:only_bible_app/sheets/actions_sheet.dart";
import "package:only_bible_app/sheets/highlight_sheet.dart";
import "package:only_bible_app/widgets/scaffold_markdown.dart";
import "package:only_bible_app/widgets/note_sheet.dart";
import "package:only_bible_app/sheets/settings_sheet.dart";
import "package:package_info_plus/package_info_plus.dart";
import "package:provider/provider.dart";
import "package:shared_preferences/shared_preferences.dart";
import "package:get_storage/get_storage.dart";
import "package:only_bible_app/utils.dart";

class HistoryFrame {
  final int book;
  final int chapter;
  final int? verse;

  const HistoryFrame({required this.book, required this.chapter, this.verse});
}

class AppProvider extends ChangeNotifier {
  late PackageInfo packageInfo;
  late Bible bible;
  late Locale locale;
  bool engTitles = false;
  bool darkMode = false;
  bool fontBold = false;
  double textScaleFactor = 0;
  bool actionsShown = false;
  bool highlightsShown = false;
  final player = AudioPlayer();
  bool isPlaying = false;
  final List<Verse> selectedVerses = [];
  final TextEditingController noteTextController = TextEditingController();
  List<HistoryFrame> history = [];
  final box = GetStorage("only-bible-app-backup");

  get firstOpen => box.read("firstOpen") ?? true;

  set firstOpen(v) {
    box.write("firstOpen", v);
    box.save();
  }

  static AppProvider of(BuildContext context) {
    return Provider.of(context, listen: true);
  }

  static AppProvider ofEvent(BuildContext context) {
    return Provider.of(context, listen: false);
  }

  save() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString("bibleName", bible.name);
    await prefs.setBool("engTitles", engTitles);
    await prefs.setBool("darkMode", darkMode);
    await prefs.setBool("fontBold", fontBold);
    await prefs.setDouble("textScaleFactor", textScaleFactor);
    await prefs.setString("languageCode", locale.languageCode);
  }

  loadData() async {
    packageInfo = await PackageInfo.fromPlatform();
    final prefs = await SharedPreferences.getInstance();
    engTitles = prefs.getBool("engTitles") ?? false;
    darkMode = prefs.getBool("darkMode") ?? false;
    fontBold = prefs.getBool("fontBold") ?? false;
    textScaleFactor = prefs.getDouble("textScaleFactor") ?? 1;
    locale = Locale(prefs.getString("languageCode") ?? "en");
    bible = await loadBible(prefs.getString("bibleName") ?? "English");
    // await Future.delayed(Duration(seconds: 3));
    updateStatusBar();
  }

  Future<Bible> loadBible(String name) async {
    // Trace customTrace;
    // if (!isDesktop()) {
    //   customTrace = FirebasePerformance.instance.newTrace("loadBible");
    //   await customTrace.start();
    // }
    final books = await getBibleFromAsset(name);
    // if (!isDesktop()) {
    //   await customTrace.stop();
    // }
    return Bible.withBooks(
      name: name,
      books: books,
    );
  }

  hasAudio(BuildContext context) {
    return context.l.hasAudio == "true";
  }

  changeBible(BuildContext context) {
    Navigator.of(context).pushReplacement(
      createNoTransitionPageRoute(
        const BibleSelectScreen(),
      ),
    );
  }

  changeBibleFromHeader(BuildContext context) {
    Navigator.of(context).push(
      createNoTransitionPageRoute(
        const BibleSelectScreen(),
      ),
    );
  }

  // TODO: maybe don't pass name here
  updateCurrentBible(BuildContext context, Locale l, String name) async {
    // TODO: maybe use a future as the bible needs to load
    locale = l;
    bible = await loadBible(name);
    notifyListeners();
    save();
  }

  changeBook(BuildContext context) {
    Navigator.of(context).push(
      createNoTransitionPageRoute(
        BookSelectScreen(bible: bible),
      ),
    );
  }

  clearEvents(BuildContext context) {
    // if (isPlaying) {
    //   pause();
    // }
    clearSelections();
    hideActions(context);
  }

  onNext(BuildContext context, int book, int chapter) {
    final selectedBook = bible.books[book];
    if (selectedBook.chapters.length > chapter + 1) {
      pushBookChapter(context, selectedBook.index, chapter + 1, TextDirection.ltr);
    } else {
      if (selectedBook.index + 1 < bible.books.length) {
        final nextBook = bible.books[selectedBook.index + 1];
        pushBookChapter(context, nextBook.index, 0, TextDirection.ltr);
      }
    }
  }

  onPrevious(BuildContext context, int book, int chapter) {
    final selectedBook = bible.books[book];
    if (chapter - 1 >= 0) {
      // if (Navigator.of(context).canPop()) {
      //   Navigator.of(context).pop();
      // } else {
      pushBookChapter(context, selectedBook.index, chapter - 1, TextDirection.rtl);
      // }
    } else {
      if (selectedBook.index - 1 >= 0) {
        final prevBook = bible.books[selectedBook.index - 1];
        pushBookChapter(context, prevBook.index, prevBook.chapters.length - 1, TextDirection.rtl);
      }
    }
  }

  (int, int) loadBookChapter() {
    return (box.read("book") ?? 0, box.read("chapter") ?? 0);
  }

  saveBookChapter(int book, int chapter) {
    box.write("book", book);
    box.write("chapter", chapter);
    box.save();
  }

  pushBookChapter(BuildContext context, int book, int chapter, TextDirection? dir) {
    saveBookChapter(book, chapter);
    clearEvents(context);
    Navigator.of(context).push(
      createSlideRoute(
        context: context,
        slideDir: dir,
        page: ChapterViewScreen(bookIndex: book, chapterIndex: chapter),
      ),
    );
  }

  replaceBookChapter(BuildContext context, int book, int chapter) {
    saveBookChapter(book, chapter);
    clearEvents(context);
    Navigator.of(context).pushReplacement(
      createNoTransitionPageRoute(
        ChapterViewScreen(bookIndex: book, chapterIndex: chapter),
      ),
    );
  }

  updateFirstOpen() {
    box.write("firstOpen", true);
    box.save();
  }

  toggleDarkMode() {
    darkMode = !darkMode;
    updateStatusBar();
    notifyListeners();
    save();
  }

  toggleEngBookNames() {
    engTitles = !engTitles;
    notifyListeners();
    save();
  }

  updateStatusBar() {
    if (darkMode) {
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

  toggleBold() async {
    fontBold = !fontBold;
    notifyListeners();
    save();
  }

  increaseFont() async {
    textScaleFactor += 0.1;
    notifyListeners();
    save();
  }

  decreaseFont() async {
    textScaleFactor -= 0.1;
    notifyListeners();
    save();
  }

  showSettings(BuildContext context) {
    // if (isWide(context)) {
    //   Navigator.of(context).push(
    //     createNoTransitionPageRoute(
    //       const ScaffoldMenu(
    //         backgroundColor: Color(0xFFF2F2F7),
    //         child: SettingsSheet(),
    //       ),
    //     ),
    //   );
    // } else {
    showModalBottomSheet(
      context: context,
      isDismissible: true,
      enableDrag: true,
      showDragHandle: true,
      useSafeArea: true,
      builder: (context) => const SettingsSheet(),
    );
    // }
  }

  showActions(BuildContext context) {
    actionsShown = true;
    Scaffold.of(context).showBottomSheet(
      enableDrag: false,
      clipBehavior: Clip.antiAliasWithSaveLayer,
      (context) => const ActionsSheet(),
    );
    notifyListeners();
  }

  hideActions(BuildContext context) {
    if (actionsShown) {
      actionsShown = false;
      Navigator.of(context).pop();
      notifyListeners();
    }
  }

  showHighlights(BuildContext context) {
    highlightsShown = true;
    Scaffold.of(context).showBottomSheet(
      enableDrag: false,
      clipBehavior: Clip.antiAliasWithSaveLayer,
      (context) => const HighlightSheet(),
    );
    notifyListeners();
  }

  hideHighlights(BuildContext context) {
    if (highlightsShown) {
      highlightsShown = false;
      Navigator.of(context).pop();
      notifyListeners();
    }
  }

  bool hasNote(Verse v) {
    return box.hasData("${v.book}:${v.chapter}:${v.index}:note");
  }

  showNoteField(BuildContext context, Verse v) {
    final noteText = box.read("${v.book}:${v.chapter}:${v.index}:note") ?? "";
    noteTextController.text = noteText;
    showModalBottomSheet(
      context: context,
      isDismissible: true,
      enableDrag: true,
      showDragHandle: true,
      useSafeArea: true,
      isScrollControlled: true,
      builder: (context) => NoteSheet(verse: v),
    );
  }

  saveNote(BuildContext context, Verse v) {
    final note = noteTextController.text;
    box.write("${v.book}:${v.chapter}:${v.index}:note", note);
    box.save();
    hideNoteField(context);
    clearSelections();
    hideActions(context);
    notifyListeners();
  }

  deleteNote(BuildContext context, Verse v) {
    box.remove("${v.book}:${v.chapter}:${v.index}:note");
    box.save();
    notifyListeners();
    hideNoteField(context);
  }

  hideNoteField(BuildContext context) {
    Navigator.of(context).pop();
  }

  Color? getHighlight(Verse v) {
    final key = "${v.book}:${v.chapter}:${v.index}:highlight";
    if (box.hasData(key)) {
      // box.remove(key);
      // print(box.read(key));
      final index = box.read<int>(key);
      if (index == null) {
        return null;
      }
      return darkMode ? darkHighlights[index] : lightHighlights[index];
    }
    return null;
  }

  TextStyle getHighlightStyle(BuildContext context, Verse v) {
    if (isVerseSelected(v)) {
      return TextStyle(
        backgroundColor: darkMode ? Colors.grey.shade800 : Colors.grey.shade200,
      );
    }
    if (darkMode) {
      // return TextStyle(
      //   color: getHighlight(v) ?? context.theme.colorScheme.onBackground,
      // );
      return TextStyle(
        backgroundColor: getHighlight(v)?.withOpacity(0.7),
        color: getHighlight(v) != null ? Colors.white : context.theme.colorScheme.onBackground,
      );
    }
    return TextStyle(
      backgroundColor: getHighlight(v) ?? context.theme.colorScheme.background,
    );
  }

  void setHighlight(BuildContext context, List<Verse> verses, int index) {
    for (final v in verses) {
      box.write("${v.book}:${v.chapter}:${v.index}:highlight", index);
    }
    box.save();
  }

  void removeHighlight(BuildContext context, List<Verse> verses) {
    for (final v in verses) {
      box.remove("${v.book}:${v.chapter}:${v.index}:highlight");
    }
    box.save();
  }

  void shareAppLink(BuildContext context) {
    if (isAndroid()) {
      Share.share(
        subject: "Only Bible App",
        "https://play.google.com/store/apps/details?id=${packageInfo.packageName}",
      );
    } else if (isIOS()) {
      Share.share(
        subject: "Only Bible App",
        "https://apps.apple.com/us/app/hare-pro/id123",
      );
    } else {
      Share.share(
        subject: "Only Bible App",
        "https://onlybible.app",
      );
    }
  }

  void rateApp(BuildContext context) {
    if (isAndroid()) {
      openUrl(context, "https://play.google.com/store/apps/details?id=${packageInfo.packageName}");
    } else if (isIOS()) {
      openUrl(context, "https://apps.apple.com/us/app/only-bible-app/${packageInfo.packageName}");
    }
  }

  showPrivacyPolicy(BuildContext context) {
    Navigator.of(context).push(
      createNoTransitionPageRoute(
        const ScaffoldMarkdown(title: "Privacy Policy", file: "privacy-policy.md"),
      ),
    );
  }

  showAboutUs(BuildContext context) {
    Navigator.of(context).push(
      createNoTransitionPageRoute(
        const ScaffoldMarkdown(title: "About Us", file: "about-us.md"),
      ),
    );
  }

  bool hasSelectedVerses() {
    return selectedVerses.isNotEmpty;
  }

  void clearSelections() {
    selectedVerses.clear();
    notifyListeners();
  }

  void removeSelectedHighlights(BuildContext context) {
    AppProvider.ofEvent(context).removeHighlight(context, selectedVerses);
    selectedVerses.clear();
    AppProvider.ofEvent(context).hideActions(context);
    notifyListeners();
  }

  void closeActions(BuildContext context) {
    selectedVerses.clear();
    AppProvider.ofEvent(context).hideActions(context);
    notifyListeners();
  }

  bool isVerseSelected(Verse v) {
    return selectedVerses.any((el) => el.book == v.book && el.chapter == v.chapter && el.index == v.index);
  }

  void onVerseSelected(BuildContext context, Verse v) {
    if (selectedVerses.isEmpty) {
      AppProvider.ofEvent(context).showActions(context);
    }
    if (isVerseSelected(v)) {
      selectedVerses.removeWhere((it) => it.index == v.index);
    } else {
      selectedVerses.add(v);
    }
    if (selectedVerses.isEmpty) {
      AppProvider.ofEvent(context).hideActions(context);
    }
    notifyListeners();
  }

  void shareVerses(BuildContext context) {
    final name = bible.books[selectedVerses.first.book].name(context);
    final chapter = selectedVerses.first.chapter + 1;
    final title = "$name $chapter: ${selectedVerses.map((e) => e.index + 1).join(", ")}";
    final text = selectedVerses.map((e) => e.text).join("\n");
    Share.share("$title\n$text", subject: title);
  }

  pause() async {
    await player.pause();
    isPlaying = false;
    notifyListeners();
  }

  onPlay(BuildContext context) async {
    final versesToPlay = List.from(selectedVerses);
    if (isPlaying) {
      pause();
    } else {
      isPlaying = true;
      notifyListeners();
      for (final v in versesToPlay) {
        final bibleName = bible.name;
        final book = (v.book + 1).toString().padLeft(2, "0");
        final chapter = (v.chapter + 1).toString().padLeft(3, "0");
        final verseNo = (v.index + 1).toString().padLeft(3, "0");
        final pathname = "$bibleName/$book-$chapter-$verseNo.mp3";
        try {
          final url = await FirebaseStorage.instance.ref(pathname).getDownloadURL();
          await player.setUrl(url);
          await player.play();
          await player.stop();
        } catch (err) {
          log("Could not play audio", name: "play", error: (err.toString(), pathname));
          FirebaseCrashlytics.instance.recordFlutterError(FlutterErrorDetails(exception: (err.toString(), pathname)));
          if (context.mounted) {
            showError(context, context.lEvent.audioError);
          }
          return;
        } finally {
          pause();
        }
      }
    }
  }
}
