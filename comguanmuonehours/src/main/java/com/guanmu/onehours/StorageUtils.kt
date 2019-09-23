package com.guanmu.onehours

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.FileProvider

/**
 * Created by bartl on 4/16/2016.
 *
 *
 * Storage utils of storing image knowledge
 */
object StorageUtils {

    val MEDIA_TYPE_IMAGE = 1

    /**
     * Create a file Uri for saving an image
     */
    fun getOutputMediaFileUri(context: Context, fileName: String?): Uri? {
        val file = getOutputMediaFile(context, fileName) ?: return null
        return FileProvider.getUriForFile(context,
                context.packageName,
                file)

        //        return Uri.fromFile(getOutputMediaFile(context, fileName));
    }

    /**
     * Create a File for saving an image
     */
    private fun getOutputMediaFile(context: Context, fileName: String?): File? {
        // To be safe, check that the SDCard is mounted
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Log.d("One-Hours", "getOutputMediaFile: Media not mounted")
            return null
        }

        val mediaStorageDir = File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "ImageKnowledge")
        // A standard location for saving pictures and videos which are
        // associated with your application

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("One-Hours", "failed to create directory")
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaFile: File
        if (fileName != null) {
            mediaFile = File(mediaStorageDir.path + File.separator + fileName)
        } else {
            mediaFile = File(mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".png")
        }

        return mediaFile
    }
}
