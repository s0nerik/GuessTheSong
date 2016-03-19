package org.fairytail.guessthesong.networking.entities;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.fairytail.guessthesong.model.game.MpGame;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
@AllArgsConstructor
@RequiredArgsConstructor
public class SocketMessage {
    public enum Message {
        PLAYER_INFO,
        PREPARE
    }

    public enum Type {
        REQUEST, RESPONSE, OFFER
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

    @SneakyThrows
    public static <T> SocketMessage newMessage(Type type, Message msg, Status status, T obj) {
        if (obj == null) return new SocketMessage(type, msg, status, null);
        return new SocketMessage(type, msg, status, LoganSquare.serialize(obj));
    }

    public static SocketMessage newMessage(Type type, Message msg, Status status, String str) {
        if (str == null) return new SocketMessage(type, msg, status, null);
        return new SocketMessage(type, msg, status, str);
    }

    public static SocketMessage newPrepareMessage(MpGame game) {
        return newMessage(Type.OFFER, Message.PREPARE, Status.OK, game);
    }

    public static SocketMessage newPrepareCompletedMessage(String id) {
        return newMessage(Type.OFFER, Message.PREPARE, Status.OK, id);
    }

    public static SocketMessage newPlayerInfoMessage(PlayerInfo info) {
        return newMessage(Type.OFFER, Message.PLAYER_INFO, Status.OK, info);
    }
}