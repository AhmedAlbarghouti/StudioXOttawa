package com.example.studioxottawa.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.studioxottawa.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


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
    private String url;
    private ImageView imageView;

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

        // Set text Strings for contents
        TextView text = (TextView)result.findViewById(R.id.FragmentText);
        text.setText(getText);

        TextView title = (TextView)result.findViewById(R.id.FragmentTitle);
        title.setText(dataFromActivity.getString(NewsActivity.NEWS_TITLE).replace("_b","\n"));

        TextView description = (TextView)result.findViewById(R.id.FragmentDescription);
        description.setText(dataFromActivity.getString(NewsActivity.NEWS_DESCRIPTION).replace("_b","\n"));

        url = dataFromActivity.getString(NewsActivity.NEWS_LINK);
        imageView = (ImageView)result.findViewById(R.id.FragmentImage);
        new ImageLoadTask(url, imageView).execute();

        TextView date = (TextView)result.findViewById(R.id.FragmentDate);
        date.setText(dataFromActivity.getString(NewsActivity.NEWS_DATE).replace("_b","\n"));

        Button hideButton = (Button)result.findViewById(R.id.hideButton);
        hideButton.setOnClickListener( clk -> {
            getActivity().onBackPressed();

            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            getActivity().onBackPressed();
        });

        return result;
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

    /**
     * Inner class used to load images from website
     */
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        // Load the image in background
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}


