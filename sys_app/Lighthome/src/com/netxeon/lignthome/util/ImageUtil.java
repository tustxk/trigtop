package com.netxeon.lignthome.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;

public class ImageUtil {
	public static Bitmap createReflectionBitmap(Resources res, int imgId) {
		Bitmap bitmap = BitmapFactory.decodeResource(res, imgId);
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, false);
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(reflectionBitmap, 0, reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, reflectionGap, width, height, 0x70ffffff,
                0x00ffffff, TileMode.MIRROR);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, 0, width, height, paint);
        return newBitmap;
    }

}
