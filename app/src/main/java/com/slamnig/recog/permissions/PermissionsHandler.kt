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

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.slamnig.recog.R
import com.slamnig.recog.dialog.data.AlertData
import com.slamnig.recog.dialog.fragment.AlertBox
import com.slamnig.recog.dialog.listener.AlertListener

/**
 * Activity permissions helper.
 */
class PermissionsHandler(
    val activity: FragmentActivity,
    val permissions: Array<String>
){
    private val LOGTAG = this.javaClass.simpleName

    private val requester = PermissionsRequester(
        activity,
        object: PermissionsRequester.PermissionsRequesterListener {
            override fun onGranted() {

            }

            override fun onDenied() {
                AlertBox().show(
                    activity,
                    AlertData(
                        activity.getString(R.string.permissions_denied),
                        activity.getString(R.string.permissions_close_app),
                        activity.getString(R.string.permissions_ok_but),
                        null,
                    ),
                    object : AlertListener {
                        override fun onOk() {
                            activity.finish()
                        }

                        override fun onCancel() {
                            activity.finish()
                        }
                    }
                )
            }

            override fun onDeniedPermanently() {
                AlertBox().show(
                    activity,
                    AlertData(
                        activity.getString(R.string.permissions_permanently_denied),
                        activity.getString(R.string.permissions_enable_in_settings),
                        activity.getString(R.string.permissions_ok_but),
                        activity.getString(R.string.permissions_cancel_but)
                    ),
                    object : AlertListener{
                        override fun onOk() {
                            // Take user to app settings:
                            activity.startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", activity.getPackageName(), null)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                // addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                // addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            })
                            activity.finish()
                        }

                        override fun onCancel() {
                            activity.finish()
                        }
                    }
                )
            }
        }
    )

    fun checkPermissions() : Boolean
    {
        return requester.checkPermissions(permissions)
    }

    fun requestPermissions()
    {
        if(requester.checkPermissions(permissions) == false)
            requester.requestPermissions(permissions)
    }
}