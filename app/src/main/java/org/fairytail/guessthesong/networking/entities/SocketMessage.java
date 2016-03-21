package org.fairytail.guessthesong.networking.entities;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@JsonObject
@AllArgsConstructor
@NoArgsConstructor
public class SocketMessage {
    public enum Message {
        PLAYER_INFO,
        SONG,
        PREPARE
    }

    public enum Type {
        REQUEST, RESPONSE, OFFER
    }

    public enum Status {
        OK, ERROR
    }

    public SocketMessage(String userId, Type type, Message message) {
        this.userId = userId;
        this.type = type;
        this.message = message;
    }

    public SocketMessage(String userId, Type type, Message message, Status status) {
        this(userId, type, message);
        this.status = status;
    }

    public SocketMessage(String userId, Type type, Message message, String body) {
        this(userId, type, message);
        this.body = body;
    }

    @JsonField
    public String userId;
    @JsonField
    public Type type;
    @JsonField
    public Message message;
    @JsonField
    public Status status;
    @JsonField
    public String body;

    public static SocketMessage newMessage(String userId, Type type, Message msg, Status status, String str) {
        return new SocketMessage(userId, type, msg, status, str);
    }
}