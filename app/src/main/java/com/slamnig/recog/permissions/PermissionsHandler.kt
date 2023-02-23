package com.slamnig.recog.permissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.slamnig.recog.R
import com.slamnig.recog.dialog.data.AlertData
import com.slamnig.recog.dialog.fragment.AlertBox
import com.slamnig.recog.dialog.listener.AlertListener

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
                                //addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                //addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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