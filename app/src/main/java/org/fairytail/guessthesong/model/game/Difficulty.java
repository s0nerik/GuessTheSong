package org.fairytail.guessthesong.model.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Difficulty {
    long duration;
    int variants;
    boolean proposeSimilarStyles;
}