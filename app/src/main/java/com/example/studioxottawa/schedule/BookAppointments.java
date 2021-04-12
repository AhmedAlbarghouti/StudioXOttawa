package com.example.studioxottawa.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
        String name = passedEvent.getString("EVENT_NAME");
        String date = passedEvent.getString("EVENT_DATE");
        String time = passedEvent.getString("EVENT_TIME");
        String staff = passedEvent.getString("EVENT_STAFF");

        eventName.setText(passedEvent.getString("EVENT_NAME"));
        eventDate.setText(passedEvent.getString("EVENT_DATE"));
        eventTime.setText(passedEvent.getString("EVENT_TIME"));
        eventStaff.setText(passedEvent.getString("EVENT_STAFF"));
        /**
         * product name with event fields delimited with --
         */
        String itemName= name+"--"+date+"--"+time+"--"+staff;

        Log.i("FULL NAME",itemName);
        //Universally Unique Event ID
        String Uid = passedEvent.getString("EVENT_UID");

        bookbtn.setOnClickListener(click-> {
            /**
             * If book is clicked, new intent will redirect user to checkout to pay for the selected event
             */
            FirebaseUser user=  FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(user.getUid()).child(Uid);
            Product temp2 = new Product(itemName,15.00,1);
            ref.setValue(temp2);

            Intent bookingIntent = new Intent(this, MainActivity.class);
            bookingIntent.putExtra("UID",Uid);
            bookingIntent.putExtra("isService",true);
            startActivity(bookingIntent);

        });

    }
}