package org.fairytail.guessthesong.dagger.modules;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.activities.MainActivity;
import org.fairytail.guessthesong.bus.MainThreadBus;
import org.fairytail.guessthesong.player.MusicPlayer;
import org.fairytail.guessthesong.player.Player;
import org.fairytail.guessthesong.prefs.PrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MainActivity.class
        },
        library = true
)
public class MainModule {

    private final App application;

    public MainModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    PrefManager providePrefManager() {
        return new PrefManager(application);
    }

    @Provides
    @Singleton
    Bus provideBus() {
        return new MainThreadBus(ThreadEnforcer.ANY);
    }

    @Provides
    @Singleton
    Player providePlayer() {
        return new MusicPlayer();
    }

}
