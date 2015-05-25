package org.fairytail.guessthesong.model;

import android.content.ContentUris;
import android.net.Uri;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(exclude = {"source"})
public class Song implements Serializable {

    private static final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");

    private String title;
    private String artist;
    private String album;
    private String lyrics;
    private String source;

    private long songId;
    private long artistId;
    private long albumId;

    private int duration;

    public String getDurationString() {
        int seconds = duration / 1000;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        return minutes+":"+ String.format("%02d", seconds);
    }

    public Uri getAlbumArtUri(){
        return ContentUris.withAppendedId(artworkUri, albumId);
    }

}
