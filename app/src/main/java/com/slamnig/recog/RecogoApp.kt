package com.slamnig.recog

import android.app.Application
import com.slamnig.recog.dialog.data.ItemData

class RecogoApp : Application()
{
    override fun onCreate() {
        super.onCreate()

        // init select mode items in Constants.kt when resources context available:
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