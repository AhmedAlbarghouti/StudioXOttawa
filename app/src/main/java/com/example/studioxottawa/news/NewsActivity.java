package com.example.studioxottawa.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studioxottawa.R;
import com.example.studioxottawa.welcome.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    private static ArrayList<News> allNews = new ArrayList<>();
    private ListView myList;
    private MyListAdapter myAdapter;

    public static final String NEWS_TITLE = "TITLE";
    public static final String NEWS_DESCRIPTION = "DESCRIPTION";
    public static final String NEWS_LINK = "LINK";
    public static final String NEWS_DATE = "DATE";
    public static final String NEWS_POSITION = "POSITION";
    public static final String NEWS_ID = "ID";
    DetailsFragment dFragment;
    private static final String TAG = "GetData";

    /**
     * @param savedInstanceState - the Bundle object that is passed into the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        boolean isTablet = findViewById(R.id.frameLayout) != null;

        Log.i("gycreport", "MyList Ready");
        myList = findViewById(R.id.newsListView);
        myList.setAdapter( myAdapter = new MyListAdapter());
        myList.setOnItemClickListener( (list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(NEWS_TITLE, allNews.get(position).getTitle() );
            dataToPass.putString(NEWS_DESCRIPTION, allNews.get(position).getDescription() );
            dataToPass.putString(NEWS_LINK, allNews.get(position).getLink() );
            dataToPass.putString(NEWS_DATE, allNews.get(position).getDate() );

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

            myAdapter.notifyDataSetChanged();
        }   );

        Button returnButton = (Button)findViewById(R.id.goBack);
        returnButton.setOnClickListener( new View.OnClickListener()
        {  public void onClick(View v){
            Intent nextActivity = new Intent(NewsActivity.this, MainActivity.class);
            startActivity(nextActivity);
        } });

    }

    /**
     * Used to load news from Firebase database
     */
    public static void loadNews() {
        // Connect with Firebase database
        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("News");

        referenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Retrieve each news from database and add to Arraylist
                for(DataSnapshot ds : snapshot.getChildren()){

                    String title = String.valueOf(ds.child("title").getValue());
                    String description = String.valueOf(ds.child("description").getValue());
                    String link = String.valueOf(ds.child("link").getValue());
                    String date = String.valueOf(ds.child("date").getValue());

                    allNews.add(new News(title, description, link, date));
                    Log.i("gycreport", title+" "+description+" "+link+" "+ allNews.size());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    /**
     * the adapter inner class that provide data for the listView
     */
    private class MyListAdapter extends BaseAdapter {

        /**
         * @return the number of items
         */
        public int getCount() {
            return allNews.size();
        }

        /**
         * @param position - the row position of a listView content
         * @return the object to show at row position
         */
        public Object getItem(int position) {
            return allNews.get(position);
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
            tView.setText("  "+n.getTitle().replace("_b",""));
            TextView tViewDate = newView.findViewById(R.id.newsDate);
            tViewDate.setText(n.getDate().replace("_b",""));
            return newView;
        }
    }
}
