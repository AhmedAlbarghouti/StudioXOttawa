package com.example.studioxottawa.staff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.studioxottawa.R;

import java.util.ArrayList;
import java.util.Arrays;

public class Report extends AppCompatActivity {
    private ArrayList<Integer> elements = new ArrayList<>( Arrays.asList( 1, 2 ) );
    private MyListAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        ListView myList = findViewById(R.id.ListyView);
        myList.setAdapter( myAdapter = new MyListAdapter());
        myList.setOnItemClickListener( (parent, view, pos, id) -> {

            elements.remove(pos);
            myAdapter.notifyDataSetChanged();
        }   );
             }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return elements.size();
        }

        public Object getItem(int position) {
            return (position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            View newView = inflater.inflate(R.layout.row_layout, parent, false);


            TextView tView = newView.findViewById(R.id.textGoesHere);
            String name = "Jane Mary "+(position+1);
            tView.setText(name);

            //return it to be put in the table
            return newView;
        }
    }
    }


