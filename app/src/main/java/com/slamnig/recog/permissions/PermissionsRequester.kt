package com.slamnig.recog.permissions

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionsRequester(val activity: ComponentActivity, val listener: PermissionsRequesterListener)
{
    private val LOGTAG = this.javaClass.simpleName

    interface PermissionsRequesterListener{
        fun onGranted()
        fun onDenied()
        fun onDeniedPermanently()
    }

    companion object {
        private const val DENIED = "denied"
        private const val EXPLAINED = "explained"
    }

    fun checkPermissions(list: Array<String>) : Boolean
    {
        for(permission in list){
            if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
                return false
        }

        return true;
    }

    fun requestPermissions(list: Array<String>)
    {
        requestMultiplePermissions.launch(list)
    }

    private val requestMultiplePermissions =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { result: Map<String, Boolean> ->

            val deniedList: List<String> = result.filter {
                !it.value
            }.map {
                it.key
            }

            if(Build.VERSION.SDK_INT >= 23)
            {
                if(deniedList.isNotEmpty())
                {
                    val map = deniedList.groupBy { permission ->
                        if (activity.shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                    }

                    if(map[EXPLAINED] != null) {
                        Log.d(LOGTAG, "Permissions permanently denied")
                        listener.onDeniedPermanently()
                    }
                    else{
                        Log.d(LOGTAG, "Permissions denied")
                        listener.onDenied()
                    }
                }
                else{
                    Log.d(LOGTAG, "All permissions granted")
                    listener.onGranted()
                }
            }
            // SDK 22 and lower:
            else
            {
                if(deniedList.isNotEmpty()){
                    Log.d(LOGTAG, "Permissions denied")
                    listener.onDenied()
                }
                else{
                    Log.d(LOGTAG, "All permissions granted")
                    listener.onGranted()
                }
            }
        }
}