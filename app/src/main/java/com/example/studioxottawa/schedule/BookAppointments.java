package com.example.studioxottawa.schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studioxottawa.R;

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

        //Universally Unique Event ID
        String Uid = passedEvent.getString("EVENT_UID");

        bookbtn.setOnClickListener(click-> {
            //Change second argument to goto class
            Intent bookingIntent = new Intent(BookAppointments.this, BookAppointments.class);
            bookingIntent.putExtra("UID",Uid);
            Toast.makeText(getApplicationContext(),"Successfully Booked!",Toast.LENGTH_SHORT).show();
        });

    }
}