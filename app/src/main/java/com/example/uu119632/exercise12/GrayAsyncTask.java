package com.example.uu119632.exercise12;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ImageView;

public class GrayAsyncTask extends AsyncTask<Bitmap, Integer, Bitmap> implements DialogInterface.OnCancelListener {

    private ImageView imageView;
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncTaskListener listener;

    interface AsyncTaskListener {
        void OnTaskFinished();

        void OnTaskCancelled();
    }

    public GrayAsyncTask(Context context, ImageView imageView, AsyncTaskListener listener) {
        this.context = context;
        this.imageView = imageView;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(this);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

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
                // モノクロ化
                int y = (int) (0.299 * Color.red(pixelColor) +
                        0.587 * Color.green(pixelColor) +
                        0.114 * Color.blue(pixelColor));
                out.setPixel(i, j, Color.rgb(y, y, y));
            }
            publishProgress(i + j * height);
        }

        return out;
    }

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

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        this.cancel(true);
        if (listener != null) {
            listener.OnTaskCancelled();
        }
    }
}
