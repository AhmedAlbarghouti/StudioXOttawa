package com.example.studioxottawa.VODPlayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.google.firebase.database.Exclude;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

//Video class containing header and modifier values to identify how it handles individual kinds of videos, to enable video filtering.
public class Video implements Parcelable {
    //This is a placeholder File server, to be replaced by a production file repository.
    //Further services can be set up by modifying headers below, and associating them with a new tab on the vod library page. Youtube Header is utilized to pull video data currently.
    //TODO - Set PREMIUM_HEADER and FREE_HEADER to the URL paths for free and premium videos on web server / data storage service
    public static final String PREMIUM_HEADER = "http://76.10.173.120:2355/videos/";
    public static final int PREMIUM_MODIFIER = 4;
    private int type;
    private String URL, title, UID;
    private Bitmap thumbnail;
    public static final int YOUTUBE_MODIFIER = 1;
    public static final String YOUTUBE_HEADER = "https://www.youtube.com/watch?v=";
    public static final String ZOOM_HEADER = ""; //Change to header of file repository containing zoom clips
    public static final int ZOOM_MODIFIER = 2; //Zoom currently unsupported, needs web repository of videos. No special loading instructions required
    public static final String FREE_HEADER = "http://76.10.173.120:2355/free/";
    public static final int FREE_MODIFIER = 3;

    //Required for Parcelable
    public Video() {

    }

    public Video(String title, String vidID, Bitmap image, int origin) {
        this.setTitle(title);
        setType(origin);
        setThumbnailImage(image);
        setURL(vidID);
        setUID(String.valueOf(UUID.randomUUID()));
    }

    public Video(Parcel in) {
        setTitle(in.readString());
        setURL(in.readString());
        String thumbnailString = in.readString();
        byte[] data = Base64.decode(thumbnailString, Base64.DEFAULT);
        setThumbnailImage(BitmapFactory.decodeByteArray(data, 0, data.length));
        setType(in.readInt());
        setUID(in.readString());
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    private void setUID(String UID) {
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    //Series of getters/setters to retreive and set values of a video
    public void setTitle(String title) { this.title = title; }

    public String getTitle() { return title; }

    public void setType(int type) { this.type = type; }

    public int getType() { return type; }

    public void setURL(String URL) {
            this.URL = URL;
    }

    public String getURL() {
        return URL;
    }

    //Used to append headers for various services to the video URLs.
    @Exclude
    public String getURLFull() {
        if (getType() == YOUTUBE_MODIFIER)
           return YOUTUBE_HEADER + URL;
        else if (getType() == ZOOM_MODIFIER)
            return URL;//Currently Unsupported, fall back to default case.
        else if (getType() == FREE_MODIFIER)
            return FREE_HEADER + URL;
        else if (getType() == PREMIUM_MODIFIER)
            return PREMIUM_HEADER + URL;
        else
            return URL;
    }

    public void setThumbnailImage(Bitmap thumbnail) { this.thumbnail = thumbnail; }

    //Required to handle importing encoded Strings from firebase
    public void setThumbnail(String thumbnailString) {
        if (thumbnailString != null) {
            byte[] data = Base64.decode(thumbnailString, Base64.DEFAULT);
            setThumbnailImage(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
        else {
            setThumbnailImage(null);
        }
    }

    //Returns the bitmap image, used for loading into UI
    @Exclude
    public Bitmap getThumbnailImage() { return thumbnail; }

    public String getThumbnail() {
        if (thumbnail != null) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            getThumbnailImage().compress(Bitmap.CompressFormat.JPEG, 100, bao);
            getThumbnailImage().recycle();
            byte[] thumbnailBytes = bao.toByteArray();
            return Base64.encodeToString(thumbnailBytes, Base64.DEFAULT);
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Creates a parcel with the data contained in the current video object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getURL());
        if (thumbnail != null) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            getThumbnailImage().compress(Bitmap.CompressFormat.JPEG,100, bao);
            getThumbnailImage().recycle();
            byte[] thumbnailBytes = bao.toByteArray();
            String base64Image = Base64.encodeToString(thumbnailBytes, Base64.DEFAULT);
            dest.writeString(base64Image);
        }
        dest.writeInt(getType());
        dest.writeString(getUID());
    }

}
