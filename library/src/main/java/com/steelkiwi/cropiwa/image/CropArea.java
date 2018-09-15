package com.steelkiwi.cropiwa.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * @author yarolegovich
 * 25.02.2017.
 */
public class CropArea {

    public static CropArea create(RectF coordinateSystem, RectF imageRect, RectF cropRect, Matrix matrix) {
        return new CropArea(
                moveRectToCoordinateSystem(coordinateSystem, imageRect),
                moveRectToCoordinateSystem(coordinateSystem, cropRect), matrix);
    }

    private static Rect moveRectToCoordinateSystem(RectF system, RectF rect) {
        float originX = system.left, originY = system.top;
        return new Rect(
                Math.round(rect.left - originX), Math.round(rect.top - originY),
                Math.round(rect.right - originX), Math.round(rect.bottom - originY));
    }

    private final Rect imageRect;
    private final Rect cropRect;

    private Matrix croppedMatrix;

    public CropArea(Rect imageRect, Rect cropRect, Matrix matrix) {
        this.imageRect = imageRect;
        this.cropRect = cropRect;
        this.croppedMatrix = matrix;
    }

    public Bitmap applyCropTo(Bitmap bitmap) {
        bitmap = rotateBitmap(bitmap, croppedMatrix);

        int x = findRealCoordinate(bitmap.getWidth(), cropRect.left, imageRect.width());
        int y = findRealCoordinate(bitmap.getHeight(), cropRect.top, imageRect.height());
        int width = findRealCoordinate(bitmap.getWidth(), cropRect.width(), imageRect.width());
        int height = findRealCoordinate(bitmap.getHeight(), cropRect.height(), imageRect.height());

        int sx = 0;
        int sy = 0;

        if (x < 0) {
            sx = -x;

            width += x;
            x = 0;
        }
        if (y < 0) {
            sy = -y;

            height += y;
            y = 0;
        }
        if(x + width > bitmap.getWidth()) {
            width = bitmap.getWidth() - x;
        }
        if(y + height > bitmap.getHeight()) {
            height = bitmap.getHeight() - y;
        }

        Bitmap immutableCropped = Bitmap.createBitmap(bitmap,
                x,
                y,
                width,
                height);

        return immutableCropped;
    }

    public Bitmap combineImages(Bitmap c, Bitmap s, int sx, int sy) {
        Bitmap cs = null;

        int width, height = 0;

        /*if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }*/

        width = c.getWidth();
        height = c.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, sx, sy, null);

        return cs;
    }

    public Bitmap rotateBitmap(Bitmap source, Matrix matrix)
    {
        RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
        matrix.mapRect(rect);

        float ratio = rect.height()/rect.width();
        float scale = source.getWidth()/rect.width();
        matrix.postTranslate(-rect.left, -rect.top);
        matrix.postScale(scale, scale, 0, 0);

        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), (int) (source.getWidth()*ratio), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, matrix, null);
        source.recycle();
        return bitmap;
    }

    private int findRealCoordinate(int imageRealSize, int cropCoordinate, float cropImageSize) {
        return Math.round((imageRealSize * cropCoordinate) / cropImageSize);
    }

}
