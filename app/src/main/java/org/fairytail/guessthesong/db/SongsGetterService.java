package org.fairytail.guessthesong.db;

import android.database.Cursor;

import com.joanzapata.android.asyncservice.api.annotation.AsyncService;

import org.fairytail.guessthesong.model.Song;

import java.util.ArrayList;
import java.util.List;

@AsyncService
public class SongsGetterService {

    public List<Song> getAllSongs(Order order) {
        Cursor cursor = new SongsCursorGetter().getSongsCursor(order);
        List<Song> songs = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    songs.add(
                            Song.builder()
                                .songId(cursor.getLong(SongsCursorGetter._ID))
                                .artistId(cursor.getLong(SongsCursorGetter.ARTIST_ID))
                                .albumId(cursor.getLong(SongsCursorGetter.ALBUM_ID))
                                .title(cursor.getString(SongsCursorGetter.TITLE))
                                .artist(cursor.getString(SongsCursorGetter.ARTIST))
                                .album(cursor.getString(SongsCursorGetter.ALBUM))
                                .source(cursor.getString(SongsCursorGetter.DATA))
                                .duration(cursor.getInt(SongsCursorGetter.DURATION))
                                .build()
                    );
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return songs;
    }
}
