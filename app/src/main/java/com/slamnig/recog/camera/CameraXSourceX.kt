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

import android.util.Log
import android.util.Size
import androidx.annotation.RequiresPermission
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CameraXSourceX(
    val config: CameraSourceConfigX,
    val previewView: PreviewView? = null
): LifecycleOwner
{
    // no preview constructor:
    constructor(config: CameraSourceConfigX): this(config, null)

    private val LOGTAG = this.javaClass.simpleName

    private val context = config.context
    private val cameraFacing = config.cameraFacing
    private val requestedPreviewSize = config.requestedPreviewSize
    private val detectorProc = config.detectorProc

    // will be set to analyzer ImageProxy dimensions:
    var previewSize = requestedPreviewSize
        ?: Size(CameraSourceConfigX.DEFAULT_PREVIEW_WIDTH, CameraSourceConfigX.DEFAULT_PREVIEW_HEIGHT)

    var camera: Camera? = null

    private val registry = LifecycleRegistry(this)
    private var preview: Preview? = null

    init {
        Log.d(LOGTAG, "init")

        registry.setCurrentState(Lifecycle.State.INITIALIZED)
        registry.setCurrentState(Lifecycle.State.CREATED)

        startCamera()
    }

    override fun getLifecycle(): Lifecycle {
        return registry
    }

    @RequiresPermission("android.permission.CAMERA")
    fun start()
    {
        if (registry.getCurrentState() != Lifecycle.State.STARTED) {
            if (registry.getCurrentState() != Lifecycle.State.CREATED) {
                val e = IllegalStateException("Camera has not been created or has already been closed.")
                throw e
            } else {
                registry.setCurrentState(Lifecycle.State.STARTED)
            }
        }
    }

    fun stop()
    {
        if (registry.getCurrentState() == Lifecycle.State.CREATED) {
            Log.d(LOGTAG, "stop() - already in the CREATE state")
        } else if (registry.getCurrentState() == Lifecycle.State.STARTED) {
            registry.setCurrentState(Lifecycle.State.CREATED)
        }
    }

    fun close()
    {
        stop()

        if (registry.getCurrentState() == Lifecycle.State.CREATED) {
            registry.setCurrentState(Lifecycle.State.DESTROYED)
            Log.d(LOGTAG, "close()")
            camera = null
            detectorProc.close()
        }
    }

    private fun initPreview() {
        val builder = Preview.Builder()

        requestedPreviewSize?.let {
            builder.setTargetResolution(it)
        }

        preview = builder.build()

        previewView?.let {
            preview?.setSurfaceProvider(it.surfaceProvider)
        }
    }

    private fun startCamera()
    {
        Log.d(LOGTAG, "startCamera()")

        // ImageProcessor.processImageProxy will use another thread to run the detection underneath,
        // so we can run the analyzer itself on main thread:
        val cameraExecutor = ContextCompat.getMainExecutor(context)

        if(previewView != null)
            initPreview()

        val builder = ImageAnalysis.Builder()

        requestedPreviewSize?.let{
            builder.setTargetResolution(requestedPreviewSize)
        }

        val imageAnalyzer = builder.build()
            .also { analysis ->
                cameraExecutor?.let { exec ->
                    analysis.setAnalyzer(
                        exec
                    ) { imageProxy ->
                        imageProxy?.let { imgp ->
                            // set preview size here:
                            previewSize = Size(imgp.width, imgp.height)
                            processImageProxy(imgp)
                        }
                    }
                }
           }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(
                if(cameraFacing == CameraSourceConfigX.CAMERA_FACING_BACK)
                    CameraSelector.LENS_FACING_BACK
                else
                    CameraSelector.LENS_FACING_FRONT
            )
            .build()

        lifecycleScope.launch {
            val cameraProvider = ProcessCameraProvider
                .getInstance(context)
                .await()

            try {
                cameraProvider.unbindAll() // unbind all usecases

                camera = if(preview != null) {
                    // with preview:
                    cameraProvider.bindToLifecycle(
                        this@CameraXSourceX,
                        cameraSelector,
                        imageAnalyzer,
                        preview
                    )
                }
                else{
                    // without preview:
                    cameraProvider.bindToLifecycle(
                        this@CameraXSourceX,
                        cameraSelector,
                        imageAnalyzer
                    )
                }

            } catch (e: Exception) {
                Log.e(LOGTAG, "Error: binding usecases $e")
            }

            Log.d(LOGTAG, "camera: $camera")
            Log.d(LOGTAG, "cameraControl: ${camera?.cameraControl}")
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy)
    {
        // camera input now paused
        imageProxy.image?.let {
            detectorProc.process(it, imageProxy.imageInfo.rotationDegrees)
                ?.addOnCompleteListener{
                    // release imageProxy to reenable camera input:
                    imageProxy.close()
                }
        }
    }
}