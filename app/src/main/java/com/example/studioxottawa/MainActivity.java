package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button aboutusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the reference of Button's
        aboutusBtn = (Button) findViewById(R.id.aboutusButton);  //xiaoxi

// perform setOnClickListener event on aboutus btn //xiaoxi  {
        aboutusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load First Fragment
//                loadFragment(new FragmentAboutus());
                loadAboutusActivity();
            }


        });  //xiaoxi }
    }

    //xiaoxi {
    private void loadAboutusActivity() {

        Intent myIntent = new Intent(MainActivity.this, AboutusActivity.class);
        MainActivity.this.startActivity(myIntent);

    }   //xiaoxi }

    }
