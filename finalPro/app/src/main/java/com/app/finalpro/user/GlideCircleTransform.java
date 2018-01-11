package com.app.finalpro.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import static com.app.finalpro.user.ChangeBitmap.getRoundedCornerBitmap;

/**
 * Created by ZXG on 2018/1/8.
 */

//圆形图片
public class GlideCircleTransform extends BitmapTransformation {
    public GlideCircleTransform(Context context) {
        super(context);
    }

    @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
//        if (source == null) return null;
//
//        int size = Math.min(source.getWidth(), source.getHeight());
//        int x = (source.getWidth() - size) / 2;
//        int y = (source.getHeight() - size) / 2;
//
//        // TODO this could be acquired from the pool too
//        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
//
//        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
//        if (result == null) {
//            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
//        }
//
//        Canvas canvas = new Canvas(result);
//        Paint paint = new Paint();
//        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
//        paint.setAntiAlias(true);
//        float r = size / 2f;
//        canvas.drawCircle(r, r, r, paint);
//        return result;
        int rawHeight = source.getHeight();
        int rawWidth = source.getWidth();
        int newHeight = 500;
        int newWidth = 500;
        float heightScale = ((float) newHeight) / rawHeight;
        float widthScale = ((float) newWidth) / rawWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);
        Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, rawWidth, rawHeight, matrix, true);
        Bitmap newBitmap2=getRoundedCornerBitmap(newBitmap,500);
        return newBitmap2;
    }

    @Override public String getId() {
        return getClass().getName();
    }
}
