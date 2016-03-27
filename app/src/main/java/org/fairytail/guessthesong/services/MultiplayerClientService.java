package org.fairytail.guessthesong.services;

import android.net.wifi.WifiManager;

import com.f2prateek.rx.receivers.wifi.RxWifiManager;
import com.peak.salut.Salut;

import org.fairytail.guessthesong.helpers.JSON;
import org.fairytail.guessthesong.model.game.MpGame;
import org.fairytail.guessthesong.model.game.Quiz;
import org.fairytail.guessthesong.networking.entities.SocketMessage;
import org.fairytail.guessthesong.player.Player;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import in.workarounds.bundler.annotations.RequireBundler;
import lombok.val;
import ru.noties.debug.Debug;
import rx.Observable;
import rx.Subscription;

@RequireBundler
public class MultiplayerClientService extends MultiplayerService {

    @Inject
    Player player;

    public Salut getNetwork() {
        return network;
    }

    @Override
    protected Subscription[] subscribeListeners() {
        val subs = new Subscription[1];

        subs[0] = requests.filter(msg -> msg.message == SocketMessage.Message.PREPARE)
                          .map(msg -> JSON.parseSilently(msg.body, MpGame.class))
                          .concatMap(this::prepareNewGame)
//                          .doOnNext(mpGame -> {
//                              Debug.d("Starting playback!");
//                              Debug.d(mpGame.getGame().getQuizzes().get(0).getCorrectSong().toString());
//                              player.prepare(
//                                      mpGame.getGame().getQuizzes().get(0).getCorrectSong(),
//                                      Player::start
//                              );
//                          })
                          .subscribe(mpGame -> network.sendToHost(
                                  msgFactory.newPrepareCompletedResponse(),
                                  () -> Debug.e("Can't send a prepare completion response to the host.")));

        return subs;
    }

    private Observable<Void> enableWiFiIfNecessary() {
        return Observable.defer(() -> {
            if (!wifiManager.isWifiEnabled()) {
                return RxWifiManager.wifiStateChanges(getApplicationContext())
                                    .filter(state -> state == WifiManager.WIFI_STATE_ENABLED)
                                    .take(1)
                                    .map(s -> (Void) null)
                                    .delay(1, TimeUnit.SECONDS)
                                    .timeout(5, TimeUnit.SECONDS)
                                    .doOnSubscribe(() -> wifiManager.setWifiEnabled(true));
            }
            return Observable.just(null);
        });
    }

    private Observable<MpGame> prepareNewGame(MpGame game) {
        Observable<MpGame> observable = Observable.just(game);
        for (Quiz q : game.getGame().getQuizzes()) {
            val song = q.getCorrectSong();
//            observable = observable.concatMap(mpGame -> prepareSong(song).map(s -> mpGame));
        }
        return observable;
    }

//    private Observable<Song> prepareSong(Song s) {
//        val request = new Request.Builder()
//                .url(network.registeredHost.txtRecord.get("ip"))
//                .get()
//                .build();
//        val client = new OkHttpClient();
//        return Observable.<Response>create(subscriber -> {
//            try {
//                val response = client.newCall(request).execute();
//                subscriber.onNext(response);
//                subscriber.onCompleted();
//            } catch (IOException e) {
//                subscriber.onError(e);
//            }
//        }).map(o -> o.);

//        return new
//        return responses.filter(msg -> msg.message == SocketMessage.Message.SONG)
//                        .take(1)
//                        .doOnNext(Checked.a1(msg -> {
//                            File file = getNewFileToWrite(s.getSource());
//                            Files.write(msg.body, file, Charset.defaultCharset());
//                            s.setSource(file.getAbsolutePath());
//                        }))
//                        .doOnSubscribe(() -> network.sendToHost(msgFactory.newSongRequest(s.getSource()),
//                                                                () -> Debug.e("Can't request a song from the host!")))
//                        .map(msg -> s);
//    }

    private File getNewFileToWrite(String originalPath) {
        val latterPart = originalPath.replaceAll("(.*)(/.*/.*)", "$2");
        val finalPath = getApplicationContext().getFilesDir().getAbsolutePath()+latterPart;

        val file = new File(finalPath);
        file.getParentFile().mkdirs();

        return file;
    }

}
