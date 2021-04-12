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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.TextView;


import com.example.studioxottawa.DBHelper;
import com.example.studioxottawa.R;

import com.example.studioxottawa.VODPlayer.VODLibraryFragment;
import com.example.studioxottawa.news.NewsFragment;
import com.example.studioxottawa.schedule.ScheduleFragment;
import com.example.studioxottawa.services.ServicesActivity;
import com.example.studioxottawa.VODPlayer.VODActivity;
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

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    public Context lv_ctxt ;
    public static String userID;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.menuBottomNav);
        lv_ctxt = this;   //xiao






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
                if(item.getItemId() == R.id.nav_more){
                    selectedFragment = new MoreFragment();
                }


                assert selectedFragment != null;
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
        checkNotificationData();
    }







    private void checkNotificationData() {
        ArrayList<String> array_list = new ArrayList<String>();

        //Firebase reference use for reading or writing data to database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        userRef.child(uid).child("Events Purchased").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String name = String.valueOf(ds.child("name").getValue());
                    String date = String.valueOf(ds.child("date").getValue());
                    String time = String.valueOf(ds.child("time").getValue());
                    String staff = String.valueOf(ds.child("staff").getValue());
                    String uid = String.valueOf(ds.child("uid").getValue());

                    String str = "";
                    str = name + "," + date+ "," + time;
                    array_list.add(str);
                }//end of for loop

                //check if we need to load notification
                if (array_list.size()!=0) {
                    loadnotifActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }//end of checkNotificationData



    //Xiao
    private void loadnotifActivity() {

        //NotificationManager allow to put notification into the titilebar of your App
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "reminder";

        //check only for version 0 and newer versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //all built in android classes to create notification icon on the top of the screen
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(lv_ctxt,"reminder")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.bell3)
                        .setContentTitle("Studio X Ottawa Notifications")
                        .setContentText("Select for details")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notifIntent = new Intent(MainActivity.this, notifActivity.class);

        //PendingIntent to pass the content of the notification and set the content
        PendingIntent contentIntent = PendingIntent.getActivity(lv_ctxt, 0, notifIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        //finally build the notification
        notificationManager.notify(0, builder.build());

    }   //xiaoxi }






}
