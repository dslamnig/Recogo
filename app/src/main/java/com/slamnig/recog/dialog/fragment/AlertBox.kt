package com.slamnig.recog.dialog.fragment

import androidx.compose.ui.platform.ComposeView
import com.slamnig.recog.dialog.compose.AlertView
import com.slamnig.recog.dialog.viewmodel.AlertViewModel
import com.slamnig.recog.dialog.viewmodel.DialogViewModel

class AlertBox : DialogBox(viewModel = AlertViewModel())
{
    override fun createComposeView(viewModel: DialogViewModel): ComposeView?
    {
        return ComposeView(requireContext()).apply {
            setContent {
                AlertView(viewModel as AlertViewModel)
            }
        }
    }
}
