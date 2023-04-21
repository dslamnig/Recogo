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

package com.slamnig.recog.recog

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.util.Size
import androidx.camera.view.PreviewView
import androidx.lifecycle.Observer
import com.google.mlkit.vision.camera.CameraSourceConfig
import com.google.mlkit.vision.camera.CameraXSource
import com.google.mlkit.vision.camera.DetectionTaskCallback
import com.google.mlkit.vision.interfaces.Detector
import com.slamnig.recog.*
import com.slamnig.recog.camera.CameraSourceConfigX
import com.slamnig.recog.camera.CameraXSourceX
import com.slamnig.recog.viewmodel.LiveRecogViewModel

/**
 * Live camera recognizer.
 */
@Suppress("UNCHECKED_CAST")
open class BaseLiveRecog(val viewModel: LiveRecogViewModel, val preview: PreviewView, val startFacing: Int)
{
    private val LOGTAG = this.javaClass.simpleName

    companion object {
        const val CAM_SOURCE = 0   // CameraXSource
        const val CAM_SOURCE_X = 1 // CameraXSourceX
        // flag that determines whether to use CameraXSource or CameraXSourceX:
        const val camSource = CAM_SOURCE_X
    }

    var cameraSource: CameraXSource? = null
    var cameraSourceX: CameraXSourceX? = null

    var zoom = 1.0f

    private var switchCameraObserver: Observer<Unit>
    private var zoomObserver: Observer<Float>

    var mode = RECOG_TEXT
    var cameraFacing = startFacing

    init{
        viewModel.setCameraFacing(cameraFacing)

        switchCameraObserver = Observer<Unit>(){
            // toggle front/back camera:
            cameraFacing =
                when(cameraFacing){
                    BACK_CAMERA ->
                        FRONT_CAMERA
                    else ->
                        BACK_CAMERA
                }
            viewModel.setCameraFacing(cameraFacing)
            restart()
        }.also{ observer ->
            viewModel.switchCamera.observeForever(observer)
        }

        // Zoom works only with CameraXSourceX:
        zoomObserver = Observer<Float>(){ gestureZoom ->
            if(gestureZoom < 0.0f){
                // animate to min zoom:
                ValueAnimator.ofFloat(zoom, 1.0f).apply {
                    duration = 200

                    addUpdateListener { updatedAnimation ->
                        zoom = updatedAnimation.animatedValue as Float
                        applyZoom()
                    }

                    start()
                }
            }
            else{
                zoom = (zoom * gestureZoom).coerceIn(1.0f..5.0f)
                applyZoom()
            }

            applyZoom()
        }.also{ observer ->
            viewModel.zoom.observeForever(observer)
        }
    }

    fun init(recogMode: Int)
    {
        stop()
        close()
        setRecogMode(recogMode)
    }

    @SuppressLint("MissingPermission")
    fun start()
    {
        if(camSource == CAM_SOURCE_X)
            cameraSourceX?.start()
        else
            cameraSource?.start()
    }

    fun stop()
    {
        if(camSource == CAM_SOURCE_X)
            cameraSourceX?.stop()
        else
            cameraSource?.stop()
    }

    fun close()
    {
        if(camSource == CAM_SOURCE_X){
            cameraSourceX?.close()
            cameraSourceX = null
        }
        else {
            cameraSource?.close()
            cameraSource = null
        }
    }

    fun restart()
    {
        init(mode)
        start()
    }

    fun destroy()
    {
        stop()
        close()

        viewModel.switchCamera.removeObserver(switchCameraObserver)
        viewModel.zoom.removeObserver(zoomObserver)
    }

    open fun setRecogMode(recogMode: Int) {
        mode = recogMode
        viewModel.setShowCameraSwitch(true)
    }

    open fun setCameraSource(
        detector: Detector<Any>,
        callback: DetectionTaskCallback<Any>
    ){
        Log.d(LOGTAG, "setCameraSource()")

        val orientation = preview.context.resources.configuration.orientation

        val size = if(orientation == Configuration.ORIENTATION_LANDSCAPE)
            Size(650, 400)
        else
            Size(400, 650)

        if(camSource == CAM_SOURCE_X){
            CameraSourceConfigX.Builder(
                preview.context,
                detector,
                callback
            )
                .setFacing(cameraFacing)
                .setRequestedPreviewSize(size.width, size.height)
                .build()
                .let { config ->
                    cameraSourceX = CameraXSourceX(config, preview)

                    // set initial zoom:
                    preview.post{
                        applyZoom()
                    }
                }
        }
        else {
            CameraSourceConfig.Builder(
                preview.context,
                detector,
                callback
            )
                .setFacing(cameraFacing)
                .setRequestedPreviewSize(size.width, size.height)
                .build()
                .let { config ->
                    cameraSource = CameraXSource(config, preview)
                }
        }
    }

    open fun getPreviewSize(): Size?
    {
        if(cameraSource == null && cameraSourceX == null)
            return null
        else {
            val size = if(camSource == CAM_SOURCE_X) cameraSourceX!!.previewSize else cameraSource!!.previewSize
            var tsize = size

            if(size != null){
                preview.viewPort?.rotation.let { rotation ->
                    if (rotation == 0 || rotation == 2) {
                        // switch w/h for portrait:
                        tsize = Size(size.height, size.width)
                    }
                }
            }
            else
                Log.w(LOGTAG, "getPreviewSize() - size null")

            return tsize
        }
    }

    // Zoom works only with CameraXSourceX:
    private fun applyZoom()
    {
        cameraSourceX?.camera?.cameraControl?.setZoomRatio(zoom)
    }
}