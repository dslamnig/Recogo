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

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Gesture zoom and drag.
 * Tap restores default scale and position.
 */
@Composable
fun gestureTransformModifier(): Modifier
{
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var animate by remember { mutableStateOf(false) }

    val animScale: Float by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        )
    )

    val animOffset: Offset by animateOffsetAsState(
        targetValue = offset,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        )
    )

    return Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, _ ->
                    val oldScale = scale
                    val newScale = (scale * gestureZoom).coerceIn(0.5f..5f)
                    // don't animate transform:
                    animate = false

                    // For natural zooming and rotating, the centroid of the gesture should
                    // be the fixed point where zooming and rotating occurs.
                    // We compute where the centroid was (in the pre-transformed coordinate
                    // space), and then compute where it will be after this delta.
                    // We then compute what the new offset should be to keep the centroid
                    // visually stationary for rotating and zooming, and also apply the pan.
                    offset = (offset + gestureCentroid / oldScale) -
                            (gestureCentroid / newScale + gesturePan / oldScale)
                    scale = newScale
                }
            )
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    // animate transform:
                    animate = true
                    scale = 1f
                    offset = Offset.Zero
                }
            )
        }
        .graphicsLayer {
            // animate only on tap:
            val off = if(animate) animOffset else offset
            val zoom = if(animate) animScale else scale

            TransformOrigin(0f, 0f).also { transformOrigin = it }
            translationX = -off.x * zoom
            translationY = -off.y * zoom
            scaleX = zoom
            scaleY = zoom
        }
}