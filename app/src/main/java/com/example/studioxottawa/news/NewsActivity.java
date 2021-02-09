package com.example.studioxottawa.news;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.studioxottawa.MainActivity;
import com.example.studioxottawa.R;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.os.SystemClock.sleep;

public class NewsActivity extends AppCompatActivity {


    private ArrayList<News> elements = new ArrayList<>();
    private ListView myList;
    private MyListAdapter myAdapter;
    public static final String ACTIVITY_NAME = "BBC_ACTIVITY";
    private ArrayList<News> tempFavourite = new ArrayList<>();
    //Load content
    private ProgressBar progressBar;
    private String title;
    private String description;
    private String link;
    private String date;
    SQLiteDatabase db;
    //    MyOpener dbOpener;
    //fragment
    public static final String NEWS_TITLE = "TITLE";
    public static final String NEWS_DESCRIPTION = "DESCRIPTION";
    public static final String NEWS_LINK = "LINK";
    public static final String NEWS_DATE = "DATE";
    public static final String NEWS_POSITION = "POSITION";
    public static final String NEWS_ID = "ID";
    DetailsFragment dFragment;
    private static final String TAG = "GetData";

    //Initially load 2 pages
    private int pageCount = 2;

    /**
     * @param savedInstanceState - the Bundle object that is passed into the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Thread thread = new TestThread();
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


        progressBar = (ProgressBar) findViewById(R.id.bbcProgressBar);
        progressBar.setVisibility(View.VISIBLE);

//        dbOpener = new MyOpener(this);
//        db = dbOpener.getWritableDatabase();


        //Create list view
        myList = findViewById(R.id.newsListView);
        myList.setAdapter(myAdapter = new MyListAdapter());
        boolean isTablet = findViewById(R.id.frameLayout) != null;

        //create the fragment of news
        myList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(NEWS_TITLE, elements.get(position).getTitle() );
            dataToPass.putString(NEWS_DESCRIPTION, elements.get(position).getDescription() );
            dataToPass.putString(NEWS_LINK, elements.get(position).getLink() );
            dataToPass.putString(NEWS_DATE, elements.get(position).getDate() );

            dataToPass.putInt(NEWS_POSITION, position);
            dataToPass.putLong(NEWS_ID, id);

            if(isTablet)
            {
                dFragment = new DetailsFragment();
                dFragment.setArguments( dataToPass );
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, dFragment)
                        .commit();
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(NewsActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        Button favouriteButton = (Button)findViewById(R.id.More);
        favouriteButton.setOnClickListener( new View.OnClickListener()
        {  public void onClick(View v){
            if(pageCount<=15){
                pageCount +=1;
                Thread thread = new LoadMore();
                thread.start();
                try {
                    thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //refresh listview
                myList = findViewById(R.id.newsListView);
                myList.setAdapter(myAdapter = new MyListAdapter());
            }else{
                Toast.makeText(NewsActivity.this, "No more", Toast.LENGTH_LONG ).show();
            }

        } });





//        Button favouriteButton = (Button)findViewById(R.id.GoToFavourite);
//        favouriteButton.setOnClickListener( new View.OnClickListener()
//        {  public void onClick(View v){
//            Intent goToChatRoom = new Intent(BbcActivity.this, FavourityActivity.class);
//            startActivity(goToChatRoom);
//        } });


        //create the fragment of news
//        myList.setOnItemClickListener((list, item, position, id) -> {
//            //Create a bundle to pass data to the new fragment
//            Bundle dataToPass = new Bundle();
//            dataToPass.putString(NEWS_TITLE, elements.get(position).getTitle() );
//            dataToPass.putString(NEWS_DESCRIPTION, elements.get(position).getDescription() );
//            dataToPass.putString(NEWS_LINK, elements.get(position).getLink() );
//            dataToPass.putString(NEWS_DATE, elements.get(position).getDate() );
//
//            dataToPass.putInt(NEWS_POSITION, position);
//            dataToPass.putLong(NEWS_ID, id);
//
//            if(isTablet)
//            {
//                dFragment = new DetailsFragment();
//                dFragment.setArguments( dataToPass );
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.frameLayout, dFragment)
//                        .commit();
//            }
//            else //isPhone
//            {
//                Intent nextActivity = new Intent(BbcActivity.this, EmptyActivity.class);
//                nextActivity.putExtras(dataToPass); //send data to next activity
//                startActivity(nextActivity); //make the transition
//            }
//        });

//        //Toolbar and NavigationDraw
//        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(tBar);
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
//                drawer, tBar, R.string.BBC_open, R.string.BBC_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * inner class that deals with thread synchronization
     */
    private class TestThread extends Thread{

        public void run(){
            String html;
            for(int i=1; i<pageCount+1; i++){
                String url = "https://www.studioxottawa.com/news/page/"+i+"/";
                html = OkHttpUtils.OkGetArt(url);
                elements.addAll(GetData.spiderArticle(html));
            }
        }
    }

    private class LoadMore extends Thread{

