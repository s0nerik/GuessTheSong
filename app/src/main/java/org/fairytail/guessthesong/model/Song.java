package org.fairytail.guessthesong.model;

import android.content.ContentUris;
import android.net.Uri;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonObject
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"source"})
public class Song implements Serializable {

    private static final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");

    @JsonField
    String title;
    @JsonField
    String artist;
    @JsonField
    String album;
    @JsonField
    String lyrics;
    @JsonField
    String source;
    @JsonField
    String remoteSource;

    private long songId;
    private long artistId;
    private long albumId;

    @JsonField
    int duration;

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
