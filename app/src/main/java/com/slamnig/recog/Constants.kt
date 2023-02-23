package com.slamnig.recog

import com.google.mlkit.vision.camera.CameraSourceConfig
import com.slamnig.recog.dialog.data.ItemData

// recognition modes:
const val RECOG_TEXT = 1
const val RECOG_BARCODE = 2
const val RECOG_OBJECT = 3
const val RECOG_FACE_BOX = 4
const val RECOG_FACE_CONTOURS = 5
const val RECOG_FACE_MESH = 6

// image source:
const val IMAGE_SOURCE_KEY = "ImageSource"
const val PHOTO_SOURCE = 1
const val GALLERY_SOURCE = 2

// camera facing:
const val FRONT_CAMERA = CameraSourceConfig.CAMERA_FACING_FRONT
const val BACK_CAMERA = CameraSourceConfig.CAMERA_FACING_BACK

// mode select radiogroup data, will be set by RecogoApp:
var RECOG_SELECT_MODE_ITEMS = listOf(ItemData(0, "Empty"))