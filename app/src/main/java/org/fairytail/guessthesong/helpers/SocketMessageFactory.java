package org.fairytail.guessthesong.helpers;

import com.bluelinelabs.logansquare.LoganSquare;

import org.fairytail.guessthesong.model.game.MpGame;
import org.fairytail.guessthesong.networking.entities.PlayerInfo;
import org.fairytail.guessthesong.networking.entities.SocketMessage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class SocketMessageFactory {
    public final String userId;

    public SocketMessage newSongRequest(String path) {
        return newRequest(SocketMessage.Message.SONG, path);
    }

    public SocketMessage newSongResponse(byte[] songBytes) {
        return newResponse(SocketMessage.Message.SONG, SocketMessage.Status.OK, new String(songBytes));
    }

    @SneakyThrows
    public SocketMessage newPrepareRequest(MpGame game) {
        return newRequest(SocketMessage.Message.PREPARE, LoganSquare.serialize(game));
    }

    public SocketMessage newPrepareCompletedResponse() {
        return newResponse(SocketMessage.Message.PREPARE);
    }

    @SneakyThrows
    public SocketMessage newPlayerInfoOffer(PlayerInfo info) {
        return newOffer(SocketMessage.Message.PLAYER_INFO, LoganSquare.serialize(info));
    }

    //region Basic factory methods
    private SocketMessage newOfType(SocketMessage.Type type,
                                    SocketMessage.Message message,
                                    SocketMessage.Status status,
                                    String body) {
        return SocketMessage.newMessage(userId, type, message, status, body);
    }

    private SocketMessage newOfType(SocketMessage.Type type,
                                    SocketMessage.Message message,
                                    String body) {
        return newOfType(type, message, SocketMessage.Status.OK, body);
    }

    private SocketMessage newOfType(SocketMessage.Type type,
                                    SocketMessage.Message message,
                                    SocketMessage.Status status) {
        return newOfType(type, message, status, null);
    }

    private SocketMessage newOfType(SocketMessage.Type type,
                                    SocketMessage.Message message) {
        return newOfType(type, message, SocketMessage.Status.OK, null);
    }

    private SocketMessage newOffer(SocketMessage.Message message, String body) {
        return newOfType(SocketMessage.Type.OFFER, message, body);
    }

    private SocketMessage newOffer(SocketMessage.Message message) {
        return newOfType(SocketMessage.Type.OFFER, message);
    }

    private SocketMessage newRequest(SocketMessage.Message message, String body) {
        return newOfType(SocketMessage.Type.REQUEST, message, body);
    }

    private SocketMessage newRequest(SocketMessage.Message message) {
        return newOfType(SocketMessage.Type.REQUEST, message);
    }

    private SocketMessage newResponse(SocketMessage.Message message, SocketMessage.Status status, String body) {
        return newOfType(SocketMessage.Type.RESPONSE, message, status, body);
    }

    private SocketMessage newResponse(SocketMessage.Message message, SocketMessage.Status status) {
        return newOfType(SocketMessage.Type.RESPONSE, message, status);
    }

    private SocketMessage newResponse(SocketMessage.Message message, String body) {
        return newOfType(SocketMessage.Type.RESPONSE, message, body);
    }

    private SocketMessage newResponse(SocketMessage.Message message) {
        return newOfType(SocketMessage.Type.RESPONSE, message);
    }
    //endregion
}
