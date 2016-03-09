package org.fairytail.guessthesong.fragments;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.activities.GameActivity;
import org.fairytail.guessthesong.async.SongsGetterService;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.networking.PlayersReadyToStartEvent;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.networking.http.StreamServer;
import org.fairytail.guessthesong.networking.ws.GameWebSocketServer;
import org.fairytail.guessthesong.player.Player;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;

public class CreateGameFragment extends Fragment {
    @Inject
    Bus bus;
    @Inject
    WifiP2pManager manager;
    @Inject
    GameWebSocketServer webSocketServer;
    @Inject
    StreamServer streamServer;
    @Inject
    Player player;
    @Inject
    Handler handler;

    @InjectService
    SongsGetterService songsGetterService;

    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.start_game)
    FButton btnStartGame;

    private boolean isCreatingGame = false;
    private Game game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        AsyncService.inject(this);
        bus.register(this);

//        initIntentFilter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        adapter = new WiFiP2pDevicesAdapter(peers);

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recycler.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @OnMessage
    public void onSongsAvailable(SongsGetterService.SongsListLoadedEvent e) {
        Log.d(App.TAG, e.getSongs().toString());
        if (isCreatingGame) {
            game = Game.newRandom(e.getSongs());
            webSocketServer.initWithGame(game);
            webSocketServer.start();
            try {
                streamServer.start();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            isCreatingGame = false;
        }
    }

    @OnClick(R.id.start_game)
    public void onStartGameClicked() {
        webSocketServer.startMultiplayerGame();
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra("game", game);
        intent.putExtra("multiplayer", true);
        startActivity(intent);
    }

    @Subscribe
    public void onPlayersReadyToStart(PlayersReadyToStartEvent event) {
        btnStartGame.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
