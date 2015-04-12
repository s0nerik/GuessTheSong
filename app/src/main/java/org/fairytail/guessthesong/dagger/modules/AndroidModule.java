package org.fairytail.guessthesong.dagger.modules;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.WindowManager;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.db.SongsCursorGetter;
import org.fairytail.guessthesong.fragments.DifficultyFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WIFI_SERVICE;

@Module(
        injects = {
            SongsCursorGetter.class,
            DifficultyFragment.class
        },
        library = true
)
public class AndroidModule {
    private final App application;

    public AndroidModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    WifiManager provideWifiManager() {
        return (WifiManager) application.getSystemService(WIFI_SERVICE);
    }

    @Provides
    @Singleton
    LayoutInflater provideLayoutInflater() {
        return (LayoutInflater) application.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Provides
    @Singleton
    AudioManager provideAudioManager() {
        return (AudioManager) application.getSystemService(AUDIO_SERVICE);
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager() {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @Singleton
    Resources provideResources() {
        return application.getResources();
    }

    @Provides
    @Singleton
    ContentResolver provideContentResolver() {
        return application.getContentResolver();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return application.getSharedPreferences("", MODE_PRIVATE);
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    @Singleton
    WindowManager provideWindowManager() {
        return (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
    }

}