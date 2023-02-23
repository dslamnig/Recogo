package com.slamnig.recog.dialog.fragment

import androidx.compose.ui.platform.ComposeView
import com.slamnig.recog.dialog.compose.SelectView
import com.slamnig.recog.dialog.viewmodel.DialogViewModel
import com.slamnig.recog.dialog.viewmodel.SelectViewModel

class SelectBox : DialogBox(viewModel = SelectViewModel())
{
    override fun createComposeView(viewModel: DialogViewModel): ComposeView?
    {
        return ComposeView(requireContext()).apply {
            setContent {
                SelectView(viewModel as SelectViewModel)
            }
        }
    }
}
