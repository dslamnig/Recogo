package com.slamnig.recog.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.facemesh.FaceMesh
import com.slamnig.recog.util.Aspect

fun DrawScope.drawMeshes(
    meshes: List<FaceMesh>,
    sourceSize: Size,
    flip: Boolean
){
    val a = Aspect(
        sourceSize,
        size,
        flip
    )

    for(mesh in meshes)
        drawMesh(mesh, a)
}

fun DrawScope.drawMesh(
    mesh: FaceMesh,
    a: Aspect
){
    for(triangle in mesh.allTriangles){
        val points = a.transPoints3d(triangle.allPoints)

        connectPoints(points[0], points[1])
        connectPoints(points[1], points[2])
        connectPoints(points[2], points[0])
    }
}

fun DrawScope.connectPoints(start: PointF3D, end: PointF3D)
{
    drawLine(
        color = Color.Cyan,
        start = Offset(start.x, start.y),
        end = Offset(end.x, end.y),
        strokeWidth = 1f
    )
}