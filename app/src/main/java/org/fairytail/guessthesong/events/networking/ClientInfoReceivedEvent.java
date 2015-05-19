package org.fairytail.guessthesong.events.networking;

import org.fairytail.guessthesong.networking.entities.ClientInfo;
import org.java_websocket.WebSocket;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientInfoReceivedEvent {

    public final WebSocket webSocket;
    public final ClientInfo clientInfo;

}
