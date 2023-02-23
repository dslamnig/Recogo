package com.slamnig.recog.dialog.compose

import android.provider.MediaStore.Audio.Radio
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.*
import com.slamnig.recog.dialog.data.AlertData
import com.slamnig.recog.dialog.viewmodel.AlertViewModel
import com.slamnig.recog.ui.theme.RecogoTheme

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