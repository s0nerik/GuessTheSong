package org.fairytail.guessthesong.networking.http;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.model.Song;
import org.fairytail.guessthesong.player.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import fi.iki.elonen.NanoHTTPD;

public class StreamServer extends NanoHTTPD {

    @Inject
    Player player;

    @Inject
    Context context;

    public static final String PORT = "8888";

    public interface Method {
        String CURRENT_INFO = "/info";
        String CURRENT_ALBUMART = "/albumart";
        String STREAM = "/stream";
    }

    public interface Url {
        String SERVER_ADDRESS = "http://192.168.43.1:" + PORT;
        String CURRENT_INFO = SERVER_ADDRESS + Method.CURRENT_INFO;
        String CURRENT_ALBUMART = SERVER_ADDRESS + Method.CURRENT_ALBUMART;
        String STREAM = SERVER_ADDRESS + Method.STREAM;
    }

    public StreamServer() {
        super(8888);
        Injector.inject(this);
    }

    @Override
    public Response serve(IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        String uri = session.getUri();
        String clientIP = session.getHeaders().get("remote-addr");

        Log.d("LWM", "\nserve:\nmethod: " + method + "\nuri: " + uri);

        Map<String, String> files = new HashMap<String, String>();
        try {
            session.parseBody(files);
        } catch (IOException ioe) {
            return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        } catch (ResponseException re) {
            return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
        }
        Map<String, String> params = session.getParms();

        switch(method){
            case GET: // Outcoming info
                Song song = player.getCurrentSong();
                if (song != null) {
                    switch (uri) {
                        case Method.STREAM:
                            return stream(song);
                        case Method.CURRENT_INFO:
                            return currentInfo(song);
                        case Method.CURRENT_ALBUMART:
                            return currentAlbumArt(song);
                    }
                } else {
                    return new Response(Response.Status.NO_CONTENT, MIME_PLAINTEXT, "Player doesn't have current song.");
                }
            default:
                return new Response(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Only REQUEST is supported.");
        }
    }

    private Response stream(Song song) {
        Log.d(App.TAG, "StreamServer: STREAM");
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(song.getSource());

        } catch (FileNotFoundException e) {e.printStackTrace();}

        Response res = new Response(Response.Status.OK, "audio/x-mpeg", fis);
        res.addHeader("Connection", "Keep-Alive");
//        res.setChunkedTransfer(true);
        return res;
    }

    private Response currentInfo(Song song) {
        Log.d(App.TAG, "StreamServer: CURRENT_INFO");
        return new Response(Response.Status.OK, "application/json", getSongInfoJSON(song));
    }

    private Response currentAlbumArt(Song song) {
        Log.d(App.TAG, "StreamServer: CURRENT_ALBUMART");
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(song.getAlbumArtUri());
        } catch (FileNotFoundException ignored) {}
        return new Response(Response.Status.OK, "image", is);
    }

    private String getSongInfoJSON(Song song){
        return new Gson().toJson(song);
    }

}