package com.app.finalpro.user;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by ER on 2017/12/30.
 */

public class ChangeBitmap {

    public static Bitmap show(Bitmap rawBitmap){
        int rawHeight = rawBitmap.getHeight();
        int rawWidth = rawBitmap.getWidth();
        int newHeight = 500;
        int newWidth = 500;
        float heightScale = ((float) newHeight) / rawHeight;
        float widthScale = ((float) newWidth) / rawWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);
        Bitmap newBitmap = Bitmap.createBitmap(rawBitmap, 0, 0, rawWidth, rawHeight, matrix, true);
        Bitmap newBitmap2=getRoundedCornerBitmap(newBitmap,500);
        return newBitmap2;
    }
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){
        Bitmap output = Bitmap.createBitmap(bitmap.getHeight(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF,roundPx,roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    public static Bitmap getSquareBitmap(Bitmap rawBitmap){
        int rawHeight = rawBitmap.getHeight();
        int rawWidth = rawBitmap.getWidth();
        int newHeight = 400;
        int newWidth = 300;
        float heightScale = ((float) newHeight) / rawHeight;
        float widthScale = ((float) newWidth) / rawWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);

        Bitmap newBitmap = Bitmap.createBitmap(rawBitmap, 0, 0, rawWidth, rawHeight, matrix, true);
        return newBitmap;
    }
}
