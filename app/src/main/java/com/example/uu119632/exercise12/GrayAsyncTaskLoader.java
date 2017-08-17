package com.example.uu119632.exercise12;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.AsyncTaskLoader;

public class GrayAsyncTaskLoader extends AsyncTaskLoader<Bitmap> {

    Bitmap bitmap;

    public GrayAsyncTaskLoader(Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
    }

    @Override
    public Bitmap loadInBackground() {
        Bitmap out = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = out.getWidth();
        int height = out.getHeight();
        int i, j;
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                int pixelColor = out.getPixel(i, j);
                // モノクロ化
                int y = (int) (0.299 * Color.red(pixelColor) +
                        0.587 * Color.green(pixelColor) +
                        0.114 * Color.blue(pixelColor));
                out.setPixel(i, j, Color.rgb(y, y, y));
            }
        }

        return out;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
