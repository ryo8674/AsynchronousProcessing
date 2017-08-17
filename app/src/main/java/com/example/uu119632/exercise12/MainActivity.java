package com.example.uu119632.exercise12;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements GrayAsyncTask.AsyncTaskListener {

    public static final String ARG_EXTRA_PARAM = "param_args";

    static Button asyncTaskBtn;
    static Button postBtn;
    static Button asyncTaskLoaderBtn;
    static ImageView imageView;
    private ProgressDialog progressDialog;
    private Button resetBtn;
    private Bitmap bmp;
    private GrayAsyncTask task;

    private final int LOADER_ID = 100;
    private final LoaderManager.LoaderCallbacks<Bitmap> callbacks = new LoaderManager.LoaderCallbacks<Bitmap>() {
        @Override
        public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
            // Loaderが生成された
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(false);
            progressDialog.show();

            return new GrayAsyncTaskLoader(MainActivity.this, bmp);
        }

        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
            // Loaderの破棄
            imageView.setImageBitmap(data);
            imageView.invalidate();
            progressDialog.dismiss();
            asyncTaskBtn.setEnabled(false);
            postBtn.setEnabled(false);
            asyncTaskLoaderBtn.setEnabled(false);
            getSupportLoaderManager().destroyLoader(LOADER_ID);
        }

        @Override
        public void onLoaderReset(Loader<Bitmap> loader) {
            // リセットしたい場合
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asyncTaskBtn = (Button) findViewById(R.id.async_task_btn);
        postBtn = (Button) findViewById(R.id.post_btn);
        asyncTaskLoaderBtn = (Button) findViewById(R.id.async_task_loader_btn);
        resetBtn = (Button) findViewById(R.id.reset_btn);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.android_eat_apple);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);

        task = new GrayAsyncTask(this, imageView, this);

        asyncTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.execute(bmp);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Handler handler = new Handler();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap out = bmp.copy(Bitmap.Config.ARGB_8888, true);
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
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(out);
                                progressDialog.dismiss();
                                asyncTaskBtn.setEnabled(false);
                                postBtn.setEnabled(false);
                            }
                        });

                    }
                }).start();


            }
        });

        asyncTaskLoaderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt(ARG_EXTRA_PARAM, R.drawable.android_eat_apple);
                getSupportLoaderManager().initLoader(LOADER_ID, args, callbacks);

            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncTaskBtn.setEnabled(true);
                postBtn.setEnabled(true);
                asyncTaskLoaderBtn.setEnabled(true);
                imageView.setImageBitmap(bmp);
            }
        });
    }

    @Override
    public void OnTaskFinished() {
        task = new GrayAsyncTask(this, imageView, this);
    }

    @Override
    public void OnTaskCancelled() {
        task = new GrayAsyncTask(this, imageView, this);
    }
}
