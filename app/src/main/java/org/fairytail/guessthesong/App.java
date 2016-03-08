package org.fairytail.guessthesong;

import android.app.Application;
import android.support.annotation.NonNull;

import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.networking.ws.GameWebSocketClient;

import java.net.URI;

import javax.inject.Inject;

import ru.noties.debug.Debug;

public class App extends Application {

    public static final String TAG = "GuessTheSong";

    private GameWebSocketClient gameWebSocketClient;

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

    public GameWebSocketClient getWebSocketMessageClient(@NonNull URI uri) {
        if (gameWebSocketClient == null || !uri.equals(gameWebSocketClient.getURI())) {
//            URI.create("ws://" + groupOwnerAddress.getHostAddress() + ":4807")
            gameWebSocketClient = new GameWebSocketClient(uri);
        }
        return gameWebSocketClient;
    }

}
