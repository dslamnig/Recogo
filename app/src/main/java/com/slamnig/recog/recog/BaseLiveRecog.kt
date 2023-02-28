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

import android.annotation.SuppressLint
import android.util.Log
import android.util.Size
import androidx.camera.view.PreviewView
import androidx.lifecycle.Observer
import com.google.mlkit.vision.camera.CameraSourceConfig
import com.google.mlkit.vision.camera.CameraXSource
import com.google.mlkit.vision.camera.DetectionTaskCallback
import com.google.mlkit.vision.interfaces.Detector
import com.slamnig.recog.*
import com.slamnig.recog.viewmodel.LiveRecogViewModel

/**
 * Live camera recognizer.
 */
@Suppress("UNCHECKED_CAST")
open class BaseLiveRecog(val viewModel: LiveRecogViewModel, val preview: PreviewView, val startFacing: Int)
{
    private val LOGTAG = this.javaClass.simpleName

    var cameraSource: CameraXSource? = null
    private var switchCameraObserver: Observer<Unit>
    var mode = RECOG_TEXT
    var cameraFacing = startFacing

    init{
        viewModel.setCameraFacing(cameraFacing)

        switchCameraObserver = Observer<Unit>(){
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
    }

    fun init(recogMode: Int)
    {
        stop()
        close()

        /*
        // if text or barcode recognition, force back camera:
        if(recogMode == RECOG_TEXT || recogMode == RECOG_BARCODE && cameraFacing == FRONT_CAMERA) {
            cameraFacing = BACK_CAMERA
            viewModel.setCameraFacing(cameraFacing)
        }
        */

        setRecogMode(recogMode)
    }

    @SuppressLint("MissingPermission")
    fun start()
    {
        cameraSource?.start()
    }

    fun stop()
    {
        cameraSource?.stop()
    }

    fun close()
    {
        cameraSource?.close()
        cameraSource = null
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
    }

    open fun setRecogMode(recogMode: Int) {
        mode = recogMode

        viewModel.setShowCameraSwitch(true)
        /*
            // back camera only for text and barcode:
            viewModel.setShowCameraSwitch(
                when(mode){
                    RECOG_TEXT, RECOG_BARCODE ->
                        false
                    else ->
                        true
                }
            )
            */
    }

    open fun setCameraSource(
        detector: Detector<Any>,
        callback: DetectionTaskCallback<Any>
    ){
        CameraSourceConfig.Builder(
            preview.context,
            detector,
            callback
        )
            .setFacing(cameraFacing)
            .build()
            .let { config ->
                cameraSource = CameraXSource(config, preview)
            }
    }

    open fun getPreviewSize(): Size?
    {
        if(cameraSource == null)
            return null
        else {
            val size = cameraSource!!.previewSize
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
}