package org.fairytail.guessthesong.events.networking;


import org.fairytail.guessthesong.networking.entities.ClientInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientStateChangedEvent {

    public enum State { CONNECTED, DISCONNECTED }

    public final ClientInfo clientInfo;
    public final State state;

}
