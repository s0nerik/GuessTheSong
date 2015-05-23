package org.fairytail.guessthesong;

import android.app.Application;

import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.dagger.modules.AndroidModule;

import javax.inject.Inject;

import ru.noties.debug.Debug;

public class App extends Application {

    public static final String TAG = "GuessTheSong";

    @Inject
    public static Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        AsyncService.inject(this);
        Injector.init(new AndroidModule(this));
        Injector.injectStatics();
        Debug.init(BuildConfig.DEBUG);
    }
}
