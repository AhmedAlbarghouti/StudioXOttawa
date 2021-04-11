package com.example.studioxottawa.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.studioxottawa.R;
import com.example.studioxottawa.welcome.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class notifActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;
    TextView textView3;
    Button button;
    TableLayout tabaptlayout;
    public static Context lv_ctxt2 ;
    String loggedusername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        lv_ctxt2 = this;

        //for call studio x ottawa button
        button = (Button) findViewById(R.id.buttonCall);
        button.setOnClickListener(click -> {

            Intent contact = new Intent(this, com.example.studioxottawa.contact.ContactUs.class);
            startActivity(contact);

        });


        textView = (TextView) findViewById(R.id.notif1);
        //get full name of the logged on user from data base
        FirebaseUser user1;
        DatabaseReference userRef1 = FirebaseDatabase.getInstance().getReference("Users");
        user1 = FirebaseAuth.getInstance().getCurrentUser();
        String uid1 = user1.getUid();

        userRef1.child(uid1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User signInUser = snapshot.getValue(User.class);
                loggedusername = signInUser.fullName;

                String   msg = loggedusername+','+"\n\n"+
                        "This is a friendly reminder that you have following events with Studio X Ottawa on: \n";
                textView.setText(msg);

                textView3 = (TextView) findViewById(R.id.notif3);
                msg =  "\n"+"If you have any questions, please click to contact us:";
                textView3.setText(msg);

                textView2 = (TextView) findViewById(R.id.notif2);
                msg = "\n" + "Thanks and see you soon!"+"\n\n" + "Sincerely,"+"\n\n"+"Studio X Ottawa";
                textView2.setText(msg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        });

        //table data strings
        String str_date = "";
        String str_time = "";
        String str_event = "";

        tabaptlayout=(TableLayout)findViewById(R.id.tabaptmnt);

        //creating column headings
        View tableRowapt = LayoutInflater.from(this).inflate(R.layout.tabrow,null,false);
        TextView col11  = (TextView) tableRowapt.findViewById(R.id.c1);
        TextView col21  = (TextView) tableRowapt.findViewById(R.id.c2);
        TextView col31  = (TextView) tableRowapt.findViewById(R.id.c3);

        col11.setText("EVENT");
        col21.setText("DATE");
        col31.setText("TIME");
        tabaptlayout.addView(tableRowapt);

        //get events data from database
        userRef1.child(uid1).child("Events Purchased").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    //get each column's value
                    String name = String.valueOf(ds.child("name").getValue());
                    String date = String.valueOf(ds.child("date").getValue());
                    String time = String.valueOf(ds.child("time").getValue());

                    View tableRow = LayoutInflater.from(lv_ctxt2).inflate(R.layout.tabrow, null, false);
                    TextView col1 = (TextView) tableRow.findViewById(R.id.c1);
                    col1.setPaintFlags(col11.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    TextView col2 = (TextView) tableRow.findViewById(R.id.c2);
                    TextView col3 = (TextView) tableRow.findViewById(R.id.c3);

                    //set column value
                    col1.setText(name);

                    //text view link to list activity
                    col1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent booked = new Intent(lv_ctxt2, com.example.studioxottawa.welcome.EventsBooked.class);
                            startActivity(booked);
                        }
                    });









                    col2.setText(date);
                    col3.setText(time);
                    tabaptlayout.addView(tableRow);
                }// end of for loop
            } // end of onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    } //end of onCreate override method

    @Override
    //the system is temporarily destroying this instance of the activity to save space
    protected void onDestroy() {
        super.onDestroy();

    }




}