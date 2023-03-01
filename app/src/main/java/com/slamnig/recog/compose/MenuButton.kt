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

package com.slamnig.recog.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

private val BUT_PAD = 8.dp

/**
 * Menu button.
 */
@Composable
fun RowScope.MenuButton(iconId: Int, text: String, clickFun: () -> Unit)
{
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(BUT_PAD)
            .weight(1f),

        shape = RoundedCornerShape(corner = CornerSize(6.dp)),

        onClick = {
            clickFun()
        },
    ){
        // get current app configuration:
        val configuration = LocalConfiguration.current

        // if landscape orientation, show row:
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        painter = painterResource(iconId),
                        contentDescription = text
                    )
                    Text(text = text)
                }
            }

            // else show column:
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.padding(bottom = 4.dp),
                        painter = painterResource(iconId),
                        contentDescription = text
                    )
                    Text(text = text)
                }
            }
        }
    }
}