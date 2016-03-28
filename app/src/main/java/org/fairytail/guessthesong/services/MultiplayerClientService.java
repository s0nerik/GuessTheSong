package org.fairytail.guessthesong.services;

import android.net.wifi.WifiManager;

import com.f2prateek.rx.receivers.wifi.RxWifiManager;
import com.peak.salut.Salut;

import org.fairytail.guessthesong.helpers.GamePlayer;
import org.fairytail.guessthesong.helpers.JSON;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;
import org.fairytail.guessthesong.networking.entities.SocketMessage;
import org.fairytail.guessthesong.networking.http.StreamServer;

import java.util.concurrent.TimeUnit;

import in.workarounds.bundler.annotations.RequireBundler;
import lombok.val;
import ru.noties.debug.Debug;
import rx.Observable;
import rx.Subscription;

@RequireBundler
public class MultiplayerClientService extends MultiplayerService {

    private GamePlayer gamePlayer;

    public Salut getNetwork() {
        return network;
    }

    private void setCurrentGame(Game game) {
        currentGame = convertGameSongsToRemote(game,
                                               network.registeredHost.getServiceAddress(),
                                               network.registeredHost.txtRecord.get("http_port"));
        gamePlayer = new GamePlayer(game);
    }

    private Game convertGameSongsToRemote(Game game, String address, String port) {
        for (Quiz q : game.getQuizzes()) {
            val song = q.getCorrectSong();
            song.setRemoteSource("http://"+address+":"+port+StreamServer.Method.SONG+"/"+song.getRemoteSource());
            Debug.d("Song Remote Source: "+song.getRemoteSource());
        }
        return game;
    }

    @Override
    protected Subscription[] subscribeListeners() {
        val subs = new Subscription[1];

        subs[0] = requests.filter(msg -> msg.message == SocketMessage.Message.PREPARE)
                          .map(msg -> JSON.parseSilently(msg.body, Game.class))
                          .doOnNext(this::setCurrentGame)
                          .concatMap(mpGame -> gamePlayer.prepare(false))
                          .concatMap(game -> gamePlayer.start(game.getQuizzes().get(0)).map(o -> game))
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
}
