package com.example.studioxottawa.news;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studioxottawa.R;
import com.example.studioxottawa.welcome.MainActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {


    private ArrayList<News> elements = new ArrayList<>();
    private ListView myList;
    private MyListAdapter myAdapter;

    private ProgressBar progressBar;
    private String title;
    private String description;
    private String link;
    private String date;
    SQLiteDatabase db;

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

//        Thread thread = new TestThread();
//        thread.start();
//        try {
//            thread.join();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        elements.addAll(MainActivity.elements);

        progressBar = (ProgressBar) findViewById(R.id.bbcProgressBar);
        progressBar.setVisibility(View.VISIBLE);

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

        Button returnButton = (Button)findViewById(R.id.goBack);
        returnButton.setOnClickListener( new View.OnClickListener()
        {  public void onClick(View v){
//            if(pageCount<=15){
//                pageCount +=1;
//                Thread thread = new LoadMore();
//                thread.start();
//                try {
//                    thread.join();
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                //refresh listview
//                myList = findViewById(R.id.newsListView);
//                myList.setAdapter(myAdapter = new MyListAdapter());
//            }else{
//                Toast.makeText(NewsActivity.this, "No more", Toast.LENGTH_LONG ).show();
//            }
            Intent nextActivity = new Intent(NewsActivity.this, MainActivity.class);
            startActivity(nextActivity);
        } });

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
            newView = inflater.inflate(R.layout.row_layout_news, parent, false);
            News n = (News)getItem(position);
            TextView tView = newView.findViewById(R.id.newsTitle);
            tView.setText("  "+n.getTitle());
            TextView tViewDate = newView.findViewById(R.id.newsDate);
            tViewDate.setText(n.getDate());
            return newView;
        }
    }
}

