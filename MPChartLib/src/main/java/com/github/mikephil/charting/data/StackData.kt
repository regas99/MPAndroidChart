package com.github.mikephil.charting.data

import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IStackDataSet

class StackData(vararg sets: IStackDataSet) : BarLineScatterCandleBubbleData<IStackDataSet>(sets.toList()) {
    val sets : List<IStackDataSet> get() = super.mDataSets

    override fun getEntryForHighlight(highlight: Highlight): Entry? {

        return when {
            highlight.dataSetIndex < 0 -> null
            highlight.stackIndex >= 0 ->
                (sets[highlight.dataSetIndex] as StackDataSet).entries[highlight.stackIndex]
            else -> super.getEntryForHighlight(highlight)
        }
    }

    fun copy() : StackData {
        val clone  = sets.map { (it as StackDataSet).copy() as StackDataSet }
        return StackData(*(clone.toTypedArray()))
    }
}