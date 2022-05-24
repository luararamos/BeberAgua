package com.example.beberagua;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
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
        activated = preferences.getBoolean("activated", false);

        if (activated) {
            btnNotify.setText(R.string.pause);
            ColorStateList color = ContextCompat.getColorStateList(this, android.R.color.black);
            btnNotify.setBackgroundTintList(color);
            activated = true;

            preferences.getInt("interval",0);
            preferences.getInt("hour",timePicker.getCurrentHour());
            preferences.getInt("minute",timePicker.getCurrentMinute());

            editMinutes.setText(String.valueOf(interval));
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);

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
            editor.putBoolean("activated", activated);
            editor.putInt("interval", interval);
            editor.putInt("hour", hour);
            editor.putInt("minute", minute);
            editor.apply();

        } else {
            btnNotify.setText(R.string.notify);
            ColorStateList color = ContextCompat.getColorStateList(this, R.color.colorAccent);
            btnNotify.setBackgroundTintList(color);
            activated = false;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("activated", activated);
            editor.remove("interval");
            editor.remove("hour");
            editor.remove("minute");
            editor.apply();

        }

        Log.d("Teste", "hora: " + hour + " minuto: " + minute + " intervalo: " + interval);

    }

}