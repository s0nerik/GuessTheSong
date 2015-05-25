package org.fairytail.guessthesong.networking.ws;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import org.fairytail.guessthesong.activities.GameActivity;
import org.fairytail.guessthesong.dagger.Injector;
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

public class GameWebSocketClient extends WebSocketClient {

    @Inject
    Player player;
    @Inject
    Bus bus;
    @Inject
    Gson gson;
    @Inject
    Prefs prefs;

    private Context context;

    private ClientInfo clientInfo;

    private Game game;

    public GameWebSocketClient(Context context, URI serverURI) {
        super(serverURI);
        Injector.inject(this);
        this.context = context;
        clientInfo = new ClientInfo(prefs.clientName().getOr(Build.MODEL));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Debug.d("GameWebSocketClient: opened with handshake:"
                + "\nStatus: " + handshakedata.getHttpStatus()
                + "\nMessage: " + handshakedata.getHttpStatusMessage());
        bus.register(this);
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
                    send(gson.toJson(new SocketMessage(SocketMessage.Type.POST, SocketMessage.Message.CLIENT_INFO, info)));
                    break;
                default:
                    Debug.e("Can't process message: "+socketMessage.message.name());
            }
        } else if (socketMessage.type == SocketMessage.Type.POST) {
            switch (socketMessage.message) {
                case START_GAME:
                    Debug.d("socketMessage: START_GAME");
                    if (game == null) {
                        throw new RuntimeException("game == null");
                    }
                    startMultiplayerGameClient(game);
                    send(gson.toJson(new SocketMessage(
                                    SocketMessage.Type.POST,
                                    SocketMessage.Message.START_GAME,
                                    SocketMessage.Status.OK))
                    );
                    break;
                case GAME:
                    Debug.d("socketMessage: GAME");
                    game = gson.fromJson(body, Game.class);
                    convertGameToMultiplayerGame(game);
                    send(gson.toJson(new SocketMessage(
                            SocketMessage.Type.POST,
                            SocketMessage.Message.GAME,
                            SocketMessage.Status.OK))
                    );
                    break;
                case PREPARE_AND_SEEK:
                    Debug.d("socketMessage: PREPARE_AND_SEEK");
                    StartFromEntity startFromEntity = gson.fromJson(body, StartFromEntity.class);
                    player.prepareAndSeekTo(
                            startFromEntity.song,
                            startFromEntity.time,
                            p -> send(gson.toJson(new SocketMessage(SocketMessage.Type.POST, SocketMessage.Message.PREPARE_AND_SEEK, SocketMessage.Status.OK)))
                    );
                    send(gson.toJson(new SocketMessage(
                                    SocketMessage.Type.POST,
                                    SocketMessage.Message.PREPARE_AND_SEEK,
                                    SocketMessage.Status.OK))
                    );
                    break;
                case START:
                    Debug.d("socketMessage: START");
                    player.start();
                    break;
                case STOP:
                    Debug.d("socketMessage: STOP");
                    player.stop();
                    break;
                case CLIENT_INFO:
                    Debug.d("socketMessage: CLIENT_INFO");
                    ClientInfo clientInfo = gson.fromJson(body, ClientInfo.class);
                    bus.post(new ClientInfoReceivedEvent(getConnection(), clientInfo));
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

    private void startMultiplayerGameClient(Game game) {
        Debug.d();
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("game", game);
        intent.putExtra("multiplayer", true);
        context.startActivity(intent);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Debug.d("GameWebSocketClient: closed:\nCode: "+code+" Reason: "+reason);
//        bus.post(new SocketClosedEvent());
        bus.unregister(this);
    }

    @Override
    public void onError(Exception ex) {
        Debug.d("GameWebSocketClient: error:\n" + ex);
    }
}
