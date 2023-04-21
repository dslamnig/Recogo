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

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import com.google.mlkit.vision.interfaces.Detector
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.slamnig.recog.*
import com.slamnig.recog.viewmodel.ImageRecogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Still image recognizer - full.
 */
@Suppress("UNCHECKED_CAST")
class ImageRecog(viewModel: ImageRecogViewModel) : BaseImageRecog(viewModel = viewModel)
{
    private val LOGTAG = this.javaClass.simpleName

    override fun setRecogMode(recogMode: Int)
    {
        super.setRecogMode(recogMode)

        detector = when (mode) {
            RECOG_TEXT ->
                initText()
            RECOG_BARCODE ->
                initBarcode()
            RECOG_OBJECT ->
                initObject()
            RECOG_FACE_BOX ->
                initFaceBox()
            RECOG_FACE_CONTOURS ->
                initFaceContours()
            RECOG_FACE_MESH ->
                initFaceMesh()
            else ->
                null
        }
    }

    private fun initText(): Detector<Any>
    {
        return TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) as Detector<Any>
    }

    private fun initBarcode(): Detector<Any>
    {
        // recognize all types:
        val options = BarcodeScannerOptions.Builder()
            //.setBarcodeFormats(
            //    Barcode.FORMAT_QR_CODE,
            //    Barcode.FORMAT_AZTEC)
            .build()

        return BarcodeScanning.getClient(options) as Detector<Any>
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

    private fun initObject(): Detector<Any>
    {
        // Image detection:
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        return ObjectDetection.getClient(options) as Detector<Any>
    }

    private fun initFaceMesh(): Detector<Any>
    {
        val options = FaceMeshDetectorOptions.Builder()
            .build()

        return FaceMeshDetection.getClient(options) as Detector<Any>
    }

    override fun recog(image: InputImage)
    {
        detector?.apply {
            when(this){
                is TextRecognizer ->
                    process(image)
                        .addOnSuccessListener { text ->
                            viewModel.setText(text)
                        }
                is BarcodeScanner ->
                    process(image)
                        .addOnSuccessListener { barcodes ->
                            barcodes?.let {
                                viewModel.setBarcodes(it.filterNotNull())
                            }
                        }
                is FaceDetector ->
                    process(image)
                        .addOnSuccessListener { faces ->
                            faces?.let {
                                viewModel.setFaces(it.filterNotNull())
                            }
                        }
                is ObjectDetector ->
                    process(image)
                        .addOnSuccessListener { objects ->
                            viewModel.setObjects(objects)
                        }
                is FaceMeshDetector ->
                    process(image)
                        .addOnSuccessListener { meshes ->
                            viewModel.setMeshes(meshes)
                        }
            }
        }
    }
}