package com.github.mikephil.charting.data

import android.graphics.Color

import com.github.mikephil.charting.interfaces.datasets.IStackDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.pp

/**
 * Consists of a single stack with a label and a list of StackEntry's
 */
class StackDataSet(
            override val min : Float = 0f,
            override val max: Float = 100f,
            entries: List<StackEntry>, label: String,
            override var stackSpace: Float = 0.1f,
            override var showShadow : Boolean = true,
            shadowWidth : Float = 0.1f,
            override var shadowColor: Int = Color.DKGRAY
    ) : LineScatterCandleRadarDataSet<StackEntry>(entries, label), IStackDataSet {

    override val entries: List<StackEntry> get() = mValues
    override var shadowWidth : Float = 3f  // width of the the empty spaces

    init {
        mValues = entries
        this.shadowWidth = shadowWidth
    }

    /**
     * Extends min and max to include e.
     */
    override fun calcMinMax(e: StackEntry) {
        if (e.min < yMin) mYMin = e.min
        if (e.max > yMax) mYMax = e.max
        super.calcMinMax(e)
    }

    override fun getEntryIndex(e: StackEntry): Int {
        return values.indexOf(e)
    }

    fun getItem(entryIndex: Int, unitIndex: Int, itemIndex: Int) : StackItem? {
        return entries.getOrNull(entryIndex)?.getItem(unitIndex, itemIndex)
    }

    override fun copy(): DataSet<StackEntry> {
        val es = entries.map { it.copy() }
        return StackDataSet(min, max, es, label, stackSpace, showShadow, shadowWidth, shadowColor)
    }

    override fun toString() = "StackDataSet[${values.size}]: x = '${xMin.pp()} -> ${xMax.pp()}; y = ${min.pp()} -> ${max.pp()}"
}