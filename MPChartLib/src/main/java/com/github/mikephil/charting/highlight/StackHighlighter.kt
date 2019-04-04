package com.github.mikephil.charting.highlight

import android.util.Log
import com.github.mikephil.charting.charts.StackChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT
import com.github.mikephil.charting.components.YAxis.AxisDependency.RIGHT
import com.github.mikephil.charting.highlight.Highlight.Type.*
import com.github.mikephil.charting.interfaces.dataprovider.StackDataProvider
import com.github.mikephil.charting.utils.MPPointD
import com.github.mikephil.charting.utils.getIndexContaining
import com.github.mikephil.charting.utils.pp

private const val LOG_TAG = "StackHighlighter"
var logEnabled = true
class StackHighlighter(val chart : StackChart) : ChartHighlighter<StackDataProvider>(chart) {

    override fun getHighlight(xPix : Float, yPix : Float) : Highlight {
        val touchPoint = getValsForTouch(xPix, yPix)
        var xVal = touchPoint.x.toFloat()
        var yVal = touchPoint.y.toFloat()
        MPPointD.recycleInstance(touchPoint)
        if (logEnabled) Log.v("getHighlight", "touch ${xPix.pp()}, ${yPix.pp()}, data ${xVal.pp()}, ${yVal.pp()}")

        // look for an axis touch
        val axisType = when {
            chart.hasXAxis() && yPix > chart.contentRect.bottom -> X_AXIS
            chart.hasLeftAxis() && xPix < chart.contentRect.left -> LEFT_AXIS
            chart.hasRightAxis() && yPix > chart.contentRect.right -> RIGHT_AXIS
            else -> NULL
        }

        // if we have an axis highlight, return it
        if (axisType.isAxis()) {
            if (axisType.isRightAxis)  {
                // we assumed LEFT axis above; recompute touch for RIGHT axis
                val touch = getValsForTouch(xPix, yPix, YAxis.AxisDependency.RIGHT)
                xVal = touch.x.toFloat()
                yVal = touch.y.toFloat()
            }
            return Highlight(xVal, yVal, axisType, xPix, yPix)
        }

        // drill down to determine which element was touched: entry, unit or item
        chart.data.sets.mapIndexed set@{ setIndex, set ->
            val closestEntry = set.getEntryForXValue(xVal, yVal)
            val entryIndex = set.getEntryIndex(closestEntry)
            if (logEnabled) Log.v("getHighlight", "closestEntry to ${xVal.pp()}, ${yVal.pp()}: [$entryIndex] $closestEntry")

            val unitIndex = closestEntry.units.getIndexContaining(yVal)
            val unitClicked = closestEntry.units.getOrNull(unitIndex)
            if (logEnabled) Log.v("getHighlight", "  closestUnit: [$unitIndex] $unitClicked")

            val itemIndex = unitClicked?.items?.getIndexContaining(yVal) ?: -1
            val itemClicked = unitClicked?.getItem(itemIndex)
            if (logEnabled) Log.v("getHighlight", "    closestItem: [$itemIndex] $itemClicked")

            // build the value highlight
            val hl = StackHighlight(xVal, yVal, xPix, yPix, setIndex, entryIndex, unitIndex, itemIndex, set.axisDependency)
            super.mHighlightBuffer.add(hl)
            if (logEnabled) Log.v(LOG_TAG, "getHighlight: $hl")

            // return the single highlight closest to the touch point, or a "null" highlight if none were found
            return mHighlightBuffer.minBy { getDistance(xVal, 0f, it.x, 0f) }
                    ?: StackHighlight(0f, 0f, 0f, 0f, -1, -1, -1, -1, YAxis.AxisDependency.LEFT)
        }
        return Highlight(NULL)
    }

}