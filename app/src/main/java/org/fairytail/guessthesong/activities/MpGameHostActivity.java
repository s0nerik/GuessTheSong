package org.fairytail.guessthesong.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.squareup.otto.Bus;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.helpers.service_binder.RxServiceBinding;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.services.MultiplayerHostService;
import org.fairytail.guessthesong.services.MultiplayerService;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Subscription;

@RequireBundler
public class MpGameHostActivity extends FragmentActivity {

    @Inject
    Bus bus;

    @InjectView(R.id.progress_bar)
    MaterialProgressBar progressBar;

    @Arg
    HashMap<String, String> serviceRecord;

    @Arg
    Game game;

    private RxServiceBinding<MultiplayerService.Binder> binder = new RxServiceBinding<>(this);

    private Subscription mpServiceSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp_game);
        ButterKnife.inject(this);
        Injector.inject(this);
        bus.register(this);

        Bundler.inject(this);
        mpServiceSubscription = binder.bindService(MultiplayerHostService.class,
                                                   Bundler.multiplayerService(serviceRecord).bundle())
                                      .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                                      .concatMap(service -> service.prepareNewGame(game))
                                      .doOnNext(service -> progressBar.setVisibility(View.GONE))
                                      .subscribe();
    }

    @Override
    protected void onDestroy() {
        mpServiceSubscription.unsubscribe();
        super.onDestroy();
    }
}