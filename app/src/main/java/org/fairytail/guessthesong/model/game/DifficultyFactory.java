package org.fairytail.guessthesong.model.game;

public class DifficultyFactory {

    enum Type {
        EASY, MEDIUM, HARD
    }

    public Difficulty create(Type type) {
        switch (type) {
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