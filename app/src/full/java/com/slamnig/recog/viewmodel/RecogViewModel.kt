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

package com.slamnig.recog.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.launch

/**
 * Recognition view model - full.
 */
open class RecogViewModel: BaseRecogViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    open fun setText(text: Text)
    {
        viewModelScope.launch {
            _recogState.value = RecogState(text = text)
        }
    }

    open fun setBarcodes(barcodes: List<Barcode>)
    {
        viewModelScope.launch {
            _recogState.value = RecogState(barcodes = barcodes)
        }
    }

    fun setFaces(faces: List<Face>)
    {
        viewModelScope.launch {
            _recogState.value = RecogState(faces = faces)
        }
    }

    fun setObjects(objects: List<DetectedObject>)
    {
        viewModelScope.launch {
            _recogState.value = RecogState(objects = objects)
        }
    }

    fun setMeshes(meshes: List<FaceMesh>)
    {
        viewModelScope.launch {
            _recogState.value = RecogState(meshes = meshes)
        }
    }
}