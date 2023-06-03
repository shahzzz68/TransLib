package com.example.translib.windows

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.translib.utils.exfuns.dpToPixel
import com.example.translib.utils.exfuns.getMyColor
import com.example.translib.R
import com.example.translib.TranslationUtilBuilder


class TranslationLayout @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(
    ctx,
    attrs,
    defStyleAttr
) {


    private var statusbarHeight = 0
    val rect: Rect by lazy {
        Rect()
    }

    private val paintDrawable by lazy {
        PaintDrawable()
    }

    init {
//        addBackground()
    }


    fun updateRect(rect: Rect, statusbarHeight: Int) {
        this.rect.set(rect)
        this.statusbarHeight = statusbarHeight
        requestLayout()
    }

    private fun addBackground() {
        addView(View(context).apply {
            background = PaintDrawable().apply {
                setCornerRadius(4f.dpToPixel(context).toFloat())
                setTint(Color.WHITE)
            }
        })
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

//        val parentWidthHalf = (r - l) / 2
//        val parentHeightHalf = (b - t) / 2

        val parentWidthHalf = (rect.width()) / 2
        val parentHeightHalf = (rect.height()) / 2
        val parentWidthQuar = ((rect.width()) / 4)
        val parentHeighQuar = ((rect.height()) / 4)

        for (i in 0 until childCount) {

            val child = getChildAt(i)

            child.measure(
                MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY)
            )

//            val childWidthHalf = child.measuredWidth / 2
//            val childHeighthHalf = child.measuredHeight / 2
//
//            val left = (parentWidthHalf) - (childWidthHalf)
//            val right = (parentWidthHalf) + (childWidthHalf)
//            val top = (parentHeightHalf) - (childHeighthHalf)
//            val bottom = (parentHeightHalf) + (childHeighthHalf)

//            child.layout(left, top, right, bottom)
            child.apply {
                when (this) {
                    is ProgressBar -> layout(
                        rect.left + parentWidthQuar,
                        rect.top + parentHeighQuar - statusbarHeight,
                        rect.right - parentWidthQuar,
                        rect.bottom - parentHeighQuar - statusbarHeight
                    )

                    is ImageView -> {

                        background = paintDrawable.apply {
                            setCornerRadius(4f.dpToPixel(context).toFloat())
                            setTint(context.getMyColor(TranslationUtilBuilder.screenTranslatorWindowBgColor))
                        }
//                        updateLayout()
                        layout(
                            rect.left,
                            rect.top - statusbarHeight,
                            rect.right,
                            rect.bottom - statusbarHeight
                        )

                    }

                    else -> layout(
                        rect.left,
                        rect.top - statusbarHeight,
                        rect.right,
                        rect.bottom - statusbarHeight
                    )

                }


//                layoutParams.apply {
//                    height = rect.height()
//                    width = rect.width()
//                }.also {
//                    layoutParams = it
//                }
            }
        }
    }

//    private fun updateLayout() {
//        layout(rect.left, rect.top - statusbarHeight, rect.right, rect.bottom - statusbarHeight)
//    }

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
            if (mLeftWidth / context.resources.displayMetrics.widthPixels > rowCount) {
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