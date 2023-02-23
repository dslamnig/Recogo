package com.slamnig.recog.dialog.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slamnig.recog.dialog.data.SelectData
import com.slamnig.recog.dialog.viewmodel.SelectViewModel
import com.slamnig.recog.ui.theme.RecogoTheme

@Composable
fun SelectView(viewModel: SelectViewModel)
{
    RecogoTheme {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(0.dp),
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer)
        ) {
            val data: SelectData = viewModel.data.observeAsState().value as SelectData

            Column(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                val selectedValue = remember{ mutableStateOf(data.startItemId) }

                SelectMenu(
                    items = data.items,
                    textColor = data.textColor,
                    selectedItemId = selectedValue.value,
                    onItemSelected = fun(value: Int) {
                        selectedValue.value = value
                        viewModel.onSelect(value)
                    }
                )
            }
        }
    }
}
