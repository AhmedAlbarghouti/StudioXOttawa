package com.example.studioxottawa.schedule;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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

/**
 * @Author Ahmed Albarghouti
 * @Date Feb 2021
 * @Purpose Extends fragment & inflates activity_schedule
 *          Displays Schedule to user and allows for change of date to view schedule on a different day
 */
public class ScheduleFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private ArrayList<Event> allEvents = new ArrayList<>(); // Arraylist that holds all Events no specifications
    private ArrayList<Event> events = new ArrayList<>(); // Arraylist that holds Events that only match the user's request via Calender Date Picker
    TextView pickedDate;
    ImageButton dateButton;
    EventListAdapter listAdapter = new EventListAdapter();



    private FirebaseUser user; // Firebase User Object
    private Calendar c = Calendar.getInstance(); // calender obj with the current day set

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_schedule,container,false); // inflates activity_schedule into the fragment

        ListView eventList = root.findViewById(R.id.eventList);
        eventList.setAdapter(listAdapter);
        pickedDate = root.findViewById(R.id.pickedDate);
        dateButton = root.findViewById(R.id.dateButton);
        loadEvents();


        pickedDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));

        /*
          Set on click if user clicks schedule button to change the date of the displayed schedule
         */
        dateButton.setOnClickListener(new View.OnClickListener(){
            // if dateButton is clicked then show the DatePicker Fragment
            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getChildFragmentManager(),"Date Picker");
            }
        });


        // if any item in the listview is clicked then a new bundle is created containing the clicked event's info
        //and passed to bookingAppointments
        eventList.setOnItemClickListener((list, item, position, id) -> {
            Bundle eventToPass = new Bundle();
            eventToPass.putString("EVENT_UID",events.get(position).getUid());
            eventToPass.putString("EVENT_NAME",events.get(position).getName());
            eventToPass.putString("EVENT_DATE",events.get(position).getDate());
            eventToPass.putString("EVENT_TIME",events.get(position).getTime());
            eventToPass.putString("EVENT_STAFF",events.get(position).getStaff());

            Intent bookingIntent = new Intent(getActivity(), BookAppointments.class);
            bookingIntent.putExtras(eventToPass);
            startActivity(bookingIntent);
        });

        return root;
    }

    //when a date is set and user clicks positive button
    //calender object c is changed to the date picked then all events that have matching date will be added to the list
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        events.clear();
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


    //fetches all Event objects from Firebase Events table and add its to the existing all events arraylist
    public void loadEvents() {
        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("Events");
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String currentDateString = (day+"/"+(month+1)+"/"+year);

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
                for (Event e : allEvents){
                    String x = e.getDate();

                    if(currentDateString.contentEquals(x)) {
                        events.add(e);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listAdapter.notifyDataSetChanged();
            }
        });
        listAdapter.notifyDataSetChanged();

    }
    /**
     * the adapter inner class that provide data for the listView
     */
    public class EventListAdapter extends BaseAdapter {

        /**
         * @return the number of items
         */
        @Override
        public int getCount() {
            return events.size();
        }

        /**
         * @param position - the row position of a listView content
         * @return the object to show at row position
         */
        @Override
        public Object getItem(int position) {
            return events.get(position);
        }

        /**
         * @param position - the row position of a listView content
         * @return database id of the item at the position
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }


        /**
         * @param position - the row position of a listView content
         * @param convertView - the previous view at the position
         * @param parent   - contains other views, describes the layout of the Views in the group
         * @return a View object to go in a row of the ListView
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            Event event = events.get(position);
            @SuppressLint("ViewHolder") View eView = inflater.inflate(R.layout.event_row, parent, false);

            ImageView img = eView.findViewById(R.id.eventImage);
            TextView nTxt = eView.findViewById(R.id.eventName);
            TextView tTxt = eView.findViewById(R.id.eventTime);
            nTxt.setText(event.getName());
            tTxt.setText(event.getTime());
            return eView;
        }
    }
}
