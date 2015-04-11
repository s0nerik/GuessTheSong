package org.fairytail.guessthesong.model.game;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Game {
    private final Difficulty difficulty;
    private final List<Quiz> quizzes;
}