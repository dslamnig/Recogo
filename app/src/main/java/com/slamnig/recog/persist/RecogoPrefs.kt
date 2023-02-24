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

package com.slamnig.recog.persist

import android.content.Context
import com.slamnig.recog.*

/**
 * Recogo shared preferences.
 */
public class RecogoPrefs(
    context: Context
) : Prefs(
    context = context,
    prefsName = "RecogoPrefs"
){
    companion object{
        const val RECOG_MODE = "RecogMode"
        const val RECOG_MODE_DEFAULT = RECOG_TEXT

        const val CAMERA_FACING = "CameraFacing"
        const val CAMERA_FACING_DEFAULT = BACK_CAMERA

        const val ALPHA_SLIDER_POSITION = "AlphaSliderPosition"
        const val ALPHA_SLIDER_POSITION_DEFAULT = 0.5f

        private var instance: RecogoPrefs? = null

        fun getInstance(context: Context): RecogoPrefs
        {
            if(instance == null)
                instance = RecogoPrefs(context)

            return instance!!
        }
    }

    fun getRecogMode(): Int
    {
        return getOptionInt(RECOG_MODE, RECOG_MODE_DEFAULT)
    }

    fun setRecogMode(mode: Int)
    {
        setOptionInt(RECOG_MODE, mode)
    }

    fun getCameraFacing(): Int
    {
        return getOptionInt(CAMERA_FACING, CAMERA_FACING_DEFAULT)
    }

    fun setCameraFacing(facing: Int)
    {
        setOptionInt(CAMERA_FACING, facing)
    }

    fun getAlphaSliderPosition(): Float
    {
        return getOptionFloat(ALPHA_SLIDER_POSITION, ALPHA_SLIDER_POSITION_DEFAULT)
    }

    fun setAlphaSliderPosition(position: Float)
    {
        setOptionFloat(ALPHA_SLIDER_POSITION, position)
    }
}