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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshPoint
import com.slamnig.recog.graph.Aspect

// extend Aspect to handle mesh points:
fun Aspect.transMeshPoints(meshpoints: List<FaceMeshPoint>): List<PointF3D>
{
    val tp = meshpoints.map{ meshpoint ->
        val point = meshpoint.position
        val tx = point.x * scale + xoff
        val x = if(flip == false) tx else overlaySize.width - tx
        val y = point.y * scale + yoff
        val z = point.z * scale
        PointF3D.from(x, y, z)
    }

    return tp
}

/**
 * Face mesh recognition overlay.
 */
fun DrawScope.drawMeshes(
    meshes: List<FaceMesh>,
    sourceSize: Size,
    flip: Boolean
){
    val a = Aspect(sourceSize, size, flip)

    for(mesh in meshes)
        drawMesh(mesh, a)
}

fun DrawScope.drawMesh(
    mesh: FaceMesh,
    a: Aspect
){
    for(triangle in mesh.allTriangles){
        val points = a.transMeshPoints(triangle.allPoints)

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