package com.example.studioxottawa.VODPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.studioxottawa.R;
import com.example.studioxottawa.welcome.User;
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

public class VODLibraryActivity extends AppCompatActivity {
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

    private void authorize(Button premButton) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User signInUser = snapshot.getValue(User.class);
                Boolean isStaff = signInUser.staff;
                premButton.setEnabled(isStaff);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPremiumLibrary() {
        if (currLibrary != PREMIUM_LIBRARY) {
            currLibrary = PREMIUM_LIBRARY;
            TextView pageNum = findViewById(R.id.pageNum);
            if (videoLibrary.size() != 0) {
                videoLibrary.clear();
                currentPage.clear();
            }
            if (premiumLibrary.size() == 0) {
                Video meditationVid = new Video("Deep Breathing Meditation", "breathing_meditation.mp4", BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.meditation), Video.PREMIUM_MODIFIER);
                Video lionsVid = new Video("Hearts & Colors: Lions", "hearts_and_colors_lions.mp4", BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.lions), Video.PREMIUM_MODIFIER);
                premiumLibrary.add(meditationVid);
                premiumLibrary.add(lionsVid);
            }
            videoLibrary.clear();
            videoLibrary.addAll(premiumLibrary);
            currentPage.clear();
            populate(1);

            setPageNum(1);
            pageNum.setText(String.valueOf(getPageNum()));

