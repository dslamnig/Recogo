package com.slamnig.recog.dialog.data

import androidx.compose.ui.graphics.Color

class SelectData(
    val items: List<ItemData>,
    val startItemId: Int,
    val textColor: Color = Color.Black
) : DialogData()
