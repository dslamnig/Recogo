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

package com.slamnig.recog.dialog.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.*
import com.slamnig.recog.dialog.data.AlertData
import com.slamnig.recog.dialog.viewmodel.AlertViewModel
import com.slamnig.recog.ui.theme.RecogoTheme

/**
 * Alert box view.
 */
@OptIn(ExperimentalUnitApi::class)
@Composable
fun AlertView(viewModel: AlertViewModel)
{
    RecogoTheme {

        val data: AlertData = viewModel.data.observeAsState().value as AlertData

        AlertDialog(
            onDismissRequest = {
                viewModel.onCancel()
            },

            title = {
                if (data.title != null) {
                    Text(data.title)
                }
            },

            text = {
                if (data.message != null) {
                    Text(
                        text = data.message,
                        style = typography.bodyLarge
                    )
                }
            },

            confirmButton = {
                if (data.butOk != null) {
                    Button(
                        onClick = {
                            viewModel.onOk()
                        }
                    ) {
                        Text(data.butOk)
                    }
                }
            },

            dismissButton = {
                if (data.butCancel != null) {
                    Button(
                        onClick = {
                            viewModel.onCancel()
                        }
                    ) {
                        Text(data.butCancel)
                    }
                }
            }
        )
    }
}