package com.example.studioxottawa.staff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studioxottawa.VODPlayer.Video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


    //Handles youtube API calls to retreive video data.
    //TODO: Refactor and move to an administrative task to populate database with video IDs, preventing api quota from being used up by regular users
    public class UpdateLibrary extends AsyncTask<String, Integer, String> {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition tryAgain = lock.newCondition();
        private volatile boolean finished = false;
        private String nextPageToken = "";
        private Boolean exists = false, next = false;
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

        public void run() {
            this.execute();
        }

        public String generateURL() {
            return BASE_URL + CHANNEL_ID + URL_MID + URL_END + API_KEY;
        }

        public String generateNextURL() {
            if (!getNextPageToken().isEmpty()) {
                return BASE_URL + CHANNEL_ID + TOKEN_URL + nextPageToken + URL_END + API_KEY;
            }
            else {
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
                    BufferedReader reader = new BufferedReader(new InputStreamReader(queryResponse, StandardCharsets.UTF_8), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    String result = sb.toString();

                    JSONObject jObject = new JSONObject(result);

                    if (jObject.has("nextPageToken")) {
                        nextPageToken = jObject.getString("nextPageToken");
                    } else {
                        nextPageToken = "";
                    }

                    JSONArray videos = jObject.getJSONArray("items");
                    Log.d("DEBUG - VIDEOS COUNT", "Videos has " + videos.length() + " Elements");

                    DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference().child("Videos");
                    for (int i = 0; i < videos.length(); i++) {//Retrieves the current token's video IDs, titles and thumbnails
                            exists = false;
                            next = false;
                            JSONObject currVid = videos.getJSONObject(i);
                            vidId = currVid.getJSONObject("id").getString("videoId");
                            title = currVid.getJSONObject("snippet").getString("title");

                            URL imagePath = new URL("https://i.ytimg.com/vi/" + vidId + "/default.jpg");
                            HttpURLConnection imageConnection = (HttpURLConnection) imagePath.openConnection();
                            imageConnection.connect();
                            int responseCode = imageConnection.getResponseCode();
                            if (responseCode == 200) {
                                image = BitmapFactory.decodeStream(imageConnection.getInputStream());
                            }

                            baseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Video currVid = ds.getValue(Video.class);
                                        if (currVid.getURL().equals(vidId))
                                            setExists();
                                    }
                                    if (!exists) {
                                        try {
                                            Video newVideo = new Video(title, vidId, image, Video.YOUTUBE_MODIFIER);
                                            DatabaseReference videosReference = FirebaseDatabase.getInstance().getReference().child("Videos");

                                            videosReference.child(newVideo.getUID()).setValue(newVideo);
                                        } catch (Exception e) {
                                            if (e != null) {
                                                Log.d("DEBUG - ", "E is not null");
                                                e.printStackTrace();
                                                if (e.getMessage() != null)
                                                    Log.e("YoutubeAPIConnector", e.getMessage());
                                            }
                                        }
                                    }
                                    next = true;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            while (!next);
                    }
                        if (getNextPageToken().isEmpty())
                            finished = true;
                } while (!finished);
            }
            catch (Exception e) {
                Log.e("YoutubeAPIConnector", e.getMessage());
            } finally{
                lock.unlock();
            }
            return "Done.";
        }

        private void setExists() {
            exists = true;
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
                finished = true;

            } finally {
                lock.unlock();
            }
        }

        public String getNextPageToken() { return nextPageToken; }
    }

