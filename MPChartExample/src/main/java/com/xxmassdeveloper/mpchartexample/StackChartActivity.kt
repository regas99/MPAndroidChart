package com.xxmassdeveloper.mpchartexample

import android.graphics.Color

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.SimpleHourFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.StackHighlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.custom.alert.StackChartMenu
import com.xxmassdeveloper.mpchartexample.custom.marker.StackMarkerView
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlinx.android.synthetic.main.activity_stackchart.*


/**
 * A simple Stack Chart Example
 */
class StackChartActivity : DemoBase() {
    private val chartMin = 8f    // 9:00AM
    private val chartMax = 17f   // 5:00 PM

    val labels = arrayOf("Entry 1", "Entry 2", "Entry 3", "Entry 4")
    val colors = IntArray(16)
    val yFormatter = SimpleHourFormatter()
    val xFormatter = IndexAxisValueFormatter(labels)

    lateinit var optionsMenu: StackChartMenu
    var firstMenuPass = false

    var logEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_stackchart)
        title = "StackChartActivity"

        // hide the seek bars, etc, which are not used in this activity
        seek_group.visibility = View.GONE

        // build colors
        System.arraycopy(ColorTemplate.MATERIAL_COLORS_16, 0, colors, 0, 16)

        // build marker view to display on highlight
        val markerView = StackMarkerView(this, R.layout.custom_marker_stackchart)

        with (chart) {
            isLogEnabled = true
            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            legend.isEnabled = false
            setMaxVisibleValueCount(60)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setTouchEnabled(true)
            markerView.chartView = this
            marker = markerView
            this.setOnChartValueSelectedListener(valueSelectedListener)

            with (renderer) {
                paintValues.color = Color.BLUE
                paintValues.textSize = 20f
            }

            with(xAxis!!) {
                position = BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = xFormatter
                textSize = 15f
                extraBottomOffset = 10f
            }
            with(rendererXAxis) {
                setHighlightTextColor(Color.RED)
                setHighlightFillColor(Color.BLACK)
            }

            with(axisLeft) {
                // set axis label count to 1 + the number of hours displayed
                val range = Math.ceil(chartMax.toDouble()) - Math.floor(chartMin.toDouble())
                setLabelCount(range.toInt() + 1, true)
                setDrawGridLines(true)
                setDrawAxisLine(true)
                axisMinimum = chartMin
                axisMaximum = chartMax
                valueFormatter = yFormatter
            }

            with (axisRight) {
                isEnabled = false
            }
        }
        val data = makeData()
        data.valueFormatter = yFormatter
        displayData(data)
    }

    /**
     * Makes labels.size StackUnits with pre-defined data, then as many
     * additional randomized units as needed
     */
