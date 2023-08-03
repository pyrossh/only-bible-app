import "package:flutter/material.dart";
import "package:flutter_reactive_value/flutter_reactive_value.dart";
import "../state.dart";

class VerseText extends StatelessWidget {
  final int index;
  final String text;

  const VerseText({super.key, required this.index, required this.text});

  @override
  Widget build(BuildContext context) {
    var selected = selectedVerses.reactiveValue(context).contains(index);
    onTap() {
      onVerseSelected(index);
    }
    return MouseRegion(
        cursor: SystemMouseCursors.click,
        child: GestureDetector(
          onTap: onTap,
          child: Container(
            decoration: BoxDecoration(
              color: selected ? theme.value.highlightColor : Colors.white,
            ),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  margin: const EdgeInsets.only(right: 4),
                  child:  Transform.translate(
                    offset: const Offset(0, 2),
                    child: Text("${index + 1}", style: theme.value.labelText),
                  ),
                ),
                Flexible(
                  child: Text(text, style: theme.value.bodyText),
                )
              ],
            ),
          ),
        ));
  }
}
