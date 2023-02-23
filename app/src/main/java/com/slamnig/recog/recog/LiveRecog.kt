package com.slamnig.recog.recog

import android.annotation.SuppressLint
import android.util.Log
import android.util.Size
import androidx.camera.view.PreviewView
import androidx.lifecycle.Observer
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.camera.CameraSourceConfig
import com.google.mlkit.vision.camera.CameraXSource
import com.google.mlkit.vision.camera.DetectionTaskCallback
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import com.google.mlkit.vision.interfaces.Detector
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.slamnig.recog.*
import com.slamnig.recog.viewmodel.LiveRecogViewModel

@Suppress("UNCHECKED_CAST")
class LiveRecog(val viewModel: LiveRecogViewModel, val preview: PreviewView, val startFacing: Int)
{
    private val LOGTAG = this.javaClass.simpleName

    private var cameraSource: CameraXSource? = null
    private var switchCameraObserver: Observer<Unit>
    private var mode = RECOG_TEXT
    private var cameraFacing = startFacing

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
        // if text or barcode recognition, force back camera:
        if(recogMode == RECOG_TEXT || recogMode == RECOG_BARCODE && cameraFacing == FRONT_CAMERA) {
            cameraFacing = BACK_CAMERA
            viewModel.setCameraFacing(cameraFacing)
        }

        setMode(recogMode)
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

    private fun setMode(recogMode: Int)
    {
        mode = recogMode

        // back camera only for text and barcode:
        viewModel.setShowCameraSwitch(
            when(mode){
                RECOG_TEXT, RECOG_BARCODE ->
                    false
                else ->
                    true
            }
        )

        when (mode) {
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
        }
    }

    private fun initText()
    {
        setCameraSource(
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) as Detector<Any>,
            DetectionTaskCallback<Text>() { task ->
                task.addOnSuccessListener { text ->
                    text?.let {
                        getPreviewSize()?.let{ size ->
                            viewModel.setPreviewSize(size)
                        }
                        viewModel.setText(text)
                    }
                }
            } as DetectionTaskCallback<Any>
        )
    }

    private fun initBarcode()
    {
        // all formats:
        val options = BarcodeScannerOptions.Builder()
            //.setBarcodeFormats(
            //    Barcode.FORMAT_QR_CODE,
            //    Barcode.FORMAT_AZTEC)
            .build()

        setCameraSource(
            BarcodeScanning.getClient(options) as Detector<Any>,
            DetectionTaskCallback<List<Barcode>>() { task ->
                task.addOnSuccessListener { list ->
                    list?.let { barcodeList ->
                        getPreviewSize()?.let{ size ->
                            viewModel.setPreviewSize(size)
                        }

                        viewModel.setBarcodes(barcodeList)
                    }
                }
            } as DetectionTaskCallback<Any>
        )
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

    private fun initObject()
    {
        // live detection and tracking:
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        setCameraSource(
            ObjectDetection.getClient(options) as Detector<Any>,
            DetectionTaskCallback<List<DetectedObject>>() { task ->
                task.addOnSuccessListener { list ->
                    list?.let { objectList ->
                        getPreviewSize()?.let{ size ->
                            viewModel.setPreviewSize(size)
                        }

                        viewModel.setObjects(objectList)
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

    private fun setCameraSource(
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

    private fun getPreviewSize(): Size?
    {
        if(cameraSource == null)
            return null
        else {
            val size = cameraSource!!.previewSize
            var tsize = size

            if(size != null){
                preview.viewPort?.rotation.let { rotation ->
                    Log.d(LOGTAG, "getPreviewSize() - rotation:$rotation")

                    if (rotation == 0 || rotation == 2) {
                        // switch w/h for portrait:
                        tsize = Size(size.height, size.width)
                    }
                }
            }
            else
                Log.d(LOGTAG, "getPreviewSize() - size null")

            return tsize
        }
    }
}