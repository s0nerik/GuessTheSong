package org.fairytail.guessthesong.events;

import org.fairytail.guessthesong.model.game.Game;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MultiplayerGameStartedEvent {
    public final Game game;
}
