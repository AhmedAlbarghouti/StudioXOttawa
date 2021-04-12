package com.example.studioxottawa.staff;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studioxottawa.R;

public class ReportDetail extends AppCompatActivity {

    private Intent intent;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        intent = getIntent();
        bundle=intent.getBundleExtra("data");

        Log.i("datatoreceive", bundle.getString("FULL_NAME")+bundle.getString("EVENT"));

        String fullName = bundle.getString("FULL_NAME");
        String email = bundle.getString("EMAIL");
        String phone = bundle.getString("PHONE");
        String event = bundle.getString("EVENT");
        String product = bundle.getString("PRODUCT");

        TextView tViewName = (TextView)findViewById(R.id.fullName1);
        tViewName.setText("Name:\n"+fullName+"\n");
        TextView tViewEmail = findViewById(R.id.email1);
        tViewEmail.setText("Email:\n"+email+"\n");
        TextView tViewPhone = findViewById(R.id.phoneNumber1);
        tViewPhone.setText("Phone:\n"+phone+"\n");
        TextView tViewEvents = findViewById(R.id.event1);
        tViewEvents.setText("Event purchased:\n"+event);
        TextView tViewProduct = findViewById(R.id.product1);
        tViewProduct.setText("Product purchased:\n"+product);
    }
}
