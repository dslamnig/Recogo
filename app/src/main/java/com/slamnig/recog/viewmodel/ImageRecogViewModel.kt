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

package com.slamnig.recog.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Still image recognition view model.
 */
class ImageRecogViewModel: RecogViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap

    private val _takePhoto = MutableLiveData<Unit>()
    val takePhoto: LiveData<Unit> = _takePhoto

    private val _pickImage = MutableLiveData<Unit>()
    val pickImage: LiveData<Unit> = _pickImage

    fun setBitmap(bitmap: Bitmap)
    {
        viewModelScope.launch {
            _bitmap.value = bitmap
        }
    }

    fun onTakePhoto()
    {
        viewModelScope.launch {
            _takePhoto.value = Unit
        }
    }

    fun onPickImage()
    {
        viewModelScope.launch {
            _pickImage.value = Unit
        }
    }
}
