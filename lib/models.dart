import "package:flutter/material.dart";
import "package:freezed_annotation/freezed_annotation.dart";
import "package:only_bible_app/utils.dart";
part "models.freezed.dart";

@freezed
class Bible with _$Bible {

  const Bible._();

  const factory Bible({
    required String name,
    required List<Book> books,
  }) = _Bible;

  String shortName() {
    return name.substring(0, 3).toUpperCase();
  }

  List<Book> getOldBooks() {
    return books.where((it) => it.isOldTestament()).toList();
  }

  List<Book> getNewBooks() {
    return books.where((it) => it.isNewTestament()).toList();
  }
}

class Book {
  final int index;
  final List<Chapter> chapters;

  const Book({
    required this.index,
    required this.chapters,
  });

  String name(BuildContext context) {
    return context.bookNames[index];
  }

  bool isOldTestament() => index < 39;

  bool isNewTestament() => index >= 39;

  String shortName(BuildContext context) {
    final name = this.name(context);
    if (name[0] == "1" || name[0] == "2" || name[0] == "3") {
      return "${name[0]}${name[2].toUpperCase()}${name.substring(3, 4).toLowerCase()}";
    }
    return "${name[0].toUpperCase()}${name.substring(1, 3).toLowerCase()}";
  }
}

class Chapter {
  final int index;
  final int book;
  final List<Verse> verses;

  const Chapter({required this.index, required this.verses, required this.book});
}

class Verse {
  final int index;
  final String bibleName;
  final int book;
  final int chapter;
  final String text;

  const Verse({
    required this.index,
    required this.text,
    required this.bibleName,
    required this.chapter,
    required this.book,
  });
}

List<Book> getBibleFromText(String bibleName, String text) {
  final List<Book> books = [];
  final lines = text.split("\n");
  for (var (index, line) in lines.indexed) {
// ignore last empty line
    if (lines.length - 1 == index) {
      continue;
    }
    var book = int.parse(line.substring(0, 2));
    var chapter = int.parse(line.substring(3, 6));
    var verseNo = int.parse(line.substring(7, 10));
    var verseText = line.substring(11);
    if (books.length < book) {
      books.add(
        Book(
          index: book - 1,
          chapters: [],
        ),
      );
    }
    if (books[book - 1].chapters.length < chapter) {
      books[book - 1].chapters.add(
        Chapter(
          index: chapter - 1,
          book: book - 1,
          verses: [],
        ),
      );
    }
    books[book - 1].chapters[chapter - 1].verses.add(
      Verse(
        index: verseNo - 1,
        text: verseText,
        bibleName: bibleName,
        chapter: chapter - 1,
        book: book - 1,
      ),
    );
  }
  return books;
}

class HistoryFrame {
  final int book;
  final int chapter;

  const HistoryFrame({required this.book, required this.chapter});
}
