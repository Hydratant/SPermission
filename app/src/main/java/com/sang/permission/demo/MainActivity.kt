package com.sang.permission.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sang.permission.permission

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    fun test() {
        val test = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
        permission {
            permissions = test
            onGranted = {
                Log.i(TAG, "onGranted Call")
            }
            onDenied = {
                Log.i(TAG, "onDenied Call")
            }
            denyMessage = "DenyMessage" // 필수 아님
            denyTitle = "타이틀이지롱"  // 필수 아님
        }
    }
}
