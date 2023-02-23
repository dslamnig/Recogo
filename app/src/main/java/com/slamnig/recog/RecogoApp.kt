package com.slamnig.recog

import android.app.Application
import com.slamnig.recog.dialog.data.ItemData

class RecogoApp : Application()
{
    /*
    companion object{
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

        var RECOG_SELECT_MODE_ITEMS = listOf(ItemData(0, "Empty"))
    }
    */

    override fun onCreate() {
        super.onCreate()

        // init select mode items when resources context is available:
        RECOG_SELECT_MODE_ITEMS = listOf(
            ItemData(RECOG_TEXT, getString(R.string.ocr)),
            ItemData(RECOG_BARCODE, getString(R.string.barcode)),
            ItemData(RECOG_OBJECT, getString(R.string.object_detection)),
            ItemData(RECOG_FACE_BOX, getString(R.string.face_box)),
            ItemData(RECOG_FACE_CONTOURS, getString(R.string.face_contours)),
            ItemData(RECOG_FACE_MESH, getString(R.string.face_mesh))
        )
    }
}