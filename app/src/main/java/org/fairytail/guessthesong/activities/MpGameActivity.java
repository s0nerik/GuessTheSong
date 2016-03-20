package org.fairytail.guessthesong.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.squareup.otto.Bus;

import org.fairytail.guessthesong.MultiplayerService;
import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.model.game.Game;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import ru.noties.debug.Debug;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@RequireBundler
public class MpGameActivity extends FragmentActivity {

    @Inject
    Bus bus;

    @InjectView(R.id.progress_bar)
    MaterialProgressBar progressBar;

    @Arg
    HashMap<String, String> serviceRecord;

    @Arg
    Game game;

    private Observable<MultiplayerService> bindMpService(Intent intent) {
        final Subject<MultiplayerService, MultiplayerService> subject = PublishSubject.create();
        final MultiplayerService[] mpService = new MultiplayerService[1];
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Debug.d();
                mpService[0] = ((MultiplayerService.MultiplayerServiceBinder) service).getService();
                subject.onNext(mpService[0]);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Debug.d();
                subject.onCompleted();
            }
        };
        return subject
                .doOnSubscribe(() -> {
                    startService(intent);
                    bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                })
                .doOnUnsubscribe(() -> {
                    stopService(intent);
                    unbindService(serviceConnection);
                });
    }

    private Subscription mpServiceSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp_game);
        ButterKnife.inject(this);
        Injector.inject(this);
        bus.register(this);

        Bundler.inject(this);
        mpServiceSubscription = bindMpService(Bundler.multiplayerService(serviceRecord).intent(this))
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