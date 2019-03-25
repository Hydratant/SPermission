package com.sang.permission

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat

const val REQ_PERMISSION_SETTING = 20

fun Context.isGranted(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (isDenied(permission)) {
            return false
        }
    }
    return true
}

fun Context.isGranted(permission: String): Boolean = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
fun Context.isDenied(permission: String): Boolean = !isGranted(permission)


fun Activity.startPermissionSetting() {
    val settingIntent = getSettingIntent()
    this.startActivityForResult(settingIntent, REQ_PERMISSION_SETTING)
}


private fun Context.getSettingIntent(): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.parse("package:" + this@getSettingIntent.packageName)
        }


inline fun Activity.permission(setting: SPermission.Builder.() -> Unit) {
    SPermission.Builder(this).apply {
        setting()
        check()
    }
}

inline fun Activity.permission(vararg permissions: String, setting: SPermission.Builder.() -> Unit) {
    SPermission.Builder(this).apply {
        this.permissions = permissions
        setting()
        check()
    }
}

@TargetApi(Build.VERSION_CODES.M)
fun Activity.startOverlay(requestCode: Int) {
    val intent = getOverlayIntent()
    startActivityForResult(intent, requestCode)
}

@TargetApi(Build.VERSION_CODES.M)
private fun Context.getOverlayIntent(): Intent =
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:" + this@getOverlayIntent.packageName)
        }

fun Context.hasOverlayPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(this)
    else true
}

