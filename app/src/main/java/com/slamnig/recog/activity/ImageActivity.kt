package com.slamnig.recog.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentActivity
import com.slamnig.recog.*
import com.slamnig.recog.compose.ImageRecogView
import com.slamnig.recog.dialog.fragment.SelectBox
import com.slamnig.recog.dialog.data.SelectData
import com.slamnig.recog.dialog.listener.SelectListener
import com.slamnig.recog.persist.RecogoPrefs
import com.slamnig.recog.recog.ImageRecog
import com.slamnig.recog.source.GalleryPicker
import com.slamnig.recog.source.PhotoCamera
import com.slamnig.recog.util.Utils
import com.slamnig.recog.viewmodel.ImageRecogViewModel

class ImageActivity : FragmentActivity()
{
    private val LOGTAG = this.javaClass.simpleName

    private lateinit var picker: GalleryPicker
    private lateinit var camera: PhotoCamera
    private lateinit var recog: ImageRecog

    private val viewModel: ImageRecogViewModel by viewModels()
    private lateinit var prefs: RecogoPrefs

    private val selectBox = SelectBox()
    private var firstRun = true

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        firstRun = true

        prefs = RecogoPrefs.getInstance(this)
        val mode = prefs.getRecogMode()

        recog = ImageRecog(viewModel).apply{
            init(mode)
        }

        picker = GalleryPicker(this){ bitmap ->
            bitmap?.let {
                recognize(it)
            }
        }

        camera = PhotoCamera(this){ bitmap ->
            bitmap?.let {
                recognize(it)
            }
        }

        viewModel.pickImage.observe(this){
            picker.pickImage()
        }

        viewModel.takePhoto.observe(this){
            camera.takePhoto()
        }

        viewModel.openSettings.observe(this){
            openSelectBox()
        }

        Log.i(LOGTAG, "onCreate")

        setContent{
            ImageRecogView(viewModel)
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

        viewModel.setAlphaSliderPosition(prefs.getAlphaSliderPosition())

        // When started, take photo or pick from gallery:
        if(firstRun == true){
            firstRun = false

            when(intent.getIntExtra(IMAGE_SOURCE_KEY, PHOTO_SOURCE)){
                PHOTO_SOURCE ->
                    camera.takePhoto()
                GALLERY_SOURCE ->
                    picker.pickImage()
            }
        }
    }

    override fun onPause()
    {
        viewModel.alphaSliderPosition.value?.let {
            prefs.setAlphaSliderPosition(it)
        }

        super.onPause()
    }

    override fun onDestroy()
    {
        Log.d(LOGTAG, "onDestroy")
        recog.close()
        super.onDestroy()
    }

    private fun recognize(bitmap: Bitmap)
    {
        recog.recognize(Utils.maxPixelBitmap(bitmap, 1024 * 1024))
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
            listener = object: SelectListener {
                override fun onSelect(mode: Int) {
                    Log.d(LOGTAG, "openSelectBox() - onSelect(): $mode")
                    prefs.setRecogMode(mode)
                    recog.rescan(mode)
                }
            }
        )
    }
}