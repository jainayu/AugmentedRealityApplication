package com.example.artest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    Button button, downloadButton;
    ProgressBar progressBar;
    TextView percentage;
    private static final String TAG = "MainActivity";
    DownloadARModelFileTask downloadARModelFileTask;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
        button = findViewById(R.id.button);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, com.example.artest.ViroActivity.class));
//            }
//        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //downloadModelFile();
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "UPES_MAP.gltf");
                if(file.exists())
                    startActivity(new Intent(MainActivity.this, com.example.artest.ViroActivity.class));
//                    Toast.makeText(getApplicationContext(), "File Already Exists", Toast.LENGTH_LONG).show();
                else {
                    //downloadModelFile();
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setMessage("Download 200mb file to continue");
                    alertDialog.setCancelable(false);

                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            downloadModelFile();
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
    }
    private void downloadModelFile() {

        RetrofitInterface downloadService = createService(RetrofitInterface.class, "https://upesacm.org/upesacmacmwapp/ARModel/");
        Call<ResponseBody> call = downloadService.downloadFileByUrl("UPES_MAP.gltf");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Got the body for the file");

                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

                    downloadARModelFileTask = new DownloadARModelFileTask();
                    downloadARModelFileTask.execute(response.body());

                } else {
                    Log.d(TAG, "Connection failed " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().build())
                .build();
        return retrofit.create(serviceClass);
    }
    private class DownloadARModelFileTask extends AsyncTask<ResponseBody, Long , String> {

        ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressDialog mProgressDialog = new progressDialog();
        int prog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show(getSupportFragmentManager(),"Downloading");
        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call
            saveToDisk(urls[0], "UPES_MAP.gltf");
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            Log.d(TAG, "onProgressUpdate: "+(int)((double)(values[0]/(double)185641883*100)));

            prog = (int)((double)(values[0]/(double)185641883*100));

            if(prog == 100){
                mProgressDialog.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("Do you wish to continue ?");
                alert.setCancelable(false);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(MainActivity.this, com.example.artest.ViroActivity.class));
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
            alert.show();
            }


            if(prog>0){
                Log.d(TAG, "onProgressUpdate: greater" );
                //Progress Update
            }

            if(prog == -1){
                Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
                boolean delete = file.delete();
                if (delete)
                    Toast.makeText(getApplicationContext(), "File also deleted", Toast.LENGTH_SHORT).show();
            }
        }

//        protected void onProgressUpdate(Pair<Integer, Long>... progress) {
//
//            Log.d("API123", progress[0].second + " ");
//
//            if (progress[0].first == 100)
//                Toast.makeText(getApplicationContext(), "File downloaded successfully", Toast.LENGTH_SHORT).show();
//
//
//            if (progress[0].second > 0 ) {
//                Log.d(TAG, "onProgressUpdate: inn");
//                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
////                progressBar.setProgress(currentProgress);
////                percentage.setText("Progress " + currentProgress + "%");
//
//            }
//
//            if (progress[0].first == -1) {
//                Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
//                boolean delete = file.delete();
//                if (delete)
//                    Toast.makeText(getApplicationContext(), "File also deleted", Toast.LENGTH_SHORT).show();
//            }
//
//        }

        public void doProgress(Long progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
    private void saveToDisk(ResponseBody body, String filename) {
        try {

            File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            //File destinationFile = new File("file:///android_asset/", filename);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                long progress = 0;
                long fileSize = body.contentLength();
                Log.d(TAG, "File Size=" + fileSize);
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    //Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadARModelFileTask.doProgress(progress);
                    Log.d(TAG, "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                }

                outputStream.flush();

                Log.d(TAG, destinationFile.getParent());
                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadARModelFileTask.doProgress(progress);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
                downloadARModelFileTask.doProgress((long)-1);
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }


}
