package com.sunallies.pvm.floatsidebar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align.CENTER
import android.graphics.Rect
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 * 索引
 * Created by jinwenxiu on 2017/11/21.
 */
class SideBar : View {

    interface OnIndexChooseListener {
        fun chooseIndex(position: Int, index: String)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
        defStyleAttr) {
        init(context, attrs)
    }

    private val MAX_INDEX_MOVE_X = 100
    private val CHOOSE_SCALE = 2.0f
    private val UNCHOOSE_SCALE_MAX = 2.0f

    private var mChooseListener: OnIndexChooseListener? = null

    private var mIndexArray: Array<out String> = emptyArray()

    private val mDefaultPaint: Paint = Paint()

    private val mIndexRect: Rect = Rect()

    private var mWidth: Int = 0

    private var mHeight: Int = 0

    private var mIndexHeight: Int = 0

    private var mInitDownY: Float = -1f

    private var mInitDownX: Float = -1f

    private var mY: Float = 0f

    private var mX: Float = 0f

    private var mPressTime: Float = 0f

    private var mActivePointerId: Int = 0

    private var mChooseIndex: Int = -1

    private var mIsBeingDragged: Boolean = false

    fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SideBar)
        val indexArrayId = typedArray?.getResourceId(R.styleable.SideBar_index,
            R.array.sidebar_index)
        mIndexArray = context.resources.getStringArray(indexArrayId!!)
        typedArray.recycle()
        mDefaultPaint.color = Color.GRAY
        mDefaultPaint.textAlign = CENTER
        mDefaultPaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 整体宽高
        mWidth = w - dp2px(16)
        mHeight = h - paddingTop - paddingBottom
        // 计算一个索引的高度 字体大小
        val length = getIndexLength()
        mIndexHeight = mHeight / length
        mDefaultPaint.textSize = mHeight * 0.7f / length
        // 计算索引绘制区域
        mIndexRect.set(w - dp2px(16 * 2), 0, w, h)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked

        when (action) {

            MotionEvent.ACTION_DOWN -> {

                mActivePointerId = event.getPointerId(0)
                val initDownY = getMotionEventY(event, mActivePointerId)
                val initDownX = getMotionEventX(event, mActivePointerId)
                if (initDownY == -1f) {
                    return false
                }
                if (!mIndexRect.contains(event.x.toInt(), event.y.toInt())) {
                    return false
                }
                mIsBeingDragged = true
                mInitDownY = initDownY
                mInitDownX = initDownX
                mY = initDownY
                findChooseIndex(mY)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == ViewDragHelper.INVALID_POINTER) {
                    return false
                }
                val y = getMotionEventY(event, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                mY = y
                mX = getMotionEventX(event, mActivePointerId)
                findChooseIndex(y)
                invalidate()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(event)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = ViewDragHelper.INVALID_POINTER
                mPressTime = 0f
                mInitDownY = -1f
                mInitDownX = -1f
                mChooseIndex = -1
                mIsBeingDragged = false
                invalidate()
                return false
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        var indexY: Int
        var i = 0
        var diffX = 0f
        var diffY = 0f
        var scale: Float
        for (item in mIndexArray) {
            indexY = mIndexHeight * (i + 1) + paddingTop

            if (i == mChooseIndex) {
                scale = Math.min(CHOOSE_SCALE, 1f + mPressTime * CHOOSE_SCALE/100)
                mDefaultPaint.color = Color.rgb(0, 168, 255)
            } else {
                val maxPos = Math.abs( Math.pow((mY - indexY)/18.0, 2.0).toFloat() / mHeight * 8f)
                scale = Math.max(1f, Math.min(UNCHOOSE_SCALE_MAX, mPressTime * UNCHOOSE_SCALE_MAX/115 + 1) - maxPos)
                if (!mIsBeingDragged) {
                    scale = 1f
                }
                diffY = maxPos * 50f * (if (indexY >= mY) -1 else 1).toFloat()
                diffX = maxPos * 100

                mDefaultPaint.color = Color.GRAY
            }
            canvas?.save()

            canvas?.scale(scale, scale, mWidth.toFloat() * 1.2f + diffX + Math.min(dp2px(100).toFloat(), (mInitDownX-mX)), indexY.toFloat() + diffY)
            canvas?.drawText(mIndexArray[i], mWidth.toFloat(), indexY.toFloat(),
                mDefaultPaint)
            canvas?.restore()

            i++
        }

        if (mIsBeingDragged && mPressTime < MAX_INDEX_MOVE_X) {
            mPressTime += 10f
            invalidate()
        } else {
            if (mPressTime > 0 && !mIsBeingDragged) {
                mPressTime -= 10f
                invalidate()
            }
        }
    }

    /**
     * 获取索引数量
     *
     * @return int,大于等于0
     */
    private fun getIndexLength(): Int {
        return mIndexArray.size
    }

    private fun dp2px(dp: Int): Int {
        return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    private fun getMotionEventY(event: MotionEvent, activePointer: Int): Float {
        val pointerIndex = event.findPointerIndex(activePointer)

        if (pointerIndex < 0) {
            return -1f
        }

        return event.getY(pointerIndex)
    }

    private fun getMotionEventX(event: MotionEvent, activePointer: Int): Float {
        val pointerIndex = event.findPointerIndex(activePointer)

        if (pointerIndex < 0) {
            return -1f
        }

        return event.getX(pointerIndex)
    }

    private fun onSecondaryPointerUp(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = event.getPointerId(newPointerIndex)
        }
    }

    private fun findChooseIndex(y: Float) {
        val chooseIndex: Int = (y - paddingTop).toInt() / mIndexHeight

        if (mChooseIndex != chooseIndex) {
            if (chooseIndex >= 0 && chooseIndex < mIndexArray.size) {
                mChooseIndex = chooseIndex
                mChooseListener?.chooseIndex(mChooseIndex, mIndexArray[mChooseIndex])
            }
        }
    }

    fun setOnChooseIndexListener(listener: OnIndexChooseListener) {
        mChooseListener = listener
    }
}