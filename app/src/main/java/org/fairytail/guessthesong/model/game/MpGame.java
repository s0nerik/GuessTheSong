package org.fairytail.guessthesong.model.game;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonObject
@Data
@NoArgsConstructor
public class MpGame {
    @JsonField
    Game game;
    @JsonField
    UUID uuid = UUID.randomUUID();

    public MpGame(Game game) {
        this.game = game;
    }
}
