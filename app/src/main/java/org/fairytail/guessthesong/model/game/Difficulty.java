package org.fairytail.guessthesong.model.game;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Difficulty implements Serializable {

    public enum Level {
        EASY, MEDIUM, HARD
    }

    private String name;

    private long songDuration;
    private int variants;
    private boolean proposeSimilarStyles;

    public static class Factory {

        public static Difficulty create(Level level) {
            switch (level) {
                case EASY:
                    return Difficulty.builder()
                            .name("Easy")
                            .songDuration(1000 * 30) // 30 seconds
                            .proposeSimilarStyles(false)
                            .variants(3)
                            .build();
                case MEDIUM:
                    return Difficulty.builder()
                            .name("Medium")
                            .songDuration(1000 * 20)  // 20 seconds
                            .proposeSimilarStyles(false)
                            .variants(4)
                            .build();
                case HARD:
                    return Difficulty.builder()
                            .name("Hard")
                            .songDuration(1000 * 10)  // 10 seconds
                            .proposeSimilarStyles(true)
                            .variants(6)
                            .build();
            }
            return null;
        }

    }

}