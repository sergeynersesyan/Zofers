package com.zofers.zofers.staff;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.OpenableColumns;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {
    public static String getFilePath(Uri uri, Activity activity) {
        String filePath;
        if (uri.toString().contains("content:")) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
            if (cursor == null) return null;
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndex(projection[0]);
            filePath =  cursor.getString(column_index);
            cursor.close();
        } else if (uri.toString().contains("file:")) {
            filePath = uri.getPath();
        } else {
            filePath = null;
        }



        return filePath;
    }

    public static byte[] getImageBinary (Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            InputStream inputStream2 = context.getContentResolver().openInputStream(imageUri);
            InputStream inputStream3 = context.getContentResolver().openInputStream(imageUri);

            ExifInterface oldExif = new ExifInterface(inputStream3);
            String exif = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);

            inputStream3.close();

            Bitmap thumbnail = ImagePickUpUtil.decodeSampledBitmapFromFile(inputStream, inputStream2, exif);
            if(thumbnail == null)
            {
                Toast.makeText(context, "Error. Please choose another image", Toast.LENGTH_SHORT).show();
                return null;
            }

            inputStream2.close();
            inputStream.close();


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
            byte[] binaryImage = out.toByteArray();
            out.close();
            thumbnail.recycle();

            return binaryImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    private static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
