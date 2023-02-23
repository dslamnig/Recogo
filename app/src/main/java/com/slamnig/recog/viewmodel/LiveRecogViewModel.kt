package com.slamnig.recog.viewmodel

import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.slamnig.recog.*
import kotlinx.coroutines.launch

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