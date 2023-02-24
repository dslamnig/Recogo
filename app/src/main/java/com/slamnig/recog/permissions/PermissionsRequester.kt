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

package com.slamnig.recog.permissions

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * Permissions requests.
 */
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