package com.example.studioxottawa.staff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.studioxottawa.R;
import com.example.studioxottawa.welcome.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class Report extends AppCompatActivity {

    private ArrayList<Integer> elements = new ArrayList<>( Arrays.asList( 1, 2 ) );
    private MyListAdapter myAdapter = new MyListAdapter();
    private ArrayList<User> allUsers = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        loadUsers();

        ListView myList = findViewById(R.id.ListyView);
        myList.setAdapter( myAdapter = new MyListAdapter());
        myList.setOnItemClickListener( (parent, view, pos, id) -> {

            elements.remove(pos);
            myAdapter.notifyDataSetChanged();
        }   );
    }

    /**
     * Used to load users from Firebase database
     */
    private void loadUsers() {
        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("Users");

        referenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){

                    String fullName = String.valueOf(ds.child("fullName").getValue());
                    String phoneNumber = String.valueOf(ds.child("PhoneNumber").getValue());
                    String email = String.valueOf(ds.child("email").getValue());
//                    boolean staff = Boolean.valueOf(String.valueOf(ds.child("staff").getValue()));

                    allUsers.add(new User(fullName, email, phoneNumber));
                    Log.i("value", fullName+" "+email+" "+phoneNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myAdapter.notifyDataSetChanged();
    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return allUsers.size();
        }

        public Object getItem(int position) {
            return (position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            View newView = inflater.inflate(R.layout.row_layout, parent, false);
            User u = (User)getItem(position);

            TextView tViewName = newView.findViewById(R.id.fullName);
            tViewName.setText("  "+u.fullName);
            TextView tViewEmail = newView.findViewById(R.id.fullName);
            tViewEmail.setText("  "+u.email);
            TextView tViewPhone = newView.findViewById(R.id.phoneNumber);
            tViewPhone.setText("  "+u.PhoneNumber);

            //return it to be put in the table
            return newView;
        }
    }
}


