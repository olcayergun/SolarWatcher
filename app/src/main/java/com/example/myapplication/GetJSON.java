package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetJSON extends AsyncTask<String, Void, String> {
    private ProgressDialog progressDialog;

    public GetJSON(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    private AsyncTaskListener listener;
    private static String TAG = "Adaer";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (listener != null) {
            listener.onAsyncTaskFinished(s);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Log.d(TAG, "url : ".concat(strings[0]));
            URL url = new URL(strings[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json).append("\n");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            Log.e(TAG, "Notification - doInBackground...", e);
            return "No_Data";
        }
    }

    public void setListener(AsyncTaskListener listener) {
        this.listener = listener;
    }

    public interface AsyncTaskListener {
        void onAsyncTaskFinished(String string);
    }
}