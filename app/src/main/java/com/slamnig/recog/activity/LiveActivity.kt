package com.slamnig.recog.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.view.PreviewView
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentActivity
import com.slamnig.recog.*
import com.slamnig.recog.recog.LiveRecog
import com.slamnig.recog.compose.LiveRecogView
import com.slamnig.recog.dialog.fragment.SelectBox
import com.slamnig.recog.dialog.data.SelectData
import com.slamnig.recog.dialog.listener.SelectListener
import com.slamnig.recog.persist.RecogoPrefs
import com.slamnig.recog.util.Utils
import com.slamnig.recog.viewmodel.LiveRecogViewModel

class LiveActivity : FragmentActivity()
{
    private val LOGTAG = this.javaClass.simpleName

    private lateinit var prefs: RecogoPrefs
    private lateinit var liveRecog: LiveRecog
    private val viewModel: LiveRecogViewModel by viewModels()
    private val selectBox = SelectBox()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        prefs = RecogoPrefs.getInstance(this)
        val mode = prefs.getRecogMode()

        val preview = createPreview()

        liveRecog = LiveRecog(viewModel, preview, prefs.getCameraFacing()).apply {
            init(mode)
        }

        viewModel.openSettings.observe(this){
            openSelectBox()
        }

        viewModel.cameraFacing.observe(this){ facing ->
            prefs.setCameraFacing(facing)
        }

        setContent {
            LiveRecogView(viewModel, preview)
        }

        Utils.fullScreen(
            this,
            setNavigationBarColor = true,
            navigationBarColor = android.graphics.Color.BLACK
        )

        Utils.keepScreenOn(this, true)
    }

    override fun onResume()
    {
        super.onResume()
        liveRecog.start()
        viewModel.setAlphaSliderPosition(prefs.getAlphaSliderPosition())
    }

    override fun onPause()
    {
        viewModel.alphaSliderPosition.value?.let {
            prefs.setAlphaSliderPosition(it)
        }
        liveRecog.stop()
        super.onPause()
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        Log.d(LOGTAG, "onConfigurationChanged()")

        liveRecog.restart()
    }

    override fun onDestroy()
    {
        liveRecog.destroy()
        super.onDestroy()
    }

    // create camera preview:
    private fun createPreview() : PreviewView
    {
        val previewView = PreviewView(this).apply {
            // preview overlay transformations expect FIT_CENTER:
            this.scaleType = PreviewView.ScaleType.FIT_CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        return previewView
    }

    private fun openSelectBox()
    {
        selectBox.show(
            activity = this,
            data = SelectData(
                items = RECOG_SELECT_MODE_ITEMS,
                startItemId = prefs.getRecogMode(),
                textColor = Color.White
            ),
            listener = object : SelectListener {
                override fun onSelect(mode: Int) {
                    Log.d(LOGTAG, "openSelectBox() - onSelect(): $mode")
                    prefs.setRecogMode(mode)
                    liveRecog.init(mode)
                    liveRecog.start()
                }
            }
        )
    }
}
