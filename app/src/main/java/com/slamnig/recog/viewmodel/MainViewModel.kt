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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Main activity view model.
 */
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