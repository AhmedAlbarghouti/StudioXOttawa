package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button schedulebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schedulebtn = findViewById(R.id.scheduleButton);
        schedulebtn.setOnClickListener(click -> {

            Intent schedule = new Intent(this,Schedule.class);
            startActivity(schedule);
        });
    }
}