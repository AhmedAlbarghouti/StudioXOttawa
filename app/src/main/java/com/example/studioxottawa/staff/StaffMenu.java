package com.example.studioxottawa.staff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.studioxottawa.R;

public class StaffMenu extends AppCompatActivity {

    Button addServiceButton, addEventButton, reportButton, addNews, addVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        addServiceButton = findViewById(R.id.addServiceButton);
        addEventButton = findViewById(R.id.addEventButton);
        addNews = findViewById(R.id.addNews);
        addVideo = findViewById(R.id.videoButton);
        reportButton = findViewById(R.id.reportButton);

        addServiceButton.setOnClickListener(click->{
            Intent addServiceActivity = new Intent(this, AddService.class);
            Log.i("EEEEEEEEEEEEEEEEEEEEE",addServiceActivity.toString());
            startActivity(addServiceActivity);
        });

        addEventButton.setOnClickListener(click->{
            Intent addEventActivity = new Intent(this, AddEvent.class);
            startActivity(addEventActivity);
        });

        addNews.setOnClickListener(click->{
            Intent addNewsActivity = new Intent(this, AddNews.class);
            startActivity(addNewsActivity);
        });

        addVideo.setOnClickListener(click->{
            Intent videoAddActivity = new Intent(this, AddVideos.class);
            startActivity(videoAddActivity);
        });

        reportButton.setOnClickListener(click->{
            Intent reportActivity = new Intent(this, Report.class);
            startActivity(reportActivity);
        });


    }
}