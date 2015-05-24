package org.fairytail.guessthesong.fragments;

import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.adapters.WiFiP2pDevicesAdapter;
import org.fairytail.guessthesong.async.SongsGetterService;
import org.fairytail.guessthesong.broadcasts.WiFiDirectBroadcastReceiver;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.db.Order;
import org.fairytail.guessthesong.events.p2p.P2PBroadcastReceivedEvent;
import org.fairytail.guessthesong.events.ui.WifiP2pDeviceSelectedEvent;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.networking.ws.GameWebSocketServer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.hoang8f.widget.FButton;
import ru.noties.debug.Debug;

public class CreateGameFragment extends Fragment {

    @Inject
    Bus bus;

    @Inject
    WifiP2pManager manager;

    @Inject
    GameWebSocketServer server;

    @InjectService
    SongsGetterService songsGetterService;

    private final IntentFilter intentFilter = new IntentFilter();

    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.start_game)
    FButton btnStartGame;

    private WifiP2pManager.Channel channel;
    private WiFiDirectBroadcastReceiver receiver;
    private List<WifiP2pDevice> peers = new ArrayList<>();

    private WiFiP2pDevicesAdapter adapter;

    private boolean isCreatingGame = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        AsyncService.inject(this);
        bus.register(this);

        initIntentFilter();

        channel = manager.initialize(getActivity(), getActivity().getMainLooper(), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new WiFiP2pDevicesAdapter(peers);

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver();
        getActivity().registerReceiver(receiver, intentFilter);

        manager.requestGroupInfo(channel, group -> {
            if (group != null) {
                manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        createGroup();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Debug.d("" + reason);
                    }
                });
            } else {
                createGroup();
            }
        });
    }

    private void createGroup() {
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Debug.d("MANAGER: created");
                isCreatingGame = true;
                songsGetterService.loadAllSongs(Order.RANDOM);
            }

            @Override
            public void onFailure(int reason) {
                Debug.d("" + reason);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @OnMessage
    public void onSongsAvailable(SongsGetterService.SongsListLoadedEvent e) {
        Log.d(App.TAG, e.getSongs().toString());
        if (isCreatingGame) {
            server.initWithGame(Game.newRandom(e.getSongs()));
            server.start();

            isCreatingGame = false;
        }
    }

    @Subscribe
    public void onP2PBroadcastReceived(P2PBroadcastReceivedEvent event) {
        Debug.d("onP2PBroadcastReceived");
        switch (event.intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                Debug.d("WIFI_P2P_STATE_CHANGED_ACTION");
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                Debug.d("WIFI_P2P_PEERS_CHANGED_ACTION");
                if (manager != null) {
                    manager.requestPeers(channel, peerList -> {
                        // Out with the old, in with the new.
                        peers.clear();
                        peers.addAll(peerList.getDeviceList());

                        adapter.notifyDataSetChanged();

                        Debug.d("Peers:");
                        for (WifiP2pDevice p : peers) {
                            Debug.d("deviceAddress: " + p.deviceAddress + "; deviceName: " + p.deviceName);
                        }

//                        if (!peers.isEmpty()) {
//                            connect(0);
//                        }
                    });
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                Debug.d("WIFI_P2P_CONNECTION_CHANGED_ACTION");
                NetworkInfo networkInfo = event.intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {

                    // We are connected with the other device, request connection
                    // info to find group owner IP

                    manager.requestConnectionInfo(channel, info -> {

                        // InetAddress from WifiP2pInfo struct.
                        InetAddress groupOwnerAddress = info.groupOwnerAddress;

                        // After the group negotiation, we can determine the group owner.
                        if (info.groupFormed && info.isGroupOwner) {
                            // Do whatever tasks are specific to the group owner.
                            // One common case is creating a server thread and accepting
                            // incoming connections.
                        } else if (info.groupFormed) {
                            // The other device acts as the client. In this case,
                            // you'll want to create a client thread that connects to the group
                            // owner.
                        }
                    });
                }

                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                Debug.d("WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                break;
        }
    }

    private void initIntentFilter() {
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Subscribe
    public void onDeviceSelected(WifiP2pDeviceSelectedEvent event) {
        Debug.d("onDeviceSelected: " + peers.indexOf(event.device));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
