package com.example.uu119632.exercise12;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * グレースケール化とプログレスバーを管理するクラス
 *
 * @author :ryo.yamada
 * @since :1.0 :2017/08/18
 */
class GrayAsyncTask extends AsyncTask<Bitmap, Integer, Bitmap> implements DialogInterface.OnCancelListener {

    private static final double GRAY_RED = 0.299;
    private static final double GRAY_GREEN = 0.587;
    private static final double GRAY_BLUE = 0.114;

    private final ImageView imageView;
    private ProgressDialog progressDialog;
    private final Context context;
    private final AsyncTaskListener listener;

    /**
     * CallbackInterface
     */
    interface AsyncTaskListener {
        void OnTaskFinished();

        void OnTaskCancelled();
    }

    /**
     * コンストラクタ
     *
     * @param context   context
     * @param imageView imageView
     * @param listener  listener
     */
    GrayAsyncTask(Context context, ImageView imageView, AsyncTaskListener listener) {
        this.context = context;
        this.imageView = imageView;
        this.listener = listener;
    }

    /**
     * 非同期処理を行う前に行うメソッド
     * プログレスバーの設定
     */
    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(this);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    /**
     * ワーカースレッドで行う処理
     *
     * @param bitmap bitmap
     * @return グレースケール化した画像
     */
    @Override
    protected Bitmap doInBackground(Bitmap... bitmap) {
        Bitmap out = bitmap[0].copy(Bitmap.Config.ARGB_8888, true);
        int width = out.getWidth();
        int height = out.getHeight();
        progressDialog.setMax(width * height);
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
            publishProgress(i + j * height);
        }
        return out;
    }

    /**
     * ワーカースレッドで行う処理後に行う処理
     *
     * @param result グレースケール化した画像
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (progressDialog != null && progressDialog.isShowing() && listener != null) {
            progressDialog.dismiss();
            MainActivity.postBtn.setEnabled(false);
            MainActivity.asyncTaskBtn.setEnabled(false);
            imageView.setImageBitmap(result);
            listener.OnTaskFinished();
        }
    }

    /**
     * プログレスバーの更新を行う。
     *
     * @param values value
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
    }

    /**
     * AsyncTaskのキャンセル処理
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * ダイアログのキャンセル処理
     *
     * @param dialogInterface dialogInterface
     */
    @Override
    public void onCancel(DialogInterface dialogInterface) {
        this.cancel(true);
        if (listener != null) {
            listener.OnTaskCancelled();
        }
    }
}
