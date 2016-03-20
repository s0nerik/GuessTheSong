package org.fairytail.guessthesong.helpers.service_binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import java.util.concurrent.atomic.AtomicReference;

import lombok.RequiredArgsConstructor;
import ru.noties.debug.Debug;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@RequiredArgsConstructor
public class RxServiceBinding<TBinder extends RxServiceBinder> {
    private final Context context;

    public <TService> Observable<TService> bindService(Class<TService> clazz, Bundle b) {
        final Intent intent = new Intent(context, clazz).putExtras(b);

        final Subject<TService, TService> subject = PublishSubject.create();
        final AtomicReference<TService> service = new AtomicReference<>(null);
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Debug.d();
                service.set((TService) ((TBinder) binder).getService());
                subject.onNext(service.get());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Debug.d();
                subject.onCompleted();
            }
        };

        return subject
                .doOnSubscribe(() -> {
                    context.startService(intent);
                    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                })
                .doOnUnsubscribe(() -> {
                    context.stopService(intent);
                    context.unbindService(serviceConnection);
                });
    }
}
