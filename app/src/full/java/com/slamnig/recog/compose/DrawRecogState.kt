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