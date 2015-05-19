package org.fairytail.guessthesong.prefs;

import android.content.Context;
import android.preference.PreferenceManager;

import com.tale.prettysharedpreferences.PrettySharedPreferences;
import com.tale.prettysharedpreferences.StringEditor;

@SuppressWarnings("unchecked")
public class Prefs extends PrettySharedPreferences<Prefs> {

    public Prefs(Context context) {
        super(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public StringEditor<Prefs> clientName() {
        return getStringEditor("client_name");
    }

}