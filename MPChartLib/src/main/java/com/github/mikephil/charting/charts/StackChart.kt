package com.github.mikephil.charting.charts

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.data.StackData
import com.github.mikephil.charting.data.StackDataSet
import com.github.mikephil.charting.highlight.StackHighlighter
import com.github.mikephil.charting.interfaces.dataprovider.StackDataProvider
import com.github.mikephil.charting.interfaces.datasets.IStackDataSet
import com.github.mikephil.charting.renderer.StackChartRenderer

/**
 * A chart for stacks.
 *
 * Values display is controlled by
 *      dataSet.isDrawValuesEnabled,
 *      stackUnit.drawShadowValues, and
 *      stackItem.drawValues.
 * The number of values to display gets high pretty quickly if all three are true.
 * Be sure to set chart.maxVisibleCount accordingly.
 *
 */
class StackChart : BarLineChartBase<StackData>, StackDataProvider {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
            : super(context, attrs, defStyle)

    override fun init() {
        super.init()
        mRenderer = StackChartRenderer(this, animator, viewPortHandler)
        setHighlighter(StackHighlighter(this))
        xAxis!!.spaceMin = 0.5f
        xAxis!!.spaceMax = 0.5f
    }

    override fun stackData(): StackData {
        return mData
    }

    fun copy() : StackData {
        return StackData(*mData.sets.map { (it as StackDataSet).copy() as IStackDataSet }.toTypedArray())
    }
}