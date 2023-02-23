package com.slamnig.recog.dialog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slamnig.recog.dialog.data.DialogData
import com.slamnig.recog.dialog.listener.DialogListener
import kotlinx.coroutines.launch

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