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

import android.graphics.Point
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.toPointF

/**
 * 2D Vector class.
 */
class Vector2(var x: Float = 0f, var y: Float = 0f)
{
    private val LOGTAG = this::class.java.simpleName

    companion object{
        fun sub(v1: Vector2, v2: Vector2): Vector2
        {
            return Vector2(v1).sub(v2)
        }

        fun getDist(v1: Vector2, v2: Vector2): Float
        {
            return sub(v1, v2).length();
        }

        // clockwise angle between a and b:
        fun getAngle2(a: Vector2, b: Vector2): Float
        {
            val an = a.normalize();
            val bn = b.normalize();

            return Math.atan2(an.crossProduct(bn).toDouble(), an.dotProduct(bn).toDouble()).toFloat();
        }

        // clockwise angle between ab and ac:
        fun getAngle2(a: Vector2, b: Vector2, c: Vector2): Float
        {
            return getAngle2(b.sub(a), c.sub(a))
        }
    }

    constructor(v: Vector2): this(){
        x = v.x
        y = v.y
    }

    constructor(p: Point): this(){
        val fp = p.toPointF()
        x = fp.x
        y = fp.y
    }

    fun set(_x: Float, _y: Float)
    {
        x = _x;
        y = _y;
    }

    fun set(v: Vector2)
    {
        x = v.x
        y = v.y
    }

    fun length(): Float
    {
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    fun normalize(): Vector2
    {
        val v2 = Vector2();

        val length = Math.sqrt((x * x + y * y).toDouble()).toFloat();

        if(length != 0f){
            v2.x = x / length;
            v2.y = y / length;
        }

        return v2;
    }

    fun scale(scaleFactor: Float): Vector2
    {
        return Vector2(x * scaleFactor, y * scaleFactor)
    }

    fun sub(v: Vector2): Vector2
    {
        return Vector2(x - v.x, y - v.y)
    }

    fun dotProduct(v: Vector2): Float
    {
        return x * v.x + y * v.y;
    }

    fun crossProduct(v: Vector2): Float
    {
        return x * v.y - y * v.x;
    }

    fun angleTranslate(angle: Float, distance: Float)
    {
        // zero angle to positive x:
        x += (Math.cos(angle.toDouble()) * distance).toFloat()
        y += (Math.sin(angle.toDouble()) * distance).toFloat()

        // zero angle to positive y:
        // x += (Math.sin(angle.toDouble()) * distance).toFloat()
        // y += (Math.cos(angle.toDouble()) * distance).toFloat()
    }

    fun offset() : Offset
    {
        return Offset(x, y)
    }
}