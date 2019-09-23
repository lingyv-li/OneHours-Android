package com.guanmu.onehours.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView
import com.guanmu.onehours.StorageUtils
import com.guanmu.onehours.model.MPoint
import java.io.FileNotFoundException

/**
 * Created by bartl on 4/18/2016.
 *
 *
 * A TextView for testing text point(TYPE_NORMAL)
 */
class TestImageView : ImageView, Testable {
    private var point: MPoint? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(mPoint: MPoint) {
        this.point = mPoint
        val cr = context.contentResolver
        val backgroundUri = StorageUtils.getOutputMediaFileUri(context, mPoint.metaContent!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        val foregroundUri = StorageUtils.getOutputMediaFileUri(context, mPoint.metaContent!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])

        try {
            val background = BitmapFactory.decodeStream(cr.openInputStream(backgroundUri!!)).copy(Bitmap.Config.ARGB_8888, true)
            val foreground = BitmapFactory.decodeStream(cr.openInputStream(foregroundUri!!))
            val canvas = Canvas(background)
            canvas.drawBitmap(foreground, 0f, 0f, Paint(Paint.DITHER_FLAG))
            setImageBitmap(background)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    override fun showAll() {
        val cr = context.contentResolver
        val backgroundUri = StorageUtils.getOutputMediaFileUri(context, point!!.metaContent!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        try {
            val background = BitmapFactory.decodeStream(cr.openInputStream(backgroundUri!!)).copy(Bitmap.Config.ARGB_8888, true)
            setImageBitmap(background)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }
}
