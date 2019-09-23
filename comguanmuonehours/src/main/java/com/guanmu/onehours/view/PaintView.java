package com.guanmu.onehours.view;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.guanmu.onehours.StorageUtils;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by bartl on 4/16/2016.
 * A view to draw overlay on bitmap
 */
public class PaintView extends View {
    // judge your fingers' tremble
    private static final float TOUCH_TOLERANCE = 4;
    private Context context;
    private Uri mBackgroundUri;
    private Bitmap mBitmap, mBitmapBackGround, mBitmapForeground;
    private Canvas mCanvas;
    private Paint mEraser, mPaint, mBitmapPaint;
    private Path mPath, mPreviewPath;
    private boolean mIsPaint = true;
    private float mX, mY, left, top;


    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mPaint = new Paint();
        mEraser = new Paint();
        mPath = new Path();
        mPreviewPath = new Path();
        initPaint(mPaint, 0xFF880088, 50);
        initEraser(mEraser, 80);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmapBackGround == null) return;

        if (mBitmap == null) {
            float heightRatio = (float) mBitmapBackGround.getHeight() / (float) getHeight();
            float widthRatio = (float) mBitmapBackGround.getWidth() / (float) getWidth();
            float scaleRatio = heightRatio > widthRatio ? heightRatio : widthRatio;
            mBitmapBackGround = Bitmap.createScaledBitmap(mBitmapBackGround,
                    Math.round(mBitmapBackGround.getWidth() / scaleRatio),
                    Math.round(mBitmapBackGround.getHeight() / scaleRatio),
                    true);
            mBitmap = Bitmap.createBitmap(mBitmapBackGround.getWidth(), mBitmapBackGround.getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            if (mBitmapForeground!=null) {
                mCanvas.drawBitmap(mBitmapForeground, 0, 0, mBitmapPaint);
            }
        }

        left = (getWidth() - mBitmap.getWidth()) / 2;
        top = (getHeight() - mBitmap.getHeight()) / 2;
        canvas.drawBitmap(mBitmapBackGround, left, top, mBitmapPaint);

        canvas.drawBitmap(mBitmap, left, top, mBitmapPaint);

        if (mIsPaint) {
            canvas.drawPath(mPreviewPath, mPaint);
        }

    }

    private void touchDown(Path path, Path offsetPath, float x, float y) {
        path.reset();
        offsetPath.reset();
        path.moveTo(x, y);
        offsetPath.moveTo(x + left, y + top);
        mX = x;
        mY = y;
        if (!mIsPaint){
            mCanvas.drawPath(path, mEraser);
        }
    }


    private void touchMove(Path path, Path offsetPath, float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            offsetPath.quadTo(mX + left, mY + top, (x + mX) / 2 + left, (y + mY) / 2 + top);
            mX = x;
            mY = y;
        }
        if (!mIsPaint){
            mCanvas.drawPath(path, mEraser);
        }
    }

    private void touchUp(Path path, Path offsetPath, Paint paint) {
        path.lineTo(mX, mY);
        if (mCanvas != null) {
            mCanvas.drawPath(path, paint);
        }
        path.reset();
        offsetPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - left;
        float y = event.getY() - top;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(mPath, mPreviewPath, x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                touchMove(mPath, mPreviewPath, x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (mIsPaint) {
                    touchUp(mPath, mPreviewPath, mPaint);
                } else {
                    touchUp(mPath, mPreviewPath, mEraser);
                }
                invalidate();
                break;
        }
        return true;
    }

    public void setBackground(Uri backUri, @Nullable Uri foreUri) throws FileNotFoundException {
        ContentResolver cr = context.getContentResolver();
        mBackgroundUri = backUri;
        mBitmapBackGround = BitmapFactory.decodeStream(cr.openInputStream(backUri));
        if(foreUri !=null)
            mBitmapForeground = BitmapFactory.decodeStream(cr.openInputStream(foreUri));
    }

    public boolean cleanKnowledge() {
        Bitmap emptyBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
        return !mBitmap.sameAs(emptyBitmap);
    }

    public Pair<String, String> savePictures() throws FileNotFoundException {
        ContentResolver cr = context.getContentResolver();
        List<String> filePath = mBackgroundUri.getPathSegments();
        String fileName = filePath.get(filePath.size() - 1);
        mBitmapBackGround.compress(Bitmap.CompressFormat.PNG, 100, cr.openOutputStream(mBackgroundUri));

        String[] tmpArr = fileName.split("\\.");
        tmpArr[tmpArr.length - 2] = tmpArr[tmpArr.length - 2] + "_MASK";
        String maskFileName = TextUtils.join(".", tmpArr);
        Uri maskUri = StorageUtils.getOutputMediaFileUri(context, maskFileName);
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, cr.openOutputStream(maskUri));
        return Pair.create(fileName, maskFileName);
    }

    // init paint
    private void initPaint(Paint paint, int color, int width) {
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
    }

    // init paint
    private void initEraser(Paint paint, int width) {
        paint.setAntiAlias(true);
        paint.setDither(false);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        // The most important
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public int getPenColor() {
        return mPaint.getColor();
    }

    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    public void togglePenState() {
        mIsPaint = !mIsPaint;
    }

    public int getPenWidth() {
        return Math.round(mPaint.getStrokeWidth());
    }

    public void setPenWidth(int width) {
        mPaint.setStrokeWidth(width);
    }

    public int getEraserWidth() {
        return Math.round(mEraser.getStrokeWidth());
    }

    public void setEraserWidth(int width) {
        mEraser.setStrokeWidth(width);
    }

    public boolean isPaint() {
        return mIsPaint;
    }
}
