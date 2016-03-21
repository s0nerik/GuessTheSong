package org.fairytail.guessthesong.helpers;

import android.content.res.Resources;
import android.support.annotation.ArrayRes;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.f2prateek.rx.preferences.Preference;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.fairytail.guessthesong.App;
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

import lombok.Data;
import lombok.val;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class MpGameCreationHelper extends Daggered {

    @Data
    public static class Result {
        private final HashMap<String, String> serviceRecord;
        private final Game game;
    }

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

    public Observable<Result> createNewGame(List<Song> allSongs) {
        return Observable.create(subscriber -> {
            CompositeSubscription sub = new CompositeSubscription();

            MaterialDialog dialog = new MaterialDialog.Builder(App.getCurrentActivity())
                    .customView(R.layout.dialog_create_mp_game, false)
                    .title("New multiplayer game")
                    .cancelable(true)
                    .canceledOnTouchOutside(true)
                    .negativeText("Cancel")
                    .positiveText("Create")
                    .onPositive((dialog1, which) -> {
                        subscriber.onNext(prepareNewGame(allSongs));
                        subscriber.onCompleted();
                    })
                    .dismissListener(d -> {
                        sub.unsubscribe();
                        subscriber.onCompleted();
                    })
                    .build();

            View view = dialog.getCustomView();

            EditText name = (EditText) view.findViewById(R.id.name);
            name.setText(mpGameName.get());
            sub.add(RxTextView.textChanges(name)
                              .skip(1)
                              .map(CharSequence::toString)
                              .subscribe(mpGameName.asAction()));

            Spinner maxPlayers = (Spinner) view.findViewById(R.id.max_players);
            maxPlayers.setSelection(indexInStrArray(mpGameMaxPlayers.get().toString(), R.array.max_players_values));
            sub.add(RxAdapterView.itemSelections(maxPlayers)
                                 .skip(1)
                                 .map(i -> res.getStringArray(R.array.max_players_values)[i])
                                 .map(Integer::valueOf)
                                 .subscribe(mpGameMaxPlayers.asAction()));

            Spinner diff = (Spinner) view.findViewById(R.id.difficulty);
            diff.setSelection(indexInStrArrayIgnoreCase(mpGameDifficulty.get().getLevel().toString(), R.array.difficulties));
            sub.add(RxAdapterView.itemSelections(diff)
                                 .skip(1)
                                 .map(i -> res.getStringArray(R.array.difficulties)[i])
                                 .map(String::toUpperCase)
                                 .map(Difficulty.Level::valueOf)
                                 .map(Difficulty.Factory::create)
                                 .subscribe(mpGameDifficulty.asAction()));

            dialog.show();
        });
    }

    private Result prepareNewGame(List<Song> allSongs) {
        //  Create a string map containing information about your service.
        val record = new HashMap<String, String>();
        record.put("port", String.valueOf(8888));
        record.put("name", mpGameName.get());
        record.put("max_players", mpGameMaxPlayers.get().toString());
        record.put("difficulty", mpGameDifficulty.get().getLevel().toString());

        val game = Game.newRandom(mpGameDifficulty.get().getLevel(), allSongs);

        return new Result(record, game);
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
