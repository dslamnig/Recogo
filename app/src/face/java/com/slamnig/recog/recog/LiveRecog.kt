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

package com.slamnig.recog.recog

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.camera.DetectionTaskCallback
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import com.google.mlkit.vision.interfaces.Detector
import com.slamnig.recog.*
import com.slamnig.recog.viewmodel.LiveRecogViewModel

/**
 * Live camera recognizer - full.
 */
@Suppress("UNCHECKED_CAST")
class LiveRecog(viewModel: LiveRecogViewModel, preview: PreviewView, startFacing: Int)
    : BaseLiveRecog(viewModel = viewModel, preview = preview, startFacing = startFacing)
{
    private val LOGTAG = this.javaClass.simpleName

    override fun setRecogMode(recogMode: Int)
    {
        super.setRecogMode(recogMode)

        when (mode) {
            RECOG_FACE_BOX ->
                initFaceBox()
            RECOG_FACE_CONTOURS ->
                initFaceContours()
            RECOG_FACE_MESH ->
                initFaceMesh()
        }
    }

    private fun initFaceBox()
    {
        val options = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()

        initFace(options)
    }

    private fun initFaceContours()
    {
        val options = FaceDetectorOptions.Builder()
            // this kills boxes, shows contour for only one face:
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()

        initFace(options)
    }

    private fun initFace(options: FaceDetectorOptions)
    {
        setCameraSource(
            FaceDetection.getClient(options) as Detector<Any>,
            DetectionTaskCallback<List<Face>>() { task ->
                task.addOnSuccessListener { list ->
                    list?.let { faceList ->
                        getPreviewSize()?.let { size ->
                            viewModel.setPreviewSize(size)
                        }
                        viewModel.setFaces(faceList)
                    }
                }
            } as DetectionTaskCallback<Any>
        )
    }

    private fun initFaceMesh()
    {
        val options = FaceMeshDetectorOptions.Builder()
            .build()

        setCameraSource(
            FaceMeshDetection.getClient(options) as Detector<Any>,
            DetectionTaskCallback<List<FaceMesh>>() { task ->
                task.addOnSuccessListener { list ->
                    list?.let { meshList ->
                        getPreviewSize()?.let{ size ->
                            viewModel.setPreviewSize(size)
                        }

                        viewModel.setMeshes(meshList)
                    }
                }
            } as DetectionTaskCallback<Any>
        )
    }
}