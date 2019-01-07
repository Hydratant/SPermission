package com.sang.permission

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager

class PermissionActivity : AppCompatActivity() {

    companion object {
        /// Intent Parsing Key
        const val PERMISSIONS = "PERMISSIONS"
        const val DENY_TITLE = "DENY_TITLE"
        const val DENY_MESSAGE = "DENY_MESSAGE"

        // startActivityForResult Int
        private const val REQ_START = 10
        private const val REQ_PERMISSION = REQ_START
        private const val REQ_OVERLAY_PERMISSION = REQ_START + 1

        // Listener 허용 / 거부
        var onGranted: (() -> Unit)? = null
        var onDenied: ((deniedPermissions: ArrayList<String>) -> Unit)? = null
    }

    lateinit var mContext: Context
    lateinit var mActivity: Activity

    lateinit var permissions: Array<String>

    var denyTitle: CharSequence? = null
    var denyMessage: CharSequence? = null

    private val deniedPermissions = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mActivity = this
        mContext = mActivity
        init()
    }

    private fun init() {
        permissions = intent.getStringArrayExtra(PERMISSIONS)
        denyTitle = intent.getStringExtra(DENY_TITLE)
        denyMessage = intent.getStringExtra(DENY_MESSAGE)
        checkPermissions()
    }

    private fun checkPermissions() {
        permissions.forEach {
            if (it == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                val hasNotOverlayPermission = !hasOverlayPermission()
                if (hasNotOverlayPermission) {
                    startOverlay(REQ_OVERLAY_PERMISSION)
                } else {
                    resultGranted()
                }
                return
            }
        }
        ActivityCompat.requestPermissions(mActivity, permissions, REQ_PERMISSION)
    }


    private fun resultGranted() {
        onGranted?.invoke()
        finish()
    }

    //////////////////////////// Denied ////////////////////////////////////////
    private fun showDenied() {
        denyMessage?.let { message ->
            showDialog(title = denyTitle,
                    message = message,
                    positiveButtonText = "확인",
                    positiveListener = DialogInterface.OnClickListener { _, _ ->
                        resultDenied()
                    })
        } ?: resultDenied()
    }

    private fun resultDenied() {
        onDenied?.invoke(deniedPermissions)
        finish()
    }
    //////////////////////////// Denied ////////////////////////////////////////


    // Dialog
    private fun showDialog(
            title: CharSequence? = null,
            message: CharSequence? = null,
            positiveButtonText: CharSequence? = null,
            positiveListener: DialogInterface.OnClickListener? = null): android.support.v7.app.AlertDialog? {
        val dialog = AlertDialog.Builder(mContext).apply {
            if (title != null) setTitle(title)
            if (message != null) setMessage(message)
            if (positiveButtonText != null) setPositiveButton(positiveButtonText, positiveListener)
        }.create()

        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return null
        }
        if (isFinishing) {
            return null
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        return dialog
    }

    override fun finish() {
        onGranted = null
        onDenied = null
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            REQ_PERMISSION -> {
                permissions.forEach {
                    if (isDenied(it)) {
                        deniedPermissions.add(it)
                    }
                }
                resultPermission(permissions)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_OVERLAY_PERMISSION -> {
                val overlayPermission = android.Manifest.permission.SYSTEM_ALERT_WINDOW
                val hasNotOverlayPermission = !hasOverlayPermission()
                if (hasNotOverlayPermission) {
                    deniedPermissions.add(overlayPermission)
                    showDenied()
                } else {
                    resultGranted()
                }
            }
        }
    }

    private fun resultPermission(permissions: Array<out String>) {
        val isGranted = isGranted(*permissions)
        if (isGranted) resultGranted()
        else showDenied()
    }
}