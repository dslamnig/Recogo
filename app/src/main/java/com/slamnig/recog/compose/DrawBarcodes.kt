package com.slamnig.recog.compose

import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.google.mlkit.vision.barcode.common.Barcode
import com.slamnig.recog.util.Aspect
import kotlin.math.ceil

private val LOGTAG = "DrawBarcodes.kt"

private val paintText = Paint().asFrameworkPaint().apply {
    isAntiAlias = true
    color = android.graphics.Color.CYAN
    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    textSize = 50f
}

private val paintBox = Paint().asFrameworkPaint().apply {
    isAntiAlias = true
    color = android.graphics.Color.CYAN
    style = android.graphics.Paint.Style.STROKE
    strokeWidth = 4f
}

private val paintBg = Paint().asFrameworkPaint().apply {
    isAntiAlias = true
    color = android.graphics.Color.BLACK
    alpha = 128
}

fun DrawScope.drawBarcodes(
    barcodes: List<Barcode>,
    sourceSize: Size
) {
    val a = Aspect(sourceSize, size)

    for(barcode in barcodes){
        barcode.displayValue?.let { fullText ->
            Log.d(LOGTAG, "drawBarcodes() - $fullText")

            barcode.boundingBox?.let { box ->
                // set origin to box top :
                val x = a.transX(box.left)
                val y = a.transY(box.top)
                val w = a.scale(box.width())
                val h = a.scale(box.height())

                // barcode rect:
                val barRect = Rect(x.toInt(), y.toInt(), (x + w).toInt(), (y + h).toInt())

                // get text pixel widths:
                val fullTextWidth = paintText.measureText(fullText)
                val maxTextWidth = size.width - x - 30;

                // split text to fit max width:
                val split: Pair<Float, List<String>> =
                    // if width too small, revert to single line:
                    if(maxTextWidth < size.width / 2)
                        Pair(fullTextWidth, listOf(fullText))
                    else {
                        splitText(
                            fullText,
                            fullTextWidth,
                            maxTextWidth
                        )
                    }

                val textWidth = split.first
                val lines = split.second
                val metrics = paintText.getFontMetrics()
                // set text pixel heights:
                val lineHeight = paintText.textSize + metrics.descent
                val textHeight = lines.size * lineHeight

                // center text vertically in box:
                val textStartY = y + (h - textHeight) / 2

                // adjust text rect:
                val textRect = Rect(
                    (x - metrics.leading).toInt(),
                    (textStartY - metrics.leading).toInt(),
                    (x + textWidth + metrics.leading).toInt(),
                    (textStartY + textHeight + metrics.descent).toInt()
                )

                drawIntoCanvas { canvas ->
                    // barcode box:
                    canvas.nativeCanvas.drawRect(barRect, paintBox)
                    // text background:
                    canvas.nativeCanvas.drawRect(textRect, paintBg)

                    var textY = textStartY

                    // text lines:
                    for(s in lines){
                        canvas.nativeCanvas.drawText(s, x, textY - metrics.ascent, paintText)
                        textY += lineHeight
                    }
                }
            }
        }
    }
}

// Split text into lines that fit maximum width:
private fun splitText(text: String, textWidth: Float, maxWidth: Float): Pair<Float, List<String>>
{
    val finalLines = mutableListOf<String>()

    // of all text fits in max width, return that:
    if(textWidth <= maxWidth){
        finalLines.add(text)
        return Pair(textWidth, finalLines)
    }

    // calculate max character count in line:
    val fullLength = text.length
    val lineLength = ceil((fullLength.toFloat() * maxWidth / textWidth)).toInt() - 1

    // split text into elements, keeping delimiters in result:
    val delim = "\\Q <([{^\\-=/$!|]})?*+.:;\"'>\\E"
    val regex = Regex("(?<=[$delim])|(?=[$delim])")
    val lines1 = text.split(regex)
    val lines2 = mutableListOf<String>()
    var line = ""

    // split elements that are too long:
    for(s in lines1){
        if(s.length <= lineLength)
            lines2.add(s)
        else{
            var ss = s
            do{
                lines2.add(ss.substring(0, lineLength))
                ss = ss.substring(lineLength)
            }while(ss.length > lineLength)

            if(ss.length > 0)
                lines2.add(ss)
        }
    }

    // add elements to line
    for(s in lines2){
        // if limit reached, add line to final list and clear:
        if(line.length + s.length >= lineLength){
            finalLines.add(line.trim())
            line = ""
        }

        // add element to line:
        line += s
    }

    // add last line to final list:
    finalLines.add(line.trim())

    return Pair(maxWidth, finalLines)
}

