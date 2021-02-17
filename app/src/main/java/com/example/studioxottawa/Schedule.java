package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    Calendar c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        eventList = findViewById(R.id.eventList);
        eventList.setAdapter(listAdapter);
        pickedDate = findViewById(R.id.pickedDate);
        dateButton = findViewById(R.id.dateButton);
        c = Calendar.getInstance();

        pickedDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));
        dateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                events.clear();
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"Date Picker");

            }
        });

        loadEvents();

    }

    private void loadEvents() {
        Calendar o = Calendar.getInstance();
        o.set(Calendar.YEAR,2021);
        o.set(Calendar.MONTH,1);
        o.set(Calendar.DAY_OF_MONTH,17);
        allEvents.add(new Event("Virtual Yoga class",o,"6:00PM - 6:55PM","Soul & Nadege"));
        allEvents.add(new Event("Virtual Backata Technique & Footworks",o,"8:10PM - 9:10PM","Soul & Nadege"));

        Calendar e = Calendar.getInstance();
        e.set(Calendar.YEAR,2021);
        e.set(Calendar.MONTH,1);
        e.set(Calendar.DAY_OF_MONTH,18);
        allEvents.add(new Event("Virtual Zumba Toning/Sentao Class",e,"6:00PM - 6:45PM","Soul & Nadege"));
        allEvents.add(new Event("Virtual Salsa Technique & Footworks",e,"8:10PM - 9:10PM","Soul & Nadege"));

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        pickedDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));
        for (Event e : allEvents){
            String i = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            String x = DateFormat.getDateInstance(DateFormat.FULL).format(e.getDate().getTime());

            if(i.contentEquals(x)) {
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


    public class Event{
        private String name;
        private Calendar date;
        private String time;
        private String staff;


        public Event(String name, Calendar date, String time, String staff) {
            this.name = name;
            this.date = date;
            this.time = time;
            this.staff = staff;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Calendar getDate() {
            return date;
        }

        public void setDate(Calendar date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getStaff() {
            return staff;
        }

        public void setStaff(String staff) {
            this.staff = staff;
        }


    }
}