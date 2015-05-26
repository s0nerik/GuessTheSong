package org.fairytail.guessthesong.dagger.modules;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.activities.GameActivity;
import org.fairytail.guessthesong.activities.MainActivity;
import org.fairytail.guessthesong.activities.ScoreActivity;
import org.fairytail.guessthesong.adapters.WiFiP2pDevicesAdapter;
import org.fairytail.guessthesong.broadcasts.WiFiDirectBroadcastReceiver;
import org.fairytail.guessthesong.bus.MainThreadBus;
import org.fairytail.guessthesong.db.SongsCursorGetter;
import org.fairytail.guessthesong.fragments.CreateGameFragment;
import org.fairytail.guessthesong.fragments.DifficultyFragment;
import org.fairytail.guessthesong.fragments.GameFragment;
import org.fairytail.guessthesong.fragments.JoinGameFragment;
import org.fairytail.guessthesong.model.game.Quiz;
import org.fairytail.guessthesong.networking.http.StreamServer;
import org.fairytail.guessthesong.networking.ws.GameWebSocketClient;
import org.fairytail.guessthesong.networking.ws.GameWebSocketServer;
import org.fairytail.guessthesong.player.MusicPlayer;
import org.fairytail.guessthesong.player.Player;
import org.fairytail.guessthesong.prefs.Prefs;

import java.net.InetSocketAddress;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WIFI_P2P_SERVICE;
import static android.content.Context.WIFI_SERVICE;

@Module(
        injects = {
                // Activities
                MainActivity.class,
                GameActivity.class,
                ScoreActivity.class,

                // Fragments
                DifficultyFragment.class,
                GameFragment.class,
                JoinGameFragment.class,
                CreateGameFragment.class,

                // Adapters
                WiFiP2pDevicesAdapter.class,

                // Others
                SongsCursorGetter.class,
                WiFiDirectBroadcastReceiver.class,
                Quiz.class,
                GameWebSocketServer.class,
                GameWebSocketClient.class,
                StreamServer.class
        },
        staticInjections = {
                App.class
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
    WifiP2pManager provideWifiP2pManager() {
        return (WifiP2pManager) application.getSystemService(WIFI_P2P_SERVICE);
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

    @Provides
    @Singleton
    Prefs providePrefs() {
        return new Prefs(application);
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

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    GameWebSocketServer provideWebSocketMessageServer() {
        return new GameWebSocketServer(new InetSocketAddress(4807));
    }

    @Provides
    @Singleton
    StreamServer provideStreamServer() {
        return new StreamServer();
    }

    @Provides
    @Singleton
    App provideApp() {
        return application;
    }
}