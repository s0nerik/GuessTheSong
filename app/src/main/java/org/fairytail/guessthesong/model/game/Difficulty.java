package org.fairytail.guessthesong.model.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Difficulty {

    private String name;

    private long songDuration;
    private int variants;
    private boolean proposeSimilarStyles;
}