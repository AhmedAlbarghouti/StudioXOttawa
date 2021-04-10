package com.example.studioxottawa.VODPlayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.studioxottawa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class VODLibraryFragment extends Fragment {
    YoutubeAPIConnector connection;
    ArrayList<Video> videoLibrary = new ArrayList<>();
    ArrayList<Video> currentPage = new ArrayList<>();
    ArrayList<Video> freeLibrary = new ArrayList<>();
    ArrayList<Video> premiumLibrary = new ArrayList<>();
    ArrayList<Video> youtubeLibrary = new ArrayList<>();
    videoAdapter adapter = new videoAdapter();
    private final String ACTIVITY_NAME = "VOD_LIBRARY_ACTIVITY";
    private String resumeToken = "";
    private int pageNumCounter = 1;
    private int currLibrary = -1;
    private final int PREMIUM_LIBRARY = 2;
    private final int YOUTUBE_LIBRARY = 1;
    private final int FREE_LIBRARY = 0;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    //Checks User Authorization via Firebase to enable/disable video types as necessary
    private void authorize(Button premButton) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("Premium Video Access (1 month)").exists()) {
                        premButton.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Method to load the Premium video library tab
    private void loadPremiumLibrary() {
       //Does nothing if already on the premium library tab
        if (currLibrary != PREMIUM_LIBRARY) {

            currLibrary = PREMIUM_LIBRARY;
            TextView pageNum = getActivity().findViewById(R.id.pageNum);
            //If the current video library page contains videos, wipe the data in preparation for new data
            if (videoLibrary.size() != 0) {
                videoLibrary.clear();
                currentPage.clear();
            }

            if (premiumLibrary.size() == 0) { //Test Section, adds videos if no videos are present, adds them to the premiumLibrary
                Video meditationVid = new Video("Deep Breathing Meditation", "breathing_meditation.mp4", BitmapFactory.decodeResource(getActivity().getBaseContext().getResources(),R.drawable.meditation), Video.PREMIUM_MODIFIER);
                Video lionsVid = new Video("Hearts & Colors: Lions", "hearts_and_colors_lions.mp4", BitmapFactory.decodeResource(getActivity().getBaseContext().getResources(),R.drawable.lions), Video.PREMIUM_MODIFIER);
                premiumLibrary.add(meditationVid);
                premiumLibrary.add(lionsVid);
            }

            //Adds the contents of premiumLibrary to videoLibrary, and populates the data from page 1
            videoLibrary.addAll(premiumLibrary);
            populate(1);

            //Updates the on-screen page number
            setPageNum(1);
            pageNum.setText(String.valueOf(getPageNum()));

            //Disables the next and previous page buttons. These are run here as test-methods, when populated with further videos it should handle this automatically based on library size
            disableNextPage();
            disablePrevPage();
        }
    }

    //Method to Load free videos
    private void loadFreeLibrary() {
        //Does nothing if already on Free Video tab
        if (currLibrary != FREE_LIBRARY) {
            currLibrary = FREE_LIBRARY;
            TextView pageNum = getActivity().findViewById(R.id.pageNum);
            if (videoLibrary.size() != 0) {
                videoLibrary.clear();
                currentPage.clear();
            }
            if (freeLibrary.size() == 0) {
                Video stretchingVid = new Video("Stretching Demo", "stretching.mp4", null, Video.FREE_MODIFIER);
                freeLibrary.add(stretchingVid);
            }

            //Adds all free videos to current library, populates page, and updates page number on-screen
            videoLibrary.addAll(freeLibrary);
            populate(1);
            setPageNum(1);
            pageNum.setText(String.valueOf(getPageNum()));

            //Test Methods to disable page controls.
            disableNextPage();
            disablePrevPage();
        }
    }

    //Pulls youtube API data, and loads it into the on-screen library
    private void loadYoutubeLibrary() {
        //Does nothing if youtube library is already selected
        if (currLibrary != YOUTUBE_LIBRARY) {
            //Sets current library to youtube Library
            currLibrary = YOUTUBE_LIBRARY;
            TextView pageNum = getActivity().findViewById(R.id.pageNum);
            ImageButton nextPage = getActivity().findViewById(R.id.nextPage);
            ImageButton prevPage = getActivity().findViewById(R.id.prevPage);

            //Clears current Library in preparation for new data to prevent merging
            if (videoLibrary.size() != 0) {
                videoLibrary.clear();
                currentPage.clear();
            }

            //Creates the connection to the API, and begins pulling data if the youtube library is empty.
            if (youtubeLibrary.size() == 0) {
                connection = new YoutubeAPIConnector();
                connection.execute();
            }
            else {//Youtube library is not empty. Pull data from it and populate the page.

                videoLibrary.addAll(youtubeLibrary);
                currentPage.clear();
                populate(1);
                setPageNum(1);
                pageNum.setText(String.valueOf(getPageNum()));
            }

            enableNextPage();//Ensures next button is enabled after loading data

            prevPage.setOnClickListener(v -> {//Handler for retrieving previous pages of youtube videos.
                if (getPageNum() != 1) {
                    if ((((getPageNum()-1)*10 + adapter.getCount()) >= videoLibrary.size()) && connection.getNextPageToken().isEmpty()) {
                        enableNextPage();
                    }
                    resetList();
                    setPageNum(getPageNum()-1);
                    populate(getPageNum());
                    pageNum.setText(String.valueOf(getPageNum()));
                    if (getPageNum() == 1) {
                        disablePrevPage();
                    }

                }
            });

            nextPage.setOnClickListener(v -> { //Handler for retrieving next pages of youtube videos.
                if (((getPageNum()-1)*10 + adapter.getCount()) >= videoLibrary.size()) {
                    if (connection.getStatus() != AsyncTask.Status.RUNNING) {
                        connection = new YoutubeAPIConnector();
                        resetList();
                        connection.execute(resumeToken);
                    }
                    else if (!connection.getNextPageToken().isEmpty()) {
                        resetList();
                        connection.runAgain();
                    }
                }
                else {
                    if (getPageNum() == 1) {
                        enablePrevPage();
                    }
                    resetList();
                    setPageNum(getPageNum()+1);
                    populate(getPageNum());
                    if ((((getPageNum()-1)*10 + adapter.getCount()) >= videoLibrary.size()) && connection.getNextPageToken().isEmpty()) {
                        disableNextPage();
                    }
                }
                pageNum.setText(String.valueOf(getPageNum()));
            });
        }
    }

    //Start point of activity. Sets up listeners for the menu, and loads the free videos to start.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_vod_library,container,false);


        ListView vodDisplay = root.findViewById(R.id.vod_list);
        vodDisplay.setAdapter(adapter);

        Button freeButton = root.findViewById(R.id.freeVidsButton);
        Button youtubeButton = root.findViewById(R.id.youtubeButton);
        Button premiumButton = root.findViewById(R.id.premiumVids);

        authorize(premiumButton);

        freeButton.setOnClickListener(v -> loadFreeLibrary());

        youtubeButton.setOnClickListener(v -> loadYoutubeLibrary());

        premiumButton.setOnClickListener(v -> loadPremiumLibrary());

        loadFreeLibrary();

        TextView pageNum = root.findViewById(R.id.pageNum);
        pageNum.setText(String.valueOf(getPageNum()));
        return root;
    }

    //Method to dynamically enable the next page button
    private void enableNextPage() {
        ImageButton nextPage = getActivity().findViewById(R.id.nextPage);

        nextPage.setAlpha((float) 1.0);
        nextPage.setClickable(true);
        nextPage.setEnabled(true);
    }

    //Method to dynamically enable the previous page button
    private void enablePrevPage() {
        ImageButton prevPage =  getActivity().findViewById(R.id.prevPage);

        prevPage.setAlpha((float) 1.0);
        prevPage.setClickable(true);
        prevPage.setEnabled(true);
    }

    //Method to dynamically disable the next page button
    private void disableNextPage() {
        ImageButton nextPage = getActivity().findViewById(R.id.nextPage);

        nextPage.setAlpha((float) 0.3);
        nextPage.setClickable(false);
        nextPage.setEnabled(false);
    }
    //Method to dynamically disable the previous page button
    private void disablePrevPage() {
        ImageButton prevPage =  getActivity().findViewById(R.id.prevPage);

        prevPage.setEnabled(false);
        prevPage.setClickable(false);
        prevPage.setAlpha((float)0.3);
    }

    //method to update page number
    private void setPageNum(int i) {
        pageNumCounter = i;
    }

    //method to retrieve page number
    private int getPageNum() {
        return pageNumCounter;
    }

    //Handles youtube API calls to retreive video data.
    //TODO: Refactor and move to an administrative task to populate database with video IDs, preventing api quota from being used up by regular users
    public class YoutubeAPIConnector extends AsyncTask<String, Integer, String> {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition tryAgain = lock.newCondition();
        private volatile boolean finished = false;
        private String nextPageToken = "";
        private static final String CHANNEL_ID = "UC4RbMe6c61zSWZ2kfvDVXaw"; //Channel ID for StudioX Ottawa youtube channel
        //private static final String API_KEY = "AIzaSyB5ITzudZxRCaveKEfE4XbZO6V0y2NWS-8"; //API key to access youtube search APIs.
        private static final String API_KEY = "AIzaSyBYFnzWHuWti9WT5SG3QJIfYxNtHKxIHic"; //Backup API key for testing in case of quota limit exceeding
        private static final String BASE_URL = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&channelId=";
        private static final String URL_MID = "&maxResults=50&order=date";
        private static final String TOKEN_URL = "&maxResults=50&order=date&pageToken=";
        private static final String URL_END = "&type=video&key=";


        Bitmap image = null;
        String vidId = "";
        String title = "";

        public String generateURL() {
            return BASE_URL + CHANNEL_ID + URL_MID + URL_END + API_KEY;
        }

        public String generateNextURL() {
            if (!getNextPageToken().isEmpty()) {
                setPageNum(getPageNum()+1);
                return BASE_URL + CHANNEL_ID + TOKEN_URL + nextPageToken + URL_END + API_KEY;
            }
            else {
                setPageNum(1);
                return generateURL();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (!getNextPageToken().isEmpty())
                runAgain();
        }

        //Handles the actual calls to the API in the background thread.
        @Override
        protected String doInBackground(String... args) {
            try {
                lock.lockInterruptibly();
                do {
                    String urlString;

                    if (getNextPageToken().isEmpty() && args.length != 0) {//Retreives the next token and generates a new URL for the API call.
                        nextPageToken = args[0];
                        urlString = generateNextURL();
                    }
                    else if (getNextPageToken().isEmpty())
                        urlString = generateURL(); //Generates the default URL for first call
                    else
                        urlString = generateNextURL(); //Default case for resuming

                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream queryResponse = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(queryResponse, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    String result = sb.toString();

                    JSONObject jObject = new JSONObject(result);

                    if (jObject.has("nextPageToken")) {
                        nextPageToken = jObject.getString("nextPageToken");
                    } else {
                        nextPageToken = "";
                    }

                    JSONArray videos = jObject.getJSONArray("items");

                    for (int i = 0; i < videos.length(); i++) {//Retrieves the current token's video IDs, titles and thumbnails
                        JSONObject currVid = videos.getJSONObject(i);
                        vidId = currVid.getJSONObject("id").getString("videoId");
                        title = currVid.getJSONObject("snippet").getString("title");
                        if (!fileExistance(vidId + ".jpg")) {
                            URL imagePath = new URL("https://i.ytimg.com/vi/" + vidId + "/default.jpg");
                            HttpURLConnection imageConnection = (HttpURLConnection) imagePath.openConnection();
                            imageConnection.connect();
                            int responseCode = imageConnection.getResponseCode();
                            if (responseCode == 200) {
                                image = BitmapFactory.decodeStream(imageConnection.getInputStream());
                            }

                            FileOutputStream outputStream = getActivity().openFileOutput(vidId + ".jpg", Context.MODE_PRIVATE);
                            image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } else {
                            FileInputStream fis = null;
                            try {
                                fis = getActivity().openFileInput(vidId + ".jpg");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            image = BitmapFactory.decodeStream(fis);
                            fis.close();
                        }
                        Video newVideo = new Video(title, vidId, image, Video.YOUTUBE_MODIFIER);
                        videoLibrary.add(newVideo);
                        youtubeLibrary.add(newVideo);
                        if (i <= 10)
                            currentPage.add(newVideo); //Adds first 10 results to the current page listing
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        //Update the library dataset, page number and next button opacity.
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            TextView pageNumView = getActivity().findViewById(R.id.pageNum);
                            pageNumView.setText(String.valueOf(getPageNum()));

                        }
                    });
                    if (getNextPageToken().isEmpty())
                        finished = true;
                    else
                        tryAgain.await();
                } while (!finished);
            }
            catch (Exception e) {
                Log.e("YoutubeAPIConnector", e.getMessage());
            } finally{
                lock.unlock();
            }
            return "Done.";
        }

        public boolean fileExistance(String fname){
            File file = getActivity().getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        public void runAgain() {
            // Call this to request data from the server again
            lock.lock();
            try {
                tryAgain.signal();
            } finally {
                lock.unlock();
            }
        }

        @Override
        protected void onCancelled() {
            // Make sure we clean up if the task is killed
            lock.lock();
            try {
                if (!getNextPageToken().isEmpty()) {
                }
                else {
                    resumeToken = getNextPageToken();
                }
                finished = true;

            } finally {
                lock.unlock();
            }
        }

        public void terminate() {
            lock.lock();
            try {
                if (!getNextPageToken().isEmpty())
                    resumeToken = getNextPageToken();
                finished = true;
                tryAgain.signal();
            } finally {
                lock.unlock();
            }
        }

        public String getNextPageToken() { return nextPageToken; }
    }

    //Class to keep track of currently loaded videos, and to pass video data to the player
    private class videoAdapter extends BaseAdapter {
        /**
         * Returns how many videos are currently in the list
         * @return number of entries in videoLibrary
         */
        @Override
        public int getCount() {
            return currentPage.size();
        }

        /**
         * returns the item in the given position
         * @param position Which index to access
         * @return The object found at the given index
         */
        @Override
        public Object getItem(int position) {
            return currentPage.get(position);
        }

        /**
         * Required override of BaseAdapter getItemId
         * @param position the index to access
         * @return the database id of the object found at the given index.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Populates the ListView with data and creates a listener for the entities inside it.
         * @param position the current position of the adapter being populated
         * @param convertView The old view to reuse, if possible.
         * @param parent the parent to attach this view to.
         * @return Returns the view created with the given data
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView;
            TextView tv;
            ImageView iv;
            LayoutInflater inflater = getLayoutInflater();
            Video currVideo = (Video) getItem(position);
            newView = inflater.inflate(R.layout.video_info, parent, false);
            tv = newView.findViewById(R.id.video_name);
            iv = newView.findViewById(R.id.thumbnail_box);
            tv.setText(currVideo.getTitle());
            if (currVideo.getThumbnail() != null) {
                iv.setImageBitmap(currVideo.getThumbnail());
            }
            newView.setOnClickListener(v -> {//Handler for when a video is clicked on
                if (currVideo.getType()== Video.YOUTUBE_MODIFIER) { //Runs the code to pull youtube video data, pass it through the encryption api and loads it to player
                    Log.d(ACTIVITY_NAME, "Starting extraction of youtube video URL");
                    Log.d(ACTIVITY_NAME, "URL to be extracted is: "+currVideo.getURL());
                    connection.terminate();
                    new YouTubeExtractor(getActivity().getBaseContext()) {
                        @Override
                        public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                            if (ytFiles != null) {
                                int itag22 = 22, itag18 = 18;
                                YtFile extracted = ytFiles.get(itag22);
                                if (extracted != null) {
                                    Log.d(ACTIVITY_NAME, "The URL is " + ytFiles.get(itag22).getUrl());
                                    launchVOD(ytFiles.get(itag22).getUrl());
                                }
                                extracted = ytFiles.get(itag18);
                                if (extracted != null) {
                                    Log.d(ACTIVITY_NAME, "The URL is " + ytFiles.get(itag18).getUrl());
                                    launchVOD(ytFiles.get(itag18).getUrl());
                                }
                                else{onExtractionFailed();}

                            }
                        }
                    }.extract(currVideo.getURL(), false, false);

                }
                else if (currVideo.getType() == Video.FREE_MODIFIER) {//Handler for Free videos. No special handling required.
                    launchVOD(currVideo.getURL());
                }
                else if (currVideo.getType() == Video.PREMIUM_MODIFIER) {//Handler for premium videos. No special handling required.
                    launchVOD(currVideo.getURL());
                }

                //TODO Convert the URL as appropriate and commence exoplayer playback in VODActivity
            });
            return newView;
        }


    }

    private void onExtractionFailed() {
        Toast playbackError = Toast.makeText(getActivity().getBaseContext(), "A video playback error has occurred", Toast.LENGTH_SHORT);
        playbackError.show();
        Log.e(ACTIVITY_NAME, "A video playback error has occurred");
    }

    //Launches the video player with the desired video URI
    private void launchVOD(String url) {
        Log.d(ACTIVITY_NAME, "Launching VOD");
        Intent vodPlayback = new Intent(getActivity(), VODActivity.class);
        vodPlayback.putExtra("playbackURI", url);
        startActivity(vodPlayback);
    }

    //Method to add videos to the current page from the library.
    public void populate(int pageNum) {
        if (videoLibrary.size() >= pageNum*10) {
            for (int i = 0; i < 10; i++) {
                currentPage.add(videoLibrary.get(i + (pageNum - 1) * 10));
            }
        }
        else {
            for (int i = 0; i < videoLibrary.size()%10; i++) {
                currentPage.add(videoLibrary.get(i + (pageNum - 1) * 10));
            }
        }
        adapter.notifyDataSetChanged();
    }

    //Clears the current page of data.
    public void resetList() {
        currentPage.clear();
        adapter.notifyDataSetChanged();
    }

    //Populates the current page on returning to the activity
    @Override
    public void onResume() {
        super.onResume();

            resetList();
            populate(getPageNum());
    }
}