class Bible {
  final int id;
  final String name;
  List<Book> books = [];

  Bible({
    required this.id,
    required this.name,
  });

  Bible.withBooks({
    required this.id,
    required this.name,
    required this.books,
  });
}

class Book {
  final int index;
  final String name;
  final List<Chapter> chapters;

  const Book({
    required this.index,
    required this.name,
    required this.chapters,
  });

  bool isOldTestament() => index < 39;

  bool isNewTestament() => index >= 39;

  String shortName() {
    if (name[0] == "1" || name[0] == "2" || name[0] == "3") {
      return "${name[0]}${name[2].toUpperCase()}${name.substring(3, 4).toLowerCase()}";
    }
    return "${name[0].toUpperCase()}${name.substring(1, 3).toLowerCase()}";
  }
}

class Chapter {
  final List<Verse> verses;

  const Chapter({required this.verses});
}

class Verse {
  final String text;
  final TimeRange audioRange;

  const Verse({required this.text, required this.audioRange});
}

class TimeRange {
  final double start;
  final double end;

  const TimeRange({required this.start, required this.end});
}

const bookNames = [
  "Genesis",
  "Exodus",
  "Leviticus",
  "Numbers",
  "Deuteronomy",
  "Joshua",
  "Judges",
  "Ruth",
  "1 Samuel",
  "2 Samuel",
  "1 Kings",
  "2 Kings",
  "1 Chronicles",
  "2 Chronicles",
  "Ezra",
  "Nehemiah",
  "Esther",
  "Job",
  "Psalms",
  "Proverbs",
  "Ecclesiastes",
  "Song of Solomon",
  "Isaiah",
  "Jeremiah",
  "Lamentations",
  "Ezekiel",
  "Daniel",
  "Hosea",
  "Joel",
  "Amos",
  "Obadiah",
  "Jonah",
  "Micah",
  "Nahum",
  "Habakkuk",
  "Zephaniah",
  "Haggai",
  "Zechariah",
  "Malachi",
  "Matthew",
  "Mark",
  "Luke",
  "John",
  "Acts",
  "Romans",
  "1 Corinthians",
  "2 Corinthians",
  "Galatians",
  "Ephesians",
  "Philippians",
  "Colossians",
  "1 Thessalonians",
  "2 Thessalonians",
  "1 Timothy",
  "2 Timothy",
  "Titus",
  "Philemon",
  "Hebrews",
  "James",
  "1 Peter",
  "2 Peter",
  "1 John",
  "2 John",
  "3 John",
  "Jude",
  "Revelation"
];

final bibles = [
  Bible(id: 1, name: "KJV"),
  Bible(id: 2, name: "Kannada"),
  Bible(id: 3, name: "Nepali"),
  Bible(id: 4, name: "Hindi"),
  Bible(id: 5, name: "Gujarati"),
  Bible(id: 6, name: "Malayalam"),
  Bible(id: 7, name: "Oriya"),
  Bible(id: 8, name: "Punjabi"),
  Bible(id: 9, name: "Tamil"),
  Bible(id: 10, name: "Telugu"),
  Bible(id: 11, name: "Bengali"),
];