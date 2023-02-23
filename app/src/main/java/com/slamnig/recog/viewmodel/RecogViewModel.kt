package com.slamnig.recog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.launch

open class RecogViewModel: ViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    private val _recogState = MutableLiveData<RecogState?>()
    val recogState: LiveData<RecogState?> = _recogState

    private val _openSettings = MutableLiveData<Unit>()
    val openSettings: LiveData<Unit> = _openSettings

    private val _alphaSliderPosition = MutableLiveData(0.5f)
    val alphaSliderPosition: LiveData<Float> = _alphaSliderPosition

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

    fun clearRecogs()
    {
        viewModelScope.launch {
            _recogState.value = RecogState()
        }
    }

    fun onOpenSettings()
    {
        viewModelScope.launch {
            _openSettings.value = Unit
        }
    }

    fun setAlphaSliderPosition(position: Float)
    {
        viewModelScope.launch{
            _alphaSliderPosition.value = position
        }
    }
}