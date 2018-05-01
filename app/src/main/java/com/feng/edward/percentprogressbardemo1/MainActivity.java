package com.feng.edward.percentprogressbardemo1;

import com.feng.edward.percentprogressbardemo1.R;
import com.feng.edward.percentprogressbardemo1.dialog.ProgressDialog;
import com.feng.edward.percentprogressbardemo1.util.IPublishProgress;
import com.feng.edward.percentprogressbardemo1.util.MyAsyncTask;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MyAsyncTask.IIsViewActive {
    private ProgressDialog mProgressDialog;
    private TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressDialog = new ProgressDialog(this);
        mainText = findViewById(R.id.main_text);
        mainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainText.setText(R.string.downloading);
                downLoad();
            }
        });
    }

    private void downLoad() {
        MyAsyncTask.<Void, Integer, Void>newBuilder()
                .setPreExecute(new MyAsyncTask.IPreExecute() {
                    @Override
                    public void onPreExecute() {
                        mProgressDialog.show();
                    }
                })
                .setDoInBackground(new MyAsyncTask.IDoInBackground<Void, Integer, Void>() {
                    @Override
                    public Void doInBackground(IPublishProgress<Integer> publishProgress, Void... voids) {
                        try {
                            for (int i = 0; i <= 100; i++) {
                                Thread.sleep(100);
                                publishProgress.showProgress(i);
                            }
                        } catch (Exception ignore) {
                        }
                        return null;
                    }
                })
                .setProgressUpdate(new MyAsyncTask.IProgressUpdate<Integer>() {
                    @Override
                    public void onProgressUpdate(Integer... values) {
                        mProgressDialog.setPercentProgress(values[0]);
                    }
                })
                .setViewActive(this)
                .setPostExecute(new MyAsyncTask.IPostExecute<Void>() {
                    @Override
                    public void onPostExecute(Void aVoid) {
                        mProgressDialog.dismiss();
                        mainText.setText(R.string.download_finish);
                    }
                })
                .start();
    }

    @Override
    public boolean isViewActive() {
        return !(isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed()));
    }


}
