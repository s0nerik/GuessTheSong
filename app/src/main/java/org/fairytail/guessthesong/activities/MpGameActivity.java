package org.fairytail.guessthesong.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.squareup.otto.Bus;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.helpers.service_binder.RxServiceBinding;
import org.fairytail.guessthesong.services.MultiplayerService;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import rx.Subscription;

@RequireBundler
public abstract class MpGameActivity extends FragmentActivity {

    @Inject
    Bus bus;

    @Arg
    HashMap<String, String> serviceRecord;

    protected RxServiceBinding<MultiplayerService.Binder> binder = new RxServiceBinding<>(this);

    private Subscription mpServiceSubscription;
    protected abstract Subscription provideBoundServiceSubscription();
    protected abstract void injectArgs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp_game);
        ButterKnife.inject(this);
        Injector.inject(this);
        bus.register(this);

        injectArgs();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState != null) return;

        mpServiceSubscription = provideBoundServiceSubscription();
    }

    @Override
    protected void onDestroy() {
        mpServiceSubscription.unsubscribe();
        super.onDestroy();
    }
}