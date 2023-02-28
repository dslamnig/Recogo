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

package com.slamnig.recog.recog

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.interfaces.Detector
import com.slamnig.recog.*
import com.slamnig.recog.viewmodel.ImageRecogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Still image recognizer.
 */
@Suppress("UNCHECKED_CAST")
open class BaseImageRecog(val viewModel: ImageRecogViewModel)
{
    private val LOGTAG = this.javaClass.simpleName

    var detector: Detector<Any>? = null
    var image: InputImage? = null
    var mode = RECOG_TEXT

    private val scope = CoroutineScope(Dispatchers.Default)

    fun init(recogMode: Int)
    {
        scope.launch{
            detector?.close()
            setRecogMode(recogMode)
        }
    }

    fun close()
    {
        scope.launch() {
            detector?.close()
            detector = null
        }
    }

    open fun setRecogMode(recogMode: Int)
    {
        mode = recogMode
    }

    fun recognize(bitmap: Bitmap)
    {
        scope.launch {
            viewModel.setBitmap(bitmap)
           // presume camera/gallery image rotated correctly:
            image = InputImage.fromBitmap(bitmap, 0)
            recognize()
        }
    }

    fun rescan(mode: Int)
    {
        scope.launch {
            init(mode)
            recognize(delay = 500L)
        }
    }

    private suspend fun recognize(delay: Long = 0L )
    {
        viewModel.clearRecogs()

        image?.let {
            delay(delay)
            recog(it)
        }
    }

    // stub:
    open fun recog(image: InputImage) {}
}