package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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

        Button generateReport=findViewById(R.id.generateReport);

        generateReport.setOnClickListener(btn->{


            Intent nextActivity = new Intent(MainActivity.this, Report.class);
            startActivity(nextActivity); //make the transitio

        });

    }
}