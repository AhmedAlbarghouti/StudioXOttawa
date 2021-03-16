package com.example.studioxottawa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.studioxottawa.news.NewsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button contactbtn=findViewById(R.id.contactButton);
        contactbtn.setOnClickListener(btn-> {
                    Intent contact = new Intent(this, contact.class);
                    startActivity(contact);
        });

        Button generateReport=findViewById(R.id.generateReport);

        generateReport.setVisibility(View.INVISIBLE);
        String username = getIntent().getExtras().getString("USER_NAME");
        if (username.equalsIgnoreCase("admin"))
            generateReport.setVisibility(View.VISIBLE);

        generateReport.setOnClickListener(btn->{

            Intent nextActivity = new Intent(MainActivity.this, Report.class);
            startActivity(nextActivity); //make the transitio

        });

        Button loginBtn2 = (Button)findViewById(R.id.newsButton);
        loginBtn2.setOnClickListener(click -> {
            Intent goToProfile = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(goToProfile);
        });

        Button schedulebtn = findViewById(R.id.scheduleButton);
        schedulebtn.setOnClickListener(click -> {

            Intent schedule = new Intent(this,Schedule.class);
            startActivity(schedule);
        });

        Button vodButton = findViewById(R.id.vodsButton);
        vodButton.setOnClickListener(v-> {
            loadVodLibrary();
        });

        Button serviceButton = findViewById(R.id.servicesButton);
        serviceButton.setOnClickListener(v-> {
            loadServices();
        });

        //xiaoxi {
        loadnotifActivity(); //loads notification...
        // get the reference of Button's
        Button aboutusBtn = (Button) findViewById(R.id.aboutusButton);  //xiaoxi

        aboutusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loads aboutus activity by calling local method
                loadAboutusActivity();
            }
        });
        //xiaoxi }
    }

    private void loadVodLibrary() {
        Intent vodLibrary = new Intent(MainActivity.this, VODLibraryActivity.class);
        //This utilizes API calls to the youtube Data v3 interface.
        // Avoid unnecessary spamming in order to preserve api call quotas during testing.
        //Use the included loadVodDebug below if testing Vod playback.
        startActivity(vodLibrary);
        //loadVodDebug();
    }

    private void loadServices() {
        Intent services = new Intent(MainActivity.this, ServicesActivity.class);
        startActivity(services);
    }

    private void loadVodDebug() {
        //Debug method to bypass library to save on api calls.  Uncomment the loadVodDebug(); line in loadVodLibrary to enable.
        String url = "https://r4---sn-ux3n588t-mjh6.googlevideo.com/videoplayback?expire=1613507657&ei=6dcrYKbFLKyL2_gPwK-j0As&ip=76.10.173.120&id=o-AOJB_WbF3NyGnjMjgrAveJZxzv1KHtFS212fW5W_vMH2&itag=22&source=youtube&requiressl=yes&mh=_8&mm=31%2C29&mn=sn-ux3n588t-mjh6%2Csn-tt1e7n7l&ms=au%2Crdu&mv=m&mvi=4&pl=21&initcwndbps=1153750&vprv=1&mime=video%2Fmp4&ns=QyuuKWKpdjLyaiS0O5bkIYgF&ratebypass=yes&dur=81.130&lmt=1411161817663260&mt=1613485724&fvip=6&c=WEB&n=eEv0tFPOg_deRiChZH&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIgNV2m5tKVBy2PyYhCGIUMNm8p1B9iild0q0elqVnjYLYCIQDrY5Tlv4E7Ua_CHJb5PRX6bXxZt8VseQUoV9hmHdKObw%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRQIgG9wOnXOENa2DcbBYnap_t_UDU_GoR_YWH9Z_xKkP0VQCIQDcpY021gCadZuYkwZzHYROdXh9seokvbCVIrrTcTV1xQ%3D%3D";
        Intent vodPlayback = new Intent(MainActivity.this, VODActivity.class);
        vodPlayback.putExtra("playbackURI", url);
        startActivity(vodPlayback);
    }

    //xiaoxi {

    private void loadAboutusActivity() {
        //create new intent and start about us activity
        Intent myIntent = new Intent(MainActivity.this, AboutusActivity.class);
        MainActivity.this.startActivity(myIntent);

    }

    //Xiao
    private void loadnotifActivity() {

        //get user name from login
        String loggedusername = getIntent().getExtras().getString("USER_NAME");

        //using sqllite within android, which stores data as file on the phone
        createmocdata();

        ArrayList<String> mynotifs = new ArrayList<String>();

        //SQlite database class object, this DB class takes cares of database operations(create, update, delete, insert)
        DBHelper mydb;
        mydb = new DBHelper(this);

        mynotifs = mydb.getData(loggedusername);
        if (mynotifs.size()==0) {
        } else {

            //all built in android classes to create notification icon on the top of the screen
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.bell5)
                            .setContentTitle("Studio X Ottawa Notifications")
                            .setContentText("Select for details")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent notifIntent = new Intent(MainActivity.this, notifActivity.class);

            //passing on logged on user name to notifActivity class
            notifIntent.putExtra("username", loggedusername);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);

            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
            //finally build the notification
            manager.notify(0, builder.build());
        } //end of if mynotifs = 0
       // } //endif username = Q1

    }   //xiaoxi }


    //xiaoxi {
    private void createmocdata() {
        //SQlite database class object, this DB class takes cares of database operations(create, update, delete, insert)
        DBHelper mydb;
        mydb = new DBHelper(this);

        ArrayList<String> mynotifs = new ArrayList<String>();
        mynotifs = mydb.getAllnotifs();

        if (mynotifs.size()==0) {
            //creating some time and date for appointments and lessons mocup data
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            Date dt = new Date();
            calendar.setTime(dt);
            //SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String date1 = "";
            String time1 = "";

            for(int i=0; i<=2; i++){
                hour = hour + i;
                minute = minute + i;
                calendar.add(Calendar.DATE, i);
                time1 = String.valueOf(hour) +":"+ String.valueOf(minute);

                dt = calendar.getTime();
                date1 = df.format(dt);

                    switch (i) {
                        case 0:
                            //str = "Latin Dance Class on" + " " + date1 + " " + time1;
                            mydb.insertNotif("Dora", "", "Yes", date1, time1);

                            mydb.insertNotif("Anna", "Latin Dance Class", "", date1, time1);
                            mydb.insertNotif("Tom", "", "Yes", date1, time1);
                            break;
                        case 1:
                            mydb.insertNotif("Dora", "", "Yes", date1, time1);
                            //str = "Fitness Class on" + " " + date1 + " " + time1;
                            mydb.insertNotif("Anna", "Fitness Class", "", date1, time1);
                            mydb.insertNotif("Tom", "Salsa Class", "", date1, time1);
                            break;
                        case 2:
                            //str = "VOD Class on" + " " + date1 + " " + time1;

                            mydb.insertNotif("Anna", "VOD Class", "", date1, time1);
                            mydb.insertNotif("Tom", "Fitness Class", "", date1, time1);
                            break;
                        default:
                    } //end of switch
                } //end of for loop
        } //end of if user has data
    }//end of createmocdata

    //xiaoxi }


}