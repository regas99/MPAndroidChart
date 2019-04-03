package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.StackEntry
import com.github.mikephil.charting.data.StackItem
import com.github.mikephil.charting.data.StackUnit
import org.junit.Test

import org.junit.Assert.assertEquals

class StackElementsTest {

    @Test
    public fun testStackItemConstructor() {

        // cannot create an item with min > max
        val inverted = StackItem(0f, 5f, 1f)
        assertEquals(1f, inverted.min)
        assertEquals(5f, inverted.max)
    }

    @Test
    public fun testStackUnitConstructor() {

        // cannot create a unit with min > max
        val inverted = StackUnit(5f, 0f)
        assertEquals(0f, inverted.min)
        assertEquals(5f, inverted.max)
    }
    @Test
    public fun testStackUnitAdd() {

        val unit = StackUnit(min = -5f, max = 5f)
        
        val lowItem  = StackItem(0f, -6f, 0f)
        val midItem  = StackItem(0f, -2f, 2f)
        val highItem = StackItem(0f, 6f, 7f)
        
        // cannot add item below the unit bounds
        val lowAdded = unit.add(lowItem)
        assertEquals(false, lowAdded)
        assertEquals(0, unit.size)
        assertEquals(true, unit.isEmpty)

        // can add item within the unit bounds
        val mediumAdded = unit.add(midItem)
        assertEquals(true, mediumAdded)
        assertEquals(false, unit.isEmpty)
        assertEquals(1, unit.size)

        // cannot add item above the unit bounds
        val highAdded = unit.add(highItem)
        assertEquals(false, highAdded)
        assertEquals(1, unit.size)
        assertEquals(false, unit.isEmpty)
        
        // cannot add item with wrong x
        val wrongXItem = StackItem(1f, 1f, 2f)
        val wrongAdded = unit.add(wrongXItem)
        assertEquals(false, wrongAdded)
        assertEquals(1, unit.size)
        
        // check bounds
        assertEquals(5f, unit.max)
        assertEquals(-5f, unit.min)
        assertEquals(2f, unit.highest)
        assertEquals(-2f, unit.lowest)
    }

    @Test
    public fun testStackEntryAdd() {


        val entry = StackEntry(0f, -5f, 5f)

        val lowUnit  = StackUnit(-6f, 0f)
        val midUnit  = StackUnit(-2f, 2f)
        val highUnit = StackUnit(0f, 7f)

        // cannot add unit below the entry bounds
        val lowUnitAdded = entry.add(lowUnit)
        assertEquals(false, lowUnitAdded)
        assertEquals(0, entry.size)
        assertEquals(true, entry.isEmpty)

        // can add unit within the unit bounds
        val mediumUnitAdded = entry.add(midUnit)
        assertEquals(true, mediumUnitAdded)
        assertEquals(1, entry.size)
        assertEquals(false, entry.isEmpty)

        // cannot add unit above the unit bounds
        val highUnitAdded = entry.add(highUnit)
        assertEquals(false, highUnitAdded)
        assertEquals(1, entry.size)
        assertEquals(false, entry.isEmpty)

        // cannot add a unit with wrong x
        val zeroXItem = StackItem(0f, -1f, -1f)
        midUnit.add(zeroXItem)

        val oneXItem  = StackItem(1f, 1f, 2f)
        val oneXUnit  = StackUnit(0f, 3f, oneXItem)

        val wrongXAdded = entry.add(oneXUnit)
        assertEquals(false, wrongXAdded)
        assertEquals(1, entry.size)
    }

}