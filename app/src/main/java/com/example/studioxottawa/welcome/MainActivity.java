package com.example.studioxottawa.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.studioxottawa.R;
import com.example.studioxottawa.news.GetData;
import com.example.studioxottawa.news.News;
import com.example.studioxottawa.news.OkHttpUtils;
import com.example.studioxottawa.staff.AddEvent;
import com.example.studioxottawa.staff.Report;
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
                String name = signInUser.fullName;
                userTV.setText(name);
                Boolean isStaff = signInUser.staff;
                if (isStaff) {
                    adminTasksButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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

        Button scheduleBtn = findViewById(R.id.scheduleButton);
        scheduleBtn.setOnClickListener(click -> {

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
        // get the reference of Button's
        Button aboutusBtn = (Button) findViewById(R.id.aboutusButton);  //xiaoxi
        ImageButton notifBtn = (ImageButton) findViewById(R.id.bellImageButton);  //xiaoxi

// perform setOnClickListener event on aboutus btn //xiaoxi  {
        aboutusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load aboutus activity by calling local method
                loadAboutusActivity();
            }
        });  //xiaoxi }

        notifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load aboutus activity by calling local method
                loadnotifActivity();
            }
        });  //xiaoxi }

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

    }   //xiaoxi }

    //xiaoxi {
    private void loadnotifActivity() {
        //create new intent and start about us activity
        Intent notifIntent = new Intent(MainActivity.this, notifActivity.class);
        MainActivity.this.startActivity(notifIntent);

    }   //xiaoxi }
}