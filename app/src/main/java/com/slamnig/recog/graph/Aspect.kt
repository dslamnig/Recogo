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

package com.slamnig.recog.graph

import android.graphics.PointF
import androidx.compose.ui.geometry.MutableRect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.common.PointF3D

/**
 * Screen coordinate transformations.
 */
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
            // flip x coordinates _and_ rectangle left <-> right
            val left = overlaySize.width - r.left
            val right = overlaySize.width - r.right
            r.left = right
            r.right = left
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