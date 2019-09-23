package com.guanmu.onehours;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

/**
 * Created by bartl on 4/16/2016.
 * <p>
 * Storage utils of storing image knowledge
 */
public class StorageUtils {

    public static final int MEDIA_TYPE_IMAGE = 1;

    /**
     * Create a file Uri for saving an image
     */
    public static Uri getOutputMediaFileUri(Context context, @Nullable String fileName) {
        File file = getOutputMediaFile(context, fileName);
        if (file == null) {
            return null;
        }
        return FileProvider.getUriForFile(context,
                context.getPackageName(),
                file);

//        return Uri.fromFile(getOutputMediaFile(context, fileName));
    }

    /**
     * Create a File for saving an image
     */
    private static File getOutputMediaFile(Context context, @Nullable String fileName) {
        // To be safe, check that the SDCard is mounted
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("One-Hours", "getOutputMediaFile: Media not mounted");
            return null;
        }

        File mediaStorageDir = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "ImageKnowledge");
        // A standard location for saving pictures and videos which are
        // associated with your application

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("One-Hours", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (fileName != null) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        } else {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".png");
        }

        return mediaFile;
    }
}
