package com.zofers.zofers.staff;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImagePickUpUtil {

    /**
     * Gets file name.
     *
     * @param context the context
     * @param uri     the uri
     * @return the file name
     */
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Gets mime type.
     *
     * @param context the context
     * @param uri     the uri
     * @return the mime type
     */
    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    /**
     * Get bytes byte [ ].
     *
     * @param inputStream the input stream
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    /**
     * Calculate in sample size int.
     *
     * @param options   the options
     * @param reqWidth  the req width
     * @param reqHeight the req height
     * @return the int
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Decode sampled bitmap from file bitmap.
     *
     * @param imagePath the image path
     * @return the bitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(InputStream imagePath, InputStream inputStream, String exif) throws IOException {
        int reqWidth = 1000;
        int reqHeight = 1000;


        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imagePath, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //return Bitmap.createScaledBitmap(bt, reqWidth, reqHeight, false);

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        if(bitmap.getWidth() < 100 || bitmap.getHeight() < 50){
            return null;
        }
        return scaleImage(bitmap, exif);
    }

    private static Bitmap scaleImage(Bitmap bitmap, String exif){
        int maxWidth = 1200;
        int maxHeight = 1200;
        float scale = Math.min(((float)maxWidth / bitmap.getWidth()), ((float)maxHeight / bitmap.getHeight()));

        if(scale >= 1) {
            scale = 1;
        }

        int newWidth = (int) (bitmap.getWidth() * scale);
        int newHeight = (int) (bitmap.getHeight() * scale);

        Matrix scaleMatrix = new Matrix();
        int exifValue = Integer.parseInt(exif);
        scaleMatrix.setScale(scale, scale, 0, 0);
        switch (exifValue){
            case ExifInterface.ORIENTATION_ROTATE_90:
                scaleMatrix.postRotate(90, newWidth / 2, newHeight / 2);
                scaleMatrix.postTranslate((newHeight - newWidth) / 2, (newWidth - newHeight) / 2);

                int height = newHeight;
                newHeight = newWidth;
                newWidth = height;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                scaleMatrix.postRotate(180, newWidth / 2, newHeight / 2);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                scaleMatrix.postRotate(270, newWidth / 2, newHeight / 2);
                scaleMatrix.postTranslate((newHeight - newWidth) / 2, (newWidth - newHeight) / 2);

                int theight = newHeight;
                newHeight = newWidth;
                newWidth = theight;

                break;
        }


        Bitmap target = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(target);


        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, scaleMatrix, paint);


        bitmap.recycle();


        return target;
    }
}
