package edu.umd.umiacs.newsstand;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;



public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		CheckBoxPreference show_all = (CheckBoxPreference)getPreferenceScreen().findPreference("all_topics");
		show_all.setDisableDependentsState(true);
		prefs = getSharedPreferences("preferences", 0);
        editor = prefs.edit();
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		String value = pref.getAll().get(key).toString();
		editor.putString(key, value);
		editor.commit();
	}
	
	public void onBackPressed() {
		super.onBackPressed();
		if (getIntent().getBooleanExtra("ts", false))
			Toast.makeText(this, "Loading..", Toast.LENGTH_SHORT).show();
	}

}
