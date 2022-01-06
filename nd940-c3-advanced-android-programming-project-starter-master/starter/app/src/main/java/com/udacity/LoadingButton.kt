package com.udacity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    private var widthSize = 0
    private var heightSize = 0

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    private val textX = resources.getDimension(R.dimen.text_x)
    private val textY = resources.getDimension(R.dimen.text_y)

    private val valueAnimator = ValueAnimator()
    private val circleAnimator = ValueAnimator()

    private var currentWidth = 0f
    private var progressCircle = 0f

    private var buttonText = context.getText(R.string.download).toString()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed)
            { p, old, new ->
            when(new) {
                ButtonState.Loading -> {
                    buttonText = context.getString(R.string.loading)
                    valueAnimator.apply{
                        setObjectValues(0F, measuredWidth.toFloat())
                        duration = 2500
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = ValueAnimator.INFINITE
                        addUpdateListener {
                            currentWidth = it.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
                    circleAnimator.apply {
                        setObjectValues(0F, 360F)
                        duration = 2500
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = ValueAnimator.INFINITE
                        addUpdateListener {
                            progressCircle = it.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
                }
                ButtonState.Completed -> {
                    buttonText = context.getString(R.string.download)

                    valueAnimator.end()
                    circleAnimator.reverse()
                    circleAnimator.end()
                }
                else ->{
                    buttonText = context.getString(R.string.download)
                }
            }
    }


    init {
        // need to check when buttonState is Completed or Loading
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRectangle(canvas)
        drawProgressBar(canvas)
        drawCircle(canvas)
        drawText(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.orange)
        canvas.drawArc(measuredWidth / 2 + 160f,
            measuredHeight / 2 -50f,
            measuredWidth/2 + 210f,
            measuredHeight/2+50f,
            0f, progressCircle, true, paint)
    }

    private fun drawProgressBar(canvas: Canvas){
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        canvas.drawRect(0f, 0f, currentWidth, measuredHeight.toFloat(), paint)
    }

    private fun drawRectangle(canvas: Canvas) {
        // rectangle
        canvas.clipRect(
            clipRectLeft, clipRectTop,
            clipRectRight, clipRectBottom
        )
        canvas.drawColor(Color.CYAN)
    }

    private fun drawText(canvas: Canvas){
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(buttonText,
            textX, textY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}