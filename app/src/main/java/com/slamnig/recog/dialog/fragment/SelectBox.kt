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

package com.slamnig.recog.dialog.fragment

import androidx.compose.ui.platform.ComposeView
import com.slamnig.recog.dialog.compose.SelectView
import com.slamnig.recog.dialog.viewmodel.DialogViewModel
import com.slamnig.recog.dialog.viewmodel.SelectViewModel

/**
 * Select box dialog.
 */
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
