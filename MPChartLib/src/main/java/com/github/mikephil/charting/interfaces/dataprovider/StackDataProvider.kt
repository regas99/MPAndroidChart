package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.StackData
import com.github.mikephil.charting.interfaces.datasets.IStackDataSet
import com.github.mikephil.charting.renderer.AxisRenderer

interface StackDataProvider : BarLineScatterCandleBubbleDataProvider {
    fun stackData() : StackData
}