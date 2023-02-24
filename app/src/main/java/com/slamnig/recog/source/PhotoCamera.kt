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
package com.slamnig.recog.activity
*/

package com.slamnig.recog.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.slamnig.recog.R
import com.slamnig.recog.util.Utils
import kotlinx.coroutines.*
import java.io.File

/**
 * Device photo camera.
 */
class PhotoCamera(
    val activity: FragmentActivity,
    val listener: PhotoCameraListener
) {
    private val LOGTAG = this.javaClass.simpleName

    fun interface PhotoCameraListener{
        fun onPhoto(bitmap: Bitmap?)
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    private val imageFile = initImageFile()
    private val imageUri = initImageUri()

    fun takePhoto()
    {
        takePicture.launch(imageUri)
    }

    private fun initImageFile(): File{
        // temp image directory:
        val tempImagesDir = File(
            // external cache dir:
            activity.applicationContext.filesDir,
            // directory for the temporary images dir:
            activity.getString(R.string.temp_images_dir)
        )

        // create it:
        tempImagesDir.mkdir()

        // create the temp image file:
        return File(
            tempImagesDir,
            activity.getString(R.string.temp_image)
        )
    }

    private fun initImageUri(): Uri
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            // returns the Uri object to be used with ActivityResultLauncher:
            return FileProvider.getUriForFile(
                activity.applicationContext,
                activity.applicationContext.packageName + ".provider",
                imageFile
            )
        }
        else{
            return Uri.fromFile(imageFile)
        }
    }

    private val takePicture = activity.registerForActivityResult(ActivityResultContracts.TakePicture())
    { result -> // Boolean
        result?.let {
            if(it == true){
               scope.launch{
                   listener.let {
                       val bitmap = scope.async {
                           loadRotateBitmap(imageFile.absolutePath)
                       }

                       activity.lifecycleScope.launch{
                           it.onPhoto(bitmap.await())
                       }
                   }
               }
            }
        }
    }

    private fun loadRotateBitmap(path: String): Bitmap?
    {
        val bitmap: Bitmap? = BitmapFactory.decodeFile(path)

        return if(bitmap != null)
            Utils.rotateBitmap(bitmap, Utils.getOrientationDegreesFromFile(path), true)
        else
            null
    }
}