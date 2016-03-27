package org.fairytail.guessthesong.model.game;

import android.net.Uri;

public class MpQuiz extends Quiz {
    private final String serverAddress;

    public MpQuiz(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public Uri getSongUri() {
        return Uri.parse(serverAddress+"/"+getCorrectSong().getSource());
    }
}
