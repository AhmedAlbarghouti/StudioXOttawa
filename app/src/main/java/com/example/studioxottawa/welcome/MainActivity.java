package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;

import com.example.studioxottawa.DBHelper;
import com.example.studioxottawa.R;
import com.example.studioxottawa.news.GetData;
import com.example.studioxottawa.news.News;
import com.example.studioxottawa.news.OkHttpUtils;
import com.example.studioxottawa.schedule.Event;
import com.example.studioxottawa.services.ServicesActivity;
import com.example.studioxottawa.vod.VODActivity;
import com.example.studioxottawa.vod.VODLibraryActivity;
import com.example.studioxottawa.aboutus.AboutusActivity;
import com.example.studioxottawa.notification.notifActivity;
import com.example.studioxottawa.schedule.Schedule;


import com.example.studioxottawa.news.NewsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;
    private TextView userTV;
    private Button adminTasksButton;

    public static ArrayList<News> elements = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button contactBtn=findViewById(R.id.contactButton);
        Button logoutBtn=findViewById(R.id.logoutButton);
        adminTasksButton =findViewById(R.id.adminTasksButton);


        userTV = findViewById(R.id.userTV);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        Thread thread = new NewsThread();
        thread.start();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User signInUser = snapshot.getValue(User.class);
                String username = signInUser.fullName;
                userTV.setText(username);
                loadnotifActivity(username);
                Boolean isStaff = signInUser.staff;
                if (isStaff) {
                    adminTasksButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Button contactbtn=findViewById(R.id.contactButton);
        contactbtn.setOnClickListener(click -> {
                    Intent contact = new Intent(this, com.example.studioxottawa.contact.contact.class);
                    startActivity(contact);

        });


        adminTasksButton.setOnClickListener(click -> {
            Intent adminTasks = new Intent(this, com.example.studioxottawa.staff.StaffMenu.class);
            startActivity(adminTasks);
        });
        contactBtn.setOnClickListener(btn-> {
                    Intent contact = new Intent(this, com.example.studioxottawa.contact.contact.class);
                    startActivity(contact);


        });





        logoutBtn.setOnClickListener(click->{
            DBHelper mydb;
            mydb = new DBHelper(this);
            SQLiteDatabase db = mydb.getWritableDatabase();
            db.execSQL("delete from notiftab2");
            db.close();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        });



        Button loginBtn2 = (Button)findViewById(R.id.newsButton);
        loginBtn2.setOnClickListener(click -> {
            Intent goToProfile = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(goToProfile);
        });


        Button loginBtn3 = (Button)findViewById(R.id.aboutusButton);
        loginBtn3.setOnClickListener(click -> {
            Intent goToProfile = new Intent(MainActivity.this, AboutusActivity.class);
            startActivity(goToProfile);
        });



        Button schedulebtn = findViewById(R.id.scheduleButton);
        schedulebtn.setOnClickListener(click -> {
            Intent schedule = new Intent(this, Schedule.class);
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

        // get the reference of Button's
        Button aboutusBtn = (Button) findViewById(R.id.aboutusButton);  //xiaoxi

        aboutusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loads aboutus activity by calling local method
                loadAboutusActivity();
            }

        });  //xiaoxi }

//        notifBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //load aboutus activity by calling local method
//                loadnotifActivity();
//            }
//        });  //xiaoxi }


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

    private class NewsThread extends Thread{
        public void run(){
            String html;
            for(int i=1; i<9; i++){
                String url = "https://www.studioxottawa.com/news/page/"+i+"/";
                html = OkHttpUtils.OkGetArt(url);
                elements.addAll(GetData.spiderArticle(html));
            }
        }
    }

    //xiaoxi {

    private void loadAboutusActivity() {
        //create new intent and start about us activity
        Intent myIntent = new Intent(MainActivity.this, AboutusActivity.class);
        MainActivity.this.startActivity(myIntent);

    }

    //Xiao
    private void loadnotifActivity(String name) {

        //get user name from login
        String loggedusername= name;


        //using sqllite within android, which stores data as file on the phone
        //changed method name from mocdata to eventdata
        createEventData(name);



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
    private void createEventData(String username) {
        //SQlite database class object, this DB class takes cares of database operations(create, update, delete, insert)
        DBHelper mydb;
        mydb = new DBHelper(this);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String o = user.getDisplayName();

        userRef.child(uid).child("Events Purchased").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String name = String.valueOf(ds.child("name").getValue());
                    String date = String.valueOf(ds.child("date").getValue());
                    String time = String.valueOf(ds.child("time").getValue());
                    String staff = String.valueOf(ds.child("staff").getValue());
                    String uid = String.valueOf(ds.child("uid").getValue());

                    mydb.insertNotif(username,name,"Yes",date,time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        ArrayList<String> mynotifs = new ArrayList<String>();
//        mynotifs = mydb.getAllnotifs();
//
//        if (mynotifs.size()==0) {
//            //creating some time and date for appointments and lessons mocup data
//            Calendar calendar = Calendar.getInstance(Locale.getDefault());
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
//            Date dt = new Date();
//            calendar.setTime(dt);
//            //SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
//            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//            String date1 = "";
//            String time1 = "";
//
//            for(int i=0; i<=2; i++){
//                hour = hour + i;
//                minute = minute + i;
//                calendar.add(Calendar.DATE, i);
//                time1 = String.valueOf(hour) +":"+ String.valueOf(minute);
//
//                dt = calendar.getTime();
//                date1 = df.format(dt);
//
//                    switch (i) {
//                        case 0:
//                            //str = "Latin Dance Class on" + " " + date1 + " " + time1;
//                            mydb.insertNotif("Dora", "", "Yes", date1, time1);
//
//                            mydb.insertNotif("Anna", "Latin Dance Class", "", date1, time1);
//                            mydb.insertNotif("Tom", "", "Yes", date1, time1);
//                            break;
//                        case 1:
//                            mydb.insertNotif("Dora", "", "Yes", date1, time1);
//                            //str = "Fitness Class on" + " " + date1 + " " + time1;
//                            mydb.insertNotif("Anna", "Fitness Class", "", date1, time1);
//                            mydb.insertNotif("Tom", "Salsa Class", "", date1, time1);
//                            break;
//                        case 2:
//                            //str = "VOD Class on" + " " + date1 + " " + time1;
//
//                            mydb.insertNotif("Anna", "VOD Class", "", date1, time1);
//                            mydb.insertNotif("Tom", "Fitness Class", "", date1, time1);
//                            break;
//                        default:
//                    } //end of switch
//                } //end of for loop
//        } //end of if user has data
    }//end of createmocdata

    //xiaoxi }


}
