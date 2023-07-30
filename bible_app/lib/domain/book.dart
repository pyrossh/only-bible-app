class Book {
  int index;
  String name;
  String localeName;
  List<Chapter> chapters;

  Book(
      {required this.index,
      required this.name,
      required this.localeName,
      required this.chapters});
}

class Chapter {
  int index;
  List<Verse> verses;

  Chapter({required this.index, required this.verses});
}

class Verse {
  int index;
  String text;
  TimeRange audioRange;

  Verse({required this.index, required this.text, required this.audioRange});
}

class TimeRange {
  double start;
  double end;

  TimeRange({required this.start, required this.end});
}

final oldTestament = [
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
];

final newTestament = [
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

final allBooks = oldTestament..addAll(newTestament);