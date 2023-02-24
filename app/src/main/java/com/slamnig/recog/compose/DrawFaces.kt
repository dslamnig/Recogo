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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.slamnig.recog.util.Aspect

private val LOGTAG = "DrawFaces.kt"

/**
 * Face recognition graphic overlay.
 */
fun DrawScope.drawFaces(
    faces: List<Face>,
    sourceSize: Size,
    flip: Boolean
) {
    val a = Aspect(sourceSize, size, flip)

    for(face in faces){
        drawFace(face, a)
    }
}

private fun DrawScope.drawFace(
    face: Face,
    a: Aspect
){
    drawFaceBox(face.boundingBox, a)
    drawFaceContours(face.allContours, a)
}

private fun DrawScope.drawFaceBox(
    box: android.graphics.Rect,
    a: Aspect
)
{
    val boxRect = a.transRect(box)

    drawRect(
        color = Color.Cyan,
        style = Stroke(width = 2.dp.toPx()),
        topLeft = Offset(boxRect.left, boxRect.top),
        size = Size(boxRect.width, boxRect.height)
    )
}

private fun DrawScope.drawFaceContours(contours: MutableList<FaceContour>, a: Aspect)
{
    for(contour in contours.toList()){
        drawFaceContour(contour, a)
    }
}

private fun DrawScope.drawFaceContour(
    contour: FaceContour,
    a: Aspect
)
{
    val points = a.transPoints(contour.points)

    val path = Path()

    path.moveTo(points[0].x, points[0].y)

    for(k in 1..points.size - 1){
        path.lineTo(points[k].x, points[k].y)
    }

    if(contour.faceContourType == FaceContour.FACE){
        // close shape:
        path.lineTo(points[0].x, points[0].y)
    }

    drawPath(path, Color.Magenta, style = Stroke(width = 3.dp.toPx()))
}
