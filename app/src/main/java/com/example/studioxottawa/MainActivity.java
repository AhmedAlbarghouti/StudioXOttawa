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
            Bundle dataToPass = new Bundle();
//            dataToPass.putString(DATE_OF_MATCH, listToDisplay.get(position).getDate());


            ReportFragment dFragment = new ReportFragment(); //add a DetailFragment
            dFragment.setArguments(dataToPass); //pass it a bundle for information

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment. Calls onCreate() in DetailFragment

            Intent nextActivity = new Intent(MainActivity.this, Report_empty.class);
            nextActivity.putExtras(dataToPass); //send data to next activity
            startActivityForResult(nextActivity, 1); //make the transitio

        });

    }
}