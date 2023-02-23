package com.slamnig.recog.compose

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.slamnig.recog.R
import com.slamnig.recog.ui.theme.RecogoTheme
import com.slamnig.recog.viewmodel.ImageRecogViewModel
import com.slamnig.recog.viewmodel.RecogState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageRecogView(viewModel: ImageRecogViewModel)
{
    val bitmap = viewModel.bitmap.observeAsState(initial = null)
    val recogState = viewModel.recogState.observeAsState(initial = RecogState())
    var sliderPosition = viewModel.alphaSliderPosition.observeAsState(0.5f).value

    RecogoTheme {
        Surface(
            color = Color.Black
        ) {
            Scaffold(
                content = { pad ->
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pad)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            bitmap.value?.let { bitmap ->
                                // photo:
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentScale = ContentScale.Fit,
                                    contentDescription = stringResource(R.string.photo),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(((1f - sliderPosition) * 2f).coerceAtLeast(0f))
                                )

                                // recognition graphics:
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(((sliderPosition) * 2f).coerceAtMost(1f)),
                                    onDraw = {
                                        val sourceSize =
                                            Size(bitmap.width.toFloat(), bitmap.height.toFloat())

                                        recogState.value?.text?.let { text ->
                                            drawText(text, sourceSize)
                                        }

                                        recogState.value?.barcodes?.let { barcodes ->
                                            drawBarcodes(barcodes, sourceSize)
                                        }

                                        recogState.value?.faces?.let { faces ->
                                            drawFaces(faces, sourceSize, false) // don't flip graphics
                                        }

                                        recogState.value?.objects?.let { objects ->
                                            drawObjects(objects, sourceSize, false)
                                        }

                                        recogState.value?.meshes?.let{ meshes ->
                                            drawMeshes(meshes, sourceSize, false)
                                        }
                                    }
                                )
                            }
                        }

                        // controls:
                        val (butPhoto, butGallery, butSettings, slider) = createRefs()
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

                        ActionButton(
                            modifier = Modifier
                                .padding(top = 10.dp, end = 10.dp)
                                .constrainAs(butPhoto) {
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
                            iconId = R.drawable.baseline_camera_white_24,
                            text = stringResource(R.string.photo),
                            onClick = { viewModel.onTakePhoto() }
                        )

                        ActionButton(
                            modifier = Modifier
                                .padding(top = 10.dp, end = 10.dp)
                                .constrainAs(butGallery) {
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_PORTRAIT -> {
                                            top.linkTo(parent.top)
                                            end.linkTo(butPhoto.start)
                                        }
                                        else -> {
                                            top.linkTo(butPhoto.bottom)
                                            end.linkTo(parent.end)
                                        }
                                    }
                                },
                            iconId = R.drawable.baseline_collections_white_24,
                            text = stringResource(R.string.gallery),
                            onClick = { viewModel.onPickImage() }
                        )

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
                                viewModel.setAlphaSliderPosition(position)
                            }
                        )
                    }
                }
            )
        }
    }
}
