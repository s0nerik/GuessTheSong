package org.fairytail.guessthesong.events;

import org.fairytail.guessthesong.model.game.Quiz;

import lombok.Data;

@Data
public class QuizTimeOverEvent {
    private final Quiz quiz;
}
