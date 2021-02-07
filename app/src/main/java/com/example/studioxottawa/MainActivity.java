package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.studioxottawa.news.NewsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button loginBtn2 = (Button)findViewById(R.id.News_Button);
        loginBtn2.setOnClickListener(click -> {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.News_ButtonClick), Toast.LENGTH_LONG ).show();
            Intent goToProfile = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(goToProfile);
        });
    }
}