package com.greatinnovus.promotionapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ActionMode
import androidx.appcompat.widget.AppCompatEditText
import com.greatinnovus.skeleton.R

class OtpEditText : AppCompatEditText{
    private var mSpace = 24f //24 dp by default, space between the lines

    private var mNumChars = 4f
    private var mLineSpacing = 8f //8dp by default, height of the text from our lines

    private val mMaxLength = 4
    private var mLineStroke = 2f
    private var mLinesPaint: Paint? = null
    private var mClickListener: OnClickListener? = null

    constructor(context: Context?) : super(context){}


    constructor(
        context: Context,
        attrs: AttributeSet
    ) :super(context, attrs){
        init(context,attrs)
    }

    constructor (
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr){
        init(context,attrs)
    }



    private fun init(
        context: Context,
        attrs: AttributeSet
    ) {

        val multi = context.resources.displayMetrics.density
        mLineStroke = multi * mLineStroke
        mLinesPaint = Paint(paint)
        mLinesPaint!!.strokeWidth = mLineStroke
        mLinesPaint!!.color = resources.getColor(R.color.applogocolor)
        mLinesPaint!!.style = Paint.Style.STROKE
        setBackgroundResource(0)
        mSpace = multi * mSpace //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing //convert to pixels for our density
        mNumChars = mMaxLength.toFloat()
        super.setOnClickListener { v ->
            // When tapped, move cursor to end of text.
            setSelection(text!!.length)
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    @SuppressLint("ResourceAsColor")
    override fun onDraw(canvas: Canvas) {
        val availableWidth = width - paddingRight - paddingLeft
        val mCharSize: Float
        mCharSize = if (mSpace < 0) {
            availableWidth / (mNumChars * 2 - 1)
        } else {
            (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }
        var startX = paddingLeft
        val bottom = height - paddingBottom
        //Text Width
        val text = text
        val textLength = text!!.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(getText(), 0, textLength, textWidths)
        paint.color = R.color.applogocolor
        var i = 0
        while (i < mNumChars) {
            canvas.drawLine(
                startX.toFloat(),
                bottom.toFloat(),
                startX + mCharSize,
                bottom.toFloat(),
                mLinesPaint!!
            )

           // mLinesPaint!!.strokeWidth=8f;

           /* canvas.drawRect(
                startX.toFloat(), 1f, startX + mCharSize, bottom.toFloat(),
                mLinesPaint!!
            )*/
            if (getText()!!.length > i) {
                val middle = startX + mCharSize / 2
                canvas.drawText(
                    text,
                    i,
                    i + 1,
                    middle - textWidths[0] / 2,
                    bottom - mLineSpacing,
                    paint
                )
            }
            if (mSpace < 0) {
                startX=(startX+(mCharSize * 2)).toInt()

            } else {
                startX = (startX+ (mCharSize + mSpace)).toInt()
            }
            i++
        }
    }
}
