package com.xxmassdeveloper.mpchartexample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlinx.android.synthetic.main.activity_stackchart.*

/**
 * StackCart was written assuming that Units do not overlap with
 * an Element, and Items do not overlap within a Unit.
 * This activity violates that assumption in an attempt
 * to explore the effects of violating those assumptions.
 */
class StackChartOverlapActivity : DemoBase() {
    private val chartMin = 0f
    private val chartMax = 100f

    val numEntries = 3
    val colors = IntArray(16)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_stackchart)
        title = "Overlap Stack Chart"

        // hide the seek bars, etc, which are not used in this activity
        seek_group.visibility = View.GONE

        // build colors
        System.arraycopy(ColorTemplate.MATERIAL_COLORS_16, 0, colors, 0, 16)

        with (chart) {
            isLogEnabled = true
            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            legend.isEnabled = false
            setMaxVisibleValueCount(60)
            setPinchZoom(false)
            setDrawGridBackground(false)

            with (xAxis!!) {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }

            with (axisLeft) {
                setLabelCount(7, false)
                setDrawGridLines(true)
                setDrawAxisLine(true)
                axisMinimum = chartMin
                axisMaximum = chartMax
            }

            with (axisRight) {
                isEnabled = false
            }
        }
        val data = makeData()
//        xMaxTV.text = chartMax.toInt().toString()
//        xMinTV.text = chartMin.toInt().toString()
        val count = data.entries.map { entry -> entry.units.map { unit -> unit.items.size } }.fold(0) { total, next -> total + next.size }
        //xNumItemsTV.text = count.toString()
        displayData(data)
    }

    /**
     * Makes labels.size StackUnits with pre-defined data, then as many
     * additional randomized units as needed
     */
    private fun makeData() : StackDataSet {
        val entries : MutableList<StackEntry> = mutableListOf()
        val units : MutableList<StackUnit> = mutableListOf()
        val items : MutableList<StackItem> = mutableListOf()
        for (x in 0 until numEntries) {
            var entry: StackEntry? = null
            when (x) {
                0 -> {
                    // items overlap from 50 to 60, and there are two copies at 0 and 1
                    items.add(StackItem(0f, 33f, 60f, colors[(x) % 15]))
                    items.add(StackItem(0f, 50f, 75f, colors[(x + 1) % 15]))
                    units.add(StackUnit(20f, 70f, items))
                    val entry0 = StackEntry(0f, 40f,90f, units)
                    entries.add(entry0)
                    units.clear()
                    items.clear()

                    items.add(StackItem(1f, 33f, 60f, colors[(x + 2) % 15]))
                    items.add(StackItem(1f, 50f, 75f, colors[(x + 3) % 15]))
                    units.add(StackUnit(20f, 70f, items))
                    val entry1 = StackEntry(1f, 40f,90f, units)
                    entries.add(entry1)
                    units.clear()
                    items.clear()
                }
                1 -> {
                    // units overlap from 45 to 50, and there are two copies
                    items.add(StackItem(2f, 10f, 20f, colors[(x + 4) % 15]))
                    items.add(StackItem(2f, 30f, 40f, colors[(x + 5) % 15]))
                    units.add(StackUnit(5f, 60f, items))
                    items.clear()
                    items.add(StackItem(2f, 50f, 60f, colors[(x + 6) % 15]))
                    items.add(StackItem(2f, 70f, 90f, colors[(x + 7) % 15]))
                    units.add(StackUnit(45f, 95f, items))
                    val entry2 = StackEntry(2f, 40f,90f, units)
                    entries.add(entry2)

                    items.add(StackItem(3f, 10f, 20f, colors[(x + 4) % 15]))
                    items.add(StackItem(3f, 30f, 40f, colors[(x + 5) % 15]))
                    units.add(StackUnit(5f, 65f, items))
                    items.clear()
                    items.add(StackItem(3f, 50f, 60f, colors[(x + 6) % 15]))
                    items.add(StackItem(3f, 70f, 90f, colors[(x + 7) % 15]))
                    units.add(StackUnit(45f, 95f, items))
                    val entry3 = StackEntry(3f, 40f,90f, units)
                    entries.add(entry3)
                    units.clear()
                    items.clear()
                }
                2 -> {
                    val items : MutableList<StackItem> = mutableListOf()
                    items.add(StackItem(5f, 31.25f, 63.3f, colors[(x + 8) % 15]))
                    items.add(StackItem(5f, 64.5f, 85.0f, colors[(x + 9) % 15]))
                    units.add(StackUnit(30f, 72.75f, items))
                    val entry5 = StackEntry(5f, 25f,92f, units)
                    entries.add(entry5)
                    entry = StackEntry(3f, 25f,92f, units)
                    units.clear()
                    items.clear()
                }
                else -> { }
            }

            // alternate between an entry icon and item icons
            if (x % 2 == 0)
                entry?.icon = ContextCompat.getDrawable(this, R.drawable.star)
            else
                units.map { it.items.map { it.icon = ContextCompat.getDrawable(this, R.drawable.plus) } }
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

    override fun saveToGallery() {
        saveToGallery(chart, "StackChartActivity")
    }


}