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

import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.slamnig.recog.*
import kotlinx.coroutines.launch

/**
 * Live camera recognition view model.
 */
class LiveRecogViewModel : RecogViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    private val _previewSize = MutableLiveData<Size>()
    val previewSize: LiveData<Size> = _previewSize

    private val _cameraFacing = MutableLiveData(BACK_CAMERA)
    val cameraFacing: LiveData<Int> = _cameraFacing

    private val _switchCamera = MutableLiveData<Unit>()
    val switchCamera: LiveData<Unit> = _switchCamera

    private val _showCameraSwitch = MutableLiveData<Boolean>()
    val showCameraSwitch: LiveData<Boolean> = _showCameraSwitch

    fun setPreviewSize(size: Size?)
    {
        size?.let {
            viewModelScope.launch {
                _previewSize.value = it
            }
        }
    }

    fun setCameraFacing(facing: Int)
    {
        viewModelScope.launch {
            _cameraFacing.value = facing
        }
    }

    fun onSwitchCamera()
    {
        viewModelScope.launch {
            _switchCamera.value = Unit
        }
    }

    fun setShowCameraSwitch(set: Boolean)
    {
        viewModelScope.launch {
            _showCameraSwitch.value = set
        }
    }
}