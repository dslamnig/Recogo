package com.slamnig.recog.compose

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.slamnig.recog.viewmodel.RecogState

fun DrawScope.drawRecogState(state: RecogState?, previewSize: Size, flip: Boolean)
{
    state?.faces?.let { faces ->
        drawFaces(faces, previewSize, flip)
    }

    state?.meshes?.let { meshes ->
        drawMeshes(meshes, previewSize, flip)
    }
}