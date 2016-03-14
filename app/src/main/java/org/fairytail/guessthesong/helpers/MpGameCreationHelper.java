package org.fairytail.guessthesong.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.f2prateek.rx.preferences.Preference;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.fairytail.guessthesong.MultiplayerService;
import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Daggered;
import org.fairytail.guessthesong.model.Song;
import org.fairytail.guessthesong.model.game.Difficulty;
import org.fairytail.guessthesong.model.game.Game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.val;
import rx.subscriptions.CompositeSubscription;

public class MpGameCreationHelper extends Daggered {
    @Inject
    @Named("activity")
    Context context;

    @Inject
    @Named("mp game name")
    Preference<String> mpGameName;

    @Inject
    @Named("mp game max players")
    Preference<Integer> mpGameMaxPlayers;

    @Inject
    @Named("mp game difficulty")
    Preference<Difficulty> mpGameDifficulty;

    @Inject
    Resources res;

    public void createNewGame(List<Song> allSongs) {
        val sub = new CompositeSubscription();

        val dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_create_mp_game, false)
                .title("New multiplayer game")
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .negativeText("Cancel")
                .positiveText("Create")
                .onPositive((dialog1, which) -> startMultiplayerService(allSongs))
                .dismissListener(d -> sub.unsubscribe())
                .build();

        val view = dialog.getCustomView();

        val name = (EditText) view.findViewById(R.id.name);
        name.setText(mpGameName.get());
        sub.add(RxTextView.textChanges(name)
                .skip(1)
                .map(CharSequence::toString)
                .subscribe(mpGameName.asAction()));

        val maxPlayers = (Spinner) view.findViewById(R.id.max_players);
        maxPlayers.setSelection(indexInStrArray(mpGameMaxPlayers.get().toString(), R.array.max_players_values));
        sub.add(RxAdapterView.itemSelections(maxPlayers)
                .skip(1)
                .map(i -> res.getStringArray(R.array.max_players_values)[i])
                .map(Integer::valueOf)
                .subscribe(mpGameMaxPlayers.asAction()));

        val diff = (Spinner) view.findViewById(R.id.difficulty);
        diff.setSelection(indexInStrArrayIgnoreCase(mpGameDifficulty.get().getLevel().toString(), R.array.difficulties));
        sub.add(RxAdapterView.itemSelections(diff)
                .skip(1)
                .map(i -> res.getStringArray(R.array.difficulties)[i])
                .map(String::toUpperCase)
                .map(Difficulty.Level::valueOf)
                .map(Difficulty.Factory::create)
                .subscribe(mpGameDifficulty.asAction()));

        dialog.show();
    }

    private void startMultiplayerService(List<Song> allSongs) {
        //  Create a string map containing information about your service.
        val record = new HashMap<String, String>();
        record.put("port", String.valueOf(8888));
        record.put("name", mpGameName.get());
        record.put("max_players", mpGameMaxPlayers.get().toString());
        record.put("difficulty", mpGameDifficulty.get().getLevel().toString());

        val bundle = new Bundle();
        bundle.putSerializable("record", record);
        bundle.putSerializable("game", Game.newRandom(mpGameDifficulty.get().getLevel(), allSongs));

        context.startService(new Intent(context, MultiplayerService.class).putExtras(bundle));
    }

    private int indexInStrArray(String value, @ArrayRes int arrayId) {
        val strings = res.getStringArray(arrayId);
        return Arrays.asList(strings).indexOf(value);
    }

    private int indexInStrArrayIgnoreCase(String value, @ArrayRes int arrayId) {
        val strings = res.getStringArray(arrayId);
        for (int i = 0; i < strings.length; i++) {
            strings[i] = strings[i].toLowerCase();
        }
        return Arrays.asList(strings).indexOf(value.toLowerCase());
    }

}
