package com.sang.permission.demo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sang.permission.permission

class MainFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {

            onGranted = {
                Log.i("TAG", "onGranted")

            }

//            onDenied = {
//
//            }

        }
    }
}