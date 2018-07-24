package normanco.ninja;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by toman on 5/05/2018.
 */

public class Image {
    private static final String TAG = "LogMessage";
    public static final int DEFAULT_IMAGE = 100;
    private static final int IMAGE_NOT_FOUND = 200;

    private static Uri defImage;
    private static Uri image;

    public Image() {
        defImage = Uri.parse("android.resource://normanco.ninja/" + R.mipmap.ninja_default_bg);
    }

    public Image(Context mContext, String path) {
        defImage = Uri.parse("android.resource://normanco.ninja/" + R.mipmap.ninja_default_bg);
        File file = new File(path);
        image = GetUriFromPath.getImageContentUri(mContext, file);
    }

    public Uri getImageUri (){
        return image;
    }

    public Uri getDefImageUri(){
        return defImage;
    }

    public static Matrix rotateImage (Context mContext, Uri uri, int imageDefFlag){
        Log.i(TAG, "Using default flag: " + imageDefFlag);
        int orientation;
        float scale, screenHeight, screenWidth, imageHeight, imageWidth;
        Matrix matrix = new Matrix();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenHeight = displayMetrics.heightPixels;                               Log.i(TAG, "Screen height: " + screenHeight);
        screenWidth = displayMetrics.widthPixels;                                 Log.i(TAG, "Screen width: " + screenWidth);

        try{
            imageHeight = getImageHeight(mContext, uri);
            imageWidth = getImageWidth(mContext, uri);
            orientation = checkOrientation(mContext, uri);

        } catch(NullPointerException | IllegalArgumentException e){
            imageHeight = screenHeight;
            imageWidth = screenWidth;

            if(imageDefFlag == DEFAULT_IMAGE){
                orientation = DEFAULT_IMAGE;
                Log.i(TAG, "Matrix, Using default imageBackground: " + e);
            }
            else{
                Log.i(TAG, "Image path not found: " + e);
                orientation = IMAGE_NOT_FOUND;
            }
        }

        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                scale = (screenHeight / imageHeight > screenWidth / imageWidth) ? screenHeight / imageHeight : screenWidth / imageWidth;        Log.i(TAG, "Scale normal: " + scale);
                matrix.postScale(scale, scale, imageWidth / 2 , imageHeight / 2);                                                       Log.i(TAG, "Scaled imageBackground dimensions: " + imageHeight * scale);
                matrix.postTranslate((screenWidth - imageWidth) / 2, (screenHeight - imageHeight) / 2);
                matrix.postRotate(0);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                scale = (screenWidth / imageHeight > screenHeight / imageWidth) ? screenWidth / imageHeight : screenHeight / imageWidth;        Log.i(TAG, "Scale 90: " + scale);
                matrix.postScale(scale, scale, 0, 0);                                                                                   Log.i(TAG, "Scaled imageBackground dimensions: " + imageHeight * scale);
                matrix.postTranslate((screenWidth / 2 - imageWidth * scale / 2), (screenHeight / 2 - imageHeight * scale / 2));
                matrix.postRotate(90, screenWidth / 2, screenHeight / 2);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                scale = (screenHeight / imageHeight > screenWidth / imageWidth) ? screenHeight / imageHeight : screenWidth / imageWidth;        Log.i(TAG, "Scale 180: " + scale);
                matrix.postScale(scale, scale, imageWidth / 2, imageHeight / 2);                                                        Log.i(TAG, "Scaled imageBackground dimensions: " + imageHeight * scale);
                matrix.postTranslate((screenWidth - imageWidth) / 2, (screenHeight - imageHeight) / 2);
                matrix.postRotate(180, screenWidth / 2, screenHeight / 2);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                scale = (screenHeight / imageWidth > screenWidth / imageHeight) ? screenHeight / imageWidth : screenWidth / imageHeight;        Log.i(TAG, "Scale 270: " + scale);
                matrix.postScale(scale, scale, imageWidth / 2, imageHeight / 2);                                                        Log.i(TAG, "Scaled imageBackground dimensions: " + imageHeight * scale);
                matrix.postTranslate((screenWidth - imageWidth) / 2, (screenHeight - imageHeight) / 2);
                matrix.postRotate(270, screenWidth / 2, screenHeight / 2);
                break;
            case DEFAULT_IMAGE:
                imageWidth = ContextCompat.getDrawable(mContext, R.mipmap.ninja_default_bg).getIntrinsicWidth();                                Log.i(TAG, "Drawable width: " + imageWidth);
                imageHeight = ContextCompat.getDrawable(mContext, R.mipmap.ninja_default_bg).getIntrinsicHeight();                              Log.i(TAG, "Drawable height: " + imageHeight);
                scale = (screenWidth / imageWidth) * (float) 1.2;
                matrix.postScale(scale, scale, imageWidth / 2, imageHeight / 2);                                                        Log.i(TAG, "Default Image matrix scale");
                matrix.postTranslate((screenWidth - imageWidth) / 2,(screenHeight - imageHeight) * (float) 0.8);
                matrix.postRotate(0);
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                BitmapFactory.Options options = new BitmapFactory.Options();                                                                    Log.i(TAG, "No Exif Data Case");
                options.inJustDecodeBounds = true;
                String path = new GetPathFromUri().getUriRealPath(mContext, uri);                                                               Log.i(TAG, "File path: " + path);
                BitmapFactory.decodeFile(path, options);
                imageHeight = options.outHeight;
                imageWidth = options.outWidth;
                scale = (screenHeight / imageHeight > screenWidth / imageWidth) ? screenHeight / imageHeight : screenWidth / imageWidth;
                matrix.postScale(scale, scale, imageWidth / 2, imageHeight / 2);                                                        Log.i(TAG, "No orientation case width: " + imageWidth + " Height: " + imageHeight);
                matrix.postTranslate((screenWidth - imageWidth) / 2, (screenHeight - imageHeight) / 2);
                matrix.postRotate(0);
                break;
            default:
                scale = (screenWidth / imageWidth > screenHeight / imageHeight) ? (screenWidth / imageWidth) : (screenHeight / imageHeight);       Log.i(TAG, "Default Matrix scale: " + scale);
                matrix.postScale(scale, scale);
                matrix.postTranslate(0, 0);
                matrix.postRotate(0);
                break;
        }
        return matrix;
    }

    private static int checkOrientation(Context mContext, Uri uri){
        int orientation;
        String path = new GetPathFromUri().getUriRealPath(mContext, uri);
        try{
            ExifInterface exifInterface = new ExifInterface(path);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Log.i(TAG, "Image orientation: " + orientation);
        }
        catch(IOException e){
            Log.i(TAG, "Exif Interface exception: " + e);
            orientation = ExifInterface.ORIENTATION_UNDEFINED;
        }
        return orientation;
    }

    private static int getImageHeight(Context mContext, Uri uri){
        int height;
        String path = new GetPathFromUri().getUriRealPath(mContext, uri);
        try{
            ExifInterface exifInterface = new ExifInterface(path);
            height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            Log.i(TAG, "Image height: " + height);
        }
        catch(IOException e){
            Log.i(TAG, "Exif Interface height exception");
            height = 0;
        }
        return height;
    }

    private static int getImageWidth(Context mContext, Uri uri){
        int width;
        String path = new GetPathFromUri().getUriRealPath(mContext, uri);
        try{
            ExifInterface exifInterface = new ExifInterface(path);
            width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            Log.i(TAG, "Image width: " + width);
        }
        catch(IOException e){
            width = 0;
            Log.i(TAG, "Exif Interface width exception");
        }
        return width;
    }
}
