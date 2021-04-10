package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;


import com.example.studioxottawa.DBHelper;
import com.example.studioxottawa.R;

import com.example.studioxottawa.VODPlayer.VODLibraryFragment;
import com.example.studioxottawa.news.News;

import com.example.studioxottawa.news.NewsFragment;
import com.example.studioxottawa.schedule.ScheduleFragment;
import com.example.studioxottawa.services.ServicesActivity;
import com.example.studioxottawa.VODPlayer.VODActivity;
import com.example.studioxottawa.VODPlayer.VODLibraryActivity;
import com.example.studioxottawa.aboutus.AboutusActivity;
import com.example.studioxottawa.notification.notifActivity;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    public static String userID;
    private TextView userTV;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.menuBottomNav);






        userTV = findViewById(R.id.userTV);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_news){
                    selectedFragment = new NewsFragment();
                }
                else if(item.getItemId() == R.id.nav_schedule){
                    selectedFragment = new ScheduleFragment();
                }if (item.getItemId()==R.id.nav_store){
                    selectedFragment= new ServicesActivity();
                    Bundle bundle = new Bundle();
                    bundle.putString("UID",userID);
                    selectedFragment.setArguments(bundle);
                }
                else if(item.getItemId() == R.id.nav_vod) {
                    selectedFragment = new VODLibraryFragment();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.menu_fragment_container,selectedFragment).commit();
                return true;
            }

        };
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User signInUser = snapshot.getValue(User.class);
                String username = signInUser.fullName;
//                userTV.setText(username);
//                loadnotifActivity(username);
                Boolean isStaff = signInUser.staff;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.menu_fragment_container,new NewsFragment()).commit();
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

    }   //xiaoxi }



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
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "reminder";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            //all built in android classes to create notification icon on the top of the screen
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this,"reminder")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.drawable.ic_launcher_background)
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
