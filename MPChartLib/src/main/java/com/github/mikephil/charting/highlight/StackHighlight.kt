package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.StackEntry
import com.github.mikephil.charting.data.StackItem
import com.github.mikephil.charting.utils.pp

/**
 * Overrides Highlight to provide an additional data layer accessed by unitIndex.
 *
 * Aliases are provided to ease readability.
 *  Field mappings:
 *    Highlight     StackHighlight   Points to
 *    ---------     --------------   ------------
 *    dataSetIndex  dataSetIndex     StackDataSet
 *    stackIndex    entryIndex       StackEntry
 *    dataIndex     unitIndex        StackUnit
 *     ----         itemIndex        StackItem
 */
class StackHighlight(x: Float, y: Float, xPx: Float, yPx: Float,
                     setIndex: Int, entryIndex: Int, unitIndex: Int,
                     val itemIndex: Int,
                     axis: YAxis.AxisDependency)
    : Highlight(x, y, xPx, yPx, setIndex, unitIndex, entryIndex, axis) {

    val entryIndex get() = stackIndex
    val unitIndex get() = dataIndex

    fun getItem(entry: StackEntry, unitIndex: Int, itemIndex: Int): StackItem? {
        return entry.getItem(unitIndex, itemIndex)
    }

    override fun toString(): String {
        return "x: ${x.pp()}, y: ${y.pp()}, set: $dataSetIndex, entry: $entryIndex, unit: $unitIndex, item: $itemIndex"
    }
}