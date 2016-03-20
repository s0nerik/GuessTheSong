package org.fairytail.guessthesong.networking.http;

import android.content.Context;
import android.util.Log;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.dagger.Injector;
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

        switch(method) {
            case GET:
                val path = params.get("path");
                if (path != null) {
                    switch (uri) {
                        case Method.SONG:
                            return stream(path);
                    }
                } else {
                    return new Response(Response.Status.NO_CONTENT, MIME_PLAINTEXT, "Can't stream the song: Path is not specified.");
                }
            default:
                return new Response(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Only REQUEST is supported.");
        }
    }

    private Response stream(String path) {
        Log.d(App.TAG, "StreamServer: SONG");
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(path);

        } catch (FileNotFoundException e) {e.printStackTrace();}

        Response res = new Response(Response.Status.OK, "audio/x-mpeg", fis);
        res.addHeader("Connection", "Keep-Alive");
//        res.setChunkedTransfer(true);
        return res;
    }

}