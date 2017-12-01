package com.aceegg.sidebar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align.CENTER
import android.graphics.Rect
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.collections.ArrayList


/**
 * 索引
 * Created by jinwenxiu on 2017/11/21.
 */
class FloatSideBar : View {

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

    private var mIndexArray: Array<String> = emptyArray()

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

    private var mIndexMarginRight: Float = 0f
    private var mIndexTextColor: Int = 0
    private var mIndexChooseColor: Int = 0

    fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatSideBar)
        val indexArrayId = typedArray.getResourceId(R.styleable.FloatSideBar_index,
            R.array.sidebar_index)
        mIndexMarginRight = typedArray.getDimension(R.styleable.FloatSideBar_index_margin_right,
            14f)
        val indexTextSize = typedArray.getDimension(R.styleable.FloatSideBar_index_text_size, 0f)
        mIndexTextColor = typedArray.getColor(R.styleable.FloatSideBar_index_text_color, Color.GRAY)
        mIndexChooseColor = typedArray.getColor(R.styleable.FloatSideBar_index_choose_color,
            Color.rgb(0, 168, 255))
        mIndexArray = context.resources.getStringArray(indexArrayId)
        typedArray.recycle()
        mDefaultPaint.color = mIndexTextColor
        mDefaultPaint.textAlign = CENTER
        mDefaultPaint.isAntiAlias = true
        mDefaultPaint.textSize = indexTextSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 整体宽高
        mWidth = w - dp2px(mIndexMarginRight)
        mHeight = h - paddingTop - paddingBottom
        // 计算一个索引的高度 字体大小
        val length = getIndexLength()

        if (mDefaultPaint.textSize == 0f) {
            mDefaultPaint.textSize = minOf(mHeight * 0.60f / length, sp2px(12f).toFloat())
        }
        mIndexHeight = if (length == 0) 0 else minOf(mHeight / length,
            24 + mDefaultPaint.textSize.toInt())
        // 计算索引绘制区域
        val topPadding = if (length == 0) 0 else (mHeight - mIndexHeight * length) / 2
        mIndexRect.set(w - dp2px(16 * 2f), 0 + topPadding, w, h - topPadding)
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
            indexY = mIndexHeight * (i + 1) + mIndexRect.top

            if (i == mChooseIndex) {
                scale = Math.min(CHOOSE_SCALE, 1f + mPressTime * CHOOSE_SCALE / 100)
                mDefaultPaint.color = mIndexChooseColor
            } else {
                val maxPos = Math.abs(Math.pow((mY - indexY) / 18.0, 2.0).toFloat() / mHeight * 8f)
                scale = Math.max(1f, Math.min(UNCHOOSE_SCALE_MAX,
                    mPressTime * UNCHOOSE_SCALE_MAX / 115 + 1) - maxPos)
                if (!mIsBeingDragged) {
                    scale = 1f
                }
                diffY = maxPos * 50f * (if (indexY >= mY) -1 else 1).toFloat()
                diffX = maxPos * 100

                mDefaultPaint.color = mIndexTextColor
            }
            canvas?.save()

            canvas?.scale(scale, scale,
                mWidth.toFloat() * 1.2f + diffX + Math.min(dp2px(100f).toFloat(),
                    (mInitDownX - mX)), indexY.toFloat() + diffY)
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

    private fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            resources.displayMetrics).toInt()
    }

    private fun sp2px(sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
            resources.displayMetrics).toInt()
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
        val chooseIndex: Int = (y - mIndexRect.top).toInt() / mIndexHeight

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

    fun setIndexList(indexList: Array<String>) {
        mIndexArray = indexList
        postInvalidate()
    }

    fun setIndexList(indexList: ArrayList<String>) {
        mIndexArray = indexList.toTypedArray()

        postInvalidate()
    }

    fun setIndexList(indexList: Array<IndexAble>) {
        mIndexArray = emptyArray()

        indexList.forEachIndexed { index, indexAble ->
            mIndexArray[index] = indexAble.indexOfThisObject()
        }

        postInvalidate()
    }
}