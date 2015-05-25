package org.fairytail.guessthesong.networking.entities;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class SocketMessage {
    public enum Message {
        CLIENT_INFO,
        GAME,
        START_GAME,
        PREPARE_AND_SEEK,
        PLAYBACK_START, PLAYBACK_STOP,
        GAME_RESULTS,
        ALL_GAME_RESULTS
    }

    public enum Type {
        GET, POST
    }

    public enum Status {
        OK, ERROR
    }

    public SocketMessage(Type type, Message message, String body) {
        this.type = type;
        this.message = message;
        this.body = body;
    }

    public SocketMessage(Type type, Message message, Status status) {
        this.type = type;
        this.message = message;
        this.status = status;
    }

    public final Type type;
    public final Message message;
    public Status status;
    public String body;

}