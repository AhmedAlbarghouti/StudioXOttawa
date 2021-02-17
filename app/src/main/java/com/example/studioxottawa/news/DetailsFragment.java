package com.example.studioxottawa.news;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.studioxottawa.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    /**
     *Required empty public constructor
     */
    public DetailsFragment() {}

    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;
    private String getText;

    /**
     * @param inflater - instantiates layout XML file into its corresponding view object
     * @param container - contains other views, describes the layout of the Views in the group
     * @param savedInstanceState - reference to a Bundle object passed into the onCreateView method
     * @return the view created inflated by the method
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(NewsActivity.NEWS_ID );

        View result =  inflater.inflate(R.layout.fragment_details_news, container, false);

        Thread thread = new TestThread();
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        TextView text = (TextView)result.findViewById(R.id.FragmentText);
        text.setText(getText);

        TextView title = (TextView)result.findViewById(R.id.FragmentTitle);
        title.setText("TITLE: " +dataFromActivity.getString(NewsActivity.NEWS_TITLE));

        TextView description = (TextView)result.findViewById(R.id.FragmentDescription);
        description.setText("DESCRIPTION: " +dataFromActivity.getString(NewsActivity.NEWS_DESCRIPTION));

        TextView link = (TextView)result.findViewById(R.id.FragmentLink);
        link.setText("LINK: " +dataFromActivity.getString(NewsActivity.NEWS_LINK));

        TextView date = (TextView)result.findViewById(R.id.FragmentDate);
        date.setText("DATE: " +dataFromActivity.getString(NewsActivity.NEWS_DATE));

        Button hideButton = (Button)result.findViewById(R.id.hideButton);
        hideButton.setOnClickListener( clk -> {
            getActivity().onBackPressed();

            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            getActivity().onBackPressed();
        });

        return result;
    }

    /**
     * inner class that deals with thread synchronization
     */
    private class TestThread extends Thread{

        public void run(){
            String html;

            String url = dataFromActivity.getString(NewsActivity.NEWS_LINK);
            html = OkHttpUtils.OkGetArt(url);

            getText = GetText.spiderArticle(html);

        }
    }

    /**
     * callback function, called when the fragment is added to the Activity
     * @param context - the current context activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentActivity = (AppCompatActivity)context;
    }
}
