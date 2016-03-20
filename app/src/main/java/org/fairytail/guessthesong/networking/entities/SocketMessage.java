package org.fairytail.guessthesong.networking.entities;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
@AllArgsConstructor
@RequiredArgsConstructor
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

    public SocketMessage(String userId, Type type, Message message, Status status) {
        this(userId, type, message);
        this.status = status;
    }

    public SocketMessage(String userId, Type type, Message message, String body) {
        this(userId, type, message);
        this.body = body;
    }

    public final String userId;
    public final Type type;
    public final Message message;
    public Status status;
    public String body;

    public static SocketMessage newMessage(String userId, Type type, Message msg, Status status, String str) {
        if (str == null) return new SocketMessage(userId, type, msg, status, null);
        return new SocketMessage(userId, type, msg, status, str);
    }
}