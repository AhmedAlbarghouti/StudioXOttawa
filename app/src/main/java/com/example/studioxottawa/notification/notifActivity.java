package com.example.studioxottawa.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    DBHelper mydb;
    TableLayout tabaptlayout;
    public static Context lv_ctxt ;
    String loggedusername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        lv_ctxt = this;

//for call studio x ottawa button
        button = (Button) findViewById(R.id.buttonCall);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:6139125083"));
                startActivity(callIntent);
            }
        });


        textView = (TextView) findViewById(R.id.notif1);
        //getting the notification message
        //get the logged on user name passed from mainActivity to this intent
//        String loggedusername= getIntent().getStringExtra("username");

//        //////////////////////////////////////////////////////////
        DatabaseReference userRef1 = FirebaseDatabase.getInstance().getReference("Users");
        userRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User signInUser = snapshot.getValue(User.class);
                loggedusername = signInUser.fullName;

                String   msg = loggedusername+','+"\n\n"+
                        "This is a friendly reminder that you have following events with Studio X Ottawa on: \n";
                textView.setText(msg);

                textView3 = (TextView) findViewById(R.id.notif3);
                msg =  "\n"+"If you have any questions, please click to call us:";
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
//        mydb = new DBHelper();

        String str_date = "";
        String str_time = "";
        String str_event = "";

      //  Cursor res = mydb.getcursor(loggedusername);

        tabaptlayout=(TableLayout)findViewById(R.id.tabaptmnt);
//creating column headings
        View tableRowapt = LayoutInflater.from(this).inflate(R.layout.tabrow,null,false);
        TextView col11  = (TextView) tableRowapt.findViewById(R.id.c1);
        TextView col21  = (TextView) tableRowapt.findViewById(R.id.c2);
        TextView col31  = (TextView) tableRowapt.findViewById(R.id.c3);

        col11.setText("Event");
        col21.setText("Date");
        col31.setText("Time");
        tabaptlayout.addView(tableRowapt);

      //  res.moveToFirst();

      //  while(res.isAfterLast() == false){


        ArrayList<String> mynotifs = new ArrayList<String>();

//        mynotifs = mydb.get_firebase_data();
//

        FirebaseUser user;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        //String o = user.getDisplayName();


        userRef.child(uid).child("Events Purchased").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String name = String.valueOf(ds.child("name").getValue());
                    String date = String.valueOf(ds.child("date").getValue());
                    String time = String.valueOf(ds.child("time").getValue());
                    String staff = String.valueOf(ds.child("staff").getValue());
                    String uid = String.valueOf(ds.child("uid").getValue());

                    //mydb.insertNotif(username,name,"Yes",date,time);
//                    String str = "";
//                    str = name + "," + date+ "," + time;
//                    array_list.add(str);

                    View tableRow = LayoutInflater.from(lv_ctxt).inflate(R.layout.tabrow, null, false);
                    TextView col1 = (TextView) tableRow.findViewById(R.id.c1);
                    TextView col2 = (TextView) tableRow.findViewById(R.id.c2);
                    TextView col3 = (TextView) tableRow.findViewById(R.id.c3);

                    col1.setText(name);
                    col2.setText(date);
                    col3.setText(time);
                    tabaptlayout.addView(tableRow);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






//        for (int i=0; i<=mynotifs.size(); i++) {
//
//            View tableRow = LayoutInflater.from(this).inflate(R.layout.tabrow, null, false);
//            TextView col1 = (TextView) tableRow.findViewById(R.id.c1);
//            TextView col2 = (TextView) tableRow.findViewById(R.id.c2);
//            TextView col3 = (TextView) tableRow.findViewById(R.id.c3);
//            String str = mynotifs.get(i);
//            String[] str_arry = str.split(",");
//                for (int j=0; j<=str_arry.length; j++) {
//
//                    str_event = "";
//                    str_date = "";
//                    str_time = "";
//
//                    switch(j) {
//                        case 0:
//                            str_event = str_arry[0];
//                            break;
//                        case 1:
//                            str_date = str_arry[1];
//                            break;
//                        case 2:
//                            str_time = str_arry[2];
//                            break;
//                        default:
//                    }
//
//                }  //end of j for loop
//
////            str_lesson = res.getString(res.getColumnIndex("lesson"));
////            str_apt = res.getString(res.getColumnIndex("aptment"));
////            str_date = res.getString(res.getColumnIndex("date"));
////            str_time = res.getString(res.getColumnIndex("time"));
////            if (str_lesson.equals("")) {
////                str_event = "Appointment";
////            } else {
////                str_event = str_lesson;
////            }
//
//            col1.setText(str_event);
//            col2.setText(str_date);
//            col3.setText(str_time);
//            tabaptlayout.addView(tableRow);
//
//            //      res.moveToNext();
//
//            // } //end of while loop
//        } //end of for loop.
        /////////////////////////////////////////

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}