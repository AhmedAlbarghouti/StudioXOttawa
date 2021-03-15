package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class notifActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;
    TextView textView3;
    Button button;
    DBHelper mydb;
     TableLayout tabaptlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
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
        String loggedusername=getIntent().getStringExtra("username");

        String   msg = loggedusername+','+"\n\n"+
                "This is a friendly reminder that you have following events with Studio X Ottawa on: \n";
        textView.setText(msg);

        textView3 = (TextView) findViewById(R.id.notif3);
        msg =  "\n"+"If you have any questions, please click to call us:";
        textView3.setText(msg);

        textView2 = (TextView) findViewById(R.id.notif2);
        msg = "\n" + "Thanks and see you soon!"+"\n\n" + "Sincerely,"+"\n\n"+"Studio X Ottawa";
        textView2.setText(msg);

        //////////////////////////////////////////
        mydb = new DBHelper(this);

        String str_lesson = "";
        String str_apt = "";
        String str_date = "";
        String str_time = "";
        String str_event = "";

        Cursor res = mydb.getcursor(loggedusername);

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

        res.moveToFirst();

        while(res.isAfterLast() == false){

            View tableRow = LayoutInflater.from(this).inflate(R.layout.tabrow,null,false);
            TextView col1  = (TextView) tableRow.findViewById(R.id.c1);
            TextView col2  = (TextView) tableRow.findViewById(R.id.c2);
            TextView col3  = (TextView) tableRow.findViewById(R.id.c3);

            str_lesson = ""; str_apt = ""; str_date = ""; str_time = "";

            str_lesson = res.getString(res.getColumnIndex("lesson"));
            str_apt = res.getString(res.getColumnIndex("aptment"));
            str_date = res.getString(res.getColumnIndex("date"));
            str_time = res.getString(res.getColumnIndex("time"));
            if (str_lesson.equals("")) {
               str_event = "Appointment";
             } else {
                str_event = str_lesson;
            }

            col1.setText(str_event);
            col2.setText(str_date);
            col3.setText(str_time);
            tabaptlayout.addView(tableRow);

            res.moveToNext();

        }

        /////////////////////////////////////////

    }

@Override
    protected void onDestroy() {
        super.onDestroy();

}
}