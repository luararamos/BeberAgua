package com.example.beberagua;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_NOTIFY = "key_notify";
    private static final String KEY_INTERVAL = "key_interval";
    private static final String KEY_HOUR = "key_hour";
    private static final String KEY_MINUTE = "key_minute";

    private Button btnNotify;
    private EditText editMinutes;
    private TimePicker timePicker;

    private int hour;
    private int minute;
    private int interval;

    private boolean activated = false;

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNotify = findViewById(R.id.btn_notify);
        editMinutes = findViewById(R.id.edt_txt_number_interval);
        timePicker = findViewById(R.id.time_picker);

        timePicker.setIs24HourView(true);

        preferences = getSharedPreferences("db", Context.MODE_PRIVATE);
        activated = preferences.getBoolean(KEY_NOTIFY, false);

        if (activated) {
            btnNotify.setText(R.string.pause);
            ColorStateList color = ContextCompat.getColorStateList(this, android.R.color.black);
            btnNotify.setBackgroundTintList(color);
            activated = true;

            preferences.getInt(KEY_INTERVAL,0);
            preferences.getInt(KEY_HOUR,timePicker.getCurrentHour());
            preferences.getInt(KEY_MINUTE,timePicker.getCurrentMinute());

            editMinutes.setText(String.valueOf(interval));
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);

        } else {
            btnNotify.setText(R.string.notify);
            btnNotify.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                    R.color.colorAccent));
        }


    }

    public void notifyClick(View view) {
        String sInterval = editMinutes.getText().toString();

        if (sInterval.isEmpty()) {
            Toast.makeText(this, R.string.error_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        hour = timePicker.getCurrentHour();
        minute = timePicker.getCurrentMinute();
        interval = Integer.parseInt(sInterval);

        if (!activated) {
            btnNotify.setText(R.string.pause);
            ColorStateList color = ContextCompat.getColorStateList(this, android.R.color.black);
            btnNotify.setBackgroundTintList(color);
            activated = true;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_NOTIFY, activated);
            editor.putInt(KEY_INTERVAL, interval);
            editor.putInt(KEY_HOUR, hour);
            editor.putInt(KEY_MINUTE, minute);
            editor.apply();

            Intent notificationIntent =  new Intent(MainActivity.this, NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION_ID, 1 );
            notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION, "Hora de beber Água");

            PendingIntent broascast = PendingIntent.getBroadcast(MainActivity.this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            long futureInMillis = SystemClock.elapsedRealtime() + (interval *1000);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, broascast);

            activated = true;

        } else {
            btnNotify.setText(R.string.notify);
            ColorStateList color = ContextCompat.getColorStateList(this, R.color.colorAccent);
            btnNotify.setBackgroundTintList(color);
            activated = false;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_NOTIFY, activated);
            editor.remove(KEY_INTERVAL);
            editor.remove(KEY_HOUR);
            editor.remove(KEY_MINUTE);
            editor.apply();

        }

        Log.d("Teste", "hora: " + hour + " minuto: " + minute + " intervalo: " + interval);

    }

}