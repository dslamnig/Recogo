package com.slamnig.recog.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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
