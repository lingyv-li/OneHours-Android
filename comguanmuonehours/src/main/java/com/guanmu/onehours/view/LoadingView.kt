package com.guanmu.onehours.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.RelativeLayout

import com.guanmu.onehours.R

class LoadingView : RelativeLayout {
    private val REFRESH_SCALE = 1.20f
    protected var mOriginalOffsetTop: Int = 0
    protected var mFrom: Int = 0
    private var mCircleView: CircleImageView? = null
    private var mProgress: MaterialProgressDrawable? = null
    private var mNext: Drawable? = null
    private var mCircleWidth: Int = 0
    private var mCircleHeight: Int = 0
    private var mCurrentTargetOffsetTop: Int = 0
    private var mTotalDragDistance = -1f
    private val mAnimateToCorrectPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            var targetTop = 0
            var endTarget = 0
            endTarget = (mOriginalOffsetTop + mTotalDragDistance).toInt()
            targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mCircleView!!.top
            setTargetOffsetTopAndBottom(offset)
        }
    }
    private var mMediumAnimationDuration: Int = 0
    private var mScaleAnimation: Animation? = null
    private var mScaleDownAnimation: Animation? = null
    private var mDecelerateInterpolator: DecelerateInterpolator? = null
    private var mRefresh = false
    private val mRefreshListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}

        override fun onAnimationRepeat(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {
            if (mRefresh) {

                mProgress!!.start()

            } else {
                mProgress!!.stop()
                mCircleView!!.visibility = View.GONE
                ViewCompat.setScaleX(mCircleView!!, 0f)
                ViewCompat.setScaleY(mCircleView!!, 0f)
            }
            mCurrentTargetOffsetTop = mCircleView!!.top
        }
    }

    constructor(context: Context) : super(context) {
        initView(getContext())
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(getContext())
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(getContext())
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(getContext())
    }

    fun setNextable(nextable: Boolean) {
        isClickable = nextable
        if (nextable) {
            if (mNext == null) {
                mNext = ContextCompat.getDrawable(context, R.drawable.ic_chevron_right_black_24dp)
                mNext!!.alpha = 120
                //                mNext.setBounds(mProgress.getBounds());
            }
            mCircleView!!.setImageDrawable(mNext)
        } else {
            mCircleView!!.setImageDrawable(mProgress)
        }
    }

    private fun initView(context: Context) {
        createProgressView()
        val metrics = resources.displayMetrics
        mCircleWidth = (CIRCLE_DIAMETER_Big * metrics.density).toInt()
        mCircleHeight = mCircleWidth
        mTotalDragDistance = DEFAULT_CIRCLE_TARGET * metrics.density
        mMediumAnimationDuration = resources.getInteger(
                android.R.integer.config_mediumAnimTime)
        mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
        visibility = View.VISIBLE
    }

    /*创建动画View*/
    private fun createProgressView() {
        /*创建圆形的ImageView*/
        mCircleView = CircleImageView(context, CIRCLE_BG_LIGHT, (CIRCLE_DIAMETER_Big / 2).toFloat())
        /*创建加载动画Drawable*/
        mProgress = MaterialProgressDrawable(context, this)
        /*设置颜色*/
        mProgress!!.setBackgroundColor(CIRCLE_BG_LIGHT)
        mCircleView!!.setImageDrawable(mProgress)
        mCircleView!!.visibility = View.VISIBLE
        mProgress!!.alpha = 256
        /*设置加载时的颜色值*/
        mProgress!!.setColorSchemeColors(-0x364b375, -0x3648284, -0x3bc6485, -0x3d86723, -0x3d0d823, -0x338ba23, -0x3e000dc8)
        isClickable = false
        addView(mCircleView)
    }

    /*这个是动态改变动画的效果*/
    fun setScale(scale: Float) {
        mProgress!!.showArrow(true)
        mProgress!!.setArrowScale(Math.min(1.0f, scale))
        mProgress!!.setProgressRotation(scale)
        mProgress!!.setStartEndTrim(0f, Math.min(.8f, 0.8f * scale))
    }

    /*这里暂时没用到*/
    fun onRefresh(scale: Float) {

        mRefresh = true
        startScaleUpAnimation(null)
        animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener)
    }

    /*这里暂时没用到*/
    fun stopRefresh() {
        mRefresh = false
        startScaleDownAnimation(mRefreshListener)
    }

    /*开始执行动画效果*/
    fun startAnimation() {

        mProgress!!.start()
    }

    /*结束知心动画效果*/
    fun stopAnimation() {

        mProgress!!.stop()
    }

    private fun startScaleUpAnimation(listener: Animation.AnimationListener?) {
        mCircleView!!.visibility = View.VISIBLE
        mScaleAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(interpolatedTime)
            }
        }
        if (listener != null) {
            mScaleAnimation!!.setAnimationListener(listener)
        }
        mScaleAnimation!!.duration = mMediumAnimationDuration.toLong()
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleAnimation)
    }

    private fun startScaleDownAnimation(listener: Animation.AnimationListener) {
        mScaleDownAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(1 - interpolatedTime)
            }
        }
        mScaleDownAnimation!!.duration = 150
        mCircleView!!.setAnimationListener(listener)
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleDownAnimation)
    }

    private fun animateOffsetToCorrectPosition(from: Int, listener: Animation.AnimationListener?) {
        mFrom = from
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = 200
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        if (listener != null) {
            mAnimateToCorrectPosition.setAnimationListener(listener)
        }
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mAnimateToCorrectPosition)
    }

    /*当view设为可见的时候，动画开始执行，反之动画停止*/
    override fun setVisibility(visibility: Int) {
        if (visibility == View.GONE || visibility == View.INVISIBLE) {

            mProgress!!.stop()
        } else {
            mProgress!!.start()
            mProgress!!.showArrow(true)
        }
        super.setVisibility(visibility)
    }

    /**
     * @param progress
     */
    private fun setAnimationProgress(progress: Float) {
        ViewCompat.setScaleX(mCircleView!!, progress)
        ViewCompat.setScaleY(mCircleView!!, progress)
    }

    private fun setTargetOffsetTopAndBottom(offset: Int) {
        mCircleView!!.bringToFront()
        mCircleView!!.offsetTopAndBottom(offset)
        mCurrentTargetOffsetTop = mCircleView!!.top
        invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val width = measuredWidth
        val circleWidth = mCircleView!!.measuredWidth
        val circleHeight = mCircleView!!.measuredHeight
        mCircleView!!.layout(width / 2 - circleWidth / 2, 0,
                width / 2 + circleWidth / 2, circleHeight)

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mCircleView!!.measure(View.MeasureSpec.makeMeasureSpec(mCircleWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mCircleHeight, View.MeasureSpec.EXACTLY))

    }

    companion object {

        private val CIRCLE_BG_LIGHT = -0x50506
        private val DEFAULT_CIRCLE_TARGET = 64
        private val CIRCLE_DIAMETER_Big = 45
        private val DECELERATE_INTERPOLATION_FACTOR = 2f
    }
}