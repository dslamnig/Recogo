/*
Copyright 2023. Davor Slamnig

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.slamnig.recog.compose

import android.graphics.Point
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.google.mlkit.vision.text.Text
import com.slamnig.recog.graph.Vector2
import com.slamnig.recog.graph.Aspect

private val LOGTAG = "DrawText.kt"

private val paintText = Paint().asFrameworkPaint().apply {
    isAntiAlias = true
    color = 0xff00ffff.toInt()
    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    textSize = 30f
}

private val paintBg = Paint().asFrameworkPaint().apply {
    isAntiAlias = true
    color = android.graphics.Color.BLACK
    alpha = 128
}

/**
 * Text detection graphic overlay.
 */
fun DrawScope.drawText(
    text: Text,
    sourceSize: Size,
    flip: Boolean
) {
    val a = Aspect(sourceSize, size, flip)

    // process lines:
    for(block in text.textBlocks){
        for(line in block.lines){
            line.boundingBox?.let { box ->
                // get text size based on corners distance:
                val cornerSize = getCornerSize(line.cornerPoints, a.scale)

                // fit text size to box width:
                fitTextSize(paintText, a.scale(box.width()), line.text)

                // if text size too large, use corner size:
                if(paintText.textSize > cornerSize * 1.5f)
                    paintText.textSize = cornerSize

                // set origin to box top :
                val transBox = a.transRect(box)
                val x = transBox.left
                val y = transBox.top

                //val x = a.transX(box.left)
                //val y = a.transY(box.top)

                // set text dimensions:
                val metrics = paintText.getFontMetrics()
                val textW = paintText.measureText(line.text)
                val textH = paintText.textSize

                // adjust text rect:
                val textRect = Rect(
                    (x - metrics.leading).toInt(),
                    (y - metrics.leading).toInt(),
                    (x + textW + metrics.leading).toInt(),
                    (y + textH + metrics.descent).toInt()
                )

                // draw background rect and text:
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawRect(textRect, paintBg)
                    canvas.nativeCanvas.drawText(line.text, x, y - metrics.ascent, paintText)
                }
            }
        }
    }
}

private fun getCornerSize(
    corners: Array<Point>?, mult: Float
): Float {
    if(corners != null){
        val top = Vector2(corners[0]).scale(mult)
        val bot = Vector2(corners[3]).scale(mult)

        return Vector2.getDist(top, bot)
    }
    else
        return 30f
}

private fun fitTextSize(
    paint: NativePaint,
    width: Float,
    text: String
){
    paint.textSize = 30f
    val curWidth = paint.measureText(text)
    paint.textSize = width / curWidth * paint.textSize
}
