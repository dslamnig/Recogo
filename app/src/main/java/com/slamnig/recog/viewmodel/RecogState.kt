package com.slamnig.recog.viewmodel

import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.text.Text

data class RecogState(
    val text: Text? = null,
    val barcodes: List<Barcode>? = null,
    val faces: List<Face>? = null,
    val objects: List<DetectedObject>? = null,
    val meshes: List<FaceMesh>? = null
)
