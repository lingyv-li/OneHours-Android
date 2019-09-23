package com.guanmu.onehours.view;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.guanmu.onehours.StorageUtils;
import com.guanmu.onehours.model.MPoint;

import java.io.FileNotFoundException;

/**
 * Created by bartl on 4/18/2016.
 * <p>
 * A TextView for testing text point(TYPE_NORMAL)
 */
public class TestImageView extends ImageView implements Testable {
    private MPoint point;

    public TestImageView(Context context) {
        super(context);
    }

    public TestImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(MPoint point) {
        this.point = point;
        ContentResolver cr = getContext().getContentResolver();
        Uri backgroundUri = StorageUtils.getOutputMediaFileUri(getContext(), point.getMetaContent().split("/")[0]);
        Uri foregroundUri = StorageUtils.getOutputMediaFileUri(getContext(), point.getMetaContent().split("/")[1]);

        try {
            Bitmap background = BitmapFactory.decodeStream(cr.openInputStream(backgroundUri)).copy(Bitmap.Config.ARGB_8888, true);
            Bitmap foreground = BitmapFactory.decodeStream(cr.openInputStream(foregroundUri));
            Canvas canvas = new Canvas(background);
            canvas.drawBitmap(foreground, 0, 0, new Paint(Paint.DITHER_FLAG));
            setImageBitmap(background);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showAll() {
        ContentResolver cr = getContext().getContentResolver();
        Uri backgroundUri = StorageUtils.getOutputMediaFileUri(getContext(), point.getMetaContent().split("/")[0]);
        try {
            Bitmap background = BitmapFactory.decodeStream(cr.openInputStream(backgroundUri)).copy(Bitmap.Config.ARGB_8888, true);
            setImageBitmap(background);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
