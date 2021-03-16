package com.example.studioxottawa.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.studioxottawa.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Schedule extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ArrayList<Event> allEvents = new ArrayList<>();
    private ArrayList<Event> events = new ArrayList<>();
    TextView pickedDate;
    ImageButton dateButton;
    private ListView eventList;
    EventListAdapter listAdapter = new EventListAdapter();



    private FirebaseUser user;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

//        loadEventsToCloud();
        eventList = findViewById(R.id.eventList);
        eventList.setAdapter(listAdapter);
        pickedDate = findViewById(R.id.pickedDate);
        dateButton = findViewById(R.id.dateButton);


        loadEvents();
        c = Calendar.getInstance();
        currentDayTitleSetup();
        pickedDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));

        dateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                events.clear();
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"Date Picker");

            }
        });



        eventList.setOnItemClickListener((list,item,position,id) -> {
            Bundle eventToPass = new Bundle();
            eventToPass.putString("EVENT_UID",events.get(position).getUid());
            eventToPass.putString("EVENT_NAME",events.get(position).getName());
            eventToPass.putString("EVENT_DATE",events.get(position).getDate());
            eventToPass.putString("EVENT_TIME",events.get(position).getTime());
            eventToPass.putString("EVENT_STAFF",events.get(position).getStaff());

            Intent bookingIntent = new Intent(Schedule.this, BookAppointments.class);
            bookingIntent.putExtras(eventToPass);
            startActivity(bookingIntent);
        });

    }

    private void loadEventsToCloud() {
        DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference().child("Events");

        Event event1 = new Event("Zumba with Nadege & Soul","16/3/2021","6:30PM - 7:15PM","Soul & Nadege");
        Log.i("uid",event1.getUid());
        eventsReference.child(event1.getUid()).setValue(event1);

        Event event2 = new Event("Virtual Zumba with Nadege & Soul","16/3/2021","6:30PM - 7:15PM","Soul & Nadege");
        eventsReference.child(event2.getUid()).setValue(event2);

        Event event3 = new Event("Yoga with Nadège & Soul","17/3/2021","6:00PM - 6:55PM","Soul & Nadege");
        eventsReference.child(event3.getUid()).setValue(event3);

        Event event4 = new Event("Virtual Yoga with Nadège & Soul","17/3/2021","6:00PM - 6:55PM","Soul & Nadege");
        eventsReference.child(event4.getUid()).setValue(event4);

        Event event5 = new Event("Bachata for couples (Intermediate)","18/3/2021","7:00PM - 7:55PM","Soul & Nadege");
        eventsReference.child(event5.getUid()).setValue(event5);

        Event event6 = new Event("Virtual Bachata for couples (Intermediate)","18/3/2021","7:00PM - 7:55PM","Soul & Nadege");
        eventsReference.child(event6.getUid()).setValue(event6);

        Event event7 = new Event("Bachata technique & footworks (Intermediate and up solo class)","19/3/2021","8:10PM - 9:10PM","Soul & Nadege");
        eventsReference.child(event7.getUid()).setValue(event7);

        Event event8 = new Event("Virtual Bachata technique & footworks (Intermediate and up solo class)","19/3/2021","8:10PM - 9:10PM","Soul & Nadege");
        eventsReference.child(event8.getUid()).setValue(event8);


    }

    private void currentDayTitleSetup() {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String currentDateString = (day+"/"+(month+1)+"/"+year);
        for (Event e : allEvents){
            String x = e.getDate();

            if(currentDateString.contentEquals(x)) {
                events.add(e);
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    private void loadEvents() {
        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("Events");

        referenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){

                    String name = String.valueOf(ds.child("name").getValue());
                    String date = String.valueOf(ds.child("date").getValue());
                    String time = String.valueOf(ds.child("time").getValue());
                    String staff = String.valueOf(ds.child("staff").getValue());
                    String uid = String.valueOf(ds.child("uid").getValue());
                    allEvents.add(new Event(name,date,time,staff,uid));
                    Log.i("value", name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listAdapter.notifyDataSetChanged();

    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        pickedDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));


        String currentDateString = (dayOfMonth+"/"+(month+1)+"/"+year);
        for (Event e : allEvents){
            String x = e.getDate();

            if(currentDateString.contentEquals(x)) {
                events.add(e);
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    public class EventListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int position) {
            return events.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            Event event = events.get(position);
            View eView = inflater.inflate(R.layout.event_row, parent, false);

                ImageView img = eView.findViewById(R.id.eventImage);
                TextView nTxt = eView.findViewById(R.id.eventName);
                TextView tTxt = eView.findViewById(R.id.eventTime);
                nTxt.setText(event.getName());
                tTxt.setText(event.getTime());
                 return eView;
        }
    }



}