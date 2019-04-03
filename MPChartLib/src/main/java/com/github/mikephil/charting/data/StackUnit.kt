package com.github.mikephil.charting.data

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.interfaces.IHasMinMax
import com.github.mikephil.charting.interfaces.IStack
import com.github.mikephil.charting.utils.pp

/**
 * Class that represents one unit in a Stack Chart.
 * A Stack Unit is a vertical column of Stack Item's with gaps.
 * Internally, items is of a List<StackItem>
 *
 *          -----    <-- max
 *            |
 *      +-----------+   <-- highest
 *      | StackItem |
 *      +-----------+
 *            |
 *    ... StackItem's ...
 *            |
 *      +-----------+
 *      | StackItem |   
 *      +-----------+    <--- lowest
 *            |
 *          -----    <-- min
 *
 * Any icon provided is not displayed, but is passed to the items.
 *
 */
class StackUnit : IStack, IHasMinMax {

    override val elements: MutableList<IHasMinMax> = mutableListOf()
    override val min: Float
    override val max: Float
    var data: Any? = null
    var icon: Drawable? = null
    var drawIcon: Boolean = icon != null
    var shadowColor: Int = Color.BLUE   // display the shadow line?
    var drawShadows: Boolean      // display the horizontal max and min caps?
    var drawShadowCaps: Boolean = true   // display min and max values with the shadow?
    var capWidth: Float = 0.25f
    var drawValues: Boolean = true
    var valueColor: Int = Color.BLUE

    /**
     * Construct with no StackItem.
     */
    constructor( min: Float,
                 max: Float,
                 data: Any? = null,
                 icon: Drawable? = null,
                 drawIcon: Boolean = icon != null,
                 shadowColor: Int = Color.GREEN,
                 drawShadows: Boolean = true,
                 drawShadowCaps: Boolean = true,
                 capWidth: Float = 0.25f,
                 drawValues: Boolean = true,
                 valueColor: Int = Color.BLUE) {
        this.min = Math.min(min, max)  // swap max and min if inverted
        this.max = Math.max(min, max)
        this.data = data
        this.icon = icon
        this.drawIcon = drawIcon
        this.shadowColor = shadowColor
        this.drawShadows = drawShadows
        this.drawShadowCaps = drawShadowCaps
        this.capWidth = capWidth
        this.drawValues = drawValues
        this.valueColor = valueColor
    }

    /**
     * Constructor with a single StackItem
     */
    constructor( min: Float,
                 max: Float,
                 item: StackItem,
                 data: Any? = null,
                 icon: Drawable? = null,
                 drawIcon: Boolean = icon != null,
                 shadowColor: Int = Color.GREEN,
                 drawShadows: Boolean = true,
                 drawShadowCaps: Boolean = true,
                 capWidth: Float = 0.25f,
                 drawValues: Boolean = true,
                 valueColor: Int = Color.BLUE)
            : this(min, max, data, icon, drawIcon,
                shadowColor, drawShadows, drawShadowCaps, capWidth, drawValues, valueColor) {
        items.add(item)
    }


    /**
     * Constructor with list of StackItem's
     */
    constructor( min: Float,
                 max: Float,
                 items: MutableList<StackItem>,
                 data: Any? = null,
                 icon: Drawable? = null,
                 drawIcon: Boolean = icon == null,
                 shadowColor: Int = Color.BLUE,   // display the shadow line?
                 drawShadows: Boolean = true,      // display the horizontal max and min caps?
                 drawShadowCaps: Boolean = true,   // display min and max ues with the shadow?
                 capWidth: Float = 0.25f,
                 drawValues: Boolean = true,
                 valueColor: Int = Color.GREEN)
            : this(min, max, data, icon, drawIcon,
                shadowColor, drawShadows, drawShadowCaps, capWidth, drawValues, valueColor) {
        this.items.addAll(items)
    }

    val items: MutableList<StackItem> get() = elements as MutableList<StackItem>



    /**
     * Returns the index of the first occurrence of mm in ths sorted list of units
     *
     * @return index, or -1 if not found
     */
    override fun getIndexOf(mm : IHasMinMax) = elements.indexOf(mm)

    /**
     * Adds the given item, but only if it fits within min and max.
     *
     * @param unit to add
     * @return true if successful: x values match and unit min and max are in unit min .. max
     */
    fun add(item: StackItem) : Boolean {
        return when {
            items.firstOrNull()?.x ?: item.x != item.x -> false
            item.max > max -> false
            item.min < min -> false
            else           -> elements.add(item)
        }
    }

    /**
     * Adds the given items that fit within min .. max.
     *
     * @param items list of items to add
     * @return true if all were added (all item min and max are in unit min .. max
     */
    fun addAll (items: List<StackItem>) : Boolean {
        var result = true
        items.map {
            result = result && add(it)}
        return result
    }

    fun getItem(itemIndex: Int) : StackItem? {
        return items.getOrNull(itemIndex)
    }

    fun copy() : StackUnit {
        return StackUnit(min, max, items, data, icon, drawIcon,
                    shadowColor, drawShadows, drawShadowCaps, capWidth, drawValues, valueColor)
    }

    override fun toString() = "StackUnit[${items.size}]: x= ${items.firstOrNull()?.x?.pp() ?: "no_entries"} y= ${min.pp()} -> ${max.pp()}"
}