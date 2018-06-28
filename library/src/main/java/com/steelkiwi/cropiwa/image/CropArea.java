package com.steelkiwi.cropiwa.image;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

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

        Bitmap immutableCropped = Bitmap.createBitmap(bitmap,
                findRealCoordinate(bitmap.getWidth(), cropRect.left, imageRect.width()),
                findRealCoordinate(bitmap.getHeight(), cropRect.top, imageRect.height()),
                findRealCoordinate(bitmap.getWidth(), cropRect.width(), imageRect.width()),
                findRealCoordinate(bitmap.getHeight(), cropRect.height(), imageRect.height()));
        return immutableCropped.copy(immutableCropped.getConfig(), true);
    }

    public Bitmap rotateBitmap(Bitmap source, Matrix matrix)
    {
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private int findRealCoordinate(int imageRealSize, int cropCoordinate, float cropImageSize) {
        return Math.round((imageRealSize * cropCoordinate) / cropImageSize);
    }

}