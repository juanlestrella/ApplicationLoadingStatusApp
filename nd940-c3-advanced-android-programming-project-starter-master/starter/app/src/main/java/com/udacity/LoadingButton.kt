package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val textPaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.default_text_size)
    }
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val rectanglePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val circlePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
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
        // setup attributes
        setupAttributes(attrs)
    }

    @SuppressLint("ResourceType")
    private fun setupAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        ).apply {
            try{
                rectanglePaint.color = getColor(R.styleable.LoadingButton_backgroundColor,
                    ContextCompat.getColor(context, R.color.colorPrimary))
                textPaint.color = getColor(R.styleable.LoadingButton_textColor,
                    ContextCompat.getColor(context, R.color.white))
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRectangle(canvas)
        drawProgressBar(canvas)
        drawCircle(canvas)
        drawText(canvas)
    }

    private fun drawRectangle(canvas: Canvas) {
        // rectangle
        canvas.clipRect(
            clipRectLeft, clipRectTop,
            clipRectRight, clipRectBottom
        )
        canvas.drawColor(Color.CYAN)
    }

    private fun drawProgressBar(canvas: Canvas){
        progressPaint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        canvas.drawRect(0f, 0f, currentWidth, measuredHeight.toFloat(), progressPaint)
    }

    private fun drawCircle(canvas: Canvas) {
        circlePaint.color = ContextCompat.getColor(context, R.color.orange)
        canvas.drawArc(measuredWidth / 2 + 160f,
            measuredHeight / 2 -50f,
            measuredWidth/2 + 210f,
            measuredHeight/2+50f,
            0f, progressCircle, true, circlePaint)
    }

    private fun drawText(canvas: Canvas){
        //textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(buttonText,
            textX, textY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}