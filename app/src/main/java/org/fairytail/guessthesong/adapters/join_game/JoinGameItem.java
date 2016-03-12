package org.fairytail.guessthesong.adapters.join_game;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.peak.salut.SalutDevice;

import org.fairytail.guessthesong.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JoinGameItem extends AbstractFlexibleItem<JoinGameViewHolder> {

    @Getter
    private final SalutDevice device;

    @Override
    public int getLayoutRes() {
        return R.layout.item_join_game;
    }

    @Override
    public JoinGameViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new JoinGameViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, JoinGameViewHolder holder, int position, List payloads) {
        holder.setDevice(device);
    }

    @Override
    public boolean equals(Object o) {
        return device.equals(o);
    }

}
