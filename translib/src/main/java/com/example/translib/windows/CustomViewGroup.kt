package com.example.translib.windows

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class CustomViewGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {


    private val deviceWidth = context.resources.displayMetrics.widthPixels

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        var curWidth: Int
        var curHeight: Int
        var curLeft: Int
        var curTop: Int
        var maxHeight: Int

        //get the available size of child view

        //get the available size of child view
        val childLeft = this.paddingLeft
        val childTop = this.paddingTop
        val childRight = this.measuredWidth - this.paddingRight
        val childBottom = this.measuredHeight - this.paddingBottom
        val childWidth = childRight - childLeft
        val childHeight = childBottom - childTop

        maxHeight = 0
        curLeft = childLeft
        curTop = childTop

        for (i in 0 until count) {
            val child: View = getChildAt(i)
            if (child.visibility == GONE) return

            //Get the maximum size of the child
            child.measure(
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
            )
            curWidth = child.measuredWidth
            curHeight = child.measuredHeight
            //wrap is reach to the end
            if (curLeft + curWidth >= childRight) {
                curLeft = childLeft
                curTop += maxHeight
                maxHeight = 0
            }
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)

            //store the max height
            if (maxHeight < curHeight) maxHeight = curHeight
            curLeft += curWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        // Measurement will ultimately be computing these values.
        var maxHeight = 0
        var maxWidth = 0
        var childState = 0
        var mLeftWidth = 0
        var rowCount = 0

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == GONE) continue

            // Measure the child.
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            maxWidth += maxWidth.coerceAtLeast(child.measuredWidth)
            mLeftWidth += child.measuredWidth
            if (mLeftWidth / deviceWidth > rowCount) {
                maxHeight += child.measuredHeight
                rowCount++
            } else {
                maxHeight = maxHeight.coerceAtLeast(child.measuredHeight)
            }
            childState = combineMeasuredStates(childState, child.measuredState)
        }

        // Check against our minimum height and width
        maxHeight = maxHeight.coerceAtLeast(suggestedMinimumHeight)
        maxWidth = maxWidth.coerceAtLeast(suggestedMinimumWidth)

        // Report our final dimensions.
        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            resolveSizeAndState(
                maxHeight,
                heightMeasureSpec,
                childState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )
    }
}