            disableNextPage();
            disablePrevPage();
        }
    }

    private void loadFreeLibrary() {
        if (currLibrary != FREE_LIBRARY) {
            currLibrary = FREE_LIBRARY;
            TextView pageNum = findViewById(R.id.pageNum);
            if (videoLibrary.size() != 0) {
                videoLibrary.clear();
                currentPage.clear();
            }
            if (freeLibrary.size() == 0) {
                Video stretchingVid = new Video("Stretching Demo", "video.mp4", null, Video.FREE_MODIFIER);
                freeLibrary.add(stretchingVid);
            }
            videoLibrary.clear();
            videoLibrary.addAll(freeLibrary);
            currentPage.clear();
            populate(1);

            setPageNum(1);
            pageNum.setText(String.valueOf(getPageNum()));

            disableNextPage();
            disablePrevPage();
        }
    }

    private void loadYoutubeLibrary() {
        if (currLibrary != YOUTUBE_LIBRARY) {
            currLibrary = YOUTUBE_LIBRARY;
            TextView pageNum = findViewById(R.id.pageNum);
            ImageButton nextPage = findViewById(R.id.nextPage);
            ImageButton prevPage = findViewById(R.id.prevPage);

            if (videoLibrary.size() != 0) {
                videoLibrary.clear();
                currentPage.clear();
            }

            if (youtubeLibrary.size() == 0) {
                connection = new YoutubeAPIConnector();
                connection.execute();
            }
            else {

                videoLibrary.addAll(youtubeLibrary);
                currentPage.clear();
                populate(1);
                setPageNum(1);
                pageNum.setText(String.valueOf(getPageNum()));
            }

            enableNextPage();

            prevPage.setOnClickListener(v -> {
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

            nextPage.setOnClickListener(v -> {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_library);


        ListView vodDisplay = findViewById(R.id.vod_list);
        vodDisplay.setAdapter(adapter);

        Button freeButton = findViewById(R.id.freeVidsButton);
        Button youtubeButton = findViewById(R.id.youtubeButton);
        Button premiumButton = findViewById(R.id.premiumVids);

        authorize(premiumButton);

        freeButton.setOnClickListener(v -> loadFreeLibrary());

        youtubeButton.setOnClickListener(v -> loadYoutubeLibrary());

        premiumButton.setOnClickListener(v -> loadPremiumLibrary());

        loadFreeLibrary();

        TextView pageNum = findViewById(R.id.pageNum);
        pageNum.setText(String.valueOf(getPageNum()));
    }

    private void enableNextPage() {
        ImageButton nextPage = findViewById(R.id.nextPage);

        nextPage.setAlpha((float) 1.0);
        nextPage.setClickable(true);
        nextPage.setEnabled(true);
    }

    private void enablePrevPage() {
        ImageButton prevPage =  findViewById(R.id.prevPage);

        prevPage.setAlpha((float) 1.0);
        prevPage.setClickable(true);
        prevPage.setEnabled(true);
    }

    private void disableNextPage() {
        ImageButton nextPage = findViewById(R.id.nextPage);

        nextPage.setAlpha((float) 0.3);
        nextPage.setClickable(false);
        nextPage.setEnabled(false);
    }

    private void disablePrevPage() {
        ImageButton prevPage =  findViewById(R.id.prevPage);

        prevPage.setEnabled(false);
        prevPage.setClickable(false);
        prevPage.setAlpha((float)0.3);
    }

    private void setPageNum(int i) {
        pageNumCounter = i;
    }

    private int getPageNum() {
        return pageNumCounter;
    }

    public class Video {
        public static final String PREMIUM_HEADER = "http://76.10.173.120:2355/videos/"; //This is a WIP needs file server setup
        public static final int PREMIUM_MODIFIER = 4;
        private int type;
        private String URL, title;
        private Bitmap thumbnail;
        private static final int YOUTUBE_MODIFIER = 1;
        private static final String YOUTUBE_HEADER = "https://www.youtube.com/watch?v=";
        private static final int ZOOM_MODIFIER = 2; //Zoom currently unsupported, needs web repository of videos. No special loading instructions required
        private static final String FREE_HEADER = "https://www.wellnessliving.com/a/drive-download/V2xcVmlkZW9cVmlkZW9GaWxlOjo4NTQ4OTo6M2J2Q0Uzcg%3D%3D/";
        private static final int FREE_MODIFIER = 3;

        public Video(String title, String vidID, Bitmap image, int origin) {
            this.setTitle(title);
            setType(origin);
            setThumbnail(image);
            setURL(vidID);
        }

        public void setTitle(String title) { this.title = title; }

        public String getTitle() { return title; }

        public void setType(int type) { this.type = type; }

        public int getType() { return type; }

        public void setURL(String URL) {
            if (getType() == YOUTUBE_MODIFIER)
                this.URL = YOUTUBE_HEADER + URL;
            else if (getType() == ZOOM_MODIFIER)
                this.URL = URL;//Currently Unsupported, fall back to default case.
            else if (getType() == FREE_MODIFIER)
                this.URL = FREE_HEADER + URL;
            else if (getType() == PREMIUM_MODIFIER)
                this.URL = PREMIUM_HEADER + URL;
            else
                this.URL = URL;
        }

        public String getURL() { return URL; }

        public void setThumbnail(Bitmap thumbnail) { this.thumbnail = thumbnail; }

        public Bitmap getThumbnail() { return thumbnail; }
    }

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

        @Override
        protected String doInBackground(String... args) {
            try {
                lock.lockInterruptibly();
                do {
                    String urlString;

                    if (getNextPageToken().isEmpty() && args.length != 0) {
                        nextPageToken = args[0];
                        urlString = generateNextURL();
                    }
                    else if (getNextPageToken().isEmpty())
                        urlString = generateURL();
                    else
                        urlString = generateNextURL();

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

                    for (int i = 0; i < videos.length(); i++) {
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

                            FileOutputStream outputStream = openFileOutput(vidId + ".jpg", Context.MODE_PRIVATE);
                            image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } else {
                            FileInputStream fis = null;
                            try {
                                fis = openFileInput(vidId + ".jpg");
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
                    runOnUiThread(new Runnable() {
                        //Update the library dataset, page number and next button opacity.
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            TextView pageNumView = findViewById(R.id.pageNum);
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
            File file = getBaseContext().getFileStreamPath(fname);
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
            newView.setOnClickListener(v -> {
                if (currVideo.getType()==Video.YOUTUBE_MODIFIER) {
                    Log.d(ACTIVITY_NAME, "Starting extraction of youtube video URL");
                    Log.d(ACTIVITY_NAME, "URL to be extracted is: "+currVideo.getURL());
                    connection.terminate();
                    new YouTubeExtractor(getBaseContext()) {
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
                else if (currVideo.getType() == Video.FREE_MODIFIER) {
                    launchVOD(currVideo.getURL());
                }
                else if (currVideo.getType() == Video.PREMIUM_MODIFIER) {
                    launchVOD(currVideo.getURL());
                }

                //TODO Convert the URL as appropriate and commence exoplayer playback in VODActivity
            });
            return newView;
        }


    }

    private void onExtractionFailed() {
        Toast playbackError = Toast.makeText(getBaseContext(), "A video playback error has occurred", Toast.LENGTH_SHORT);
        playbackError.show();
        Log.e(ACTIVITY_NAME, "A video playback error has occurred");
    }

    private void launchVOD(String url) {
        Log.d(ACTIVITY_NAME, "Launching VOD");
        Intent vodPlayback = new Intent(VODLibraryActivity.this, VODActivity.class);
        vodPlayback.putExtra("playbackURI", url);
        startActivity(vodPlayback);
    }

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

    public void resetList() {
        currentPage.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

            resetList();
            populate(getPageNum());
    }
}