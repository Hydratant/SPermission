package com.sang.permission

import android.content.Context
import android.content.Intent
import android.os.Build
import java.lang.IllegalArgumentException


class SPermission {

    class Builder(private val context: Context) {
        var permissions: Array<String>? = null
        var onGranted: (() -> Unit)? = null
        var onDenied: ((deniedPermissions: ArrayList<String>) -> Unit)? = null

        var denyTitle: CharSequence? = null      // 거부 Alert Title
        var denyMessage: CharSequence? = null    // 거부 Alert Message

        fun check() {
            if (permissions == null) {
                throw IllegalArgumentException("permission is Null!!")
            }

            if (onGranted == null) {
                throw IllegalArgumentException("onGranted is Null")
            }

            if (onDenied == null) {
                throw IllegalArgumentException("onDenied is Null")
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                onGranted?.invoke()
                return
            }

            val isGrant = context.isGranted(*permissions!!)
            if (isGrant) {
                onGranted?.invoke()
                return
            }

            //////////////// Permission Activity Start/////////////////////////
            val intent = Intent(context, PermissionActivity::class.java)
            intent.apply {
                putExtra(PermissionActivity.PERMISSIONS, permissions)
                putExtra(PermissionActivity.DENY_TITLE, denyTitle)
                putExtra(PermissionActivity.DENY_MESSAGE, denyMessage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            PermissionActivity.onGranted = onGranted
            PermissionActivity.onDenied = onDenied
            context.startActivity(intent)
            //////////////// Permission Activity Start/////////////////////////
        }
    }
}
