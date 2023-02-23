package com.slamnig.recog.util

import android.graphics.PointF
import androidx.compose.ui.geometry.MutableRect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.facemesh.FaceMeshPoint

class Aspect(val sourceSize: Size, val overlaySize: Size, val flip: Boolean = false)
{
    // screen scaling and offsets:
    val scale: Float
    val xoff: Float
    val yoff: Float

    // get source and overlay dimensions:
    init {
        val ow = overlaySize.width
        val oh = overlaySize.height
        val sw = sourceSize.width
        val sh = sourceSize.height

        // get source and overlay screen ratios:
        val or = ow / oh
        val sr = sw / sh

        // calculate scaling and offsets:
        if (or > sr) {
            scale = oh / sh
            xoff = (ow - sw * scale) / 2
            yoff = 0f
        } else {
            scale = ow / sw
            xoff = 0f
            yoff = (oh - sh * scale) / 2
        }
    }

    fun scale(n: Float): Float
    {
        return n * scale
    }

    fun scale(n: Int): Float
    {
        return scale(n.toFloat())
    }

    fun transX(x: Float): Float
    {
        return x * scale + xoff
    }

    fun transX(x: Int): Float
    {
        val tx = transX(x.toFloat())

        return if(flip == true)
            overlaySize.width - tx
        else
            tx
    }

    fun transY(y: Float): Float
    {
        return y * scale + yoff
    }

    fun transY(y: Int): Float{
        return transY(y.toFloat())
    }

    fun transRect(rect: Rect): Rect
    {
        val r = MutableRect(
            rect.left * scale + xoff,
            rect.top * scale + yoff,
            rect.right * scale + xoff,
            rect.bottom * scale + yoff
        )

        if(flip == true){
            r.left = overlaySize.width - r.left
            r.right = overlaySize.width - r.right
        }

        return r.toRect()
    }

    fun transRect(rect: android.graphics.Rect): Rect
    {
        return transRect(composeRect(rect))
    }

    fun transPoints(points: List<PointF>): List<PointF>
    {
        val tp = points.map{ point ->
            PointF(
                point.x * scale + xoff,
                point.y * scale + yoff
            )
        }

        if(flip == true){
            tp.forEach{ point ->
                point.x = overlaySize.width - point.x
            }
        }

        return tp
    }

    fun transPoints3d(meshpoints: List<FaceMeshPoint>): List<PointF3D>
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

    private fun composeRect(rect: android.graphics.Rect): Rect
    {
        val rectF = rect.toRectF()

        return Rect(
            rectF.left,
            rectF.top,
            rectF.right,
            rectF.bottom
        )
    }
}