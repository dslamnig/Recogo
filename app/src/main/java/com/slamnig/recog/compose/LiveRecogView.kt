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
import androidx.camera.view.PreviewView
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.slamnig.recog.*
import com.slamnig.recog.R
import com.slamnig.recog.ui.theme.RecogoTheme
import com.slamnig.recog.viewmodel.LiveRecogViewModel
import com.slamnig.recog.viewmodel.RecogState

/**
 * Live camera recognition view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRecogView(
    viewModel: LiveRecogViewModel,
    previewView: PreviewView)
{
    val recogState = viewModel.recogState.observeAsState(RecogState())
    val _previewSize by viewModel.previewSize.observeAsState(android.util.Size(1, 1))
    // convert from integer Size to compose float Size:
    val previewSize = Size(_previewSize.width.toFloat(), _previewSize.height.toFloat())
    val cameraFacing = viewModel.cameraFacing.observeAsState(BACK_CAMERA).value
    val showCameraSwitch = viewModel.showCameraSwitch.observeAsState(false)
    var sliderPosition = viewModel.alphaSliderPosition.observeAsState(0.5f).value

    val gestureModifier = gestureTransformModifier()

    RecogoTheme {
        Surface(
            color = Color.Black
        ) {
            Scaffold(
                content = { pad ->
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pad),
                    ) {
                        // camera preview:
                        AndroidView(
                            modifier = gestureModifier,
                            factory = {
                                previewView
                            }
                        )

                        // alpha overlay:
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = Color(
                                        red = 0f,
                                        green = 0f,
                                        blue = 0f,
                                        alpha = (sliderPosition * 2f - 1f).coerceAtLeast(0f)
                                    )
                                )
                        )

                        // recognition graphics:
                        Canvas(
                            modifier = gestureModifier
                                .alpha(((sliderPosition) * 2f).coerceAtMost(1f)),
                            onDraw = {
                                drawRecogState(recogState.value, previewSize, cameraFacing == FRONT_CAMERA)
                            }
                        )

                        // controls:
                        val (butFlip, butSettings, slider) = createRefs()
                        // get current app configuration:
                        val configuration = LocalConfiguration.current

                        ActionButton(
                            modifier = Modifier
                                .padding(top = 10.dp, end = 10.dp)
                                .constrainAs(butSettings) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                },

                            iconId = R.drawable.baseline_settings_white_24,
                            text = stringResource(R.string.settings),
                            onClick = { viewModel.onOpenSettings() }
                        )

                        if(showCameraSwitch.value == true) {
                            ActionButton(
                                modifier = Modifier
                                    .padding(top = 10.dp, end = 10.dp)
                                    .constrainAs(butFlip) {
                                        when (configuration.orientation) {
                                            Configuration.ORIENTATION_PORTRAIT -> {
                                                top.linkTo(parent.top)
                                                end.linkTo(butSettings.start)
                                            }
                                            else -> {
                                                top.linkTo(butSettings.bottom)
                                                end.linkTo(parent.end)
                                            }
                                        }
                                    },

                                iconId = R.drawable.baseline_flip_camera_android_white_24,
                                text = stringResource(R.string.flip_camera),
                                onClick = { viewModel.onSwitchCamera() }
                            )
                        }

                        // overlay alpha slider:
                        Slider(
                            modifier = Modifier
                                .width(160.dp)
                                .padding(16.dp)
                                .constrainAs(slider) {
                                    bottom.linkTo(parent.bottom)
                                    end.linkTo(parent.end)
                                },
                            valueRange = 0f..1f,
                            value = sliderPosition,
                            onValueChange = { position ->
                                // sliderPosition = it
                                viewModel.setAlphaSliderPosition(position)
                            }
                        )
                    }
                }
            )
        }
    }
}
