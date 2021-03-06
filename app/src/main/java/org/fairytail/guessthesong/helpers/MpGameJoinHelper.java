package org.fairytail.guessthesong.helpers;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.peak.salut.Salut;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.adapters.join_game.JoinGameAdapter;
import org.fairytail.guessthesong.adapters.join_game.JoinGameItem;
import org.fairytail.guessthesong.dagger.Daggered;
import org.fairytail.guessthesong.events.ShouldStartMultiplayerGameEvent;
import org.fairytail.guessthesong.events.ui.MpGameSelectedEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.val;
import ru.noties.debug.Debug;

public class MpGameJoinHelper extends Daggered {
    @Inject
    Resources res;

    @Inject
    Bus bus;

    private List<JoinGameItem> games = new ArrayList<>();
    private JoinGameAdapter adapter = new JoinGameAdapter(games);

    private Salut network;

    public void joinGame(Salut network) {
        this.network = network;

        val dialog = new MaterialDialog.Builder(App.getCurrentActivity())
                .customView(R.layout.dialog_join_mp_game, false)
                .title("Join game")
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .negativeText("Cancel")
                .showListener(d -> bus.register(this))
                .dismissListener(d -> { bus.unregister(this); network.stopServiceDiscovery(false); })
                .build();

        val view = dialog.getCustomView();
        val recycler = (RecyclerView) view.findViewById(R.id.recycler);
        adapter = new JoinGameAdapter(games);
        recycler.setAdapter(adapter);

        dialog.show();
        discoverServices();
    }

    @Subscribe
    public void onEvent(MpGameSelectedEvent event) {
        network.stopServiceDiscovery(false);
        network.registerWithHost(event.device,
                                 () -> Debug.d("Registered!"),
                                 () -> Debug.d("Not registered!")
        );
    }

    @Subscribe
    public void onStartMultiplayerGame(ShouldStartMultiplayerGameEvent event) {
        Debug.d();
//        Intent intent = new Intent(context, GameActivity.class);
//        intent.putExtra("game", event.game);
//        intent.putExtra("multiplayer", true);
//        context.startActivity(intent);
    }

    private void discoverServices() {
        network.discoverNetworkServices(this::updateDevicesList, false);
    }

    private void updateDevicesList() {
        Debug.d("Look at all these devices! " + network.foundDevices.toString());

        games.clear();

//        for (int i = 0; i < 10; i++) {
//            val p2pDevice = new WifiP2pDevice();
//            p2pDevice.deviceName = "Device "+i;
//
//            val txtRecord = new HashMap<String, String>();
//            txtRecord.put("players", String.valueOf(i));
//
//            val device = new SalutDevice(p2pDevice, txtRecord);
//            games.add(new JoinGameItem(device));
//        }

        for (val d : network.foundDevices) {
            games.add(new JoinGameItem(d));
        }

        adapter.notifyDataSetChanged();
    }

}
