package org.fairytail.guessthesong.events;

import org.fairytail.guessthesong.model.Song;
import org.fairytail.guessthesong.model.game.Quiz;

import lombok.Data;

@Data
public class QuizSongChosenEvent {
    private final Quiz quiz;
    private final Song song;
}
