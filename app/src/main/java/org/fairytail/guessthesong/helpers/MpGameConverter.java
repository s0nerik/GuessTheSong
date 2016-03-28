package org.fairytail.guessthesong.helpers;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;

import org.fairytail.guessthesong.model.Song;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;

import java.io.File;

import lombok.RequiredArgsConstructor;
import ru.noties.debug.Debug;
import rx.Observable;

@RequiredArgsConstructor
public class MpGameConverter {

    private final Context context;

    public Observable<Game> convertToMpGame(Game game) {
        return loadFFMPEG().concatMap(ffmpeg -> {
            Game mpGame = new Game(game);
            Observable<Game> observable = Observable.<Game>empty();

            File newSourceFolder = new File(context.getFilesDir(), mpGame.getUuid().toString());
            if (!newSourceFolder.exists() && !newSourceFolder.mkdir()) {
                return Observable.error(new Exception("Can't create a folder for the game."));
            }

            for (Quiz quiz : mpGame.getQuizzes()) {
                observable = observable.mergeWith(
                        convertQuiz(quiz, newSourceFolder, ffmpeg)
                                .doOnNext(file -> {
                                    Song correctSong = quiz.getCorrectSong();
                                    correctSong.setSource(file.getAbsolutePath());
                                    String remoteSource = file.getParentFile().getName() + "/" + file.getName();
                                    Debug.d("RemoteSource: "+remoteSource);
                                    correctSong.setRemoteSource(remoteSource);

                                    quiz.setEndTime(quiz.getEndTime() - quiz.getStartTime());
                                    quiz.setStartTime(0);
                                })
                                .ignoreElements()
                                .map(aVoid -> null)
                );
            }
            return observable.concatWith(Observable.just(mpGame));
        });
    }

    private Observable<File> convertQuiz(Quiz quiz, File sourceFolder, FFmpeg ffmpeg) {
        return Observable.create(subscriber -> {
            File newSource = new File(sourceFolder, getTempName(quiz.getCorrectSong()));

            String[] command = new String[] {
                    "-ss", toSeconds(quiz.getStartTime()),
                    "-t", toSeconds(quiz.getEndTime()-quiz.getStartTime()),
                    "-i", quiz.getCorrectSong().getSource(),
                    "-acodec", "copy",
                    newSource.getAbsolutePath()
            };
            try {
                ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                    @Override
                    public void onSuccess(String message) {
                        subscriber.onNext(newSource);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(String message) {
                        Debug.e(message);
                        subscriber.onError(new Exception("Can't cut the song."));
                    }
                });
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    private String getTempName(Song song) {
        return song.hashCode()+song.getSource().substring(song.getSource().lastIndexOf("."));
    }

    private String toSeconds(long ms) {
        return String.valueOf(ms * 0.001f);
    }

    private Observable<FFmpeg> loadFFMPEG() {
        return Observable.create(subscriber -> {
            final FFmpeg ffmpeg = FFmpeg.getInstance(context);
            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                    @Override
                    public void onSuccess() {
                        subscriber.onNext(ffmpeg);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure() {
                        subscriber.onError(new Exception("Can't load ffmpeg binary."));
                    }
                });
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
