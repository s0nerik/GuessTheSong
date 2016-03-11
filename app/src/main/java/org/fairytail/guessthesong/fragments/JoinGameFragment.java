package org.fairytail.guessthesong.fragments;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.activities.GameActivity;
import org.fairytail.guessthesong.adapters.join_game.JoinGameAdapter;
import org.fairytail.guessthesong.adapters.join_game.JoinGameItem;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.ShouldStartMultiplayerGameEvent;
import org.fairytail.guessthesong.events.ui.MpGameSelectedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import lombok.val;
import ru.noties.debug.Debug;

public class JoinGameFragment extends Fragment {

    @Inject
    Bus bus;

    @Inject
    App app;

    @Inject
    Handler handler;

    @InjectView(R.id.recycler)
    RecyclerView recycler;

    private List<JoinGameItem> games = new ArrayList<>();
    private JoinGameAdapter adapter = new JoinGameAdapter(games);

    private Salut network;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        bus.register(this);

        SalutDataReceiver dataReceiver = new SalutDataReceiver(getActivity(), o -> Debug.d(o.toString()));
        SalutServiceData serviceData = new SalutServiceData("_lwm", 50489, "Device");
        network = new Salut(dataReceiver, serviceData, () -> Debug.e("Device not supported."));

        discoverServices();
    }

    @Override
    public void onStop() {
        network.stopServiceDiscovery(true);
        super.onStop();
    }

    private void discoverServices() {
        updateDevicesList();

//        network.discoverWithTimeout(
//                this::updateDevicesList,
//                () -> Debug.d("Bummer, we didn't find anyone. "),
//                5000);
    }

    private void updateDevicesList() {
        Debug.d("Look at all these devices! " + network.foundDevices.toString());

        games.clear();

        for (int i = 0; i < 10; i++) {
            val p2pDevice = new WifiP2pDevice();
            p2pDevice.deviceName = "Device "+i;

            val txtRecord = new HashMap<String, String>();
            txtRecord.put("players", String.valueOf(i));

            val device = new SalutDevice(p2pDevice, txtRecord);
            games.add(new JoinGameItem(device));
        }
        
//        for (val d : network.foundDevices) {
//            games.add(new JoinGameItem(d));
//        }

        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_game, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void oonEvent(MpGameSelectedEvent event) {
        Debug.d("Selected game: "+event.device.deviceName);
    }

    @Subscribe
    public void onStartMultiplayerGame(ShouldStartMultiplayerGameEvent event) {
        Debug.d();
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra("game", event.game);
        intent.putExtra("multiplayer", true);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
