package com.xxmassdeveloper.mpchartexample.custom.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xxmassdeveloper.mpchartexample.R

const val TAG = "StackMenuFragment"
var logEnabled = true

class StackChartMenuFragment() : Fragment() {

    lateinit var root: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (logEnabled) Log.i(TAG, "onCreateView")
        root = inflater.inflate(R.layout.select_trip_types, container, false) as ViewGroup

    }

}