package com.example.studioxottawa.news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studioxottawa.R;
import com.example.studioxottawa.welcome.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewsFragment extends Fragment {
    private static ArrayList<News> allNews = new ArrayList<>();
    public static long maxNewsID;
    public MyListAdapter myAdapter = new MyListAdapter();

    public static final String NEWS_TITLE = "TITLE";
    public static final String NEWS_DESCRIPTION = "DESCRIPTION";
    public static final String NEWS_LINK = "LINK";
    public static final String NEWS_DATE = "DATE";
    public static final String NEWS_POSITION = "POSITION";
    public static final String NEWS_ID = "ID";
    private static final String TAG = "GetData";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_news,container,false);

        Log.i("gycreport", "MyList Ready");
        ListView myList = (ListView) root.findViewById(R.id.newsListView);
        loadNews();
        myList.setAdapter( myAdapter );

        myList.setOnItemClickListener( (list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            int index = allNews.size()-position-1;
            dataToPass.putString(NEWS_TITLE, allNews.get(index).getTitle() );
            dataToPass.putString(NEWS_DESCRIPTION, allNews.get(index).getDescription() );
            dataToPass.putString(NEWS_LINK, allNews.get(index).getLink() );
            dataToPass.putString(NEWS_DATE, allNews.get(index).getDate() );

            dataToPass.putInt(NEWS_POSITION, position);
            dataToPass.putLong(NEWS_ID, id);


            Intent nextActivity = new Intent(getActivity(), EmptyActivity.class);
            nextActivity.putExtras(dataToPass); //send data to next activity
            startActivity(nextActivity); //make the transition


            myAdapter.notifyDataSetChanged();
        }   );


        return root;
    }

    public void loadNews() {
        // Connect with Firebase database
        DatabaseReference referenceEvents = FirebaseDatabase.getInstance().getReference().child("News");

        referenceEvents.orderByChild("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Refresh news contents
                allNews.clear();

                // Retrieve each news from database and add to Arraylist
                for(DataSnapshot ds : snapshot.getChildren()){

                    String title = String.valueOf(ds.child("title").getValue());
                    String description = String.valueOf(ds.child("description").getValue());
                    String link = String.valueOf(ds.child("link").getValue());
                    String date = String.valueOf(ds.child("date").getValue());
                    long id = 0;
                    if(ds.child("id").getValue() != null){
                        id = (Long)(ds.child("id").getValue());
                    }
                    maxNewsID = id;
                    Log.i("newsID", "newsID is "+id+" maxID is "+maxNewsID);
                    Log.i("link", "link is empty?"+(link.isEmpty()));
                    allNews.add(new News(title, description, link, date, id));
                    Log.i("gycreport", title+" "+description+" "+link+" "+ allNews.size());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        myAdapter.notifyDataSetChanged();
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
            return allNews.get(allNews.size()-position-1);
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
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.row_layout_news, parent, false);
//            News n = (News)getItem(position);
            News n = (News)getItem(position);
            TextView tView = newView.findViewById(R.id.newsTitle);
            tView.setText("  "+n.getTitle().replace("_b",""));
            TextView tViewDate = newView.findViewById(R.id.newsDate);
            tViewDate.setText("  "+n.getDate().replace("_b",""));
            return newView;
        }
    }
}


