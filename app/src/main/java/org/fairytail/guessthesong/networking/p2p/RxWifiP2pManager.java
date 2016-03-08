package org.fairytail.guessthesong.networking.p2p;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;

import rx.Observable;

public class RxWifiP2pManager {
    public static class RxWifiP2pManagerException extends Exception {
        public RxWifiP2pManagerException() {}
        public RxWifiP2pManagerException(String detailMessage) {
            super(detailMessage);
        }
        public RxWifiP2pManagerException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
        public RxWifiP2pManagerException(Throwable throwable) {
            super(throwable);
        }
    }

    public static class AddLocalServiceFailedException extends RxWifiP2pManagerException {
        public int code;
        public AddLocalServiceFailedException(int code) {
            this.code = code;
        }
    }

    public static Observable<Void> addLocalService(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pDnsSdServiceInfo serviceInfo) {
        return Observable.create(subscriber ->
            manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    subscriber.onNext(null);
                }

                @Override
                public void onFailure(int code) {
                    subscriber.onError(new AddLocalServiceFailedException(code));
                }
            })
        );
    }

    public static Observable<Void> discoverServices(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pDnsSdServiceInfo serviceInfo) {
        return Observable.create(subscriber ->
                manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        subscriber.onNext(null);
                    }

                    @Override
                    public void onFailure(int code) {
                        subscriber.onError(new AddLocalServiceFailedException(code));
                    }
                })
        );
    }
}
