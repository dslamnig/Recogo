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

import com.google.mlkit.vision.camera.CameraSourceConfig
import com.slamnig.recog.dialog.data.ItemData

/**
 * App constants.
 */
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