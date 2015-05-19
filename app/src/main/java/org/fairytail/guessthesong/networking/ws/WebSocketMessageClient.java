package org.fairytail.guessthesong.networking.ws;

import android.os.Build;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.networking.ClientInfoReceivedEvent;
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

public class WebSocketMessageClient extends WebSocketClient {

    @Inject
    Player player;
    @Inject
    Bus bus;
    @Inject
    Gson gson;
    @Inject
    Prefs prefs;

    private ClientInfo clientInfo;

    public WebSocketMessageClient(URI serverURI) {
        super(serverURI);
        Injector.inject(this);
        clientInfo = new ClientInfo(prefs.clientName().getOr(Build.MODEL));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Debug.d("WebSocketMessageClient: opened with handshake:"
                + "\nStatus: " + handshakedata.getHttpStatus()
                + "\nMessage: " + handshakedata.getHttpStatusMessage());
        bus.register(this);
    }

    @Override
    public void onMessage(String message) {
        Debug.d("WebSocketMessageClient: \"" + message + "\"");

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
                case PREPARE_AND_SEEK:
                    StartFromEntity startFromEntity = gson.fromJson(body, StartFromEntity.class);
                    player.prepareAndSeekTo(
                            startFromEntity.song,
                            startFromEntity.time,
                            p -> send(gson.toJson(new SocketMessage(SocketMessage.Type.POST, SocketMessage.Message.PREPARE_AND_SEEK, SocketMessage.Status.OK)))
                    );
                    break;
                case START:
                    player.start();
                    break;
                case STOP:
                    player.stop();
                    break;
                case CLIENT_INFO:
                    ClientInfo clientInfo = gson.fromJson(body, ClientInfo.class);
                    bus.post(new ClientInfoReceivedEvent(getConnection(), clientInfo));
                    break;
                default:
                    Debug.e("Can't process message: "+socketMessage.message.name());
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Debug.d("WebSocketMessageClient: closed:\nCode: "+code+" Reason: "+reason);
//        bus.post(new SocketClosedEvent());
        bus.unregister(this);
    }

    @Override
    public void onError(Exception ex) {
        Debug.d("WebSocketMessageClient: error:\n" + ex);
    }
}
