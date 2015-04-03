package org.fairytail.guessthesong;

import android.app.Application;

import com.joanzapata.android.asyncservice.api.internal.AsyncService;

import org.fairytail.guessthesong.dagger.AndroidModule;
import org.fairytail.guessthesong.dagger.Injector;

public class App extends Application {

    public static final String TAG = "GuessTheSong";

    @Override
    public void onCreate() {
        super.onCreate();
        AsyncService.inject(this);
        Injector.init(new AndroidModule(this));
    }
}
