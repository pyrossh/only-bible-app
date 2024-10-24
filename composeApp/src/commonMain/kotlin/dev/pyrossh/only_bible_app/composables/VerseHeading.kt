package dev.pyrossh.only_bible_app.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pyrossh.only_bible_app.FontType
import utils.LocalNavController

@Composable
fun VerseHeading(
    text: String,
    fontType: FontType,
    fontSizeDelta: Int,
) {
    val navController = LocalNavController.current
    Text(
        modifier = Modifier.padding(bottom = 12.dp),
        style = TextStyle(
            fontFamily = fontType.family(),
            fontSize = (16 + fontSizeDelta).sp,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        text = text,
//        text = AnnotatedString.fromHtml(
//            htmlString = text,
//            linkStyles = TextLinkStyles(
//                style = SpanStyle(
//                    fontSize = (14 + fontSizeDelta).sp,
//                    fontStyle = FontStyle.Italic,
//                    color = Color(0xFF008AE6),
//                )
//            ),
//            linkInteractionListener = {
////                view.playSoundEffect(SoundEffectConstants.CLICK)
//                val url = (it as LinkAnnotation.Url).url
//                val parts = url.split(":")
//                navController.navigate(
//                    ChapterScreenProps(
//                        bookIndex = parts[0].toInt(),
//                        chapterIndex = parts[1].toInt(),
//                        verseIndex = parts[2].toInt(),
//                    )
//                )
//            },
//        ),
    )
}