package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {
    private static String TAG = "Adaer";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Alarm time...");

        String s = "http://www.olcayergun.com/rest/select.php?test,*,NONE,zaman,DESC,0,1";
        GetJSON asyncTask = new GetJSON(null);
        asyncTask.setListener(new GetJSON.AsyncTaskListener() {
            @Override
            public void onExampleAsyncTaskFinished(String s) {
                Log.d(TAG, "onExampleAsyncTaskFinished " + s);
                String sTitle = "";
                String sText;

                if ("No_Data".equals(s)) {
                    sTitle = "No data";
                    sText = "Error";
                } else {
                    try {
                        JSONArray jsonObj = new JSONArray(s);
                        JSONObject jo = jsonObj.getJSONObject(0);
                        sTitle = jo.getString("zaman");
                        sText = "Şarj Akımı : ".concat(jo.getString("SarjAkim")).concat(" A\n");
                        sText += "Solar Gerilimi : ".concat(jo.getString("SolarGerilim")).concat(" V\n");
                        sText += "Akü Gerilimi : ".concat(jo.getString("AkuGerilim")).concat(" V\n");
                        sText += "Şebeke Gerilimi : ".concat(jo.getString("SebekeGerilim")).concat(" V\n");
                    } catch (JSONException e) {
                        Log.e(TAG, "onReceive - json...", e);
                        sText = e.getLocalizedMessage();
                    }
                }
                sendNotification(context, sTitle, sText, 5);
            }
        });
        asyncTask.execute(s);
    }

    private static void sendNotification(Context context, String title, String text, int id) {
        Log.d(TAG, "sendNotification " + id);
        Intent intent = new Intent(context, SolarWatcher.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "default")
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true);

        Objects.requireNonNull(notificationManager).notify(id, notification.build());
    }
}