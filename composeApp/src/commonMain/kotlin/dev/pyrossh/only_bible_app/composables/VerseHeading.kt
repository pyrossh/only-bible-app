package dev.pyrossh.only_bible_app.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pyrossh.only_bible_app.FontType
import dev.pyrossh.only_bible_app.screens.ChapterScreenProps
import dev.pyrossh.only_bible_app.utils.SimpleParser
import dev.pyrossh.only_bible_app.utils.TagNode
import dev.pyrossh.only_bible_app.utils.TextNode
import utils.LocalNavController

@Composable
fun VerseHeading(
    text: String,
    fontType: FontType,
    fontSizeDelta: Int,
) {
    val navController = LocalNavController.current
    val nodes = SimpleParser(text).parse()
    val annotatedString = buildAnnotatedString {
        for (n in nodes) {
            if (n is TextNode) {
                append(n.value)
            } else if (n is TagNode && n.name == "br") {
                append("\n")
            } else if (n is TagNode && n.name == "red") {
                withStyle(
                    style = SpanStyle(
                        color = Color.Red,
                    )
                ) {
                    append(n.child!!.value)
                }
            } else if (n is TagNode && n.name == "yellow") {
                withStyle(
                    style = SpanStyle(
                        background = Color.Yellow,
                    )
                ) {
                    append(n.child!!.value)
                }
            } else if (n is TagNode && n.name == "a") {
                withStyle(
                    style = SpanStyle(
                        fontSize = (14 + fontSizeDelta).sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF008AE6),
                    )
                ) {
                    append(n.child!!.value)
                    addStringAnnotation(
                        tag = "URL",
                        annotation = n.attributes["href"]!!,
                        start = n.child!!.pos.start,
                        end = n.child!!.pos.end,
                    )
                }
            }
        }
    }
    ClickableText(
        modifier = Modifier.padding(bottom = 12.dp),
        style = TextStyle(
            fontFamily = fontType.family(),
            fontSize = (16 + fontSizeDelta).sp,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        text = annotatedString,
        onClick = {
            // view.playSoundEffect(SoundEffectConstants.CLICK)
            annotatedString
                .getStringAnnotations("URL", it, it)
                .map { anno ->
                    val parts = anno.item.split(":")
                    navController.navigate(
                        ChapterScreenProps(
                            bookIndex = parts[0].toInt(),
                            chapterIndex = parts[1].toInt(),
                            verseIndex = parts[2].toInt(),
                        )
                    )
                }

        }
    )
}