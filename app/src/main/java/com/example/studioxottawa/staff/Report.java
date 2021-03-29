package com.example.studioxottawa.staff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class Report extends AppCompatActivity {

    private MyListAdapter myAdapter;
    private ListView myList;
    private ArrayList<User> allUsers = new ArrayList<User>();
    private ArrayList test= new ArrayList();
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        loadUsers();


        Button reportButton = (Button)findViewById(R.id.load_report);
        reportButton.setOnClickListener( new View.OnClickListener()
        {  public void onClick(View v){

            text = (TextView)findViewById(R.id.user_num);
            text.setText("Total Users:  "+ allUsers.size());

            Log.i("gycreport", "MyList Ready");
            myList = findViewById(R.id.ListyView);
            myList.setAdapter( myAdapter = new MyListAdapter());
            myList.setOnItemClickListener( (parent, view, pos, id) -> {

                myAdapter.notifyDataSetChanged();
            }   );

        } });

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
                    Log.i("gycreport", fullName+" "+email+" "+phoneNumber+" "+ allUsers.size());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return allUsers.size();
        }

        public Object getItem(int position) { return allUsers.get(position); }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View old, ViewGroup parent) {
            Log.i("gycreport", " getview ");
            View newView = null;
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.row_layout, parent, false);
            User u = (User)getItem(position);
            TextView tViewName = newView.findViewById(R.id.fullName);
            tViewName.setText("  "+u.fullName);
            TextView tViewEmail = newView.findViewById(R.id.email);
            tViewEmail.setText("  "+u.email);
            TextView tViewPhone = newView.findViewById(R.id.phoneNumber);
            tViewPhone.setText("  "+u.PhoneNumber);
            //return it to be put in the table
            return newView;
        }
    }
}


