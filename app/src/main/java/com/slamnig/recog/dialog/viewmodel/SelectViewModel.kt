package com.slamnig.recog.dialog.viewmodel

import androidx.lifecycle.viewModelScope
import com.slamnig.recog.dialog.listener.SelectListener
import kotlinx.coroutines.launch

class SelectViewModel() : DialogViewModel()
{
    private val LOGTAG = this.javaClass.simpleName

    fun onSelect(itemId: Int)
    {
        hideDialog()

        viewModelScope.launch{
            listener?.let{
                (it as SelectListener).onSelect(itemId)
            }
        }
    }
}