package org.fairytail.guessthesong;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutServiceData;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.model.Song;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.MpGame;
import org.fairytail.guessthesong.model.game.Quiz;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import lombok.val;
import ru.noties.debug.Debug;
import rx.Observable;

public class MultiplayerService extends Service {

    @Inject
    WifiManager wifiManager;

    private Salut network;

    public MultiplayerService() {
        super();
        Injector.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        val record = (Map<String, String>) intent.getSerializableExtra("record");
        val game = (Game) intent.getSerializableExtra("game");

        SalutDataReceiver dataReceiver = new SalutDataReceiver(App.getCurrentActivity(), o -> Debug.d(o.toString()));
        SalutServiceData serviceData = new SalutServiceData("lwm", 50489, Build.MODEL);

        network = new Salut(dataReceiver, serviceData, () -> Debug.e("Sorry, but this device does not support WiFi Direct."));
        network.thisDevice.txtRecord.putAll(record);

        network.startNetworkService(device -> Debug.d(device.readableName + " has connected!"));

        final long[] startTime = new long[1];
        convertToMpGame(game)
                .doOnSubscribe(() -> { Debug.d("Game conversion started!"); startTime[0] = System.currentTimeMillis(); })
                .subscribe(mpGame -> Debug.d("GAME SUCCESSFULLY CONVERTED! "+(System.currentTimeMillis()-startTime[0])));

        return START_NOT_STICKY;
    }

    private Observable<MpGame> convertToMpGame(Game game) {
        return loadFFMPEG().concatMap(ffmpeg -> {
            MpGame mpGame = new MpGame(game);
            Observable<MpGame> observable = Observable.<MpGame>empty();

            for (Quiz quiz : game.getQuizzes()) {
                observable = observable.mergeWith(convertQuiz(mpGame, quiz, ffmpeg).ignoreElements().map(aVoid -> null));
            }
            return observable.concatWith(Observable.just(mpGame));
        });
    }

    private Observable<Void> convertQuiz(MpGame game, Quiz quiz, FFmpeg ffmpeg) {
        return Observable.create(subscriber -> {
            File newSourceFolder = new File(getFilesDir(), game.getUuid().toString());
            if (!newSourceFolder.exists() && !newSourceFolder.mkdir()) {
                subscriber.onError(new Exception("Can't create a folder for the quiz."));
            }
            File newSource = new File(newSourceFolder, getTempName(quiz.getCorrectSong()));

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
                        quiz.getCorrectSong().setSource(newSource.getAbsolutePath());
                        subscriber.onNext(null);
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
            FFmpeg ffmpeg = FFmpeg.getInstance(this);
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

    @Override
    public void onDestroy() {
        network.stopNetworkService(false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
