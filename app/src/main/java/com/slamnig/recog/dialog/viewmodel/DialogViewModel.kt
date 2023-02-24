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

package com.slamnig.recog.dialog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slamnig.recog.dialog.data.DialogData
import com.slamnig.recog.dialog.listener.DialogListener
import kotlinx.coroutines.launch

/**
 * Base dialog view model.
 */
open class DialogViewModel() : ViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    protected var listener: DialogListener? = null

    // Initial value is false so the dialog is hidden
    private val _show = MutableLiveData(false)
    val show: LiveData<Boolean> = _show

    private val _data = MutableLiveData(DialogData())
    val data: LiveData<DialogData> = _data

    fun showDialog(data: DialogData, listener: DialogListener?)
    {
        this@DialogViewModel.listener = listener
        _data.value = data
        _show.value = true
    }

    fun hideDialog()
    {
        _show.value = false
    }

    fun onCancel()
    {
        hideDialog()
        onDialogCancel()
    }

    fun onDialogCancel()
    {
        viewModelScope.launch {
            listener?.let {
                it.onCancel()
            }
        }
    }
}