package org.fairytail.guessthesong;

import android.app.Application;

import com.joanzapata.android.asyncservice.api.internal.AsyncService;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.dagger.modules.AndroidModule;

import ru.noties.debug.Debug;

public class App extends Application {

    public static final String TAG = "GuessTheSong";

    @Override
    public void onCreate() {
        super.onCreate();
        AsyncService.inject(this);
        Injector.init(new AndroidModule(this));
        Debug.init(BuildConfig.DEBUG);
    }
}
