package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BookAppointments extends AppCompatActivity {

    TextView eventName;
    TextView eventDate;
    TextView eventTime;
    TextView eventStaff;
    Button bookbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointments);

        eventName = findViewById(R.id.bookEventName);
        eventDate = findViewById(R.id.bookEventDate);
        eventTime = findViewById(R.id.bookEventTime);
        eventStaff = findViewById(R.id.bookEventStaff);
        bookbtn = findViewById(R.id.bookEventButton);

        Intent in = getIntent();
        Bundle passedEvent = in.getExtras();
        eventName.setText(passedEvent.getString("EVENT_NAME"));
        eventDate.setText(passedEvent.getString("EVENT_DATE"));
        eventTime.setText(passedEvent.getString("EVENT_TIME"));
        eventStaff.setText(passedEvent.getString("EVENT_STAFF"));

        bookbtn.setOnClickListener(click-> {
            Toast.makeText(getApplicationContext(),"Successfully Booked!",Toast.LENGTH_SHORT).show();
        });

    }
}