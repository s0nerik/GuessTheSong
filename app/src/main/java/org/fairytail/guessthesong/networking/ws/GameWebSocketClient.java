package org.fairytail.guessthesong.networking.ws;

import android.os.Build;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.MultiplayerGameStartedEvent;
import org.fairytail.guessthesong.events.ShouldStartMultiplayerGameEvent;
import org.fairytail.guessthesong.events.networking.ClientInfoReceivedEvent;
import org.fairytail.guessthesong.model.Song;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;
import org.fairytail.guessthesong.networking.entities.ClientInfo;
import org.fairytail.guessthesong.networking.entities.SocketMessage;
import org.fairytail.guessthesong.networking.entities.StartFromEntity;
import org.fairytail.guessthesong.player.Player;
import org.fairytail.guessthesong.prefs.Prefs;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import javax.inject.Inject;

import ru.noties.debug.Debug;

import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.GAME;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.PREPARE_AND_SEEK;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.START_GAME;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Status.OK;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Type.POST;

public class GameWebSocketClient extends WebSocketClient {

    @Inject
    Player player;
    @Inject
    Bus bus;
    @Inject
    Gson gson;
    @Inject
    Prefs prefs;

    private ClientInfo clientInfo;

    private Game game;

    public GameWebSocketClient(URI serverURI) {
        super(serverURI);
        Injector.inject(this);
        clientInfo = new ClientInfo(prefs.clientName().getOr(Build.MODEL));
        bus.register(this);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Debug.d("GameWebSocketClient: opened with handshake:"
                + "\nStatus: " + handshakedata.getHttpStatus()
                + "\nMessage: " + handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        Debug.d("GameWebSocketClient: \"" + message + "\"");

        SocketMessage socketMessage = gson.fromJson(message, SocketMessage.class);
        String body = socketMessage.body;

        if (socketMessage.type == SocketMessage.Type.GET) {
            switch (socketMessage.message) {
                case CLIENT_INFO:
                    String info = gson.toJson(clientInfo, ClientInfo.class);
                    send(gson.toJson(new SocketMessage(POST, SocketMessage.Message.CLIENT_INFO, info)));
                    break;
                default:
                    Debug.e("Can't process message: "+socketMessage.message.name());
            }
        } else if (socketMessage.type == POST) {
            switch (socketMessage.message) {
                case CLIENT_INFO:
                    Debug.d("socketMessage: CLIENT_INFO");
                    ClientInfo clientInfo = gson.fromJson(body, ClientInfo.class);
                    bus.post(new ClientInfoReceivedEvent(getConnection(), clientInfo));
                    break;
                case GAME:
                    Debug.d("socketMessage: GAME");
                    game = gson.fromJson(body, Game.class);
                    convertGameToMultiplayerGame(game);
                    send(gson.toJson(new SocketMessage(
                                    POST,
                                    GAME,
                                    OK))
                    );
                    break;
                case START_GAME:
                    Debug.d("socketMessage: START_GAME");
                    if (game == null) {
                        throw new RuntimeException("game == null");
                    }
                    bus.post(new ShouldStartMultiplayerGameEvent(game));
                    break;
                case PREPARE_AND_SEEK:
                    Debug.d("socketMessage: PREPARE_AND_SEEK");
                    StartFromEntity startFromEntity = gson.fromJson(body, StartFromEntity.class);
                    player.prepare(game.getQuizzes().get(startFromEntity.quizIndex).getCorrectSong(),
                            new Player.ActionCompletedListener() {
                                @Override
                                public void onActionCompleted(Player player) {
                                    send(gson.toJson(new SocketMessage(POST, PREPARE_AND_SEEK, OK)));
                                }
                            }
                            );
//                    player.prepareAndSeekTo(
//                            game.getQuizzes().get(startFromEntity.quizIndex).getCorrectSong(),
//                            startFromEntity.time,
//                            new Player.ActionCompletedListener() {
//                                @Override
//                                public void onActionCompleted(Player player) {
//                                    send(gson.toJson(new SocketMessage(POST, PREPARE_AND_SEEK, OK)));
//                                }
//                            });
//                            p -> send(gson.toJson(new SocketMessage(POST, PREPARE_AND_SEEK, OK)))
//                    );
                    break;
                case PLAYBACK_START:
                    Debug.d("socketMessage: START");
                    player.start();
                    break;
                case PLAYBACK_STOP:
                    Debug.d("socketMessage: STOP");
                    player.stop();
                    break;
                default:
                    Debug.e("Can't process message: "+socketMessage.message.name());
            }
        }
    }

    private void convertGameToMultiplayerGame(Game game) {
        String host = getURI().getHost();
        String streamUrl = "http://"+host+":8888/stream";

        for (Quiz q : game.getQuizzes()) {
            for (Song s : q.getVariants()) {
                s.setSource(streamUrl);
            }
            q.getCorrectSong().setSource(streamUrl);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Debug.d("GameWebSocketClient: closed:\nCode: "+code+" Reason: "+reason);
//        bus.post(new SocketClosedEvent());
//        bus.unregister(this);
    }

    @Override
    public void onError(Exception ex) {
        Debug.d("GameWebSocketClient: error:\n" + ex);
    }

    @Subscribe
    public void onMultiplayerGameStarted(MultiplayerGameStartedEvent event) {
        send(gson.toJson(new SocketMessage(
                        POST,
                        START_GAME,
                        OK))
        );
    }

}
