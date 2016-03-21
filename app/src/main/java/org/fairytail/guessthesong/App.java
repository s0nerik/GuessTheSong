package org.fairytail.guessthesong;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Injector;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import ru.noties.debug.Debug;

//import org.fairytail.guessthesong.networking.ws.GameWebSocketClient;

public class App extends Application {

    public static final String TAG = "GuessTheSong";

//    private GameWebSocketClient gameWebSocketClient;

    private static WeakReference<Activity> currentActivity;

    @Inject
    public static Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        AsyncService.inject(this);
        Injector.init(new AndroidModule(this), new PrefsModule(PreferenceManager.getDefaultSharedPreferences(this)));
        Injector.injectStatics();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {
                currentActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });

        Debug.init(BuildConfig.DEBUG);
    }

    public static Activity getCurrentActivity() {
        return currentActivity.get();
    }

//    public GameWebSocketClient getWebSocketMessageClient(@NonNull URI uri) {
//        if (gameWebSocketClient == null || !uri.equals(gameWebSocketClient.getURI())) {
////            URI.create("ws://" + groupOwnerAddress.getHostAddress() + ":4807")
//            gameWebSocketClient = new GameWebSocketClient(uri);
//        }
//        return gameWebSocketClient;
//    }

}
