package org.fairytail.guessthesong.model.game;

import org.fairytail.guessthesong.model.Song;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Game implements Serializable {
    private final Difficulty difficulty;
    private final ArrayList<Quiz> quizzes;

    public int countCorrectQuizzes() {
        int cnt = 0;
        for (Quiz q : quizzes) {
            cnt += q.isCorrect() ? 1 : 0;
        }

        return cnt;
    }

    public static Game newRandom(List<Song> allSongs) {
        Random random = new Random();
        Difficulty.Level[] levels = Difficulty.Level.values();
        Difficulty.Level randomLevel = levels[(int) (random.nextFloat()*(levels.length-1))];

        List<Song> shuffled = new ArrayList<>(allSongs);
        Collections.shuffle(shuffled);

        return new Game.Creator().create(randomLevel, new ArrayList<>(shuffled.subList(0, random.nextInt(8) + 1)), shuffled);
    }

    public static class Creator {

        public Game create(Difficulty.Level level, List<Song> correctSongs, List<Song> allSongs) {
            Difficulty difficulty = Difficulty.Factory.create(level);
            List<Quiz> quizzes = createQuizzes(difficulty, correctSongs, allSongs);

            return new Game(difficulty, (ArrayList<Quiz>) quizzes);
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

            shuffledSongs = new ArrayList<>(shuffledSongs.subList(0, num - 1));
            shuffledSongs.add(song);
            Collections.shuffle(shuffledSongs);

            return shuffledSongs;
        }

    }

}