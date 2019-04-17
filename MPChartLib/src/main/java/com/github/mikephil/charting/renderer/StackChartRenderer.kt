package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align.*
import android.graphics.RectF
import android.util.Log
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.StackChart
import com.github.mikephil.charting.data.StackDataSet
import com.github.mikephil.charting.data.StackEntry
import com.github.mikephil.charting.data.StackItem
import com.github.mikephil.charting.data.StackUnit
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.Highlights
import com.github.mikephil.charting.highlight.StackHighlight
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface
import com.github.mikephil.charting.interfaces.dataprovider.StackDataProvider
import com.github.mikephil.charting.interfaces.datasets.IStackDataSet
import com.github.mikephil.charting.utils.*
import com.github.mikephil.charting.utils.ViewPortHandler.OutOfBounds.*

private const val TAG = "StackChartRenderer"
class StackChartRenderer(val chart : StackDataProvider,
                         val animator : ChartAnimator, handler : ViewPortHandler)
    : LineScatterCandleRadarRenderer(animator, handler) {

    var logEnabled = true
    private val borderPaint = Paint(super.mRenderPaint)
    private val shadowPaint = Paint(super.mRenderPaint)

    var textYOffset = 3f // amount to space value text from its 'natural' y position

    init {
        mRenderPaint.strokeWidth = 10f
        mRenderPaint.style = Paint.Style.FILL_AND_STROKE

        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = Color.BLACK
        borderPaint.strokeWidth = 5f
        
        mValuePaint.textAlign = CENTER
    }

    /**
     * ###### ###### DRAW DATA METHODS ##### ######
     */

    /**
     * Draws the actual data in form of lines, rectangles.
     */
    override fun drawData(c: Canvas) {
        if (logEnabled) Log.v(TAG, "draw data: $chart")
        chart.stackData().sets.map { it.drawDataSet(c)}
    }

    /**
     * Draws an individual data set.
     */
    fun IStackDataSet.drawDataSet(c : Canvas) {
        if (logEnabled) Log.v(TAG,"draw set: $this")
        val trans = chart.getTransformer(this.axisDependency)

        mXBounds.set(chart, this)

        with (shadowPaint) {
            strokeWidth = 1f
            color = shadowColor
            style = Paint.Style.STROKE
        }

        entry@ for (x in mXBounds.min .. mXBounds.max) {
            val entry = getEntryForIndex(x)
            if (logEnabled) Log.v(TAG, "draw entry: $entry")

            // we are done when we have moved past the right edge
            when (entry.draw(this, trans, c)) {
                OFF_SCREEN_RIGHT -> return
                OFF_SCREEN_LEFT -> continue@entry
                else -> {
                    entry.units.map { unit ->
                        unit.draw(entry, this, trans, c)
                    }
                }
            }


        }
    }

    /**
     * Draw a Stack Entry.
     *
     * This consists of an optional shadow line,
     * an optional shadow line for each StackUnit,
     * and a rectangle for each StackItem.
     */
    private fun StackEntry.draw(set: IStackDataSet, trans: Transformer, c: Canvas) : ViewPortHandler.OutOfBounds {

        // set showShadow is a global control
        if (!set.showShadow)
            return ON_SCREEN

        if (drawShadow) {
            if (logEnabled) Log.v(TAG, "  draw entry shadow x= ${x.pp()} y= ${min.pp()} -> ${max.pp()}")

            shadowPaint.color = shadowColor
            shadowPaint.textAlign = CENTER

            val box = boundingBox(capWidth, trans)

            return when {
                box.left.isNaN()  -> return OFF_SCREEN_LEFT
                box.right.isNaN() -> return OFF_SCREEN_RIGHT
                else -> {
                    drawShadow(box, drawShadowCaps, shadowPaint, c)
                    ON_SCREEN
                }
            }
        } else
            return ON_SCREEN
    }

    /**
     * Draws a Stack Unit.
     *
     * This consists of an optional shadow line and a set of rectangles.
     */
    private fun StackUnit.draw(entry: StackEntry, set: IStackDataSet, trans: Transformer, c: Canvas) {
        if (logEnabled) Log.v(TAG, "draw unit: $this")

        // offset the unit shadow if there is also an element shadow
        val drawX = entry.x + if (entry.drawShadow) set.stackSpace else 0f

        // draw unit shadow line
        if (set.showShadow && drawShadows) {
            if (logEnabled) Log.v(TAG, "  draw unit shadow x=${entry.x.pp()} y: ${min.pp()} -> ${max.pp()}")

            shadowPaint.color = shadowColor

            val box = boundingBox(drawX, capWidth, trans)
            drawShadow(box, drawShadowCaps, shadowPaint, c)
        }

        // draw items
        items.map item@{ item ->
            if (logEnabled) Log.v(TAG, "  draw item: $item")
            item.draw(set.stackSpace, trans, c)
        }
    }

    /**
     * Draws a rectangle for an individual Stack Item.
     */
    private fun StackItem.draw(space: Float, trans: Transformer, c: Canvas) {

        val rect = RectF(
                (x - 0.5f + space).phasedX(), max.phasedY(),   // left, top
                (x + 0.5f - space).phasedX(), min.phasedY())   // right, bottom
        trans.rectValueToPixel(rect)

        val box = boundingBox(space/2f, trans)

        mRenderPaint.style = Paint.Style.FILL
        mRenderPaint.color = color
        if (logEnabled) Log.v(TAG, "    draw item rect ${rect.pp()}")

        val rounding = 10f
        c.drawRoundRect(rect, rounding, rounding, mRenderPaint)
        c.drawRoundRect(rect, rounding, rounding, borderPaint)
    }

    /**
     * Draws a shadow line with optional end caps.
     *
     * @param boxPixels - pre-clipped bounds in pixels
     * @param drawCaps - if true, end caps will be drawn with width = box width
     * @param paint paint
     * @param c canvas
     */
    private fun drawShadow(boxPixels: RectF, drawCaps: Boolean, paint: Paint, c: Canvas) {

        //draw the vertical line
        val x = boxPixels.centerX()
        if (logEnabled) Log.v(TAG, "    draw shadow line x=${x.pp()}: y= ${boxPixels.bottom.pp()} -> ${boxPixels.top.pp()}")
        c.drawLine(x, boxPixels.top, x, boxPixels.bottom, paint)

        if (drawCaps) {
            c.drawLine(boxPixels.left, boxPixels.top, boxPixels.right, boxPixels.top, paint)
            c.drawLine(boxPixels.left, boxPixels.bottom, boxPixels.right, boxPixels.bottom, paint)
        }
    }

    /**
     * ###### ###### DRAW VALUE METHODS ##### ######
     */

    override fun drawValue(c: Canvas, valueText: String, x: Float, y: Float, color: Int) {
        Log.d(TAG, "drawValue is not implemented, and should not be in DataRenderer")
    }

    /**
     * Counts the number of values to draw, and compares it to chart.maxVisibleCount.
     *
     * @return false if there are too many values to draw.
     */
    override fun isDrawingValuesAllowed(chart: ChartInterface): Boolean {

        // only include sets that will be drawn
        val setsToDraw = chart.data.dataSets
                .filter { it.isDrawValuesEnabled && it.entryCount > 0 }

        // count the entry values in those sets
        var numValuesToDraw = setsToDraw.flatMap { set ->
            (set as StackDataSet).entries.filter { entry ->
                entry.drawValues
            }
        }.count() * 2   // two values per entry

        // also count the unit and item values
        setsToDraw.flatMap { set ->
            (set as StackDataSet).entries.flatMap { entry ->
                entry.units.map { unit ->
                    if (unit.drawShadows && entry.drawValues)
                        numValuesToDraw += 2  // 2 values per unit
                    unit.items.map { item ->
                        if (item.drawValues)
                            numValuesToDraw += 2    // two values per item
                    }

                    // stop counting as soon as we reach maxVisibleCount
                    if (numValuesToDraw >= chart.maxVisibleCount) return false
                }
            }
        }
        return true
    }

    /**
     * Loops over all Entry's and draws their values (labels).
     */
    override fun drawValues(c: Canvas) {

        if (logEnabled) Log.i(TAG, "draw data set: $this")

        // do not draw too many values
        if (!isDrawingValuesAllowed(chart))
            return

        chart.data.dataSets.map set@{ set ->
            if (set.isDrawValuesEnabled && shouldDrawValues(set))
                (set as IStackDataSet).drawValues(c)
            if (set.isDrawIconsEnabled)
                (set as IStackDataSet).drawIcons(c)
        }
    }

    fun IStackDataSet.drawValues(c: Canvas) {

        mXBounds.set(chart, this)
        applyValueTextStyle(this)
        val trans = chart.getTransformer(axisDependency)
        val formatter = valueFormatter
        val textHeight = Utils.calcTextHeight(mValuePaint, "Q")

        if (isDrawValuesEnabled && shouldDrawValues(this)) {

            entry@ for (x in mXBounds.min..mXBounds.max) {
                val entry = getEntryForIndex(x)

                if (entry.drawValues) {
                    if (logEnabled) Log.v(TAG, "draw entry values: $entry")

                    // get bounding box for the entry
                    val rect = entry.boundingBox(shadowWidth, trans)

                    // draw the entry values
                    mValuePaint.color = entry.valueColor
                    mValuePaint.textAlign = CENTER

                    val xPix = rect.centerX()
                    val yMaxPix = rect.top - textYOffset
                    val maxEntryLabel = formatter.getFormattedValue(entry.max)
                    c.drawText(maxEntryLabel, xPix, yMaxPix, mValuePaint)
                    if (logEnabled) Log.v(TAG, "  draw entry max: $maxEntryLabel @ ${xPix.pp()}, ${yMaxPix.pp()}")

                    val yMin = rect.bottom + textHeight + textYOffset
                    val minEntryLabel = formatter.getFormattedValue(entry.min)
                    c.drawText(minEntryLabel, rect.centerX(), yMin, mValuePaint)
                    if (logEnabled) Log.v(TAG, "  draw entry min: $minEntryLabel @ ${xPix.pp()}, ${yMin.pp()}")
                }

                entry.units.map { unit ->

                    val entryX = entry.x.phasedX()

                    if (unit.drawValues) {
                        if (logEnabled) Log.v(TAG, "    draw unit values: $unit")

                        mValuePaint.textAlign = if (entry.drawShadow) LEFT else CENTER
                        mValuePaint.color = unit.valueColor

                        val rect = unit.boundingBox(entryX, shadowWidth, trans)
                        val unitCenter = rect.centerX()

                        val yMax = rect.top - textYOffset
                        val maxUnitLabel = formatter.getFormattedValue(unit.max)
                        if (logEnabled) Log.v(TAG, "      draw unit max: $maxUnitLabel @ ${unitCenter.pp()}, ${yMax.pp()}")
                        c.drawText(maxUnitLabel, rect.centerX(), yMax, mValuePaint)

                        val yMin = rect.bottom + textHeight + textYOffset
                        val minUnitLabel = formatter.getFormattedValue(unit.min)
                        c.drawText(minUnitLabel, unitCenter, yMin, mValuePaint)
                        if (logEnabled) Log.v(TAG, "      draw unit min: $minUnitLabel @ ${unitCenter.pp()}, ${yMin.pp()}")

                   }

                    mValuePaint.textAlign = if (entry.drawShadow || unit.drawShadows) RIGHT else CENTER
                    unit.items.map { item ->

                        if (item.drawValues) {

                            if (logEnabled) Log.v(TAG, "      draw item values: $item")

                            val box = item.boundingBox(stackSpace, trans)
                            val itemCenter = box.centerX()

                            val yMax = box.top - textYOffset - borderPaint.strokeWidth
                            val maxItemLabel = formatter.getFormattedValue(item.max)
                            if (logEnabled) Log.v(TAG, "      draw item max: $maxItemLabel @ ${itemCenter.pp()}, ${yMax.pp()}")
                            c.drawText(maxItemLabel, box.centerX(), yMax, mValuePaint)

                            val yMin = box.bottom + textHeight + textYOffset
                            val minItemLabel = formatter.getFormattedValue(item.min)
                            if (logEnabled) Log.v(TAG, "      draw item min: $minItemLabel @ ${itemCenter.pp()}, ${yMin.pp()}")
                            c.drawText(minItemLabel, box.centerX(), yMin, mValuePaint)

                        }
                    }
                }
            }
        }
    }

    /**
     * ###### ###### DRAW ICON METHODS ##### ######
     */

    /**
     * Draws all the icons on the chart - while checking for enable fields.
     */
    private fun IStackDataSet.drawIcons(c: Canvas) {

        // Set.drawIcons is a global control. Draw nothing if it is false.
        if (!isDrawIconsEnabled) return

        val trans = chart.getTransformer(axisDependency)

        // other drawIcons controls are local.
        entries.map { entry ->
            entry.units.map { unit ->
                unit.items.map { item ->
                    if (item.drawIcon)
                        item.drawIcon(trans, c)
                }
                if (unit.drawIcon)
                    unit.drawIcon(trans, c)
                }
            if (entry.drawIcon)
                entry.drawIcon(trans, this, c)
        }
    }

    /**
     * Draws the entry icon at the top of the screen - if it exists and is enabled.
     */
    private fun StackEntry.drawIcon(trans: Transformer, set: IStackDataSet, c: Canvas) {
        if (!drawIcon) return
        icon?.let { icon ->
            val top = trans.getPixelForValues(x.phasedX(), set.max.phasedY())
            if (logEnabled) Log.v(TAG, "draw entry icon @ ${top.x.toInt()}, ${top.y.toInt()}")
            Utils.drawImage(c, icon, top.x.toInt(), top.y.toInt(), icon.intrinsicWidth, icon.intrinsicHeight)
            MPPointD.recycleInstance(top)
        }
    }

    private fun StackUnit.drawIcon(trans: Transformer, c: Canvas) {
        if (!drawIcon) return
        icon?.let { icon ->
            // we need an x value from a Stack Item
            val item = items.firstOrNull() ?: return@let
            val center = trans.getPixelForValues(item.x.phasedX(), middle.phasedX())
            if (logEnabled) Log.v(TAG, "draw unit icon @ ${center.x.pp()}, ${center.y.pp()}")
            Utils.drawImage(c, icon,
                    center.x.toInt(), center.y.toInt(),
                    icon.intrinsicWidth, icon.intrinsicHeight
            )
            center.recycle()
        }
    }

    private fun StackItem.drawIcon(trans: Transformer, c: Canvas) {
        if (!drawIcon) return
        icon?.let { icon ->
            val center = trans.getPixelForValues(x.phasedX(), y.phasedY())
            if (logEnabled) Log.v(TAG, "draw item icon @ ${center.x.pp()}, ${center.y.pp()}")
            Utils.drawImage(c, icon,
                    center.x.toInt(), center.y.toInt(),
                    icon.intrinsicWidth, icon.intrinsicHeight
            )
            center.recycle()
        }
    }

    /**
     * ###### ###### DRAW HIGHLIGHT METHODS ##### ######
     */

    /**
     * Draws the given highlights.
     *
     * @param c canvas
     * @param highlights highlights to draw
     */
    override fun drawHighlights(c: Canvas?, highlights: Highlights?) {

        highlights ?: return
        c ?: return

        highlights.map highlight@{ h ->
            val hl = h as StackHighlight
            if (logEnabled) Log.v(TAG, "highlighting $hl")
            if (hl.isNull) return@highlight

            val set = chart.data.dataSets[h.dataSetIndex] as StackDataSet? ?: return@highlight
            if (!set.isHighlightEnabled) return@highlight


            when (h.type) {
                Highlight.Type.NULL -> return@highlight
                Highlight.Type.VALUE -> drawHighlight(set, h, c)
                else -> return@highlight // AXIS is drawn in AxisRenderer
            }
        }
    }

    private fun drawHighlight(set: StackDataSet, hl: StackHighlight, c: Canvas) {
        val entry = set.values[hl.entryIndex]
        if (!isInBoundsX(entry, set)) return

        val left = (entry.x - 0.5f + set.stackSpace).phasedX()
        val right = (entry.x + 0.5f - set.stackSpace).phasedX()

        val rect: RectF = when {
            hl.itemIndex >= 0 -> {  // highlight an individual item
                val item = hl.getItem(entry, hl.unitIndex, hl.itemIndex)
                        ?: return
                if (logEnabled) Log.v(TAG, "highlighting item $item")
                RectF(
                        left, item.max.phasedY().phasedY(),  // left, top
                        right, item.min.phasedY()   // right, bottom
                )
            }
            hl.unitIndex >= 0 -> {  // highlight a unit
                val unit = entry.units[hl.unitIndex]
                if (logEnabled) Log.v(TAG, "highlighting unit $unit")
                RectF(
                        left, unit.max.phasedY(),  // left, top
                        right, unit.min.phasedY()  // right, bottom
                )
            }
            else -> {  // highlight the entire entry
                if (logEnabled) Log.v(TAG, "highlighting entry $entry")
                RectF(
                        left, entry.max.phasedY(),   // left, top
                        right, entry.min.phasedY()  // right, bottom
                )
            }
        }
        chart.getTransformer(set.axisDependency).rectValueToPixelHorizontal(rect)
        

        // remember where we are drawing
        // make sure that we have enough vertical space (80dp) to draw the marker
        // The value of 80 would ideally come from the marker view layout.
        val drawCenter = (rect.left + rect.right) / 2f
        val drawTop = Math.max(rect.top, 80f)
        hl.setDraw(drawCenter, drawTop)

        with(mHighlightPaint) {
            strokeWidth = 10f
            color = set.highLightColor
            style = Paint.Style.FILL_AND_STROKE
            if (logEnabled) Log.v(TAG, " highlighting rect ${rect.pp()}")
            c.drawRect(rect.left, rect.top, rect.right, rect.bottom, this)
        }
    }

    /**
     * ###### ###### DRAW EXTRAS METHODS ##### ######
     */

    /**
     * Draws any kind of additional information (e.g. line-circles).
     */
    override fun drawExtras(c: Canvas?) {
        if (logEnabled) Log.v(TAG, "drawExtras: TODO")
    }

    /**
     * ###### ###### UTILITIES ##### ######
     */

    /**
     * Return this with y phasing applied.
     */
    fun Float.phasedY(): Float {
        val min = chart.stackData().yMin
        return (this - min) * animator.phaseY + min
    }

    /**
     * Return this with x phasing applied.
     */
    private fun Float.phasedX() : Float {
        return this * animator.phaseX
    }

    /**
     * Initializes the buffers used for rendering with a new size. Since this
     * method performs memory allocations, it should only be called if
     * necessary.
     */
    override fun initBuffers() { }

    /**
     * Gets the bounding box for a StackEntry.
     *
     * @param widthVal - rect width in value units -> entry cap width
     * @param trans - transformer
     * @return bounding box in pixels. Left or right is NaN if off screen.
     */
    private fun StackEntry.boundingBox(widthVal: Float, trans: Transformer) : RectF {
        val rect = RectF(
                (x - widthVal / 2f).phasedX(), max.phasedY(),   // left, top
                (x + widthVal / 2f).phasedX(), min.phasedY())   // right, bottom
        return boundingBoxInPix(rect, trans)
    }

    /**
     * Gets the bounding box for a UnitEntry.
     *
     * @param xVal - x in value units
     * @param widthVal - rect width in value units -> entry cap width
     * @param trans - transformer
     * @return bounding box in pixels. Left or right is NaN if off screen.
     */
    private fun StackUnit.boundingBox(xVal: Float, widthVal: Float, trans: Transformer) : RectF {
        val rect = RectF(
                (xVal - widthVal / 2f).phasedX(), max.phasedY(),   // left, top
                (xVal + widthVal / 2f).phasedX(), min.phasedY())   // right, bottom
        return boundingBoxInPix(rect, trans)
    }

    /**
     * Gets the bounding box for a StackItem.
     *
     * @param widthVal - rect width in value units -> entry cap width
     * @param trans - transformer
     * @return bounding box in pixels. Left or right is NaN if off screen.
     */
    private fun StackItem.boundingBox(widthVal: Float, trans: Transformer) : RectF {
        val rect = RectF(
                (x - widthVal / 2f).phasedX(), max.phasedY(),   // left, top
                (x + widthVal / 2f).phasedX(), min.phasedY())   // right, bottom
        return boundingBoxInPix(rect, trans)
    }

    /**
     * Computes the given RectF from data values to pixels with clipping.
     * It the RectF x values are  entirely off screen, the corresponding edge is NaN.
     *
     * @param rectVal - bounding values
     * @param trans - transformer
     * @return clipped pixels. left or right is NaN if off screen.
     */
    private fun boundingBoxInPix(rectVal: RectF, trans: Transformer) : RectF {
        trans.rectValueToPixel(rectVal)
        when {
            !mViewPortHandler.isInBoundsLeft(rectVal.right) -> rectVal.left = Float.NaN
            !mViewPortHandler.isInBoundsRight(rectVal.left) -> rectVal.right = Float.NaN
            else -> rectVal.clipToViewPort(mViewPortHandler)  // clip it at the edges of the chart
        }
        return rectVal
    }
}
