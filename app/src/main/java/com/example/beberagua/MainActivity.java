package com.example.beberagua;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNotify = findViewById(R.id.btn_notify);
        editMinutes = findViewById(R.id.edt_txt_number_interval);
        timePicker = findViewById(R.id.time_picker);

        timePicker.setIs24HourView(true);


    }

    public void notifyClick(View view) {
        String sInterval = editMinutes.getText().toString();

        if (sInterval.isEmpty()){
            Toast.makeText(this, R.string.error_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        hour = timePicker.getCurrentHour();
        minute = timePicker.getCurrentMinute();
        interval = Integer.parseInt(sInterval);

        btnNotify.setText(R.string.pause);
        int color = ContextCompat.getColor(this, android.R.color.black);
        btnNotify.setBackgroundColor(color);
        Log.d("Teste", "hora: " + hour + " minuto: " + minute + " intervalo: " + interval);

    }

}