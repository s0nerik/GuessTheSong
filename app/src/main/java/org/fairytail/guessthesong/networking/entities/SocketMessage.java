package org.fairytail.guessthesong.networking.entities;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

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

    public static class MessageConverter extends StringBasedTypeConverter<Message> {
        @Override
        public Message getFromString(String string) {
            return Message.valueOf(string);
        }

        @Override
        public String convertToString(Message object) {
            return object.toString();
        }
    }

    public enum Type {
        REQUEST, RESPONSE, OFFER
    }

    public static class TypeConverter extends StringBasedTypeConverter<Type> {
        @Override
        public Type getFromString(String string) {
            return Type.valueOf(string);
        }

        @Override
        public String convertToString(Type object) {
            return object.toString();
        }
    }

    public enum Status {
        OK, ERROR
    }

    public static class StatusConverter extends StringBasedTypeConverter<Status> {
        @Override
        public Status getFromString(String string) {
            return Status.valueOf(string);
        }

        @Override
        public String convertToString(Status object) {
            return object.toString();
        }
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
    @JsonField(typeConverter = TypeConverter.class)
    public Type type;
    @JsonField(typeConverter = MessageConverter.class)
    public Message message;
    @JsonField(typeConverter = StatusConverter.class)
    public Status status;
    @JsonField
    public String body;

    public static SocketMessage newMessage(String userId, Type type, Message msg, Status status, String str) {
        return new SocketMessage(userId, type, msg, status, str);
    }
}