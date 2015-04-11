package org.fairytail.guessthesong.model.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Difficulty {

    String name;

    long songDuration;
    int variants;
    boolean proposeSimilarStyles;
}