package com.example.studioxottawa.VODPlayer;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.example.studioxottawa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class VODLibraryFragment extends Fragment {
    ViewGroup root;
    ArrayList<Video> videoLibrary = new ArrayList<>();
    ArrayList<Video> currentPage = new ArrayList<>();
    ArrayList<Video> freeLibrary = new ArrayList<>();
    ArrayList<Video> premiumLibrary = new ArrayList<>();
    ArrayList<Video> youtubeLibrary = new ArrayList<>();
    videoAdapter adapter = new videoAdapter();
    private final String ACTIVITY_NAME = "VOD_LIBRARY_ACTIVITY";
    private int pageNumCounter = 1;
    private int currLibrary = -1;
    private final int PREMIUM_LIBRARY = 2;
    private final int YOUTUBE_LIBRARY = 1;
    private final int FREE_LIBRARY = 0;
    private TextView pageNum;
    private ImageButton nextPage;
    private ImageButton prevPage;
    //Checks User Authorization via Firebase to enable/disable video types as necessary
    private void authorize(Button premButton) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        String userID = user.getUid();

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
                DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference().child("Videos");

                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Long type = (Long)ds.child("type").getValue();
                            if (type == Video.PREMIUM_MODIFIER) {
                                Video currVid = ds.getValue(Video.class);
                                premiumLibrary.add(currVid);
                            }
                        }
                        videoLibrary.addAll(premiumLibrary);
                        currentPage.clear();
                        populate(1);
                        setPageNum(1);
                        pageNum.setText(String.valueOf(getPageNum()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                baseRef.addListenerForSingleValueEvent(eventListener);
            }
            else {
                //Adds the contents of premiumLibrary to videoLibrary, and populates the data from page 1
                videoLibrary.addAll(premiumLibrary);
                populate(1);

                //Updates the on-screen page number
                setPageNum(1);
                pageNum.setText(String.valueOf(getPageNum()));
            }
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

            if (videoLibrary.size() != 0) {
                videoLibrary.clear();
                currentPage.clear();
            }
            if (freeLibrary.size() == 0) {
                DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference().child("Videos");

                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Long type = (Long)ds.child("type").getValue();
                            if (type == Video.FREE_MODIFIER) {
                                Video currVid = ds.getValue(Video.class);
                                freeLibrary.add(currVid);
                            }
                        }
                        videoLibrary.addAll(freeLibrary);
                        currentPage.clear();
                        populate(1);
                        setPageNum(1);
                        pageNum.setText(String.valueOf(getPageNum()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                baseRef.addListenerForSingleValueEvent(eventListener);
            }
            else {
                //Adds all free videos to current library, populates page, and updates page number on-screen
                videoLibrary.addAll(freeLibrary);
                populate(1);
                setPageNum(1);
                pageNum.setText(String.valueOf(getPageNum()));
            }
            //Test Methods to disable page controls.
            if (freeLibrary.size() <= 10)
                disableNextPage();
            else
                enableNextPage();
            disablePrevPage();
        }
    }

    //Pulls youtube API data, and loads it into the on-screen library
    private void loadYoutubeLibrary() {
        //Does nothing if youtube library is already selected
        if (currLibrary != YOUTUBE_LIBRARY) {
            //Sets current library to youtube Library
            currLibrary = YOUTUBE_LIBRARY;

            //Clears current Library in preparation for new data to prevent merging
            if (videoLibrary.size() != 0) {
                videoLibrary.clear();
                currentPage.clear();
            }

            //Creates the connection to the API, and begins pulling data if the youtube library is empty.
            if (youtubeLibrary.size() == 0) {
                //connection = new YoutubeAPIConnector();
                //connection.execute();
                /*updater = new UpdateLibrary();
                updater.run();*/
                DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference().child("Videos");

                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Long type = (Long)ds.child("type").getValue();
                            if (type == Video.YOUTUBE_MODIFIER) {
                                Video currVid = ds.getValue(Video.class);
                                youtubeLibrary.add(currVid);
                            }
                        }
                        videoLibrary.addAll(youtubeLibrary);
                        currentPage.clear();
                        populate(1);
                        setPageNum(1);
                        pageNum.setText(String.valueOf(getPageNum()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                baseRef.addListenerForSingleValueEvent(eventListener);

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
                    if (((getPageNum()-1)*10 + adapter.getCount()) >= videoLibrary.size()) {
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

                if (getPageNum() == 1) {
                    enablePrevPage();
                }
                resetList();
                setPageNum(getPageNum()+1);
                populate(getPageNum());
                if (((getPageNum()-1)*10 + adapter.getCount()) >= videoLibrary.size()) {
                    disableNextPage();
                }
                pageNum.setText(String.valueOf(getPageNum()));
            });
        }
    }

    //Start point of activity. Sets up listeners for the menu, and loads the free videos to start.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.activity_vod_library,container,false);

        pageNum = root.findViewById(R.id.pageNum);
        prevPage = root.findViewById(R.id.prevPage);
        nextPage = root.findViewById(R.id.nextPage);
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
        nextPage.setAlpha((float) 1.0);
        nextPage.setClickable(true);
        nextPage.setEnabled(true);
    }

    //Method to dynamically enable the previous page button
    private void enablePrevPage() {
        prevPage.setAlpha((float) 1.0);
        prevPage.setClickable(true);
        prevPage.setEnabled(true);
    }

    //Method to dynamically disable the next page button
    private void disableNextPage() {

        nextPage.setAlpha((float) 0.3);
        nextPage.setClickable(false);
        nextPage.setEnabled(false);
    }
    //Method to dynamically disable the previous page button
    private void disablePrevPage() {

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
            if (currVideo.getThumbnailImage() != null) {
                iv.setImageBitmap(currVideo.getThumbnailImage());
            }
            newView.setOnClickListener(v -> {//Handler for when a video is clicked on
                if (currVideo.getType()== Video.YOUTUBE_MODIFIER) { //Runs the code to pull youtube video data, pass it through the encryption api and loads it to player
                    Log.d(ACTIVITY_NAME, "Starting extraction of youtube video URL");
                    Log.d(ACTIVITY_NAME, "URL to be extracted is: " + currVideo.getURLFull());
                    //connection.terminate();
                    new YouTubeExtractor(getActivity()) {
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
                    }.extract(currVideo.getURLFull(), false, false);

                }
                else if (currVideo.getType() == Video.FREE_MODIFIER) {//Handler for Free videos. No special handling required.
                    launchVOD(currVideo.getURLFull());
                }
                else if (currVideo.getType() == Video.PREMIUM_MODIFIER) {//Handler for premium videos. No special handling required.
                    launchVOD(currVideo.getURLFull());
                }
            });
            return newView;
        }


    }

    private void onExtractionFailed() {
        Toast playbackError = Toast.makeText(getActivity(), "A video playback error has occurred", Toast.LENGTH_SHORT);
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