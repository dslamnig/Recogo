package com.slamnig.recog.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.slamnig.recog.*
import com.slamnig.recog.compose.MainView
import com.slamnig.recog.dialog.data.AlertData
import com.slamnig.recog.dialog.fragment.AlertBox
import com.slamnig.recog.permissions.PermissionsHandler
import com.slamnig.recog.persist.RecogoPrefs
import com.slamnig.recog.util.Utils
import com.slamnig.recog.viewmodel.MainViewModel

class MainActivity : FragmentActivity()
{
    private val LOGTAG = this.javaClass.simpleName

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    }

    private lateinit var prefs: RecogoPrefs
    private lateinit var permissionsHandler: PermissionsHandler
    private val viewModel: MainViewModel by viewModels()

    private val alertBox = AlertBox()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        prefs = RecogoPrefs.getInstance(this)

        permissionsHandler = PermissionsHandler(
            this,
            REQUIRED_PERMISSIONS
        ).apply {
            requestPermissions()
        }

        setContent {
            MainView(viewModel)
        }

        Utils.fullScreen(this, setNavigationBarColor = true, navigationBarColor = Color.BLACK)
        Utils.keepScreenOn(this, true)

        observeViewModel()
    }

    override fun onResume()
    {
        super.onResume()

        val mode = prefs.getRecogMode()

        viewModel.setMode(mode)
    }

    fun observeViewModel()
    {
        viewModel.startGallery.observe(this){ mode ->
            // testAlert()
            prefs.setRecogMode(mode)
            startImageActivity(GALLERY_SOURCE)
        }

        viewModel.startPhoto.observe(this){ mode ->
            // testModes()
            prefs.setRecogMode(mode)
            startImageActivity(PHOTO_SOURCE)
        }

        viewModel.startLive.observe(this){ mode ->
            prefs.setRecogMode(mode)
            startLiveActivity()
        }

        viewModel.showInfo.observe(this){
            showInfoDialog()
        }
    }

    fun startImageActivity(source: Int)
    {
        val intent = Intent(this, ImageActivity::class.java)
        intent.putExtra(IMAGE_SOURCE_KEY, source)
        startActivity(intent)
    }

    fun startLiveActivity()
    {
        val intent = Intent(this, LiveActivity::class.java)
        startActivity(intent)
    }

    private fun showInfoDialog()
    {
        alertBox.show(
            activity = this,
            data = AlertData(
                butOk = "OK",
                title = "Recogo",
                message = getString(R.string.app_description)
            )
        )
    }
}
