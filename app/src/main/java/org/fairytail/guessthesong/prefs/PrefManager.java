package org.fairytail.guessthesong.prefs;

import android.content.Context;
import android.preference.PreferenceManager;

import com.tale.prettysharedpreferences.PrettySharedPreferences;

public class PrefManager extends PrettySharedPreferences<PrefManager> {

    public PrefManager(Context context) {
        super(PreferenceManager.getDefaultSharedPreferences(context));
    }

}