package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.StackDataSet
import com.github.mikephil.charting.data.StackEntry
import com.github.mikephil.charting.data.StackItem
import com.github.mikephil.charting.data.StackUnit
import org.junit.Test

import org.junit.Assert.assertEquals

class StackChartActivityTest {

    @Test
    fun testMakeData() {
        val set = makeData()
        set.entries.mapIndexed { index, entry ->
            when (index) {
                0 -> {
                    assertEquals(1, entry.size)
                    val unit0 = entry.units[0]
                    assertEquals(1, unit0.size)
                }
                1 -> {
                    assertEquals(1, entry.size)
                    val unit0 = entry.units[0]
                    assertEquals(3, unit0.size)
                }
                2 -> {
                    assertEquals(2, entry.size)
                    val unit0 = entry.units[0]
                    assertEquals(3, unit0.size)
                    val unit1 = entry.units[1]
                    assertEquals(3, unit1.size)
                }
                3 -> {
                    assertEquals(1, entry.size)
                    val unit0 = entry.units[0]
                    assertEquals(2, unit0.size)
                }
            }
        }
    }


    private fun makeData(): StackDataSet {

        val max = 10f
        val min = 0f
        val range = max - min

        val labels = arrayOf("Entry 1", "Entry 2", "Entry 3", "Entry 4")

        val entries: MutableList<StackEntry> = mutableListOf()
        var colorIndex = 0
        for (i in 0 until labels.size) {
            val x = i.toFloat()
            val entry = when (i) {
                0 -> {  // one unit, one item
                    val item = StackItem(x, 10f, 14f)
                    val unit = StackUnit(9f, 15f, item)
                    unit.drawShadows = false  // TODO
                    StackEntry(x, 8f, 16f, unit)
                }

                1 -> { // one unit, two items
                    val items: MutableList<StackItem> = mutableListOf()
                    items.add(StackItem(x, 9.333f, 10f))
                    items.add(StackItem(x, 10.33333f, 12.3f))
                    items.add(StackItem(x, 13.3f, 14.2f))
                    val unit = StackUnit(9f, 15.23f, items)
                    StackEntry(x, 8.5f, 15.64f, unit)
                }

                2 -> {  // two units, multiple items
                    val units = mutableListOf<StackUnit>()
                    // AM only
                    val amItems: MutableList<StackItem> = mutableListOf()
                    amItems.add(StackItem(x, 8.1f, 8.75f))
                    amItems.add(StackItem(x, 9.25f, 10.3f))
                    amItems.add(StackItem(x, 10.5f, 11.333f))
                    units.add(StackUnit(8f, 12f, items = amItems))
                    // PM only
                    val pmItems: MutableList<StackItem> = mutableListOf()
                    pmItems.add(StackItem(x, 13f, 14.1f))
                    pmItems.add(StackItem(x, 15.25f, 16.3f))
                    pmItems.add(StackItem(x, 16.5f, 16.8f))
                    units.add(StackUnit(13f, 17f, items = pmItems))
                    StackEntry(x, 8f, 17f, units)
                }

                3 -> {  // item extends past unit - should this be allowed?
                    val items: MutableList<StackItem> = mutableListOf()
                    items.add(StackItem(x, 9.125f, 11.3f))
                    items.add(StackItem(x, 12.25f, 15.9f))
                    StackEntry(x, 10f, 13f, StackUnit(8f, 16.25f, items))
                }
                else -> {
                    val items: MutableList<StackItem> = mutableListOf()
                    val points = List(4 * 2 + 2) { Math.random() * range - min }
                    val sorted = points.sorted()
                    val low = sorted[0].toFloat()
                    val high = sorted.last().toFloat()
                    for (i in 0 until points.size step 2) {
                        val bottom = sorted[i].toFloat()
                        val top = sorted[i + 1].toFloat()
                        val item = StackItem(x, bottom, top)
                        items.add(item)
                    }
                    StackEntry(x, Math.min(low - 1, min), Math.max(high + 1, max), StackUnit(low, high, items))
                }
            }

            entries.add(entry)
        }
        return StackDataSet(min, max, entries, "label")
    }
}