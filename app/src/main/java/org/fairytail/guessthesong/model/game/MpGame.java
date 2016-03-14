package org.fairytail.guessthesong.model.game;

import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MpGame {
    private final Game game;
    private final UUID uuid = UUID.randomUUID();
}
