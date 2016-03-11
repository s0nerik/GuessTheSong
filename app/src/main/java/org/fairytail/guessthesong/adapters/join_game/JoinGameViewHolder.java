package org.fairytail.guessthesong.adapters.join_game;

import android.view.View;
import android.widget.TextView;

import com.peak.salut.SalutDevice;
import com.squareup.otto.Bus;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.ui.MpGameSelectedEvent;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;
import lombok.val;

public class JoinGameViewHolder extends FlexibleViewHolder {

    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.subtitle)
    TextView subtitle;

    @Inject
    Bus bus;

    private SalutDevice device;

    public JoinGameViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        Injector.inject(this);
        ButterKnife.inject(this, view);
    }

    @OnClick(R.id.item)
    public void onItemClicked(View item) {
        bus.post(new MpGameSelectedEvent(device));
    }

    public void setDevice(SalutDevice device) {
        this.device = device;

        title.setText(device.deviceName);

        val players = device.txtRecord.get("players");

        if (players != null)
            subtitle.setText(players+" players");
    }

}
