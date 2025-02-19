package com.funny.translation.ui

/**
 * https://github.com/jeziellago/compose-markdown
 */

// TODO Change it to
// to get more customized
// https://github.com/takahirom/jetpack-compose-markdown/blob/master/app/src/main/java/com/github/takahirom/jetpackcomposemarkdown/Markdown.kt

import android.content.Context
import android.graphics.Paint
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import coil.ImageLoader
import com.funny.translation.WebViewActivity
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.commonmark.node.Node

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    selectable: Boolean = false,
    @FontRes fontResource: Int? = null,
    style: TextStyle = LocalTextStyle.current,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null,
    // this option will disable all clicks on links, inside the markdown text
    // it also enable the parent view to receive the click event
    disableLinkMovementMethod: Boolean = false,
    imageLoader: ImageLoader? = null,
    onLinkClicked: ((Context, String) -> Unit)? = { context, url ->
        // default behavior is to open the link in the browser
        WebViewActivity.start(context, url)
    },
    onTextLayout: ((numLines: Int) -> Unit)? = null
) {
    val defaultColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    val context: Context = LocalContext.current
    val markdownRender: Markwon = remember { createMarkdownRender(context, imageLoader, onLinkClicked) }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            createTextView(
                context = ctx,
                color = color,
                defaultColor = defaultColor,
                fontSize = fontSize,
                fontResource = fontResource,
                maxLines = maxLines,
                style = style,
                textAlign = textAlign,
                viewId = viewId,
                onClick = onClick,
                selectable = selectable
            )
        },
        update = { textView ->
            markdownRender.setMarkdown(textView, markdown)
            if (disableLinkMovementMethod) {
                textView.movementMethod = null
            }
            if (onTextLayout != null) {
                textView.post {
                    onTextLayout(textView.lineCount)
                }
            }
            textView.maxLines = maxLines
        }
    )
}

private fun createTextView(
    context: Context,
    color: Color = Color.Unspecified,
    defaultColor: Color,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null,
    selectable: Boolean = false
): TextView {

    val textColor = color.takeOrElse { style.color.takeOrElse { defaultColor } }
    val mergedStyle = style.merge(
        TextStyle(
            color = textColor,
            fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
            textAlign = textAlign ?: style.textAlign,
        )
    )
    return TextView(context).apply {
        onClick?.let { setOnClickListener { onClick() } }
        setTextColor(textColor.toArgb())
        setMaxLines(maxLines)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, mergedStyle.fontSize.value)
        if (selectable) {
            setTextIsSelectable(true)
            movementMethod = BetterLinkMovementMethod.getInstance();
        }

        viewId?.let { id = viewId }
        textAlign?.let { align ->
            textAlignment = when (align) {
                TextAlign.Left, TextAlign.Start -> View.TEXT_ALIGNMENT_TEXT_START
                TextAlign.Right, TextAlign.End -> View.TEXT_ALIGNMENT_TEXT_END
                TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                else -> View.TEXT_ALIGNMENT_TEXT_START
            }
        }

        if (mergedStyle.textDecoration == TextDecoration.LineThrough) {
            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        fontResource?.let { font ->
            typeface = ResourcesCompat.getFont(context, font)
        }
    }
}

private fun createMarkdownRender(
    context: Context,
    imageLoader: ImageLoader?,
    onLinkClicked: ((Context, String) -> Unit)? = null
): Markwon {
    val coilImageLoader = imageLoader ?: ImageLoader.Builder(context)
        .apply {
            crossfade(true)
        }.build()

    return Markwon.builder(context)
        .usePlugin(HtmlPlugin.create())
        .usePlugin(CoilImagesPlugin.create(context, coilImageLoader))
        .usePlugin(SoftBreakAddsNewLinePlugin())
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TablePlugin.create(context))
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(object: AbstractMarkwonPlugin() {
            override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                builder.linkResolver { _, link ->
                    // handle individual clicks on Textview link
                    onLinkClicked?.invoke(context, link)
                }
            }
        })
        .usePlugin(LessSpacingAfterHeadingAndListPlugin)
        .build()
}

private val LessSpacingAfterHeadingAndListPlugin = object : AbstractMarkwonPlugin() {
    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {

    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.blockHandler(object : MarkwonVisitor.BlockHandler {
            override fun blockStart(visitor: MarkwonVisitor, node: Node) {
                visitor.ensureNewLine()
            }

            override fun blockEnd(visitor: MarkwonVisitor, node: Node) {
                if (visitor.hasNext(node)) {
                    visitor.ensureNewLine()
                }
            }
        })
    }
}

@Preview
@Composable
private fun PreviewMDText() {
    val markdown = "### 注音  \nhəˈlō  \n### 定义  \n**惊叹词**   \n- used as a greeting or to begin a phone conversation.  \n  - hello there, Katie!  \n  \n**名词**   \n- an utterance of “hello”; a greeting.  \n  - she was getting polite nods and hellos from people  \n  \n**动词**   \n- say or shout “hello”; greet someone.  \n  - I pressed the phone button and helloed  \n"
    MarkdownText(markdown = markdown)

}