package org.fairytail.guessthesong.activities;

import android.view.View;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.services.MultiplayerHostService;

import butterknife.InjectView;
import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Subscription;

@RequireBundler
public class MpGameHostActivity extends MpGameActivity {

    @InjectView(R.id.progress_bar)
    MaterialProgressBar progressBar;

    @Arg
    Game game;

    @Override
    protected Subscription provideBoundServiceSubscription() {
        return binder.bindService(MultiplayerHostService.class,
                                  Bundler.multiplayerHostService(serviceRecord).bundle())
                     .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                     .concatMap(service -> service.prepareNewGame(game))
                     .doOnNext(service -> progressBar.setVisibility(View.GONE))
                     .subscribe();
    }

    @Override
    protected void injectArgs() {
        Bundler.inject(this);
    }

}