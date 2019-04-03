package com.github.mikephil.charting.data

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.interfaces.IHasMinMax
import com.github.mikephil.charting.interfaces.IStack
import com.github.mikephil.charting.utils.pp


/**
 * A class that holds a vertical stack of StackUnit's.
 * Internally, stacks is a List<StackUnit>.
 * Highlight.dataIndex uses that index
 * to identify a specific StackItem.
 *
 *  SU View      Rendered
 *  -----          -----    <-- max
 *    |              |
 * +-----+        +-----+   <-- highest
 * | SU0 |        | SI0 |
 * +-----+        +-----+
 *    |              |
 *    |           +-----+
 *    |           | SI1 |
 *    |           +-----+
 *    |              |
 *    |            -----
 *    |             gap
 *    |            -----
 *    |              |
 * +-----+        +-----+
 * | SU1 |        | SI2 |
 * +-----+        +-----+
 *    |              |
 *    |           +-----+
 *    |           | SI3 |
 *    |           +-----+  <-- lowest
 *    |              |
 *  -----          -----  <-- min
 *
 */
class StackEntry : Entry, IStack, IHasMinMax {

    override val elements: MutableList<IHasMinMax> = mutableListOf()
    override val min: Float
    override val max: Float
    var drawIcon: Boolean         // draw an entry icon?
    var drawShadow: Boolean       // draw an entry shadow?
    var shadowColor: Int
    var drawShadowCaps : Boolean  // draw horizontal shadow caps?
    var capWidth: Float
    var drawValues: Boolean       // draw values for the shadow?
    var valueColor: Int

    val units: MutableList<StackUnit> get() = elements as MutableList<StackUnit>
    
    /**
     * Constructor with no StackUnit
     */
    constructor(xVal: Float,
                min: Float,
                max: Float,
                data: Any? = null,
                icon : Drawable? = null,
                drawIcon: Boolean = icon != null,
                drawShadow: Boolean = true,
                shadowColor: Int = Color.BLACK,
                drawShadowCaps : Boolean = true,  // if drawShadow, add horizontal caps?
                capWidth: Float = 0.5f,
                drawValues: Boolean = true,  // if drawShadow, draw values for the shadow?
                valueColor: Int = Color.BLACK)
            : super(xVal, (min + max) / 2, icon, data) {
        this.min = min
        this.max = max
        this.data = data
        this.icon = icon
        this.drawIcon = drawIcon
        this.drawShadow = drawShadow
        this.shadowColor = shadowColor
        this.drawShadowCaps = drawShadowCaps
        this.capWidth = capWidth
        this.drawValues = drawValues
        this.valueColor = valueColor
    }

    
    /**
     * Constructor with only one StackUnit
     */
    constructor(xVal: Float,
                min: Float,
                max: Float,
                unit : StackUnit,
                data: Any? = null,
                icon : Drawable? = null,
                drawIcon: Boolean = icon != null,
                drawShadow: Boolean = true,
                shadowColor: Int = Color.BLACK,
                drawShadowCaps : Boolean = true,  // if drawShadow, add horizontal caps?
                capWidth: Float = 0.5f,
                drawValues: Boolean = true,  // if drawShadow, draw values for the shadow?
                valueColor: Int = Color.BLACK)
            : this(xVal, min, max, data, icon, drawIcon, drawShadow,
                    shadowColor, drawShadowCaps, capWidth, drawValues, valueColor) {
        units.add(unit)
    }

    /**
     * Constructor with list of StackItem's
     */
    constructor(xVal: Float,
                min: Float,
                max: Float,
                units : List<StackUnit>,
                data: Any? = null,
                icon : Drawable? = null,
                drawIcon: Boolean = icon != null,
                drawShadow: Boolean = true,
                shadowColor: Int = Color.BLACK,
                drawShadowCaps : Boolean = true,  // if drawShadow, add horizontal caps?
                capWidth: Float = 0.5f,
                drawValues: Boolean = true,  // if drawShadow, draw values for the shadow?
                valueColor: Int = Color.BLACK)
            : this(xVal, min, max, data, icon, drawIcon, drawShadow,
                    shadowColor, drawShadowCaps, capWidth, drawValues, valueColor) {
        this.units.addAll(units)
    }

    init {
        units.map { unit -> unit.items.map { it.x = x } } // set all item x's to unit x
    }


    /**
     * Returns the index of the first occurrence of mm in ths sorted list of units
     *
     * @return index, or -1 if not found
     */
    override fun getIndexOf(mm : IHasMinMax) = units.indexOf(mm)

    fun getUnit(unitIndex: Int) : StackUnit? {
        return units.getOrNull(unitIndex)
    }
    fun getItem(unitIndex: Int, itemIndex: Int) : StackItem? {
        return getUnit(unitIndex)?.getItem(itemIndex)
    }

    /**
     * Adds the given unit, but only if it fits within min and max.
     *
     * @param unit to add
     * @return true if successful: x values match and unit min and max are in entry min .. max
     */
    fun add(unit: StackUnit) : Boolean {
        val existingX = units.firstOrNull()?.items?.firstOrNull()?.x ?: 0f
        val addingX = unit.items.firstOrNull()?.x ?: 0f
        return when {
            existingX != addingX -> false
            unit.max > max       -> false
            unit.min < min       -> false
             else                -> units.add(unit)
        }
    }

    /**
     * Adds the given units that fit within min .. max.
     *
     * @param units list of items to add
     * @return true if all were added (all item min and max are in entry min .. max
     */
    fun addAll (units: List<StackUnit>) : Boolean {
        var result = true
        units.map {
            result = result && add(it)}
        return result
    }

    override fun copy(): StackEntry {
        val unitsCopy = units.map { it.copy() }.toMutableList()
        return StackEntry(x, min, max, unitsCopy, data, icon, drawIcon, drawShadow, shadowColor,
                drawShadowCaps, capWidth, drawValues, valueColor)
    }

    override fun toString(): String {
        return "StackEntry [${units.size}]: x= ${x.pp()} y: ${min.pp()} -> ${max.pp()}"
    }
}