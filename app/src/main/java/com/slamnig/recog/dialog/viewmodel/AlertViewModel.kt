package com.slamnig.recog.dialog.viewmodel

import androidx.lifecycle.viewModelScope
import com.slamnig.recog.dialog.listener.AlertListener
import kotlinx.coroutines.launch

class AlertViewModel() : DialogViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    fun onOk()
    {
        hideDialog()

        viewModelScope.launch {
            listener?.let {
                (it as AlertListener).onOk()
            }
        }
    }
}