package com.example.studioxottawa.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.studioxottawa.R;
import com.example.studioxottawa.news.NewsFragment;
import com.example.studioxottawa.schedule.ScheduleFragment;

public class WelcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
//        ScheduleFragment sobj = new ScheduleFragment();
        NewsFragment nobj = new NewsFragment();
//        sobj.loadEvents();
        nobj.loadNews();
        int SPLASH_TIME_OUT = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(WelcomePage.this,LoginActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);


    }

}