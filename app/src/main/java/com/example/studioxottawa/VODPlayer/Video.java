package com.example.studioxottawa.VODPlayer;

import android.graphics.Bitmap;

//Video class containing header and modifier values to identify how it handles individual kinds of videos, to enable video filtering.
public class Video {
    //This is a placeholder File server, to be replaced by a production file repository.
    //Further services can be set up by modifying headers below. Youtube Header is utilized to pull video data currently.
    protected static final String PREMIUM_HEADER = "http://76.10.173.120:2355/videos/";
    protected static final int PREMIUM_MODIFIER = 4;
    private int type;
    private String URL, title;
    private Bitmap thumbnail;
    protected static final int YOUTUBE_MODIFIER = 1;
    protected static final String YOUTUBE_HEADER = "https://www.youtube.com/watch?v=";
    protected static final String ZOOM_HEADER = ""; //Change to header of file repository containing zoom clips
    protected static final int ZOOM_MODIFIER = 2; //Zoom currently unsupported, needs web repository of videos. No special loading instructions required
    protected static final String FREE_HEADER = "http://76.10.173.120:2355/free/";
    protected static final int FREE_MODIFIER = 3;

    public Video(String title, String vidID, Bitmap image, int origin) {
        this.setTitle(title);
        setType(origin);
        setThumbnail(image);
        setURL(vidID);
    }

    //Series of getters/setters to retreive and set values of a video
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
