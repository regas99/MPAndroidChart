package com.xxmassdeveloper.mpchartexample

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.SimpleHourFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.StackHighlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.pp
import com.xxmassdeveloper.mpchartexample.custom.StackMarkerView
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlinx.android.synthetic.main.xxx_activity_stackchart.*

class StackChartDayActivity : DemoBase(), OnChartValueSelectedListener, SeekBar.OnSeekBarChangeListener {
    private val title = "Stack Chart Day Activity"
    private var chartMin = 8f   // start at 8:00AM
    private var chartMax = 17f   // end at 5:00PM
    val formatter = SimpleHourFormatter()
    val colors = IntArray(16)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_stackchart)
        super.setTitle(title)

        // build colors
        System.arraycopy(ColorTemplate.MATERIAL_COLORS_16, 0, colors, 0, 16)

        // build marker view to display on highlight
        val markerView = StackMarkerView(this, R.layout.custom_marker_stackchart)

        // set seek bar listeners
        num_elements_seek.setOnSeekBarChangeListener(this)
        num_units_per_entry_seek.setOnSeekBarChangeListener(this)
        num_items_per_unit_seek.setOnSeekBarChangeListener(this)

        chart.setOnChartValueSelectedListener(this)

        val onTouchListener = View.OnTouchListener { v: View, _: MotionEvent ->
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            v.setOnTouchListener(null)
            return@OnTouchListener false
        }

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        y_min_et.also {
            val id = resources.getResourceName(it.id)
            it.setText(chartMin.pp(0), TextView.BufferType.EDITABLE)
            it.setOnEditorActionListener(OnEditorActionListenerWithId(inputMethodManager, id, ::setMin))
            it.setOnTouchListener(onTouchListener)
            it.onFocusChangeListener = MyFocusChangeListener(inputMethodManager, id)
            //it.addTextChangedListener(TextWatcherWithId(yMinET, id))
        }

        y_max_et.also {
            val id = resources.getResourceName(it.id)
            it.setText(chartMax.pp(0), TextView.BufferType.EDITABLE)
            it.setOnEditorActionListener(OnEditorActionListenerWithId(inputMethodManager, id, ::setMax))
            it.setOnTouchListener(onTouchListener)
            it.onFocusChangeListener = MyFocusChangeListener(inputMethodManager, id)
            //it.addTextChangedListener(TextWatcherWithId(yMaxET, id))
        }

        with (chart) {
            isLogEnabled = true
            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            legend.isEnabled = false
            setMaxVisibleValueCount(250)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setTouchEnabled(true)

            markerView.chartView = this
            marker = markerView

            with (xAxis!!) {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
            }

            with (axisLeft) {
                valueFormatter = formatter    // display Float as HH:MM
                setLabelCount(10, true)
                setDrawGridLines(true)
                setDrawAxisLine(true)
            }

            with (axisRight) {
                isEnabled = false
            }
        }

        // force display
        onProgressChanged(num_elements_seek, 4, false)
    }

    fun setMax(actionId: Int, max: Float) {
        chartMax = Math.max(max, chartMin + 1f)
        y_max_et.setText(chartMax.pp(0), TextView.BufferType.EDITABLE)
        when (actionId) {
            EditorInfo.IME_ACTION_NEXT,
            EditorInfo.IME_ACTION_PREVIOUS -> {
                //yMinET.requestFocus()
            }
            else -> onProgressChanged(null, 0, true)
        }
    }
    fun setMin(actionId: Int, min: Float) {
        chartMin = Math.min(min, chartMax - 1f)
        y_min_et.setText(chartMin.pp(0), TextView.BufferType.EDITABLE)
        when (actionId) {
            EditorInfo.IME_ACTION_NEXT,
            EditorInfo.IME_ACTION_PREVIOUS -> {
                //yMinET.requestFocus()
            }
            else -> onProgressChanged(null, 0, true)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val numElements = num_elements_seek.progress
        val numUnitsPerElement = num_items_per_unit_seek.progress
        val numItemsPerUnit = num_items_per_unit_seek.progress
        chart.clear()
        chartMin = y_min_tv.text.toString().toFloatOrNull() ?: chartMin
        chartMax = y_max_tv.text.toString().toFloatOrNull() ?: chartMax
        Log.v("onProgressChanged", "$numElements $numUnitsPerElement $numItemsPerUnit, ${chartMin.pp()} -> ${chartMax.pp()}")
        chart.axisLeft.axisMinimum = chartMin
        chart.axisLeft.axisMaximum = chartMax
        val dataSet = makeData(title, numElements, numUnitsPerElement, numItemsPerUnit)
        dataSet.valueFormatter = formatter
        chart.data = StackData(dataSet)
        displayData(dataSet)
    }

    private fun makeData(label : String,
                         numElements: Int = num_elements_seek.progress,
                         numUnitsPerElement: Int = num_units_per_entry_seek.progress,
                         numItemsPerUnit: Int = num_items_per_unit_seek.progress) : StackDataSet {
        val entries : MutableList<StackEntry> = mutableListOf()
        val setIcon = ContextCompat.getDrawable(this, R.drawable.star)
        val elementIcon = ContextCompat.getDrawable(this, R.drawable.plus)
        for (x in 0 until numElements) {
            // alternate between stack and element icons
            val stack = if (x % 2 == 0)
                makeStackEntry(x, numUnitsPerElement, numItemsPerUnit, setIcon, null)
            else
                makeStackEntry(x, numUnitsPerElement,numItemsPerUnit, null, elementIcon)
            entries.add(stack)
        }
        val set = StackDataSet(chartMin, chartMax, entries, label)
        set.valueTextSize = 12f
        return set
    }

    private fun makeStackEntry(x : Int, numUnitsPerElement: Int, numItemsPerUnit: Int, setIcon : Drawable?, elementIcon : Drawable?) : StackEntry {
        val units : MutableList<StackUnit> = mutableListOf()

        // Generate a sorted list of points between min and max
        // First and last points are min and max for the unit,
        // the others are for individual stack items.
        val qty = numUnitsPerElement * 2 + 2
        val highest = Math.floor(chartMax.toDouble()).toInt()
        val lowest = Math.ceil(chartMin.toDouble()).toInt()
        val range = highest - lowest
        val minSpace = range / qty
        val sorted = List(qty) { (Math.random() * range) + chartMin }.sorted()
        val min = sorted.first().toFloat()
        val max = sorted.last().toFloat()

        for ( i in 1 .. numUnitsPerElement * 2 step 2) {
            val bottom = sorted[i].toFloat()
            val top = sorted[i+1].toFloat()
            val unit = makeStackUnit(x.toFloat(), numItemsPerUnit, bottom, top, elementIcon)
            units.add(unit)
        }

        return StackEntry(x.toFloat(), min, max, units, icon = setIcon)
    }

    /**
     * Makes a StackUnit with the given number of SlackElement's, with given min and max values.
     *
     * @param x - x value
     * @param numItems - the number of items in this unit.
     * @param min - min value for the StackUnit. The lowest item will start at or above this value.
     * @param max - max value for the StackUnit. The highest item will end at or below this value.
     * @param elementIcon - optional icon to be displayed inside each unit
     * @return StackUnit with randomized StackItems
     */
    private fun makeStackUnit(x: Float, numItems: Int, min: Float, max: Float, elementIcon: Drawable?) : StackUnit {
        val items : MutableList<StackItem> = mutableListOf()

        // generate a sorted list of points between min and max. First and last are min and max for the unit.
        // the other numbers are used to generate stack items.
        val points = List(numItems * 2 + 2) { (Math.random() * (max - min)) + min }.sorted()
        for (i in 1 until points.size-1 step 2) {  // save the first and last points
            val bottom = points[i].toFloat()
            val top = points[i+1].toFloat()
            val item = StackItem(x, bottom, top, Color.YELLOW, icon = elementIcon)
            items.add(item)
        }
        return StackUnit(points[0].toFloat(), points.last().toFloat(), items)
    }

    private fun displayData(set : StackDataSet) {
        with (set) {
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawIcons(true)
        }
        val data = StackData(set)
        randomizeColors(set)
        displayNumEntries()
        displayUnitsPerEntry()
        displayItemsPerUnit()
        displayTotalItemCount()
        chart.data = data
        chart.invalidate()
    }

//    override fun onResume() {
//        yMinET.clearFocus()
//        yMaxET.clearFocus()
//        super.onResume()
//    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.stack, menu)
        return true
    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        when (item.itemId) {
//            R.id.viewGithub -> {
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/StackedBarActivity.java")
//                startActivity(i)
//            }
//            R.id.actionToggleItemValues -> {
//                chart.data.sets.map { it.entries.map { it.units.map { it.items.map { it.drawValues = !it.drawValues } } } }
//                chart.invalidate()
//            }
//            R.id.actionToggleUnitValues -> {
//                chart.data.sets.map { it.entries.map { it.units.map { it.drawShadowValues = !it.drawShadowValues } } }
//                chart.invalidate()
//            }
//            R.id.actionToggleEntryValues -> {
//                chart.data.sets.map { it.entries.map { it.drawShadowValues = !it.drawShadowValues } }
//                chart.invalidate()
//            }
//            R.id.actionToggleItemIcons -> {
//                chart.data.sets.map { it.entries.map { it.units.map { it.items.map { it.drawIcon = !it.drawIcon } } } }
//                chart.invalidate()
//            }
//            R.id.actionToggleEntryIcons -> {
//                chart.data.sets.map { it.entries.map { it.drawIcon = !it.drawIcon } }
//                chart.invalidate()
//            }
//            R.id.actionToggleHighlight -> {
//                chart.data.sets.map { it.isHighlightEnabled = ! it.isHighlightEnabled }
//                chart.setDrawMarkers(!chart.isDrawMarkersEnabled)
//                chart.invalidate()
//            }
//            R.id.actionTogglePinch -> {
//                chart.setPinchZoom(!chart.isPinchZoomEnabled)
//                chart.invalidate()
//            }
//            R.id.actionToggleAutoScaleMinMax -> {
//                chart.isAutoScaleMinMaxEnabled = !chart.isAutoScaleMinMaxEnabled
//                chart.notifyDataSetChanged()
//            }
//            R.id.animateX -> {
//                chart.animateX(2000)
//            }
//            R.id.animateY -> {
//                chart.animateY(2000)
//            }
//            R.id.animateXY -> {
//
//                chart.animateXY(2000, 2000)
//            }
//            R.id.actionSave -> {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    saveToGallery()
//                } else {
//                    requestStoragePermission(chart)
//                }
//            }
//        }
//        return true
//    }

    override fun saveToGallery() {
        saveToGallery(chart, "StackChartDayActivity")
    }

    /**
     * Provides the label and min value to the renderer via the element.data object.
     * The label is used to display the marker.
     * If the marker is above the screen, it is displayed at the min rather than the max.
     */
    override fun onValueSelected(e: Entry, h: Highlight) {
        // pass the label to the renderer via the data object
        e.data =
            if (h is StackHighlight) {
                when {
                    h.itemIndex >= 0 -> "Item ${h.itemIndex}"
                    h.unitIndex >= 0 -> "Unit ${h.unitIndex}"
                    else -> "Entry ${h.entryIndex}"
                }
            }
        else "Activity ${h.stackIndex}"
    }

    override fun onNothingSelected() { }


    override fun onStartTrackingTouch(seekBar: SeekBar?) { }
    override fun onStopTrackingTouch(seekBar: SeekBar?) { }

    private fun displayNumEntries() {
        num_elements_tv.text = "${num_elements_seek.progress} Entries"
    }
    private fun displayUnitsPerEntry() {
        num_units_per_entry_tv.text = "${num_units_per_entry_seek.progress} per Entry"
    }
    private fun displayItemsPerUnit() {
        num_items_per_unit_tv.text = "${num_items_per_unit_seek.progress} per Unit"
    }
    private fun displayTotalItemCount() {
        //num_units_per_entry__tv.text = "${num_elements_seek.progress * num_units_per_entry_seek.progress * num_items_per_unit_seek.progress}"
    }

    private fun randomizeColors(data : StackDataSet) {
        var i = 0
        val modulo = 15 // use the first 15 color for items and the 16th for highlight
        data.highLightColor = colors[15]
        data.entries.mapIndexed { entryIndex, entry ->
            entry.units.mapIndexed { unitIndex, unit ->
                unit.items.mapIndexed { itemIndex, item ->
                    item.color = colors[i % modulo]
                    Log.v("color", "$i -> ${i % modulo} -> ${Integer.toHexString(item.color)} ${Integer.toHexString(colors[i % modulo])}")
                    i++
                }
            }
        }
    }
}

class MyFocusChangeListener(val method: InputMethodManager, name: String) : View.OnFocusChangeListener {
    val n = name.substringAfterLast("/")
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (v is EditText) {
            if (hasFocus) {
                Log.v("onFocusChange", "$n got focus, text = ${v.text}")
                //v.setSelection(v.text.length)
                v.setText("")
                method.showSoftInput(v, 0)
            } else {
                Log.v("onFocusChange", "$n lost focus, text = ${v.text}")
                method.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0)
            }
        }
    }
}

