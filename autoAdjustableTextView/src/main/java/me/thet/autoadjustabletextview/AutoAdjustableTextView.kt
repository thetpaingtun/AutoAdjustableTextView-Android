package me.thet.autoadjustabletextview

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import kotlin.IllegalArgumentException


/**
 *
 * Created by thet on 13/8/2020.
 *
 */
class AutoAdjustableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {


    private var afterMeasured: Boolean = false
    private val RESIZE_PERCENTAGE = 0.2f


    //text sizes are in px
    private val maxTextSize: Float
    private var minTextSize: Float


    private val possibleTextSizes: List<Float>


    init {
        maxLines = 1

        maxTextSize = textSize

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AutoAdjustableTextView,
            0, 0
        ).apply {

            try {
                minTextSize =
                    getDimension(
                        R.styleable.AutoAdjustableTextView_adj_min_text_size,
                        context.sp(10f)
                    )
            } finally {
                recycle()
            }
        }
        possibleTextSizes = calculatePossibleTextSizes()


        //make sure the view has drawn
        viewTreeObserver.addOnGlobalLayoutListener {
            adjustTextSize()
        }
    }

    /**
     * calculate all the possible text size between maxTextSize & minTextSize using SHRINK_PERCENTAGE
     */
    private fun calculatePossibleTextSizes(): List<Float> {
        val sizes = mutableListOf<Float>()
        var cur = maxTextSize
        while (cur > minTextSize) {
            sizes.add(cur)
            cur = cur - (cur * RESIZE_PERCENTAGE)
        }
        return sizes
    }


    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (!afterMeasured) return

        adjustTextSize()
    }


    private fun adjustTextSize() {
        val textSize = resolveTextSize()

//        log("setting text =>" + textSize)
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.size)

        enableHorizontalScroll(textSize.reachLimit)
    }

    private fun enableHorizontalScroll(enabled: Boolean = true) {
        if (enabled) {

            movementMethod = ScrollingMovementMethod.getInstance()
            setHorizontallyScrolling(true)

            //get the horizontal gravity
            val horGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
            if (horGravity == Gravity.RIGHT) {
                scrollTo(computeHorizontalScrollRange() - width, 0)
            } else {
                scrollTo(getTextWidth().toInt() - width, 0)
            }

        } else {
            movementMethod = null
            setHorizontallyScrolling(false)
        }
    }


    /**
     * To find the width of the text using the paint object
     */
    private fun getTextWidth(tSize: Float? = null): Float {
        val initialTextSize = paint.textSize
        if (tSize != null) {
            paint.textSize = tSize;
        }


        val width = paint.measureText(text.toString())

        //set the initial text size back after  measuring
        paint.textSize = initialTextSize
        return width
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        afterMeasured = true
    }


    /**
     * helper method to recursively find the appropriated text size
     */
    private fun resolveTextSize(pos: Int): TextSize {
        //we reach the end of possible text size
        if (pos == possibleTextSizes.lastIndex) return TextSize(
            possibleTextSizes.last(),
            //reach the limit if textWidth using smallest text size is greater than the width of the TextView
            getTextWidth(possibleTextSizes.last()) > width
        )
        val textSize = possibleTextSizes[pos]
        val textWidth = getTextWidth(textSize)
        if (width > textWidth) {
            return TextSize(textSize, false)
        }
        return resolveTextSize(pos + 1)
    }


    /**
     * find the biggest text size from possible text size list to fit the width of the text view
     */
    private fun resolveTextSize(): TextSize {
        return resolveTextSize(0)
    }

    override fun setMaxLines(maxLines: Int) {
        if (maxLines != 1) {
            throw IllegalArgumentException("Max line can't be set other than 1")
        }
        super.setMaxLines(maxLines)
    }


    override fun setTextSize(size: Float) {
        throw UnsupportedOperationException("It's not allow to change text size dynamically.")
    }

    override fun setTextSize(unit: Int, size: Float) {
        throw UnsupportedOperationException("It's not allowed to change text size dynamically.")
    }
}