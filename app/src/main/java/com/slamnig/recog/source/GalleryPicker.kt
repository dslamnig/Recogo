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

package com.slamnig.recog.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.slamnig.recog.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Image file picker.
 */
class GalleryPicker(
    val activity: FragmentActivity,
    val listener: GalleryPickerListener
)
{
    private val LOGTAG = this.javaClass.simpleName

    fun interface GalleryPickerListener{
        fun onImage(bitmap: Bitmap?)
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    fun pickImage()
    {
        imagePicker.launch("image/*")
    }

    private val imagePicker = activity.registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? -> // Boolean
        uri?.let { _uri ->
            scope.launch{
                listener.let {
                    val bitmap = scope.async {
                        loadRotateBitmap(_uri)
                    }

                    activity.lifecycleScope.launch {
                        it.onImage(bitmap.await())
                    }
                }
            }
        }
    }

    private fun loadRotateBitmap(uri: Uri): Bitmap?
    {
        var bitmap: Bitmap? = null

        try {
            val ims: InputStream? = activity.getContentResolver().openInputStream(uri)

            ims?.let{ _ims ->
                bitmap = BitmapFactory.decodeStream(_ims)
            }

        } catch (e: FileNotFoundException) {
            bitmap = null
            Log.w(LOGTAG, "loadRotateBitmap()", e)
        }

        return if(bitmap != null)
            Utils.rotateBitmap(bitmap!!, Utils.getOrientationDegreesFromUri(activity, uri), true)
        else
            null
    }
}