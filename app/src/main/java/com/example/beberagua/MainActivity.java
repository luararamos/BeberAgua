package com.example.beberagua;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_NOTIFY = "key_notify";
    private static final String KEY_INTERVAL = "key_interval";
    private static final String KEY_HOUR = "key_hour";
    private static final String KEY_MINUTE = "key_minute";

    private Button btnNotify;
    private EditText editMinutes;
    private TimePicker timePicker;

    private SharedPreferences storage;

    private boolean activated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = getSharedPreferences("db", Context.MODE_PRIVATE);

        btnNotify = findViewById(R.id.btn_notify);
        editMinutes = findViewById(R.id.edt_txt_number_interval);
        timePicker = findViewById(R.id.time_picker);

        activated = storage.getBoolean(KEY_NOTIFY, false);

        setupUI(activated, storage);

        timePicker.setIs24HourView(true);

        btnNotify.setOnClickListener(notifyListener);

    }

    private void alert(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    private boolean intervalIsValid() {
        String sInterval = editMinutes.getText().toString();
        if (sInterval.isEmpty()) {
            alert(R.string.error_msg);
            return false;
        }
        if (sInterval.equals("0")) {
            alert(R.string.zero_value);
            return false;
        }
        return true;
    }

    private void setupUI(boolean activated, SharedPreferences storage) {
        if (activated) {
            btnNotify.setText(R.string.pause);
            btnNotify.setBackgroundResource(R.drawable.bg_button_backgroud);
            editMinutes.setText(String.valueOf(storage.getInt(KEY_INTERVAL, 0)));
            timePicker.setCurrentHour(storage.getInt(KEY_HOUR, timePicker.getCurrentHour()));
            timePicker.setCurrentMinute(storage.getInt(KEY_MINUTE, timePicker.getCurrentMinute()));

        } else {
            btnNotify.setText(R.string.notify);
            btnNotify.setBackgroundResource(R.drawable.bg_button_backgroud_accent);
        }
    }

    private void updateStorage(boolean added, int interval, int hour, int minute) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putBoolean(KEY_NOTIFY, activated);

        if (added) {
            editor.putInt(KEY_INTERVAL, interval);
            editor.putInt(KEY_HOUR, hour);
            editor.putInt(KEY_MINUTE, minute);

        } else {
            editor.remove(KEY_INTERVAL);
            editor.remove(KEY_HOUR);
            editor.remove(KEY_MINUTE);
        }
        editor.apply();
    }

    private void setupNotification(boolean added, int interval, int hour, int minute) {
        Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (added) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION_ID, 1);
            notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION, "Hora de beber Água");

            PendingIntent broascast = PendingIntent.getBroadcast(MainActivity.this, 0,
                    notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval * 60 * 1000, broascast);
        } else {
            PendingIntent broascast = PendingIntent.getBroadcast(MainActivity.this, 0,
                    notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.cancel(broascast);
        }

    }

    private View.OnClickListener notifyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!activated) {
                if (!intervalIsValid()) return;

                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                int interval = Integer.parseInt(editMinutes.getText().toString());

                updateStorage(true, interval, hour, minute);
                setupUI(true, storage);

                setupNotification(true, interval, hour, minute);
                alert(R.string.notified);

                activated = true;

            } else {
                updateStorage(false, 0, 0, 0);
                setupUI(false, storage);
                setupNotification(false, 0,0,0);
                alert(R.string.notified_pause);

                activated = false;
            }

        }
    };

}