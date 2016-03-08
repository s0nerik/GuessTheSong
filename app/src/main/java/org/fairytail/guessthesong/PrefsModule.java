package org.fairytail.guessthesong;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import org.fairytail.guessthesong.model.game.Difficulty;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lombok.val;

@Module(library = true)
public class PrefsModule {
    private final RxSharedPreferences prefs;

    public PrefsModule(SharedPreferences sharedPrefs) {
        prefs = RxSharedPreferences.create(sharedPrefs);
    }

    @Provides
    @Singleton
    @Named("mp game name")
    Preference<String> mpGameName() {
        return prefs.getString("mpGameName");
    }

    @Provides
    @Singleton
    @Named("mp game max players")
    Preference<Integer> mpGameMaxPlayers() {
        return prefs.getInteger("mpGameMaxPlayers", 2);
    }

    @Provides
    @Singleton
    @Named("mp game difficulty")
    Preference<Difficulty> mpGameDifficulty() {
        return prefs.getObject("mpGameDifficulty", Difficulty.Factory.create(Difficulty.Level.EASY), new Preference.Adapter<Difficulty>() {
            @Override
            public Difficulty get(@NonNull String key, @NonNull SharedPreferences preferences) {
                val level = Difficulty.Level.valueOf(preferences.getString(key, Difficulty.Level.EASY.toString()));
                return Difficulty.Factory.create(level);
            }

            @Override
            public void set(@NonNull String key, @NonNull Difficulty value, @NonNull SharedPreferences.Editor editor) {
                editor.putString(key, value.getLevel().toString());
            }
        });
    }

}