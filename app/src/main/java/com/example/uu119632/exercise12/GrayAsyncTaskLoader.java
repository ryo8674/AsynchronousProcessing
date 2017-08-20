package com.example.uu119632.exercise12;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.AsyncTaskLoader;

/**
 * AsyncTaskLoaderを継承したクラス
 * 画像のグレースケール化を行う。
 *
 * @author :ryo.yamada
 * @since :1.0 :2017/08/18
 */
class GrayAsyncTaskLoader extends AsyncTaskLoader<Bitmap> {

    private static final double GRAY_RED = 0.299;
    private static final double GRAY_GREEN = 0.587;
    private static final double GRAY_BLUE = 0.114;

    private final
    Bitmap bitmap;

    /**
     * コンストラクタ
     *
     * @param context context
     * @param bitmap  bitmap
     */
    GrayAsyncTaskLoader(Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
    }

    /**
     * ロード処理を行う
     * 画像のグレースケール化を行う。
     *
     * @return グレースケール化した画像
     */
    @Override
    public Bitmap loadInBackground() {
        Bitmap out = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = out.getWidth();
        int height = out.getHeight();
        int i, j;
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                int pixelColor = out.getPixel(i, j);
                // グレースケール化
                int y = (int) (GRAY_RED * Color.red(pixelColor) +
                        GRAY_GREEN * Color.green(pixelColor) +
                        GRAY_BLUE * Color.blue(pixelColor));
                out.setPixel(i, j, Color.rgb(y, y, y));
            }
        }
        return out;
    }

    /**
     * onStartLoading
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