        public void run(){
            String html;
                String url = "https://www.studioxottawa.com/news/page/"+pageCount+"/";
                html = OkHttpUtils.OkGetArt(url);
                elements.addAll(GetData.spiderArticle(html));
        }
    }
//    private void Test() {
//
//        new Thread() {
//            public void run() {
//                String html = OkHttpUtils.OkGetArt("https://www.studioxottawa.com/news/");
////                System.out.print("234"+html);
//                elements.addAll(GetData.spiderArticle(html));
//                String size = elements.size()+"";
//                Log.i(TAG, "gycsize " + size);
//                Log.i(TAG, "gyctitle " + elements.get(0).getTitle());
//                Log.i(TAG, "gycdes " + elements.get(0).getDescription());
//                Log.i(TAG, "gyclink " + elements.get(0).getLink());
//                Log.i(TAG, "gycdate " + elements.get(0).getDate());
//            }
//        }.start();
//    }


    /**
     * the adapter inner class that provide data for the listView
     */
    private class MyListAdapter extends BaseAdapter {

        /**
         * @return the number of items
         */
        public int getCount() {
            return elements.size();
        }

        /**
         * @param position - the row position of a listView content
         * @return the object to show at row position
         */
        public Object getItem(int position) {
            return elements.get(position);
        }

        /**
         * @param position - the row position of a listView content
         * @return database id of the item at the position
         */
        public long getItemId(int position) {
            return (long) position;
        }

        /**
         * @param position - the row position of a listView content
         * @param old      - the previous view at the position
         * @param parent   - contains other views, describes the layout of the Views in the group
         * @return a View object to go in a row of the ListView
         */
        public View getView(int position, View old, ViewGroup parent) {
            View newView = null;
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.row_layout, parent, false);
            News n = (News)getItem(position);
            TextView tView = newView.findViewById(R.id.newsTitle);
            tView.setText("  "+n.getTitle());
            TextView tViewDate = newView.findViewById(R.id.newsDate);
            tViewDate.setText(n.getDate());
            return newView;
        }
    }

//    /**
//     * inflates the menu from XML layout
//     * @param menu - {@link Menu} object that is applied by the activity
//     * @return true
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu3, menu);
//
//        return true;
//    }

//    /**
//     * @param item - {@link MenuItem} object in the {@link Menu}
//     * @return true
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        String message = null;
//        Intent goTo;
//        //Look at your menu XML file. Put a case for every id in that file:
//        switch(item.getItemId())
//        {
//            //what to do when the menu item is selected:
//            case R.id.bbc_fav_pic:
//                message = getResources().getString(R.string.BBC_Toolbar_Option)
//                        +" "+getResources().getString(R.string.BBC_Favourite);
//                goTo = new Intent(BbcActivity.this, FavourityActivity.class);
//                startActivity(goTo);
//                break;
//            case R.id.guardian_pic:
//                message = getResources().getString(R.string.BBC_Toolbar_Option)
//                        +" "+getResources().getString(R.string.BBC_Guardian_news);
//                goTo = new Intent(BbcActivity.this, GuardianMain.class);
//                startActivity(goTo);
//                break;
//            case R.id.nasa_pic:
//                message = getResources().getString(R.string.BBC_Toolbar_Option)
//                        +" "+getResources().getString(R.string.BBC_NASA);
//                goTo = new Intent(BbcActivity.this, ImageDay.class);
//                startActivity(goTo);
//                break;
//            case R.id.nasa_earth_pic:
//                message = getResources().getString(R.string.BBC_Toolbar_Option)
//                        +" "+getResources().getString(R.string.BBC_NASA_earth);
//                goTo = new Intent(BbcActivity.this, BingMain.class);
//                startActivity(goTo);
//                break;
//            case R.id.bbc_help:
//                message = getResources().getString(R.string.BBC_Toolbar_Option)
//                        +" "+getResources().getString(R.string.BBC_help);
//                new AlertDialog.Builder(BbcActivity.this)
//                        .setTitle(getString(R.string.BBC_AlertDialogTitle3))
//                        .setMessage(getString(R.string.BBC_HelpMes1) + "\n" + getString(R.string.BBC_HelpMes2) + "\n"
//                                + getString(R.string.BBC_HelpMes3) + "\n" + getString(R.string.BBC_HelpMes4) + "\n"
//                                + getString(R.string.BBC_HelpMes5) + "\n")
//                        .setNegativeButton(getString(R.string.BBC_Help_NegativeButton),null)
//                        .setPositiveButton(getString(R.string.BBC_Help_PositiveButton), null).show();
//                break;
//        }
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//        return true;
//    }

//    /**
//     * @param item - {@link MenuItem} object in the NavigationDrawer
//     * @return false
//     */
//    @Override
//    public boolean onNavigationItemSelected( MenuItem item) {
//
//        String message = null;
//
//        int id = item.getItemId();
//        Intent goTo;
//
//        if(id == R.id.goToFav){
//
//            goTo = new Intent(BbcActivity.this, FavourityActivity.class);
//            startActivity(goTo);
//        }else if(id == R.id.goToGardian){
//            goTo = new Intent(BbcActivity.this, GuardianMain.class);
//            startActivity(goTo);
//        }else if(id == R.id.goToNASA){
//
//            goTo = new Intent(BbcActivity.this, ImageDay.class);
//            startActivity(goTo);
//        }else if(id == R.id.goToNASAEarth){
//
//            goTo = new Intent(BbcActivity.this, BingMain.class);
//            startActivity(goTo);
//        }
//        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return false;
//    }
}

