import "package:flutter/material.dart";
import 'package:only_bible_app/widgets/book_selector.dart';
import 'package:only_bible_app/state.dart';

class SideMenuPage extends ModalRoute<void> {
  @override
  Duration get transitionDuration => const Duration(milliseconds: 300);

  @override
  Duration get reverseTransitionDuration => Duration.zero;

  @override
  bool get opaque => false;

  @override
  bool get barrierDismissible => true;

  @override
  Color get barrierColor => Colors.black.withOpacity(0.7);

  @override
  String? get barrierLabel => "Route";

  @override
  bool get maintainState => false;

  @override
  Widget buildPage(
    BuildContext context,
    Animation<double> animation,
    Animation<double> secondaryAnimation,
  ) {
    return Material(
      type: MaterialType.transparency,
      child: Container(
        color: Theme.of(context).colorScheme.background,
        margin: EdgeInsets.only(left: 0, right: isWide(context) ? 650 : 0),
        child: isWide(context)
            ? const BookSelector()
            : SlideTransition(
                position: Tween(begin: const Offset(-1, 0), end: Offset.zero).animate(animation),
                child: const BookSelector(),
              ),
      ),
    );
  }
}