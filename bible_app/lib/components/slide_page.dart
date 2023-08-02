import "package:flutter/material.dart";
import "./book_selector.dart";

import "../state.dart";

class SlidePage extends ModalRoute<void> {
  @override
  Duration get transitionDuration => const Duration(milliseconds: 300);

  @override
  Duration get reverseTransitionDuration => const Duration(milliseconds: 300);

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
        color: Colors.white,
        margin: EdgeInsets.only(left: 0, right: isDesktop() ? 650 : 0),
        child: BookSelector(),
      ),
    );
  }

  @override
  Widget buildTransitions(
      BuildContext context,
      Animation<double> animation,
      Animation<double> secondaryAnimation,
      Widget child,
      ) {
    return SlideTransition(
      position: Tween(begin: const Offset(-1, 0), end: Offset.zero).animate(animation),
      child: child,
    );
  }
}