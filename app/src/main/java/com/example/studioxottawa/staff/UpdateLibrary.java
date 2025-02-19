package com.example.studioxottawa.staff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.studioxottawa.R;
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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


    //Handles youtube API calls to retreive video data.
    public class UpdateLibrary extends AsyncTask<String, Integer, String> {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition tryAgain = lock.newCondition();
        private volatile boolean finished = false;
        private String nextPageToken = "";
        private Boolean exists = false, start = false;
        ArrayList<String> idList;
        String successMessage = "";
        Context toastContext;
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

        public UpdateLibrary(Context c) {
            toastContext = c;
        }

        public void run(String success) {

            successMessage = success;
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
                //Gets a list of video ids for Youtube videos in the database to prevent repeat calls to the firebase connection
                idList = new ArrayList<>();
                DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference().child("Videos");
                baseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Video currVid = ds.getValue(Video.class);
                            if (currVid.getType()==Video.YOUTUBE_MODIFIER)
                                idList.add(currVid.getURL());//Adds the video ID to the ArrayList
                        }
                        start = true; //Signals for the Async Task to contact the youtube API and begin parsing data
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Wait for firebase to populate the ID list before beginning calling the youtube API.
                while (!start);
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

                    //Creates a new URL, connects to that URL, and generates a JSONObject from the results of the connection containing the video data in the query
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

                    //Stores the token used to get the next set of results in a subsequent query
                    if (jObject.has("nextPageToken")) {
                        nextPageToken = jObject.getString("nextPageToken");
                    } else {
                        nextPageToken = "";
                    }

                    JSONArray videos = jObject.getJSONArray("items");

                    for (int i = 0; i < videos.length(); i++) {//Retrieves the current token's video IDs, titles and thumbnails
                            exists = false;
                            JSONObject currVid = videos.getJSONObject(i);
                            vidId = currVid.getJSONObject("id").getString("videoId");
                            title = currVid.getJSONObject("snippet").getString("title");

                            //Generate a bitmap of the video thumbnail
                            URL imagePath = new URL("https://i.ytimg.com/vi/" + vidId + "/default.jpg");
                            HttpURLConnection imageConnection = (HttpURLConnection) imagePath.openConnection();
                            imageConnection.connect();
                            int responseCode = imageConnection.getResponseCode();
                            if (responseCode == 200) {
                                image = BitmapFactory.decodeStream(imageConnection.getInputStream());
                            }
                            //Check if the video is already in the DB from the populated ID list
                            if (idList.contains(vidId))
                                setExists();//Video found, toggle the exists controller
                            if (!exists) {
                                try {//Video does not exist. Create a new Video with the parsed data, and pass it to Firebase to store
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
                    }
                    if (getNextPageToken().isEmpty())//Toggle finished once it reaches the end of an iteration and has no nextPageToken
                        finished = true;
                } while (!finished); //Controller to re-iterate as long as there's further query pages
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

        //Create a success toast to alert the user it updated.
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CharSequence success = successMessage;
            Toast.makeText(toastContext, success, Toast.LENGTH_LONG);

        }
    }

