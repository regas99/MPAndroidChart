package com.xxmassdeveloper.mpchartexample.custom.marker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log

import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.StackEntry
import com.github.mikephil.charting.formatter.SimpleHourFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.StackHighlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.custom_marker_stackchart.view.*

/**
 * Custom implementation of the MarkerView.
 *
 * @author Tom Sager
 */
@SuppressLint("ViewConstructor")
class StackMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    var logEnabled = true
    val formatter = SimpleHourFormatter()

    override fun refreshContent(e: Entry, highlight: Highlight) {

        if (e is StackEntry && highlight is StackHighlight) {
            val min: Float
            val max : Float
            val label : String
            if (highlight.itemIndex >= 0) {
                val item = e.getItem(highlight.unitIndex, highlight.itemIndex) ?: return
                min = item.min
                max = item.max
                label = "Item ${highlight.itemIndex}"
            } else if (highlight.unitIndex >= 0) {
                val unit = e.getUnit(highlight.unitIndex) ?: return
                min = unit.min
                max = unit.max
                label = "Unit  ${highlight.unitIndex}"
            } else {
                min = e.min
                max = e.max
                label = "Entry ${highlight.entryIndex}"
            }
            tvLabel.text = label
            val range = "${formatter.getFormattedValue(min)} -> ${formatter.getFormattedValue(max)}"
            tvBody.text = range
            if (logEnabled) Log.v(this.javaClass.simpleName, "refreshContent: $label -> $range")
            super.refreshContent(e, highlight)
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
