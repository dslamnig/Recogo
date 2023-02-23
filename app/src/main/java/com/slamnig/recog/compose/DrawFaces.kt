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

fun DrawScope.drawFaces(
    faces: List<Face>,
    sourceSize: Size,
    flip: Boolean
) {
    val a = Aspect(
        sourceSize,
        size,
        flip
        //if(cameraFacing == FRONT_CAMERA) true else false
    )

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
