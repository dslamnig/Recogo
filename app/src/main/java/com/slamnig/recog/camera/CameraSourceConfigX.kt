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

package com.slamnig.recog.camera

import android.content.Context
import android.util.Size
import com.google.mlkit.vision.camera.DetectionTaskCallback
import com.google.mlkit.vision.interfaces.Detector

/**
    CameraSourceConfig clone for CameraXSourceX
 */
class CameraSourceConfigX (
    val context: Context,
    val detectorProc: DetectorWithProcessorX,
    val cameraFacing: Int = CAMERA_FACING_BACK,
    val requestedPreviewSize: Size? = null
){
    companion object {
        const val CAMERA_FACING_BACK = 1
        const val CAMERA_FACING_FRONT = 0

        const val DEFAULT_PREVIEW_WIDTH = 480
        const val DEFAULT_PREVIEW_HEIGHT = 360
    }

    class Builder(
         val context: Context,
         val detector: Detector<Any>,
         val detectionTaskCallback: DetectionTaskCallback<Any>
    ){
        private val detectorWithProc = DetectorWithProcessorX(detector, detectionTaskCallback)
        private var cameraFacing = CAMERA_FACING_BACK
        private var requestedPreviewSize: Size? = null

        fun setFacing(facing: Int): Builder
        {
            cameraFacing = facing
            return this
        }

        fun setRequestedPreviewSize(width: Int, height: Int): Builder
        {
            requestedPreviewSize = Size(width, height)
            return this
        }

        fun build(): CameraSourceConfigX
        {
            return CameraSourceConfigX(context, detectorWithProc, cameraFacing, requestedPreviewSize)
        }
    }
}