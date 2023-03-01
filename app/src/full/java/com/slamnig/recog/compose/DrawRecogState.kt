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

package com.slamnig.recog.compose

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.slamnig.recog.viewmodel.RecogState

fun DrawScope.drawRecogState(state: RecogState?, previewSize: Size, flip: Boolean)
{
    state?.text?.let { text ->
        drawText(text, previewSize, flip)
    }

    state?.barcodes?.let { barcodes ->
        drawBarcodes(barcodes, previewSize, flip)
    }

    state?.faces?.let { faces ->
        drawFaces(faces, previewSize, flip)
    }

    state?.objects?.let { objects ->
        drawObjects(objects, previewSize, flip)
    }

    state?.meshes?.let { meshes ->
        drawMeshes(meshes, previewSize, flip)
    }
}