package com.malkinfo.scratchcardss

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


internal class ScratchCard : View {
    private var mDrawable: Drawable? = null
    private var mScratchWidth = 0f
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mPath: Path? = null
    private var mInnerPaint: Paint? = null
    private var mOuterPaint: Paint? = null
    private var mListener: OnScratchListener? = null

    interface OnScratchListener {
        fun onScratch(scratchCard: ScratchCard?, visiblePercent: Float)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
            context,
            attrs,
            defStyle
    ) {
        resolveAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        resolveAttr(context, attrs)
    }

    constructor(context: Context) : super(context) {
        resolveAttr(context, null)
    }

    private fun resolveAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchCard)
        mDrawable = typedArray.getDrawable(R.styleable.ScratchCard_scratchDrawable)
        mScratchWidth = typedArray.getDimension(
                R.styleable.ScratchCard_scratchWidth,
                Utils.dipToPx(context, 70f)
        )
        typedArray.recycle()
    }

    fun setOnScratchListener(listener: OnScratchListener?) {
        mListener = listener
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (mBitmap != null) mBitmap!!.recycle()
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
        if (mDrawable != null) {
            mDrawable!!.setBounds(0, 0, mBitmap!!.getWidth(), mBitmap!!.getHeight())
            mDrawable!!.draw(mCanvas!!)
        } else {
            mCanvas!!.drawColor(-0x138f9d)
        }
        if (mPath == null) {
            mPath = Path()
        }
        if (mInnerPaint == null) {
            mInnerPaint = Paint()
            mInnerPaint!!.isAntiAlias = true
            mInnerPaint!!.isDither = true
            mInnerPaint!!.style = Paint.Style.STROKE
            mInnerPaint!!.isFilterBitmap = true
            mInnerPaint!!.strokeJoin = Paint.Join.ROUND
            mInnerPaint!!.strokeCap = Paint.Cap.ROUND
            mInnerPaint!!.strokeWidth = mScratchWidth
            mInnerPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        if (mOuterPaint == null) {
            mOuterPaint = Paint()
        }
    }

    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val currentTouchX = event.x
        val currentTouchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath!!.reset()
                mPath!!.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = abs(currentTouchX - mLastTouchX)
                val dy = abs(currentTouchY - mLastTouchY)
                if (dx >= 4 || dy >= 4) {
                    val x1 = mLastTouchX
                    val y1 = mLastTouchY
                    val x2 = (currentTouchX + mLastTouchX) / 2
                    val y2 = (currentTouchY + mLastTouchY) / 2
                    mPath!!.quadTo(x1, y1, x2, y2)
                }
            }
            MotionEvent.ACTION_UP -> {
                mPath!!.lineTo(currentTouchX, currentTouchY)
                if (mListener != null) {
                    val width = mBitmap!!.width
                    val height = mBitmap!!.height
                    val total = width * height
                    var count = 0
                    var i = 0
                    while (i < width) {
                        var j = 0
                        while (j < height) {
                            if (mBitmap!!.getPixel(i, j) == 0x00000000) count++
                            j += 3
                        }
                        i += 3
                    }
                    mListener!!.onScratch(this, count.toFloat() / total * 9)
                }
            }
        }
        mCanvas!!.drawPath(mPath!!, mInnerPaint!!)
        mLastTouchX = currentTouchX
        mLastTouchY = currentTouchY
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mOuterPaint)
        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mBitmap != null) {
            mBitmap!!.recycle()
            mBitmap = null
        }
    }
}