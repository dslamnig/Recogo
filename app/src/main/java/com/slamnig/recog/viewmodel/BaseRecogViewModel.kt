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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Base recognition view model observables.
 */
open class BaseRecogViewModel: ViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    val _recogState = MutableLiveData<RecogState?>()
    val recogState: LiveData<RecogState?> = _recogState

    val _openSettings = MutableLiveData<Unit>()
    val openSettings: LiveData<Unit> = _openSettings

    val _alphaSliderPosition = MutableLiveData(0.5f)
    val alphaSliderPosition: LiveData<Float> = _alphaSliderPosition

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