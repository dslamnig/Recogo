package com.slamnig.recog.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slamnig.recog.*
import com.slamnig.recog.R
import com.slamnig.recog.dialog.compose.SelectMenu
import com.slamnig.recog.ui.theme.RecogoTheme
import com.slamnig.recog.viewmodel.MainViewModel

private val ROW_PAD = 10.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(viewModel: MainViewModel)
{
    val mode = viewModel.mode.observeAsState(RECOG_TEXT).value

    RecogoTheme {
        Surface(
            color = Color.Black
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(stringResource(R.string.app_name) + stringResource(R.string.main_title))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = colorScheme.primary
                        ),
                        actions = {
                            IconButton(
                                modifier = Modifier.padding(end = 10.dp),
                                content = {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_info_white_36),
                                        contentDescription = stringResource(R.string.info)

                                    )
                                },
                                onClick = {
                                    viewModel.onShowInfo()
                                }
                            )
                        }
                    )
                },

                content = { pad ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pad),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            // get current app configuration:
                            val configuration = LocalConfiguration.current

                            SelectMenu(
                                RECOG_SELECT_MODE_ITEMS,
                                textColor = Color.White,
                                selectedItemId = mode,
                                onItemSelected = fun(mode: Int) { viewModel.setMode(mode) },
                                split = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                            )
                        }
                    }
                },

                bottomBar = {
                    MenuButtons(viewModel, mode)
                }
            )
        }
    }
}

@Composable
fun MenuButtons(viewModel: MainViewModel, selectedMode: Int)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ROW_PAD)
    ){
        MenuButton(
            iconId = R.drawable.baseline_collections_white_36,
            text = stringResource(R.string.gallery),
            clickFun = { viewModel.onStartGallery(selectedMode) }
        )

        MenuButton(
            iconId = R.drawable.baseline_camera_white_36,
            text = stringResource(R.string.photo),
            clickFun = { viewModel.onStartPhoto(selectedMode) }
        )

        MenuButton(
            iconId = R.drawable.baseline_videocam_white_36,
            text = stringResource(R.string.live),
            clickFun = { viewModel.onStartLive(selectedMode) }
        )
    }
}