class OnEditorActionListenerWithId(val method: InputMethodManager, name: String,
               val callback: (Int, Float) -> Any) : TextView.OnEditorActionListener {
    val n = name.substringAfterLast("/")
    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        val n = if (v.text.isNotBlank()) v.text.toString().toFloat() else 0f
        return if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.v("EditorAction", "IME_ACTION_DONE $n")
            method.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0)
                callback(actionId, n)
                true
            } else false
//            EditorInfo.IME_ACTION_PREVIOUS -> {
//                Log.v("EditorAction", "IME_ACTION_PREVIOUS $n")
//                false
//            }
//            EditorInfo.IME_ACTION_NEXT -> {
//                Log.v("EditorAction", "IME_ACTION_NEXT $n")
//                callback(actionId, n)
//                false
//            }
//            else -> {
//                Log.v("EditorAction", "onEditorAction ${view.text} -> $actionId")
//                false
//            }
//        }
    }
}

class TextWatcherWithId(val editText: EditText, name: String = "_") : TextWatcher {
    val n = name.substringAfterLast("/")
    var lastLength = editText.text.length

    override fun afterTextChanged(s: Editable) { }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (editText.hasFocus()) {
            Log.v("TextWatcher", "beforeTextChanged: $n = $s -> _")
            lastLength = s.length
            editText.update("", this)
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (editText.hasFocus()) {
            Log.v("TextWatcher", "onTextChanged: $n = $s")
            val deleteKey = lastLength > s.length
            editText.update(if (deleteKey) s.dropLast(1) else s, this)
        }
    }
    private fun CharSequence.toEditable() = factory.newEditable(this)

    companion object {
        val factory = Editable.Factory.getInstance()
    }
}

private fun EditText.update(s: CharSequence, watcher: TextWatcher?) {
    update(Editable.Factory.getInstance().newEditable(s), watcher)
}
private fun EditText.update(e: Editable, watcher: TextWatcher?) {
    watcher?.let { removeTextChangedListener(watcher) }
    text = e
    append("")  // move cursor to end
    invalidate()
    watcher?.let { addTextChangedListener(watcher) }
}
