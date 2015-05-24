package org.fairytail.guessthesong.adapters;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.ui.WifiP2pDeviceSelectedEvent;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class WiFiP2pDevicesAdapter extends RecyclerView.Adapter<WiFiP2pDevicesAdapter.ViewHolder> {

    @Inject
    LayoutInflater layoutInflater;

    @Inject
    Bus bus;

    private final List<WifiP2pDevice> devices;

    public WiFiP2pDevicesAdapter(List<WifiP2pDevice> devices) {
        this.devices = devices;
        Injector.inject(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_join_game, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(devices.get(position).deviceName);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.title)
        TextView title;

        @InjectView(R.id.subtitle)
        TextView subtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.item)
        public void onItemClicked(View item) {
            bus.post(new WifiP2pDeviceSelectedEvent(devices.get(getAdapterPosition())));
        }

    }

}
