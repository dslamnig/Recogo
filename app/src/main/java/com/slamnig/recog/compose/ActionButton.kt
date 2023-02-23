package com.slamnig.recog.compose

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun ActionButton(modifier: Modifier, iconId: Int, text: String, onClick: () -> Unit)
{
    Button(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = text
        )
    }
}
