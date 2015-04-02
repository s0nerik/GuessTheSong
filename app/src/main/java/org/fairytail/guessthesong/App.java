package org.fairytail.guessthesong;

import android.app.Application;

import org.fairytail.guessthesong.dagger.AndroidModule;
import org.fairytail.guessthesong.dagger.Injector;

public class App extends Application {

    public static final String TAG = "GuessTheSong";

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.init(new AndroidModule(this));
    }
}
