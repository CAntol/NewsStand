package edu.umd.umiacs.newsstand;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;

public class Sources extends PreferenceActivity implements OnSharedPreferenceChangeListener{

		SharedPreferences prefs;
		SharedPreferences.Editor editor;
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.sources);
	        prefs = getSharedPreferences("sources", 0);
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
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		String value = sharedPreferences.getAll().get(key).toString();
		editor.putString(key, value);
		editor.commit();
	}

}
