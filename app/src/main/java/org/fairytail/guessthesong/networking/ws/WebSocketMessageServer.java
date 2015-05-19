package org.fairytail.guessthesong.networking.ws;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.networking.AllClientsReadyEvent;
import org.fairytail.guessthesong.events.networking.ClientReadyEvent;
import org.fairytail.guessthesong.events.networking.ClientStateChangedEvent;
import org.fairytail.guessthesong.networking.entities.ClientInfo;
import org.fairytail.guessthesong.networking.entities.SocketMessage;
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

public class WebSocketMessageServer extends WebSocketServer {

    public static final String URI = "ws://192.168.43.1:8080";

    private static final int TIMEOUT = 10 * 1000; // 10 seconds

    private Set<WebSocket> ready;

    private Map<WebSocket, ClientInfo> clientInfoMap = new HashMap<>();

    private long lastMessageTime = -1;

    @Inject
    Gson gson;

    @Inject
    Bus bus;

    public WebSocketMessageServer(InetSocketAddress address) {
        super(address);
        Injector.inject(this);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Debug.d("WebSocketMessageServer: New connection");
        Debug.d("WebSocketMessageServer: connections.size() = " + connections().size());
        conn.send(gson.toJson(new SocketMessage(SocketMessage.Type.GET, SocketMessage.Message.CLIENT_INFO)));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Debug.d("WebSocketMessageServer: Close connection");
        Debug.d("WebSocketMessageServer: connections.size() = "+connections().size());
        bus.post(new ClientStateChangedEvent(clientInfoMap.get(conn), ClientStateChangedEvent.State.DISCONNECTED));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Debug.d("WebSocket message: \"" + message + "\"");

        SocketMessage socketMessage = gson.fromJson(message, SocketMessage.class);
        String body = socketMessage.body;

        if (socketMessage.type == SocketMessage.Type.GET) {
            Debug.e("Can't process message: " + socketMessage.message.name());
        } else if (socketMessage.type == SocketMessage.Type.POST) {
            switch (socketMessage.message) {
                case PREPARE:
                case PREPARE_AND_SEEK:
                    processReadiness(conn);
                    break;
                case CLIENT_INFO:
                    processClientInfo(conn, gson.fromJson(body, ClientInfo.class));
                    break;
                default:
                    Debug.e("Can't process message: "+socketMessage.message.name());
            }
        }

        lastMessageTime = System.currentTimeMillis();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Debug.e("WebSocketMessageServer onError:\n", ex);
    }

    private void processReadiness(WebSocket conn) {
        if (ready == null) {
            ready = new HashSet<>();
        }
        ready.add(conn);

        bus.post(new ClientReadyEvent(conn));

        if(ready.size() == connections().size()) {
            bus.post(new AllClientsReadyEvent());
            ready = null;
        }
    }

    private void processClientInfo(WebSocket conn, ClientInfo info) {
        clientInfoMap.put(conn, info);
        bus.post(new ClientStateChangedEvent(info, ClientStateChangedEvent.State.CONNECTED));
    }

}
