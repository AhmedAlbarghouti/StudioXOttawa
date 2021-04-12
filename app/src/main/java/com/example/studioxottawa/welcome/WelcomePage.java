package com.example.studioxottawa.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.studioxottawa.R;
import com.example.studioxottawa.news.NewsFragment;
import com.example.studioxottawa.schedule.ScheduleFragment;
/**
 * @Author Ahmed Albarghouti
 * @Date April 2021
 * @Purpose Displays company logo & runs tasks that are required before app starts
 */
public class WelcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        ScheduleFragment sobj = new ScheduleFragment();
        sobj.loadEvents();
        NewsFragment.loadNews();
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