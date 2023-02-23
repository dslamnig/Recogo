package com.slamnig.recog.persist

import android.content.Context
import com.slamnig.recog.*

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