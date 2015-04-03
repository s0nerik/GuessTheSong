package org.fairytail.guessthesong.model.game;

public class DifficultyFactory {

    enum Type {
        EASY, MEDIUM, HARD
    }

    public Difficulty create(Type type) {
        switch (type) {
            case EASY:
                return Difficulty.builder()
                        .duration(1000 * 30)
                        .proposeSimilarStyles(false)
                        .variants(3)
                        .build();
            case MEDIUM:
                return Difficulty.builder()
                        .duration(1000 * 20)
                        .proposeSimilarStyles(false)
                        .variants(4)
                        .build();
            case HARD:
                return Difficulty.builder()
                        .duration(1000 * 10)
                        .proposeSimilarStyles(true)
                        .variants(6)
                        .build();
        }
        return null;
    }

}