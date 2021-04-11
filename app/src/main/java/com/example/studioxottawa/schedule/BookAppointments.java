package com.example.studioxottawa.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studioxottawa.Checkout.Cart;
import com.example.studioxottawa.Checkout.CheckoutActivityJava;
import com.example.studioxottawa.R;
import com.example.studioxottawa.services.Product;
import com.example.studioxottawa.services.ServicesActivity;
import com.example.studioxottawa.welcome.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @Author: Ahmed Albarghouti
 * @Date: Feb 2021
 * @Purpose: Displaying clicked event's details and offering to book it now
 */
public class BookAppointments extends AppCompatActivity {

    /*
    Needed TextViews & Button
     */
    TextView eventName;
    TextView eventDate;
    TextView eventTime;
    TextView eventStaff;
    Button bookbtn;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointments);

        /**
         * Finding View by IDS for elements
         */
        eventName = findViewById(R.id.bookEventName);
        eventDate = findViewById(R.id.bookEventDate);
        eventTime = findViewById(R.id.bookEventTime);
        eventStaff = findViewById(R.id.bookEventStaff);
        bookbtn = findViewById(R.id.bookEventButton);

        /**
         * Getting Bundle containing event clicked information
         */
        Intent in = getIntent();
        Bundle passedEvent = in.getExtras();
        eventName.setText(passedEvent.getString("EVENT_NAME"));
        eventDate.setText(passedEvent.getString("EVENT_DATE"));
        eventTime.setText(passedEvent.getString("EVENT_TIME"));
        eventStaff.setText(passedEvent.getString("EVENT_STAFF"));
        String itemName= eventName+" "+eventDate+" "+eventTime+" with "+eventStaff;
        //Universally Unique Event ID
        String Uid = passedEvent.getString("EVENT_UID");




        bookbtn.setOnClickListener(click-> {
            /**
             * If book is clicked, new intent will redirect user to checkout to pay for the selected event
             */
            Intent bookingIntent = new Intent(this, MainActivity.class);
            bookingIntent.putExtra("UID",Uid);
            bookingIntent.putExtra("isService",true);
            startActivity(bookingIntent);

        });

    }
}