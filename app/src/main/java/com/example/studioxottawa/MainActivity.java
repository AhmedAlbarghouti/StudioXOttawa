package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.studioxottawa.news.NewsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button contactbtn=findViewById(R.id.contactButton);
        contactbtn.setOnClickListener(btn->{
            Intent contact=new Intent(this,contact.class);
            startActivity(contact);
        });

        Button loginBtn2 = (Button)findViewById(R.id.newsButton);
        loginBtn2.setOnClickListener(click -> {
            Intent goToProfile = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(goToProfile);
        });

        Button loginBtn3 = (Button)findViewById(R.id.aboutusButton);
        loginBtn3.setOnClickListener(click -> {
            Intent goToProfile = new Intent(MainActivity.this, AboutusActivity.class);
            startActivity(goToProfile);
        });

    }
}