//    private fun makeData(): StackDataSet {
//
//        val star = if (Build.VERSION.SDK_INT >= 21)
//            resources.getDrawable(R.drawable.star, theme)
//        else resources.getDrawable(R.drawable.star)
//        val plus = if (Build.VERSION.SDK_INT >= 21)
//            resources.getDrawable(R.drawable.plus, theme)
//        else resources.getDrawable(R.drawable.plus)
//        val launch = if (Build.VERSION.SDK_INT >= 21)
//            resources.getDrawable(R.drawable.ic_launcher, theme)
//        else resources.getDrawable(R.drawable.ic_launcher)
//
//        val entries: MutableList<StackEntry> = mutableListOf()
//        var colorIndex = 0
//        for (i in 0 until labels.size) {
//            val x = i.toFloat()
//            val entry = when (i) {
//                0 -> {  // one unit, one item
//                    val item = StackItem(x, 10f, 14f, colors[colorIndex++], icon = star)
//                    val unit = StackUnit(9f, 15f, item)
//                    unit.drawShadows = false
//                    StackEntry(x, 8f, 16f, unit, icon = launch, shadowColor = Color.GREEN)
//                }
//
//                1 -> { // one unit, two items
//                    val items: MutableList<StackItem> = mutableListOf()
//                    items.add(StackItem(x, 9.333f, 10f, colors[colorIndex++], icon = star))
//                    items.add(StackItem(x, 10.33333f, 12.3f, colors[colorIndex++], icon = star))
//                    items.add(StackItem(x, 13.3f, 14.2f, colors[colorIndex++], icon = star))
//                    val unit = StackUnit(9f, 15.23f, items, icon = plus)
//                    StackEntry(x, 8.5f, 15.64f, unit, icon = launch)
//                }
//
//                2 -> {  // two units, multiple items
//                    val units = mutableListOf<StackUnit>()
//                    // AM only
//                    val amItems: MutableList<StackItem> = mutableListOf()
//                    amItems.add(StackItem(x, 8.1f, 8.75f, colors[colorIndex++], icon = star))
//                    amItems.add(StackItem(x, 9.25f, 10.3f, colors[colorIndex++], icon = star))
//                    amItems.add(StackItem(x, 10.5f, 11.333f, colors[colorIndex++], icon = star))
//                    units.add(StackUnit(8f, 12f, items = amItems, icon = plus))
//                    // PM only
//                    val pmItems: MutableList<StackItem> = mutableListOf()
//                    pmItems.add(StackItem(x, 13f, 14.1f, colors[colorIndex++], icon = star))
//                    pmItems.add(StackItem(x, 15.25f, 16.3f, colors[colorIndex++], icon = star))
//                    pmItems.add(StackItem(x, 16.5f, 16.8f, colors[colorIndex++], icon = star))
//                    units.add(StackUnit(13f, 17f, items = pmItems, icon = plus))
//                    StackEntry(x, 8f, 17f, units, icon = launch)
//                }
//
//                3 -> {  // item extends past unit - should this be allowed?
//                    val items: MutableList<StackItem> = mutableListOf()
//                    items.add(StackItem(x, 9.125f, 11.3f, colors[colorIndex++], icon = star))
//                    items.add(StackItem(x, 12.25f, 15.9f, colors[colorIndex++], icon = star))
//                    StackEntry(x, 10f, 13f, StackUnit(8f, 16.25f, items, icon = plus), icon = launch)
//                }
//                else -> {
//                    val items: MutableList<StackItem> = mutableListOf()
//                }
//            }
//        }
//        return StackDataSet(chartMin, chartMax, entries, "label")
//    }

    private fun makeData() : StackDataSet {
        val entries : MutableList<StackEntry> = mutableListOf()
        val units : MutableList<StackUnit> = mutableListOf()
        var colorIndex = 0
        for (x in 0 until labels.size) {
            val entry = when (x) {
                0 -> {
                    val items : MutableList<StackItem> = mutableListOf()
                    items.add(StackItem(x.toFloat(), 9.333f, 10f, colors[colorIndex++]))
                    items.add(StackItem(x.toFloat(), 10.33333f, 12.3f, colors[colorIndex++]))
                    items.add(StackItem(x.toFloat(), 13.3f, 14.2f, colors[colorIndex++]))
                    StackEntry(0f, 8.5f, 15.64f, StackUnit(9f, 15.23f, items))
                }
                1 -> {  // two StackUnit's
                    val units = mutableListOf<StackUnit>()
                    // AM only
                    val amItems : MutableList<StackItem> = mutableListOf()
                    amItems.add(StackItem(x.toFloat(), 8.1f, 8.75f, colors[colorIndex++]))
                    amItems.add(StackItem(x.toFloat(), 9.25f, 10.3f, colors[colorIndex++]))
                    amItems.add(StackItem(x.toFloat(), 10.5f, 11.333f, colors[colorIndex++]))
                    units.add(StackUnit(8f, 12f, amItems))
                    // PM only
                    val pmItems : MutableList<StackItem> = mutableListOf()
                    pmItems.add(StackItem(x.toFloat(), 13f, 14.1f, colors[colorIndex++]))
                    pmItems.add(StackItem(x.toFloat(), 15.25f, 16.3f, colors[colorIndex++]))
                    pmItems.add(StackItem(x.toFloat(), 16.5f, 16.8f, colors[colorIndex++]))
                    units.add(StackUnit(13f, 17f, pmItems))
                    StackEntry(1f, 8f, 17f, units)
                }
                2 -> {
                    val item = StackItem(2f, 9f, 14f, colors[colorIndex++])
                    val unit = StackUnit(8f, 15f, item)
                    unit.drawShadows = false
                    StackEntry(x.toFloat(), 8f, 16f, unit)
                }
                3 -> {
                    val items : MutableList<StackItem> = mutableListOf()
                    items.add(StackItem(x.toFloat(), 9.125f, 11.3f, colors[colorIndex++]))
                    items.add(StackItem(x.toFloat(), 12.25f, 15.9f, colors[colorIndex++]))
                    StackEntry(x.toFloat(), 8.125f, 17f, StackUnit(8f, 16.25f, items))
                }
                else -> {
                    val items : MutableList<StackItem> = mutableListOf()
                    val range = chartMax - chartMin
                    val points = List(4 * 2 + 2) { Math.random() * range - chartMin }
                    val sorted = points.sorted()
                    val low = sorted[0].toFloat()
                    val high = sorted.last().toFloat()
                    for (i in 0 until points.size step 2) {
                        val color = colors[(i + i) % 15]
                        val bottom = sorted[i].toFloat()
                        val top = sorted[i + 1].toFloat()
                        val item = StackItem(x.toFloat(), bottom, top, color)
                        items.add(item)
                    }
                    StackEntry(x.toFloat(), Math.min(low - 1, chartMin), Math.max(high + 1, chartMax), StackUnit(low, high, items))
                }
            }

            entry.icon = ContextCompat.getDrawable(this, R.drawable.star)

            entry.units.map { unit ->
                unit.drawShadows = true
                unit.items.map { item ->
                    item.drawIcon = true
                    item.icon = ContextCompat.getDrawable(this, R.drawable.plus)
                }
            }
            entries.add(entry)
        }
        return StackDataSet(chartMin, chartMax, entries, "label")
    }

    private fun displayData(set : StackDataSet) {
        with (set) {
            axisDependency =YAxis.AxisDependency.LEFT

            setDrawIcons(true)
        }
        val data = StackData(set)
        chart.data = data
        chart.invalidate()
    }

    private val valueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry, h: Highlight) {
            val hl = h as StackHighlight
            if ( !hl.isValue || hl.dataSetIndex < 0 || hl.entryIndex < 0)
                return

            if (hl.itemIndex >= 0 && hl.unitIndex >= 0) {
                Log.d("StackChartActivity", "onValueSelected - highlight item")
                doXHighlight(hl)
            } else if (hl.unitIndex >= 0) {
                Log.d("StackChartActivity", "onValueSelected - highlight unit")
                undoXHighlight()
            } else if (hl.entryIndex >= 0) {
                Log.d("StackChartActivity", "onValueSelected - highlight entry")
                undoXHighlight()
            } else {
                Log.d("StackChartActivity", "unknown highlight: $hl")
                }
            }

        override fun onNothingSelected() {
            Log.e("StackChartActivity", "onNothingSelected")
            undoXHighlight()
        }
    }

    override fun saveToGallery() {
        saveToGallery(chart, "StackChartActivity")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.e("StackChartActivity", "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.foo, menu)  // have to inflate something to get the ...
        firstMenuPass = true
        optionsMenu = StackChartMenu(this, chart, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu) : Boolean {

        if (firstMenuPass) {
            firstMenuPass = false
            return true
        }

        val set = chart.data.sets.firstOrNull()
        val entry = set?.entries?.firstOrNull()
        val unit = entry?.units?.firstOrNull()
        val item = unit?.items?.firstOrNull()

        //

        optionsMenu.setDrawStates(StackChartMenu.GROUP.VALUES,
                entry?.drawValues ?: false,
                unit?.drawValues ?: false,
                item?.drawValues ?: false)

        optionsMenu.setDrawStates(StackChartMenu.GROUP.ICONS,
                entry?.drawIcon ?: false,
                unit?.drawIcon ?: false,
                item?.drawIcon ?: false)

        optionsMenu.setDrawStates(StackChartMenu.GROUP.SHADOWS,
                entry?.drawShadow ?: false,
                unit?.drawShadows ?: false,
                false) //item?.drawShadow ?: false)

        optionsMenu.showDialog()
        return true
    }

    private fun doXHighlight(hl: Highlight) {
        fadeByXDistance(hl.x)    // redraw the items
    }

    /**
     * Fades items by distance from x axis highlight.
     */
    private fun fadeByXDistance(x : Float) {
        val maxAlpha = 0x80      // alpha of closest entry
        val minAlpha = 0x10      // alpha of farthest entry
        val maxDistance = Math.max(x - chart.xChartMin, chart.xChartMax - x)
        val step = (maxAlpha - minAlpha) / (maxDistance - 1f)
        chart.data.sets.map { it.entries.map { entry ->
            val distance = Math.abs(entry.x - x)
            val alpha = if (distance < 0.1f) 0xff else (maxAlpha - (distance - 1) * step).toInt()
            if (logEnabled) Log.v("StackChartActivity", "distance= $distance, alpha = ${Integer.toHexString(alpha)}")
            entry.units.map{entry ->
                entry.items.map { item ->
                    item.color = item.color and 0xffffff or alpha.shl(24)}}
        }}
    }
    private fun undoXHighlight() {
        chart.data.sets.map { set -> set.entries.map { entry -> entry.units.map { unit -> unit.items.map {
            it.color = ColorUtils.setAlphaComponent(it.color, 0xff)}}
        }}
        chart.xAxis!!.highlights.clear()
    }

    private fun restoreColors(colors: List<Int>) {
        var colorIndex = 0
        chart.data.sets.map { set -> set.entries.map { entry -> entry.units.map { unit -> unit.items.map { item ->
            item.color = colors[colorIndex++] }}}}
    }

}