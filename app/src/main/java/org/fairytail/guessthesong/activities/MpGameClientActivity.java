package org.fairytail.guessthesong.activities;

import org.fairytail.guessthesong.helpers.MpGameJoinHelper;
import org.fairytail.guessthesong.services.MultiplayerClientService;

import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.RequireBundler;
import rx.Subscription;

@RequireBundler
public class MpGameClientActivity extends MpGameActivity {
    @Override
    protected Subscription provideBoundServiceSubscription() {
        return binder.bindService(MultiplayerClientService.class,
                                  Bundler.multiplayerClientService(serviceRecord).bundle())
                     .subscribe(service -> {
                         new MpGameJoinHelper().joinGame(service.getNetwork());
                     });
    }

    @Override
    protected void injectArgs() {
        Bundler.inject(this);
    }
}