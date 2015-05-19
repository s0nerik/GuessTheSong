package org.fairytail.guessthesong.events.networking;


import org.java_websocket.WebSocket;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientReadyEvent {

    public final WebSocket webSocket;

}
