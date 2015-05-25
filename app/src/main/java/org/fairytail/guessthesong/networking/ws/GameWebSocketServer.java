package org.fairytail.guessthesong.networking.ws;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.networking.ClientReadyEvent;
import org.fairytail.guessthesong.events.networking.ClientStateChangedEvent;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.networking.entities.ClientInfo;
import org.fairytail.guessthesong.networking.entities.SocketMessage;
import org.fairytail.guessthesong.networking.entities.StartFromEntity;
import org.fairytail.guessthesong.player.Player;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import ru.noties.debug.Debug;

import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.GAME;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.PLAYBACK_START;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.PREPARE_AND_SEEK;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.START_GAME;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Type.GET;
import static org.fairytail.guessthesong.networking.entities.SocketMessage.Type.POST;

public class GameWebSocketServer extends WebSocketServer {

    private static final int TIMEOUT = 10 * 1000; // 10 seconds

    private Set<WebSocket> ready;

    private Map<WebSocket, ClientInfo> clientInfoMap = new HashMap<>();

    private long lastMessageTime = -1;

    @Inject
    Gson gson;

    @Inject
    Bus bus;

    @Inject
    Player player;

    private Game game;

    public GameWebSocketServer(InetSocketAddress address) {
        super(address);
        Injector.inject(this);
    }

    public void initWithGame(Game game) {
        this.game = game;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Debug.d("GameWebSocketServer: New connection");
        Debug.d("GameWebSocketServer: connections.size() = " + connections().size());
        conn.send(gson.toJson(new SocketMessage(POST, GAME, gson.toJson(game))));
//        conn.send(gson.toJson(new SocketMessage(SocketMessage.Type.GET, SocketMessage.Message.CLIENT_INFO)));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Debug.d("GameWebSocketServer: Close connection");
        Debug.d("GameWebSocketServer: connections.size() = "+connections().size());
        bus.post(new ClientStateChangedEvent(clientInfoMap.get(conn), ClientStateChangedEvent.State.DISCONNECTED));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Debug.d("WebSocket message: \"" + message + "\"");

        SocketMessage socketMessage = gson.fromJson(message, SocketMessage.class);
        String body = socketMessage.body;

        if (socketMessage.type == GET) {
            Debug.e("Can't process message: " + socketMessage.message.name());
        } else if (socketMessage.type == POST) { // Client returned some status (Assume OK)
//            if (socketMessage.status == OK) {
//                switch (socketMessage.message) {
//                    case GAME:
//                        processReadiness(conn, START_GAME);
//                        break;
//                    case START_GAME:
//                        conn.send(gson.toJson(new SocketMessage(POST, PREPARE_AND_SEEK, gson.toJson(new StartFromEntity(0, game.getQuizzes().get(0).getCorrectSong())))));
//                        break;
//                    case PREPARE_AND_SEEK:
//                        processReadiness(conn, PLAYBACK_START);
//                        break;
//                    default:
//                        Debug.e("Unknown message: "+socketMessage.message.name());
//                }
//            }
            switch (socketMessage.message) {
                case CLIENT_INFO:
                    Debug.d("webSocket: CLIENT_INFO");
                    processClientInfo(conn, gson.fromJson(body, ClientInfo.class));
                    break;
                case GAME:
                    Debug.d("webSocket: GAME");
                    processReadiness(conn, START_GAME);
                    break;
                case START_GAME:
                    Debug.d("webSocket: START_GAME");
                    player.prepareAndSeekTo(game.getQuizzes().get(0).getCorrectSong(), 40 * 1000, null);
                    conn.send(gson.toJson(
                            new SocketMessage(POST, PREPARE_AND_SEEK,
                                    gson.toJson(new StartFromEntity(40 * 1000, 0))
                            )
                    ));
                    break;
                case PREPARE_AND_SEEK:
                    Debug.d("webSocket: PREPARE_AND_SEEK");
                    processReadiness(conn, PLAYBACK_START, () -> player.start());
                    break;
                default:
                    Debug.e("Unknown message: "+socketMessage.message.name());
            }
        }

        lastMessageTime = System.currentTimeMillis();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Debug.e("GameWebSocketServer onError:\n", ex);
    }

    private void processReadiness(WebSocket conn, SocketMessage.Message message) {
        processReadiness(conn, message, null);
    }

    private void processReadiness(WebSocket conn, SocketMessage.Message message, @Nullable Runnable runnable) {
        if (ready == null) {
            ready = new HashSet<>();
        }
        ready.add(conn);

        bus.post(new ClientReadyEvent(conn));

        if(ready.size() == connections().size()) {
            if (runnable != null) runnable.run();
            conn.send(gson.toJson(new SocketMessage(POST, message)));
//            bus.post(new AllClientsReadyEvent());
            ready = null;
        }
    }

    private void processClientInfo(WebSocket conn, ClientInfo info) {
        clientInfoMap.put(conn, info);
        bus.post(new ClientStateChangedEvent(info, ClientStateChangedEvent.State.CONNECTED));
    }

}
