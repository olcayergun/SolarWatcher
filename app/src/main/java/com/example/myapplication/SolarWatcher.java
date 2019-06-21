package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SolarWatcher extends AppCompatActivity {
    private static String TAG = "Adaer";
    AlarmManager alarmMgr;
    PendingIntent pendingIntent;
    Button bBasla;
    Button bBitir;
    Button bYenile;
    private TableLayout mTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bBasla = findViewById(R.id.bBasla);
        bBasla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlarmManager();
                bBasla.setEnabled(false);
                bBitir.setEnabled(true);
            }
        });

        bBitir = findViewById(R.id.bBitir);
        bBitir.setEnabled(false);
        bBitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarmManager();
                bBasla.setEnabled(true);
                bBitir.setEnabled(false);
            }
        });

        bYenile = findViewById(R.id.bYenile);
        bYenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataForTable();
            }
        });

        mTableLayout = findViewById(R.id.tableInvoices);
        mTableLayout.setStretchAllColumns(true);

        getDataForTable();
    }

    private void getDataForTable() {
        ProgressDialog progDailog = new ProgressDialog(SolarWatcher.this);
        progDailog.setMessage("Yükleniyor...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();

        String s = "http://www.olcayergun.com/rest/select.php?test,*,NONE,zaman,DESC,0,100";
        GetJSON asyncTask = new GetJSON(progDailog);
        asyncTask.setListener(new GetJSON.AsyncTaskListener() {
            @Override
            public void onAsyncTaskFinished(String s) {
                Log.d(TAG, "onExampleAsyncTaskFinished " + s);
                ChargerData[] chargerData;

                if ("No_Data".equals(s)) {
                    chargerData = new ChargerData[1];
                    chargerData[0] = new ChargerData(-1, "No data");
                } else {
                    try {
                        chargerData = new ChargerData[100];
                        JSONArray jsonObj = new JSONArray(s);
                        for (int i = 0; i < jsonObj.length(); i++) {
                            JSONObject jo = jsonObj.getJSONObject(i);
                            chargerData[i] = new ChargerData(jo.getInt("id"),
                                    jo.getString("zaman"),
                                    jo.getString("SarjAkim"),
                                    jo.getString("AkuGerilim"),
                                    jo.getString("SolarGerilim"),
                                    jo.getString("SebekeGerilim"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "onReceive - json...", e);
                        chargerData = new ChargerData[1];
                        chargerData[0] = new ChargerData(-1, e.getLocalizedMessage());
                    }
                }
                startLoadData(chargerData);
            }
        });
        asyncTask.execute(s);
    }

    public void startLoadData(ChargerData[] data) {
        Log.d(TAG, "Starting data load...");
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;

        int textSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        int smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);

        int rows = data.length;
        getSupportActionBar().setTitle(String.format("Değerler (%d)", rows));

        mTableLayout.removeAllViews();

        // -1 means heading row
        TextView textSpacer = null;
        for (int i = -1; i < rows; i++) {
            ChargerData row = null;
            if (i > -1)
                row = data[i];
            else {
                textSpacer = new TextView(this);
                textSpacer.setText("");

            }
// Zaman
            final TextView tv2 = new TextView(this);
            if (i == -1) {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText("Zaman");
                tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv2.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                tv2.setBackgroundColor(Color.parseColor("#ffffff"));
                tv2.setTextColor(Color.parseColor("#000000"));
                tv2.setText(row.zaman);
                tv2.setGravity(Gravity.LEFT);
            }

// Şarj Akımı
            final LinearLayout laySarjAkimi = new LinearLayout(this);
            laySarjAkimi.setOrientation(LinearLayout.VERTICAL);
            laySarjAkimi.setPadding(0, 10, 0, 10);
            laySarjAkimi.setBackgroundColor(Color.parseColor("#f8f8f8"));

            final TextView tvSarjAkimi = new TextView(this);
            if (i == -1) {
                tvSarjAkimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tvSarjAkimi.setPadding(5, 5, 0, 5);
                tvSarjAkimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tvSarjAkimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tvSarjAkimi.setPadding(5, 0, 0, 5);
                tvSarjAkimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            if (i == -1) {
                tvSarjAkimi.setText("Şarj\nAkımı (A)");
                tvSarjAkimi.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tvSarjAkimi.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                tvSarjAkimi.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tvSarjAkimi.setTextColor(Color.parseColor("#000000"));
                tvSarjAkimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tvSarjAkimi.setText(row.sarjAkimi);
                tvSarjAkimi.setGravity(Gravity.RIGHT);
            }
            laySarjAkimi.addView(tvSarjAkimi);

// Solar Gerilim
            final LinearLayout laySolarGerilimi = new LinearLayout(this);
            laySolarGerilimi.setOrientation(LinearLayout.VERTICAL);
            laySolarGerilimi.setGravity(Gravity.RIGHT);
            laySolarGerilimi.setPadding(0, 10, 0, 10);
            laySolarGerilimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));


            final TextView tvSolarGerilimi = new TextView(this);
            if (i == -1) {
                tvSolarGerilimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tvSolarGerilimi.setPadding(5, 5, 1, 5);
                laySolarGerilimi.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                tvSolarGerilimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tvSolarGerilimi.setPadding(5, 0, 1, 5);
                laySolarGerilimi.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            if (i == -1) {
                tvSolarGerilimi.setText("Solar\nGerilimi (V)");
                tvSolarGerilimi.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tvSolarGerilimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tvSolarGerilimi.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                tvSolarGerilimi.setBackgroundColor(Color.parseColor("#ffffff"));
                tvSolarGerilimi.setTextColor(Color.parseColor("#000000"));
                tvSolarGerilimi.setText(row.solarGerilimi);
                tvSolarGerilimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tvSolarGerilimi.setGravity(Gravity.RIGHT);
            }

            laySolarGerilimi.addView(tvSolarGerilimi);

// Akü Gerilim
            final LinearLayout layAmounts_ = new LinearLayout(this);
            layAmounts_.setOrientation(LinearLayout.VERTICAL);
            layAmounts_.setGravity(Gravity.RIGHT);
            layAmounts_.setPadding(0, 10, 0, 10);
            layAmounts_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            final TextView tvAkuGerilimi = new TextView(this);
            if (i == -1) {
                tvAkuGerilimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tvAkuGerilimi.setPadding(5, 5, 1, 5);
                layAmounts_.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                tvAkuGerilimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tvAkuGerilimi.setPadding(5, 0, 1, 5);
                layAmounts_.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            if (i == -1) {
                tvAkuGerilimi.setText("Akü\nGerilimi (V)");
                tvAkuGerilimi.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tvAkuGerilimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tvAkuGerilimi.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                tvAkuGerilimi.setBackgroundColor(Color.parseColor("#ffffff"));
                tvAkuGerilimi.setTextColor(Color.parseColor("#000000"));
                tvAkuGerilimi.setText(row.akuGerilimi);
                tvAkuGerilimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tvAkuGerilimi.setGravity(Gravity.RIGHT);
            }

            layAmounts_.addView(tvAkuGerilimi);

//Şebeke Gerilim
            final LinearLayout laySebekeGerilimi = new LinearLayout(this);
            laySebekeGerilimi.setOrientation(LinearLayout.VERTICAL);
            laySebekeGerilimi.setGravity(Gravity.RIGHT);
            laySebekeGerilimi.setPadding(0, 10, 0, 10);
            laySebekeGerilimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            final TextView tvSebekeGerilimi = new TextView(this);
            if (i == -1) {
                tvSebekeGerilimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tvSebekeGerilimi.setPadding(5, 5, 1, 5);
                laySebekeGerilimi.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                tvSebekeGerilimi.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tvSebekeGerilimi.setPadding(5, 0, 1, 5);
                laySebekeGerilimi.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            if (i == -1) {
                tvSebekeGerilimi.setText("Şebeke\nGerilimi (V)");
                tvSebekeGerilimi.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tvSebekeGerilimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tvSebekeGerilimi.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                tvSebekeGerilimi.setBackgroundColor(Color.parseColor("#ffffff"));
                tvSebekeGerilimi.setTextColor(Color.parseColor("#000000"));
                tvSebekeGerilimi.setText(row.sebekeGerilimi);
                tvSebekeGerilimi.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tvSebekeGerilimi.setGravity(Gravity.RIGHT);
            }

            laySebekeGerilimi.addView(tvSebekeGerilimi);

            // add table row
            final TableRow tr = new TableRow(this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0, 0, 0, 0);
            tr.setLayoutParams(trParams);


            //tr.addView(tv);
            tr.addView(tv2);
            tr.addView(laySarjAkimi);
            tr.addView(laySolarGerilimi);
            tr.addView(layAmounts_);
            tr.addView(laySebekeGerilimi);

            if (i > -1) {
                tr.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TableRow tr = (TableRow) v;
                        //do whatever action is needed
                        Log.d(TAG, tr.toString().concat(" is clicked"));
                    }
                });
            }
            mTableLayout.addView(tr, trParams);

            if (i > -1) {
                // add separator row
                final TableRow trSep = new TableRow(this);
                TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(this);
                TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 4;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);

                trSep.addView(tvSep);
                mTableLayout.addView(trSep, trParamsSep);
            }
        }
    }

    public void startAlarmManager() {
        Intent dialogIntent = new Intent(getBaseContext(), AlarmReceiver.class);

        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this, 0, dialogIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 100, pendingIntent);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, pendingIntent);
    }

    public void stopAlarmManager() {
        if (alarmMgr != null)
            alarmMgr.cancel(pendingIntent);
    }
}