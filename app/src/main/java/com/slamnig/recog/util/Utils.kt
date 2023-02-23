package com.slamnig.recog.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.exifinterface.media.ExifInterface
import java.io.IOException
import java.io.InputStream

class Utils
{
    companion object {
        private const val LOGTAG = "Utils.kt"

        /**
         * @param context Current context.
         * @return Screen width in pixels
         */
        fun getScreenWidth(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        /**
         * @param context Current context.
         * @return Screen height in pixels
         */
        fun getScreenHeight(context: Context): Int {
            return context.resources.displayMetrics.heightPixels
        }

        /**
         * @param context Current context.
         * @return Shorter screen dimension in pixels
         */
        fun getScreenMinWidth(context: Context): Int {
            val dm = context.resources.displayMetrics

            return Math.min(dm.widthPixels, dm.heightPixels)
        }

        /**
         * @param bmpIn Source bitmap.
         * @param degrees Rotation degrees.
         * @param recycle True to recycle source bitmap.
         * @return Rotated bitmap.
         */
        fun rotateBitmap(bmpIn: Bitmap, degrees: Int, recycle: Boolean): Bitmap? {
            if (degrees == 0)
                return bmpIn

            val mat = Matrix()
            mat.postRotate(degrees.toFloat())
            var bmpOut = Bitmap.createBitmap(bmpIn, 0, 0, bmpIn.width, bmpIn.height, mat, false)

            if (recycle == true) {
                if (bmpOut != null) bmpIn.recycle() else bmpOut =
                    bmpIn // on failure return original bitmap
            }
            return bmpOut
        }

        /**
         * @param path JPEG path.
         * @return Rotation degrees.
         */
        fun getOrientationDegreesFromFile(path: String?): Int {
            val exif: ExifInterface
            val orientation: Int

            path?.let { _path ->
                exif = try {
                    ExifInterface(_path)
                } catch (e: IOException) {
                    Log.w(LOGTAG, "getOrientationDegreesFromFile()", e)
                    return 0
                }
                orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                return exifToDegrees(orientation)
            }

            return 0
        }

        /**
         * @param uri JPEG Uri.
         * @return Rotation degrees.
         */
        fun getOrientationDegreesFromUri(context: Context, uri: Uri?): Int {
            val istream: InputStream?
            val exif: ExifInterface
            val orientation: Int

            uri?.let { _uri ->
                try {
                    istream = context.contentResolver.openInputStream(_uri)
                    exif = ExifInterface(istream!!)
                } catch (e: IOException) {
                    Log.w(LOGTAG, "getOrientationDegreesFromUri()", e)
                    return 0
                }
                orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                try {
                    istream.close()
                } catch (e: IOException) {
                    // ignore:
                    Log.w(LOGTAG, "getOrientationDegreesFromUri()", e)
                }
                return exifToDegrees(orientation)
            }

            return 0
        }

        private fun exifToDegrees(exifOrientation: Int): Int {
            var degrees = 0
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degrees = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degrees = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degrees = 270
            }
            return degrees
        }

        /**
         * Create a scaled bitmap with a maximum pixel count.
         *
         * @param bitmap Source bitmap.
         * @param maxPixels Maximum pixel count.
         * @return Scaled bitmap.
         */
        fun maxPixelBitmap(bitmap: Bitmap, maxPixels: Int): Bitmap
        {
            val max: Float = maxPixels.toFloat()
            var width: Float = bitmap.width.toFloat()
            var height: Float = bitmap.height.toFloat()

            if(width * height > max) {
                val f = Math.sqrt((max / (width * height)).toDouble()).toFloat()
                width *= f
                height *= f
            }

            val scaled = Bitmap.createScaledBitmap(
                bitmap,
                Math.round(width), Math.round(height), true
            )

            if(scaled != bitmap && bitmap.isRecycled == false)
                bitmap.recycle()

            return scaled
        }

        /**
         * Set full screen mode.
         *
         * @param activity Calling Activity.
         * @param drawUnderSystemBars If true, draw under system bars.
         * @param hideNavigation If true, hide system and navigation bars, else hide only system bar.
         * @param drawInCutoutRegion If true, draw in cutout region.
         * @param drawContentInCutout If true, draw content in cutout region.
         * @param setNavigationBarColor If true, set navigation bar color.
         * @param navigationBarColor Nvigation bar color.
         */
        @Suppress("DEPRECATION")
        fun fullScreen(
            activity: Activity,
            drawUnderSystemBars: Boolean = false,
            hideNavigation: Boolean = false,
            drawInCutoutRegion: Boolean = false,
            drawContentInCutout: Boolean = false,
            setNavigationBarColor: Boolean = false,
            navigationBarColor: Int = Color.BLACK
        )
        {
            val w = activity.window

            // hide system bar:
            if (Build.VERSION.SDK_INT < 30) {
                w.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
            else{
                // draw under system bars:
                if(drawUnderSystemBars)
                    WindowCompat.setDecorFitsSystemWindows(w, false)

                WindowInsetsControllerCompat(w, w.decorView.findViewById(android.R.id.content)).let {
                    controller ->

                    if(hideNavigation)
                        // hide status and navigation:
                        controller.hide(WindowInsetsCompat.Type.systemBars())
                    else
                        // hide status bar only:
                        controller.hide(WindowInsetsCompat.Type.statusBars())

                    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }


            // draw in cutout region:
            if(drawInCutoutRegion) {
                if (Build.VERSION.SDK_INT >= 28) {
                    if(drawContentInCutout == true)
                        // draw content in cutout area:
                        w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    w.attributes.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }

            // set navigation bar color:
            if(setNavigationBarColor)
                w.navigationBarColor = navigationBarColor
        }

        /**
         * Prevent sleep
         *
         * @param a Calling Activity.
         * @param set True to keep screen on, false to allow sleep
         */
        fun keepScreenOn(a: Activity, set: Boolean) {
            if (set == true) a.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) else a.window.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
}