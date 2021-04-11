package com.example.studioxottawa;

import com.example.studioxottawa.VODPlayer.Video;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class VideoTest {
    String vidId = "hearts_and_colors_lions.mp4";
    String title = "Hearts and Colors: Lions";
    int type = Video.PREMIUM_MODIFIER;

    //Tests Creating a new video object without any parameters for Firebase to utilize
    @Test
    public void testCreateVideo() {
        Video newVid = new Video();
        assertNotNull(newVid);
    }

    //Tests creating a video object with parameters and that all fields are properly set and retreived
    @Test
    public void testCreateVideoParameterized() {
        Video newVid = new Video(title, vidId, null, type);
        assertNotNull(newVid);
        assertEquals(title, newVid.getTitle());
        assertEquals(vidId, newVid.getURL());
        assertEquals(type, newVid.getType());
    }

    //Tests Creating a video object, and rewriting the parameters to ensure they are correctly updated.
    @Test
    public void testChangeVideoParameters() {
        String newTitle = "Title 2";
        String newVidId = "Meditation.mp4";
        int newType = Video.FREE_MODIFIER;
        Video newVid = new Video(title, vidId, null, type);
        newVid.setTitle(newTitle);
        newVid.setURL(newVidId);
        newVid.setType(newType);

        assertNotEquals(vidId, newVid.getURL());
        assertNotEquals(title, newVid.getTitle());
        assertNotEquals(type, newVid.getType());

        assertEquals(newTitle, newVid.getTitle());
        assertEquals(newVidId, newVid.getURL());
        assertEquals(newType, newVid.getType());

    }
}
