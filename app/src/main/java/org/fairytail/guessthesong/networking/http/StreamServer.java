package org.fairytail.guessthesong.networking.http;

import android.content.Context;
import android.util.Log;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.helpers.Strings;
import org.fairytail.guessthesong.player.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import fi.iki.elonen.NanoHTTPD;
import lombok.val;

public class StreamServer extends NanoHTTPD {

    @Inject
    Player player;

    @Inject
    Context context;

    public interface Method {
        String SONG = "/song";
    }

    public StreamServer(int port) {
        super(port);
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
            val msg = "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, msg);
        } catch (ResponseException re) {
            val msg = re.getMessage();
            return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, msg);
        }

        switch(method) {
            case GET:
                if (uri.startsWith(Method.SONG)) {
                    val path = uri.substring(Method.SONG.length());
                    if (Strings.isNullOrEmpty(path)) {
                        val msg = "Can't stream the song: Path is not specified.";
                        return newFixedLengthResponse(Response.Status.NO_CONTENT, MIME_PLAINTEXT, msg);
                    } else {
                        return stream(path);
                    }
                }
            default:
                val msg = "Only REQUEST is supported.";
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, msg);
        }
    }

    private Response stream(String path) {
        Log.d(App.TAG, "StreamServer: SONG");
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(context.getFilesDir().getAbsolutePath()+path);

        } catch (FileNotFoundException e) {e.printStackTrace();}

        Response res = newChunkedResponse(Response.Status.OK, "audio/x-mpeg", fis);
        res.addHeader("Connection", "Keep-Alive");
//        res.setChunkedTransfer(true);
        return res;
    }

}