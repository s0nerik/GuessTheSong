package org.fairytail.guessthesong.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;

import butterknife.ButterKnife;

public class GameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.inject(this);
        Injector.inject(this);
    }
}
