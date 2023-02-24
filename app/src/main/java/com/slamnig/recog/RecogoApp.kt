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

package com.slamnig.recog

import android.app.Application
import com.slamnig.recog.dialog.data.ItemData

/**
 * Recogo application.
 */
class RecogoApp : Application()
{
    override fun onCreate() {
        super.onCreate()

        // init select mode items in Constants.kt when resources context becomes available:
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