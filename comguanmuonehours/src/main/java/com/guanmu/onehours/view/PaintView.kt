package com.guanmu.onehours.view

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import com.guanmu.onehours.StorageUtils
import java.io.FileNotFoundException
import kotlin.concurrent.thread

/**
 * Created by bartl on 4/16/2016.
 * A view to draw overlay on bitmap
 */
class PaintView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mBackgroundUri: Uri? = null
    private var mBitmap: Bitmap? = null
    private var mBitmapBackGround: Bitmap? = null
    private var mBitmapForeground: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mEraser: Paint
    private val mPaint: Paint
    private val mBitmapPaint: Paint
    private val mPath: Path
    private val mPreviewPath: Path
    var isPaint = true
        private set
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
    private var left: Float = 0.toFloat()
    private var top: Float = 0.toFloat()

    var penColor: Int
        get() = mPaint.color
        set(color) {
            mPaint.color = color
        }

    var penWidth: Int
        get() = Math.round(mPaint.strokeWidth)
        set(width) {
            mPaint.strokeWidth = width.toFloat()
        }

    var eraserWidth: Int
        get() = Math.round(mEraser.strokeWidth)
        set(width) {
            mEraser.strokeWidth = width.toFloat()
        }


    init {
        mPaint = Paint()
        mEraser = Paint()
        mPath = Path()
        mPreviewPath = Path()
        initPaint(mPaint, -0x77ff78, 50)
        initEraser(mEraser, 80)
        mBitmapPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mBitmapBackGround == null) return

        if (mBitmap == null) {
            val heightRatio = mBitmapBackGround!!.height.toFloat() / height.toFloat()
            val widthRatio = mBitmapBackGround!!.width.toFloat() / width.toFloat()
            val scaleRatio = if (heightRatio > widthRatio) heightRatio else widthRatio
            mBitmapBackGround = Bitmap.createScaledBitmap(mBitmapBackGround!!,
                    Math.round(mBitmapBackGround!!.width / scaleRatio),
                    Math.round(mBitmapBackGround!!.height / scaleRatio),
                    true)
            mBitmap = Bitmap.createBitmap(mBitmapBackGround!!.width, mBitmapBackGround!!.height, Bitmap.Config.ARGB_8888)
            mCanvas = Canvas(mBitmap!!)
            if (mBitmapForeground != null) {
                mCanvas!!.drawBitmap(mBitmapForeground!!, 0f, 0f, mBitmapPaint)
            }
        }

        left = ((width - mBitmap!!.width) / 2).toFloat()
        top = ((height - mBitmap!!.height) / 2).toFloat()
        canvas.drawBitmap(mBitmapBackGround!!, left, top, mBitmapPaint)

        canvas.drawBitmap(mBitmap!!, left, top, mBitmapPaint)

        if (isPaint) {
            canvas.drawPath(mPreviewPath, mPaint)
        }

    }

    private fun touchDown(path: Path, offsetPath: Path, x: Float, y: Float) {
        path.reset()
        offsetPath.reset()
        path.moveTo(x, y)
        offsetPath.moveTo(x + left, y + top)
        mX = x
        mY = y
        if (!isPaint) {
            mCanvas!!.drawPath(path, mEraser)
        }
    }


    private fun touchMove(path: Path, offsetPath: Path, x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            offsetPath.quadTo(mX + left, mY + top, (x + mX) / 2 + left, (y + mY) / 2 + top)
            mX = x
            mY = y
        }
        if (!isPaint) {
            mCanvas!!.drawPath(path, mEraser)
        }
    }

    private fun touchUp(path: Path, offsetPath: Path, paint: Paint) {
        path.lineTo(mX, mY)
        if (mCanvas != null) {
            mCanvas!!.drawPath(path, paint)
        }
        path.reset()
        offsetPath.reset()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x - left
        val y = event.y - top
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDown(mPath, mPreviewPath, x, y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                touchMove(mPath, mPreviewPath, x, y)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (isPaint) {
                    touchUp(mPath, mPreviewPath, mPaint)
                } else {
                    touchUp(mPath, mPreviewPath, mEraser)
                }
                invalidate()
            }
        }
        return true
    }

    @Throws(FileNotFoundException::class)
    fun setBackground(backUri: Uri, foreUri: Uri?) {
        val cr = context.contentResolver
        mBackgroundUri = backUri
        mBitmapBackGround = BitmapFactory.decodeStream(cr.openInputStream(backUri))
        if (foreUri != null)
            mBitmapForeground = BitmapFactory.decodeStream(cr.openInputStream(foreUri))
    }

    fun cleanKnowledge(): Boolean {
        val emptyBitmap = Bitmap.createBitmap(mBitmap!!.width, mBitmap!!.height, mBitmap!!.config)
        return !mBitmap!!.sameAs(emptyBitmap)
    }

    @Throws(FileNotFoundException::class)
    fun savePictures(): Pair<String, String> {
        val cr = context.contentResolver
        val filePath = mBackgroundUri!!.pathSegments
        val fileName = filePath[filePath.size - 1]
        thread {
            mBitmapBackGround!!.compress(Bitmap.CompressFormat.PNG, 100, cr.openOutputStream(mBackgroundUri!!))
        }

        val tmpArr = fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        tmpArr[tmpArr.size - 2] = tmpArr[tmpArr.size - 2] + "_MASK"
        val maskFileName = TextUtils.join(".", tmpArr)
        val maskUri = StorageUtils.getOutputMediaFileUri(context, maskFileName)
        thread {
            mBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, cr.openOutputStream(maskUri!!))
        }
        return Pair.create(fileName, maskFileName)
    }

    // init paint
    private fun initPaint(paint: Paint, color: Int, width: Int) {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = width.toFloat()
    }

    // init paint
    private fun initEraser(paint: Paint, width: Int) {
        paint.isAntiAlias = true
        paint.isDither = false
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = width.toFloat()
        // The most important
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun togglePenState() {
        isPaint = !isPaint
    }

    companion object {
        // judge your fingers' tremble
        private val TOUCH_TOLERANCE = 4f
    }
}
