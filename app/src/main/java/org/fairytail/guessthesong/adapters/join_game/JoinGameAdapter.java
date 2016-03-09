package org.fairytail.guessthesong.adapters.join_game;

import android.support.annotation.NonNull;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class JoinGameAdapter extends FlexibleAdapter<JoinGameItem> {
    public JoinGameAdapter(@NonNull List<JoinGameItem> items) {
        super(items);
    }
}
