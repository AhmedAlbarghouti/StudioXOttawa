package com.example.studioxottawa.staff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studioxottawa.R;
import com.example.studioxottawa.schedule.DatePickerFragment;
import com.example.studioxottawa.schedule.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * @Author Ahmed Albarghouti
 * @Date April 2021
 * @Purpose persisting new event to Firebase using user input.
 */
public class AddEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    //declaring global elements
    private EditText newEventNameET, newEventTimeET;
    private TextView newEventDateTV;
    private ImageButton newEventDateButton;
    private Button createNewEventButton;
    private Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        // init elements
        newEventNameET = findViewById(R.id.new_product_name);
        newEventTimeET = findViewById(R.id.new_product_price);
        newEventDateTV = findViewById(R.id.newEventDateTV);
        newEventDateButton = findViewById(R.id.new_product_image);
        createNewEventButton = findViewById(R.id.AddProductBtn);
        c = Calendar.getInstance(); // init c to current day
        newEventDateTV.setText(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime())); //setting TV to formatted c
        newEventDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new NewEventDatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"Date Picker");

            }
        });

        createNewEventButton.setOnClickListener(click -> {
            createNewEvent();
        });
    }

    /**
     * Creates new event using input from Edit texts and global C Calender
     */
    private void createNewEvent() {
        String name = newEventNameET.getText().toString().trim();
        String time = newEventTimeET.getText().toString().trim();

        if(name.isEmpty()){
            newEventNameET.setError("Event name is required!");
            newEventNameET.requestFocus();
            return;
        }
        if(time.isEmpty()){
            newEventTimeET.setError("Time is required!");
            newEventTimeET.requestFocus();
            return;
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String pickedDate = (day+"/"+(month+1)+"/"+year);
        String staff = "Soul & Nadege";
        DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Events");
        Event newEvent = new Event(name,pickedDate,time,staff);
        eventsReference.child(newEvent.getUid()).setValue(newEvent);
        Toast.makeText(AddEvent.this, getString(R.string.EventCreated),Toast.LENGTH_LONG).show();
        finish();
    }


    /**
     *
     * @param view  dialog view
     * @param year the chosen year
     * @param month the chosen month
     * @param dayOfMonth the chosen day of month
     * sets c object's Year Month Day & TextView's value
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        newEventDateTV.setText(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));

    }
}