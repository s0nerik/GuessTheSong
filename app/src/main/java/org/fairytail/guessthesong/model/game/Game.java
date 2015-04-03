package org.fairytail.guessthesong.model.game;

import org.fairytail.guessthesong.model.Song;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Game {
    final Difficulty difficulty;
    final List<Song> songs;
}