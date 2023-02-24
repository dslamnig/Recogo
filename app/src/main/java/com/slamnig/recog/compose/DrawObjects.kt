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
package com.slamnig.recog.activity
*/

package com.slamnig.recog.compose

import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.objects.DetectedObject
import com.slamnig.recog.util.Aspect

private val LOGTAG = "DrawObjects.kt"

private val paintText = Paint().asFrameworkPaint().apply {
    isAntiAlias = true
    color = android.graphics.Color.CYAN
    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    textSize =  50f
}

private val paintBg = Paint().asFrameworkPaint().apply {
    isAntiAlias = true
    color = android.graphics.Color.BLACK
    alpha = 128
}

/**
 * Object detection/tracking graphic overlay.
 */
fun DrawScope.drawObjects(
    objects: List<DetectedObject>,
    sourceSize: Size,
    flip: Boolean
) {
    val a = Aspect(sourceSize, size, flip)

    for(obj in objects){
        drawObject(obj, a)
    }
}

private fun DrawScope.drawObject(
    obj: DetectedObject,
    a: Aspect
){
    val box = a.transRect(obj.boundingBox)
    val x = if(a.flip) box.right else box.left
    val y = box.top

    drawBox(box)

    if(obj.labels.isNotEmpty()) {
        obj.labels.get(0).text.let { text ->
            drawLabel(text, x, y)
        }
    }
}

private fun DrawScope.drawBox(
    box: Rect
){
    drawRect(
        color = Color.Cyan,
        style = Stroke(width = 2.dp.toPx()),
        topLeft = Offset(box.left, box.top),
        size = Size(box.width, box.height)
    )
}

private fun DrawScope.drawLabel(
    text: String,
    x: Float,
    y: Float,
){
    // get text dimensions:
    val metrics = paintText.getFontMetrics()
    val textW = paintText.measureText(text)
    val textH = paintText.textSize

    // adjust text rect:
    val textRect = android.graphics.Rect(
        (x - metrics.leading).toInt(),
        (y - metrics.leading).toInt(),
        (x + textW + metrics.leading).toInt(),
        (y + textH + metrics.descent).toInt()
    )

    // draw background rect and text:
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawRect(textRect, paintBg)
        canvas.nativeCanvas.drawText(text, x, y - metrics.ascent, paintText)
    }
}

