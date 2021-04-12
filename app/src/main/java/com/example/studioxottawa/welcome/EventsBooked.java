package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.studioxottawa.R;
import com.example.studioxottawa.schedule.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * @Author Ahmed Albarghouti
 * @Date April 2021
 * @Purpose Displays current user's already booked events
 */
public class EventsBooked extends AppCompatActivity {
    // Declaring elements
    private static ArrayList<Event> eventsBookedList = new ArrayList<>();
    private BookedEventListAdapter listAdapter = new BookedEventListAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_booked);

        // init elements
        ListView events = findViewById(R.id.events_bookedLV);
        events.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * @Purpose Loads current user's already booked events into bookedevents arraylist
     */
     void loadBookedEvents() {
        eventsBookedList.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        referenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot eventsPurchasedSnapshot = snapshot.child("Events Purchased");
                for(DataSnapshot dsEvent : eventsPurchasedSnapshot.getChildren()){
                    String name = String.valueOf(dsEvent.child("name").getValue());
                    String staff = String.valueOf(dsEvent.child("staff").getValue());
                    String time = String.valueOf(dsEvent.child("time").getValue());
                    String date = String.valueOf(dsEvent.child("date").getValue());
                    String uid = String.valueOf(dsEvent.child("uid").getValue());
                    eventsBookedList.add(new Event(name,date,time,staff,uid));
                    Log.i("Event",name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listAdapter.notifyDataSetChanged();
    }


    class BookedEventListAdapter extends BaseAdapter{

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return eventsBookedList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return eventsBookedList.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            Event event = eventsBookedList.get(position);
            @SuppressLint("ViewHolder") View eView = inflater.inflate(R.layout.booked_event_row, parent, false);

            ImageView img = eView.findViewById(R.id.eventImage);
            TextView nTxt = eView.findViewById(R.id.eventName);
            TextView dTxt = eView.findViewById(R.id.eventDate);
            TextView tTxt = eView.findViewById(R.id.eventTime);
            nTxt.setText(event.getName());
            dTxt.setText(event.getDate());
            tTxt.setText(event.getTime());
            return eView;
        }
    }
}

