package com.slamnig.recog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    private val _mode = MutableLiveData<Int>()
    val mode: LiveData<Int> = _mode

    private val _startGallery = MutableLiveData<Int>()
    val startGallery: LiveData<Int> = _startGallery

    private val _startPhoto = MutableLiveData<Int>()
    val startPhoto: LiveData<Int> = _startPhoto

    private val _startLive = MutableLiveData<Int>()
    val startLive: LiveData<Int> = _startLive

    private val _showInfo = MutableLiveData<Unit>()
    val showInfo: LiveData<Unit> = _showInfo

    fun setMode(mode: Int)
    {
        viewModelScope.launch {
            _mode.value = mode
        }
    }

    fun onStartGallery(mode: Int)
    {
        viewModelScope.launch {
            _startGallery.value = mode
        }
    }

    fun onStartPhoto(mode: Int)
    {
        viewModelScope.launch {
            _startPhoto.value = mode
        }
    }

    fun onStartLive(mode: Int)
    {
        viewModelScope.launch {
            _startLive.value = mode
        }
    }

    fun onShowInfo()
    {
        viewModelScope.launch {
            _showInfo.value = Unit
        }
    }
}