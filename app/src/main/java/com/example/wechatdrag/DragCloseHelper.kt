package com.example.wechatdrag

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

class DragCloseHelper(private val mContext: Context) {

    companion object {
        // 动画执行时间
        private const val DURATION: Long = 100
        //滑动边界距离
        private const val MAX_EXIT_Y = 500
        //最小的缩放尺寸
        private const val MIN_SCALE = 0.5f
    }

    //加速度检测
    private var mVelocityTracker: VelocityTracker? = null
    //最小移动距离
    private val scaledTouchSlop by lazy {
        ViewConfiguration.get(mContext).scaledTouchSlop
    }

    private var maxExitY = MAX_EXIT_Y
    private var minScale = MIN_SCALE

    //是否在滑动关闭中，手指还在触摸中
    private var isSwipingToClose: Boolean = false

    //上次触摸坐标
    private var mLastY: Float = 0F
    private var mLastRawY: Float = 0F
    private var mLastX: Float = 0F
    private var mLastRawX: Float = 0F
    //上次触摸手指id
    private var lastPointerId: Int = 0
    //当前位移距离
    private var mCurrentTranslationY: Float = 0F
    private var mCurrentTranslationX: Float = 0F
    //正在恢复原位中
    private var isResetingAnimate = false
    //控制透明度
    private lateinit var parentV: View
    //控制Scale
    private lateinit var childV: View
    //回调
    private lateinit var dragCloseListener: DragCloseListener

    fun setDragCloseListener(dragCloseListener: DragCloseListener) {
        this.dragCloseListener = dragCloseListener
    }

    /**
     * 设置拖拽关闭的view和基本信息
     */
    fun setDragCloseViews(
        parentV: View,
        childV: View
    ) {
        this.parentV = parentV
        this.childV = childV
    }

    /**
     * 处理touch事件
     */
    fun handleEvent(event: MotionEvent): Boolean {
        if (!dragCloseListener.isDragEnable()) {
            isSwipingToClose = false
            return false
        } else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mVelocityTracker = VelocityTracker.obtain()
                    //初始化数据
                    lastPointerId = event.getPointerId(0)
                    reset(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount > 1) {
                        //如果有多个手指
                        if (isSwipingToClose) {
                            //已经开始滑动关闭，恢复原状，否则需要派发事件
                            isSwipingToClose = false
                            resetCallBackAnimation()
                            return true
                        }
                        reset(event)
                        return false
                    }
                    if (lastPointerId != event.getPointerId(0)) {
                        //手指不一致，恢复原状
                        if (isSwipingToClose) {
                            resetCallBackAnimation()
                        }
                        reset(event)
                        return false
                    }
                    val currentY = event.y
                    val currentX = event.x
                    //不能一开始就往上滑动
                    if (!isSwipingToClose && currentY - mLastY < -scaledTouchSlop) {
                        return false
                    }
                    mVelocityTracker?.addMovement(event)
                    if (isSwipingToClose ||
                        (abs(currentY - mLastY) > scaledTouchSlop &&
                                abs(currentY - mLastY) > abs(currentX - mLastX))
                    ) {
                        //已经触发或者开始触发，更新view
                        mLastY = currentY
                        mLastX = currentX
                        val currentRawY = event.rawY
                        val currentRawX = event.rawX
                        if (!isSwipingToClose) {
                            //准备开始
                            isSwipingToClose = true
                            dragCloseListener.dragStart()
                        }
                        mCurrentTranslationY = currentRawY - mLastRawY
                        mCurrentTranslationX = currentRawX - mLastRawX
                        var percent = 1 - abs(mCurrentTranslationY / (maxExitY + childV.height))
                        if (percent > 1) {
                            percent = 1f
                        } else if (percent < 0) {
                            percent = 0f
                        }
                        parentV.background.mutate().alpha = (percent * 255).toInt()
                        dragCloseListener.dragging(percent)
                        if (percent < minScale) {
                            percent = minScale
                        }
                        //中心缩放
                        childV.pivotX = event.rawX
                        childV.pivotY = event.rawY
                        childV.scaleX = percent
                        childV.scaleY = percent
                        //平移
                        childV.translationY = mCurrentTranslationY
                        childV.translationX = mCurrentTranslationX
                        return true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    //计算加速度
                    mVelocityTracker?.computeCurrentVelocity(1000)
                    val ySpeed = mVelocityTracker?.getYVelocity(lastPointerId) ?: 0F
                    releaseVelocityTracker()
                    //手指抬起事件
                    if (isSwipingToClose) {
                        if (mCurrentTranslationY >= maxExitY || ySpeed > 0F) {
                            dragCloseListener.dragClose(true)
                        } else {
                            resetCallBackAnimation()
                        }
                        isSwipingToClose = false
                        return true
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    releaseVelocityTracker()
                    //取消事件
                    if (isSwipingToClose) {
                        resetCallBackAnimation()
                        isSwipingToClose = false
                        return true
                    }
                }
            }
            return false
        }
    }

    /**
     * 释放加速度检测
     */
    private fun releaseVelocityTracker() {
        mVelocityTracker?.clear()
        mVelocityTracker?.recycle()
        mVelocityTracker = null
    }

    /**
     * 重置数据
     */
    private fun reset(event: MotionEvent) {
        isSwipingToClose = false
        mLastY = event.y
        mLastX = event.x
        mLastRawY = event.rawY
        mLastRawX = event.rawX
    }

    /**
     * 更新缩放的view
     */
    private fun updateChildView(transX: Float, transY: Float) {
        childV.translationY = transY
        childV.translationX = transX
        val percent = abs(transY / (maxExitY + childV.height))
        var scale = 1 - percent
        if (scale < minScale) {
            scale = minScale
        }
        childV.scaleX = scale
        childV.scaleY = scale
    }

    /**
     * 恢复到原位动画
     */
    private fun resetCallBackAnimation() {
        if (isResetingAnimate || mCurrentTranslationY == 0f) {
            return
        }
        val ratio = mCurrentTranslationX / mCurrentTranslationY
        val animatorY = ValueAnimator.ofFloat(mCurrentTranslationY, 0F)
        animatorY.addUpdateListener { valueAnimator ->
            if (isResetingAnimate) {
                mCurrentTranslationY = valueAnimator.animatedValue as Float
                mCurrentTranslationX = ratio * mCurrentTranslationY
                updateChildView(mCurrentTranslationX, mCurrentTranslationY)
            }
        }
        animatorY.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isResetingAnimate = true
            }

            override fun onAnimationEnd(animation: Animator) {
                if (isResetingAnimate) {
                    parentV.background.mutate().alpha = 255
                    mCurrentTranslationY = 0f
                    mCurrentTranslationX = 0f
                    isResetingAnimate = false
                    dragCloseListener.dragCancel()
                }
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        animatorY.setDuration(DURATION).start()
    }

    interface DragCloseListener {
        /**
         * 是否拦截
         */
        fun isDragEnable(): Boolean

        /**
         * 开始拖拽
         */
        fun dragStart()

        /**
         * 拖拽中
         */
        fun dragging(percent: Float)

        /**
         * 取消拖拽
         */
        fun dragCancel()

        /**
         * 拖拽结束并且关闭
         */
        fun dragClose(isShareElementMode: Boolean)
    }
}