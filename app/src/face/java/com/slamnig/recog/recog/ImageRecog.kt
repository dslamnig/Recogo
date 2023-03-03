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

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.interfaces.Detector
import com.slamnig.recog.*
import com.slamnig.recog.viewmodel.ImageRecogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Still image recognizer - face  and face mesh.
 */
@Suppress("UNCHECKED_CAST")
class ImageRecog(viewModel: ImageRecogViewModel) : BaseImageRecog(viewModel = viewModel)
{
    private val LOGTAG = this.javaClass.simpleName

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun setRecogMode(recogMode: Int)
    {
        super.setRecogMode(recogMode)

        detector = when (mode) {
            RECOG_FACE_BOX ->
                initFaceBox()
            RECOG_FACE_CONTOURS ->
                initFaceContours()
            else ->
                null
        }
    }

    private fun initFaceBox(): Detector<Any>
    {
        val options = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()

        return FaceDetection.getClient(options) as Detector<Any>
    }

    private fun initFaceContours(): Detector<Any>
    {
        val options = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()

        return FaceDetection.getClient(options) as Detector<Any>
    }

    override fun recog(image: InputImage)
    {
        detector?.apply {
            when(this){
                is FaceDetector ->
                    process(image)
                        .addOnSuccessListener { faces ->
                            faces?.let {
                                viewModel.setFaces(it.filterNotNull())
                            }
                        }
            }
        }
    }
}