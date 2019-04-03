package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.StackDataSet
import com.github.mikephil.charting.data.StackEntry
import com.github.mikephil.charting.data.StackUnit

interface IStackDataSet : ILineScatterCandleRadarDataSet<StackEntry> {
    val entries : List<StackEntry>
    var showShadow: Boolean  // do we draw the shadow line
    var shadowWidth: Float   // width of shadow line
    var shadowColor: Int     // color of shadow line
    var stackSpace: Float    // spacing between elements

    // for the shadow
    val min: Float
    val max: Float

}

