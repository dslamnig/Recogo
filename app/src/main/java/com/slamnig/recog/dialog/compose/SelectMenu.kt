package com.slamnig.recog.dialog.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.slamnig.recog.dialog.data.ItemData

@Composable
fun ColumnScope.SelectMenu(
    items: List<ItemData>,
    textColor: Color,
    selectedItemId: Int,
    onItemSelected: (id: Int) -> Unit,
    split: Boolean = false
){
    Box(
        modifier = Modifier
            .wrapContentSize()
            .align(Alignment.CenterHorizontally)
    ) {
        Row(
            modifier = Modifier
                .selectableGroup()
                .wrapContentHeight()
        ) {
            if(split == false) {
                Column(
                    modifier = Modifier
                        //.selectableGroup()
                        .wrapContentHeight()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    for (item in items) {
                        MenuItem(item, textColor, selectedItemId, onItemSelected)
                    }
                }
            }
            else{
                val half = items.size - items.size / 2

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    for (i in 0..half - 1) {
                        MenuItem(items.get(i), textColor, selectedItemId, onItemSelected)
                    }
                }

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    for (i in half..items.size - 1) {
                        MenuItem(items.get(i), textColor, selectedItemId, onItemSelected)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItem(
    item: ItemData,
    textColor: Color,
    selectedItemId: Int,
    onItemSelected: (id: Int) -> Unit
){
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
            .selectable(
                selected = item.id == selectedItemId,
                role = Role.RadioButton,
                onClick = {
                    onItemSelected(item.id)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier
                .padding(end = 10.dp),

            selected = item.id == selectedItemId,
            onClick = null
        )
        Text(
            text = item.text,
            color = textColor
        )
    }
}
