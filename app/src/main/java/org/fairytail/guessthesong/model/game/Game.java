package org.fairytail.guessthesong.model.game;

import org.fairytail.guessthesong.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Game {
    private final Difficulty difficulty;
    private final List<Quiz> quizzes;

    public int countCorrectQuizzes() {
        int cnt = 0;
        for (Quiz q : quizzes) {
            cnt += q.isCorrect() ? 1 : 0;
        }

        return cnt;
    }

    public static class Creator {

        public Game create(Difficulty.Level level, List<Song> correctSongs, List<Song> allSongs) {
            Difficulty difficulty = Difficulty.Factory.create(level);
            List<Quiz> quizzes = createQuizzes(difficulty, correctSongs, allSongs);

            return new Game(difficulty, quizzes);
        }

        private List<Quiz> createQuizzes(Difficulty difficulty, List<Song> correctSongs, List<Song> allSongs) {
            List<Quiz> quizzes = new ArrayList<>();

            for (Song s : correctSongs) {
                quizzes.add(new Quiz(s, shuffleSongsWithThis(allSongs, s, difficulty.getVariants()), difficulty));
            }

            return quizzes;
        }

        private List<Song> shuffleSongsWithThis(List<Song> songs, Song song, int num) {
            List<Song> shuffledSongs = new ArrayList<>(songs);
            Collections.shuffle(shuffledSongs);

            shuffledSongs = shuffledSongs.subList(0, num - 1);
            shuffledSongs.add(song);
            Collections.shuffle(shuffledSongs);

            return shuffledSongs;
        }

    }

}